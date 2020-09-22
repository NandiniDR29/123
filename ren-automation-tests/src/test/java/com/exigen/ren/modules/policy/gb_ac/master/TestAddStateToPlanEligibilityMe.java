package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum.PlanGenericInfoTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.OptionalBenefitTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.Tab.buttonNext;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.*;
import static com.exigen.ren.main.enums.TableConstants.Plans.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddStateToPlanEligibilityMe extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final MultiAssetList classificationManagementTabList = (MultiAssetList) GroupAccidentMasterPolicyContext.classificationManagementMPTab.getAssetList();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-37914"}, component = POLICY_GROUPBENEFITS)
    public void testAddCoverageBasisAttributeAndHealthScreeningBenefit() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "ME"), planDefinitionTab.getClass());

        LOGGER.info("REN-37914 Step#1 Check available plans");
        planDefinitionTab.getAssetList().getAsset(PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PLAN).buttonSearch.click();
        CustomAssertions.assertThat(planDefinitionTab.getAssetList().getAsset(PLAN).listboxAvailableItems)
                .hasOptions(ImmutableList.of(BASE_BUY_UP, ENHANCED_10_UNITS, VOLUNTARY_10_UNITS));
        planDefinitionTab.getAssetList().getAsset(PLAN).buttonCancel.click();

        LOGGER.info("REN-37914 Step#2 Add Basic Benefit And Navigate to Classification Management Tab");
        planDefinitionTab.fillTab(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataWithBasicBenefitsTab"));
        while (!GroupAccidentMasterPolicyContext.optionalBenefitTab.isTabSelected()) {
            buttonNext.click();
        }
        OptionalBenefitTab.tablePlans.getRow(COVERAGE_NAME.getName(), ENHANCED_ACCIDENT).getCell(7).controls.links.getFirst().click();
        buttonNext.click();
        fillInClassificationManagementTab(BASE_BUY_UP);

        LOGGER.info("REN-37914 Step#3 Change Quote Status to Premium Calculated");
        premiumSummaryTab.rate();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
        premiumSummaryTab.submitTab();

        LOGGER.info("REN-37914 Step#4 Check behavior for other 2 plans");
        checkQuoteForOtherPlans(VOLUNTARY_10_UNITS);
        checkQuoteForOtherPlans(ENHANCED_10_UNITS);

        LOGGER.info("REN-37914 Step#5 Propose And Issue Quote");
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(PROPOSED);
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(POLICY_ACTIVE);
    }

    private void checkQuoteForOtherPlans(String planValue) {
        TestData td;
        if (planValue.equals(ENHANCED_10_UNITS)) {
            td = getDefaultACMasterPolicyData();
        } else {
            List<TestData> testData = new ArrayList<>(Arrays.asList(
                    groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan").adjust(PLAN.getLabel(), planValue),
                    groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "PlanDefinitionTab_VOL10")));
            td = getDefaultACMasterPolicyData()
                    .adjust(planDefinitionTab.getMetaKey(), testData)
                    .adjust(enhancedBenefitsAtoCTab.getMetaKey(), groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "EnhancedBenefitsAtoCTab_VOL10"))
                    .adjust(enhancedBenefitsDtoFTab.getMetaKey(), groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "EnhancedBenefitsDtoFTab_VOL10"))
                    .adjust(enhancedBenefitsHtoLTab.getMetaKey(), groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "EnhancedBenefitsHtoLTab_VOL10"))
                    .adjust(enhancedBenefitsMtoTTab.getMetaKey(), groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "EnhancedBenefitsMtoTTab_VOL10"));
        }

        groupAccidentMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(PlanGenericInfoTab.PLAN_GENERIC_INFO.get());
        planDefinitionTab.fillTab(td);

        while (!GroupAccidentMasterPolicyContext.classificationManagementMPTab.isTabSelected()) {
            buttonNext.click();
        }

        fillInClassificationManagementTab(planValue);
        premiumSummaryTab.rate();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
        premiumSummaryTab.submitTab();
    }

    private void fillInClassificationManagementTab(String planValue) {
        classificationManagementTabList.getAsset(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP).setValueContains("Employment");
        classificationManagementTabList.getAsset(ClassificationManagementTabMetaData.PLAN).setValueContains(planValue);
        classificationManagementTabList.getAsset(ClassificationManagementTabMetaData.ADD_CLASSIFICATION_GROUP_TO_THE_PLAN).click();
        GroupAccidentMasterPolicyContext.classificationManagementMPTab.submitTab();
    }
}
