package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ActionConstants.ProductAction.ISSUE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteOrPolicyLifecycle extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21450", "REN-21453", "REN-21454"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteOrPolicyLifecycle() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData(),
                PlanDefinitionTab.class, true);
        assertSoftly(softly -> {

            LOGGER.info("REN-21450 Step 5, REN-21454 Step 3");
            NavigationPage.PolicyNavigation.leftMenu(POLICY_INFORMATION.get(), Waiters.AJAX.then(Waiters.SLEEP(3000)));
            softly.assertThat(policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW)).isPresent().isDisabled().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(APPLICATION_SIGNED_DATE)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).isAbsent();

            LOGGER.info("REN-21450 Step 5");
            policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW)).isRequired().isEnabled();
            policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_YES);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(APPLICATION_SIGNED_DATE)).isPresent()
                    .isRequired().isEnabled()
                    .hasValue(TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(policyInformationTab.getAssetList().getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).
                    isPresent().isOptional().isEnabled().hasValue(EMPTY);
            policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(PLAN_DEFINITION.get());

            LOGGER.info("REN-21450 Step 5, REN-21453 Step 3");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NJ_STAT);
            NavigationPage.PolicyNavigation.leftMenu(POLICY_INFORMATION.get());
            softly.assertThat(policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW)).isAbsent();
            NavigationPage.PolicyNavigation.leftMenu(PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NY_STAT);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21454"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteOrPolicyLifecycle_2() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NY_STAT);
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("1");
        planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE).setValue(ImmutableList.of("12"));
        classificationManagementMpTab.navigateToTab();
        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo.getRows().forEach(row -> {
            row.getCell(8).controls.links.get(CHANGE).click();
            classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS).setValue("1");
        });
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.fillTab(getDefaultSTMasterPolicyData()).submitTab();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

            LOGGER.info("REN-21454 Step 8");
            softly.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ISSUE);
        });

        LOGGER.info("REN-21454 Step 10");
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_YES);
        assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(APPLICATION_SIGNED_DATE))
                    .isPresent().isEnabled().hasValue(TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(policyInformationTab.getAssetList().getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).isPresent().isEnabled().isOptional().hasValue(EMPTY);
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
        });

        LOGGER.info("REN-21454 Step 12");
        premiumSummaryTab.navigateToTab().submitTab();
        assertThat(NavigationPage.comboBoxListAction).containsOption(ISSUE);

        LOGGER.info("REN-21454 Step 20");
        statutoryDisabilityInsuranceMasterPolicy.propose().perform();
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().start();
        assertThat(policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW)).isPresent().isDisabled();
    }
}





