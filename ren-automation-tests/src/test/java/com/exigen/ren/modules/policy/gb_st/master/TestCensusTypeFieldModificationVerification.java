package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.COVERAGE_NAME;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.STATEWIDE_MAX_COVERED_PAYROLL;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES_WORKFLOW;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCensusTypeFieldModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25571", component = POLICY_GROUPBENEFITS)
    public void testCensusTypeFieldModificationVerification() {
        LOGGER.info("REN-25571 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), UNDER_FIFTY_LIVES.getLabel()), VALUE_NO), PlanDefinitionTab.class, false);

        LOGGER.info("Step#1 verification");
        planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE).click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(PFL_NY);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).isPresent().isDisabled().hasValue(NONE);

            LOGGER.info("Step#2, 3 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL)).isPresent().hasValue(new Currency("72860.84").toString()); // default value according to the specification
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Statewide Average Weekly Wage"))).isAbsent();

            LOGGER.info("Step#4 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetadata.RATE_BASIS)).isPresent().hasValue("Percent of Covered Payroll");
        });

        LOGGER.info("Step#5 verification");
        NavigationPage.PolicyNavigation.leftMenu(POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
        NavigationPage.PolicyNavigation.leftMenu(PLAN_DEFINITION.get());
        PlanDefinitionTab.changeCoverageTo(STAT_NY);
        censusTypeAndPayrollVerification();

        LOGGER.info("Step#6 verification");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(ENHANCED_NY);
        NavigationPage.PolicyNavigation.leftMenu(POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
        NavigationPage.PolicyNavigation.leftMenu(PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(STAT_NY);
        censusTypeAndPayrollVerification();

        LOGGER.info("Step#7 verification");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NJ_STAT);
        NavigationPage.PolicyNavigation.leftMenu(POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(VALUE_YES);
        NavigationPage.PolicyNavigation.leftMenu(PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(STAT_NY);
        censusTypeAndPayrollVerification();

        LOGGER.info("Step#8 verification");
        assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).isEnabled();

        LOGGER.info("Step#12 verification");
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NY_STAT);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(ENHANCED_NY);
        assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).hasOptions(ImmutableList.of("Enrolled", "Eligible", "None"));
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("30");
        planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE).setValue(ImmutableList.of("12"));

        LOGGER.info("Step#13 verification");
        PlanDefinitionTab.changeCoverageTo(PFL_NY);
        assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE)).isPresent().isDisabled().hasValue(NONE);
        planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("30");
        planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE).setValue(ImmutableList.of("12"));
        NavigationPage.PolicyNavigation.leftMenu(POLICY_INFORMATION.get());
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_NO);

        LOGGER.info("Step#14 verification");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
        ClassificationManagementTab.tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), PFL_NY).getCell(1).click();
        AbstractContainer<?, ?> classificationManagementAssetList = classificationManagementMpTab.getAssetList();
        classificationManagementAssetList.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementAssetList.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        classificationManagementAssetList.getAsset(UNI_TOBACCO).setValue(VALUE_YES);
        classificationManagementAssetList.getAsset(UNISEX).setValue(VALUE_NO);
        for (Row row : ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo.getRows()) {
            row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            classificationManagementAssetList.getAsset(NUMBER_OF_PARTICIPANTS).setValue("1");
            classificationManagementAssetList.getAsset(TOTAL_VOLUME).setValue("250");
        }
        ClassificationManagementTab.tablePlansAndCoverages.getRow(COVERAGE_NAME.getName(), ENHANCED_NY).getCell(1).click();
        classificationManagementAssetList.getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementAssetList.getAsset(CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        for (Row row : ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo.getRows()) {
            row.getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            classificationManagementAssetList.getAsset(NUMBER_OF_PARTICIPANTS).setValue("1");
            classificationManagementAssetList.getAsset(TOTAL_VOLUME).setValue("250");
        }
        Tab.buttonNext.click();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
    }

    private void censusTypeAndPayrollVerification() {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE).getValue()).isEqualTo("Enrolled");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(STATEWIDE_MAX_COVERED_PAYROLL)).isAbsent();
        });
    }
}