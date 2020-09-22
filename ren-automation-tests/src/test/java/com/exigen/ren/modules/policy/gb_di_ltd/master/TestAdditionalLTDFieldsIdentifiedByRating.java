package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OFFSETS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OffsetsMetaData.INCLUDE_PERS_AND_STRS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OffsetsMetaData.SOCIAL_SECURITY_INTEGRATION_METHOD;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.FIRST_TIME_BUYER;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.ZIP_CODE_MAIN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAdditionalLTDFieldsIdentifiedByRating extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18226", "REN-18273", "REN-18861", "REN-18874"}, component = POLICY_GROUPBENEFITS)
    public void testAdditionalLTDFieldsIdentifiedByRating() {

        LOGGER.info("REN-18226 TC1 Preconditions");
        mainApp().open();

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_TYPE.getLabel()), "Legal"));
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), policyInformationTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-18226 TC1 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(ZIP_CODE_MAIN)).hasValue("90210");
            LOGGER.info("REN-18226 TC1 Step 4,5");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(FIRST_TIME_BUYER)).isPresent().isRequired().hasValue(VALUE_YES);
            LOGGER.info("REN-18273 TC1 Preconditions");
            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultLTDMasterPolicyData(), policyInformationTab.getClass(), planDefinitionTab.getClass());
            planDefinitionTab.selectDefaultPlan();
            LOGGER.info("REN-18273 TC1 Step 1");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(TEST_DEFINITION)).isPresent().isRequired().hasValue("Loss of Duties and Earnings");
            LOGGER.info("REN-18273 TC1 Step 4");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(OWN_OCCUPATION_EARNINGS_TEST)).isPresent().isRequired().hasValue("80%");
            LOGGER.info("REN-18273 TC1 Step 6");
            softly.assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(ANY_OCCUPATION_EARNINGS_TEST)).isPresent().isRequired().hasValue("60%");
            LOGGER.info("REN-18861 TC1 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OFFSETS).getAsset(SOCIAL_SECURITY_INTEGRATION_METHOD)).isPresent().isRequired().hasValue("Family");
            LOGGER.info("REN-18861 TC1 Step 4,7");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OFFSETS).getAsset(INCLUDE_PERS_AND_STRS)).isPresent().isEnabled().isRequired().hasValue(VALUE_YES);
            LOGGER.info("REN-18874 TC1 Step 2");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(SELF_REPORTED_CONDITIONS_LIMITATION)).isPresent().hasValue("24 Months");
            LOGGER.info("REN-18874 TC1 Step 4-6");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(MENTAL_ILLNESS_EVENT)).isPresent().isEnabled().isRequired().hasValue("Lifetime");
            LOGGER.info("REN-18874 TC1 Step 9-11");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(SUBSTANCE_ABUSE_EVENT)).isPresent().isEnabled().isRequired().hasValue("Lifetime");
            LOGGER.info("REN-18874 TC1 Step 14-16");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(NON_VERIFIABLE_SYMPTOMS_EVENT)).isPresent().isEnabled().isRequired().hasValue("Lifetime");
            LOGGER.info("REN-18874 TC1 Step 19-21");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(SPECIAL_CONDITIONS_EVENT)).isPresent().isEnabled().isRequired().hasValue("Lifetime");
            LOGGER.info("REN-18874 TC1 Step 24,25");
            planDefinitionTab.getOptionsAsset().getAsset(INFECTIOUS_DISEASE).setValue("6 Months");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(INFECTIOUS_DISEASE_DURATION)).isPresent().isRequired().hasValue("5 years");
            LOGGER.info("REN-18874 TC1 Step 27-29");
            planDefinitionTab.getOptionsAsset().getAsset(RECOVERY_INCOME_BENEFIT).setValue("3 Months");
            softly.assertThat(planDefinitionTab.getOptionsAsset().getAsset(RECOVERY_INCOME_PROTECTION_MAX)).isPresent().isEnabled().isRequired().hasValue("Less than 60%");
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18226"}, component = POLICY_GROUPBENEFITS)
    public void testDefaultZipCodeTwoAddress() {

        LOGGER.info("REN-18226 TC2 Preconditions");
        mainApp().open();
        customerNonIndividual.create(tdSpecific().getTestData("TestData_TwoAddress"));
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());

        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), policyInformationTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-18226 TC1 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(ZIP_CODE_MAIN)).hasValue("90210-0806");
            LOGGER.info("REN-18226 TC1 Step 4,5");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(FIRST_TIME_BUYER)).isPresent().isRequired().hasValue(VALUE_YES);
        });
    }
}
