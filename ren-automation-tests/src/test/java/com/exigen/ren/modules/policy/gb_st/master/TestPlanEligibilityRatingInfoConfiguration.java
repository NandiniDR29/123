package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanEligibilityRatingInfoConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private TestData tdGatherTestData;
    private static String VALUE_FOUR = "4";
    private static String VALUE_ZERO = "0";
    private static String VALUE_TWENTY = "20";
    private static String VALUE_TWENTY_ZERO = "20.00";


    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16397", "REN-16398", "REN-16399", "REN-16543"}, component = POLICY_GROUPBENEFITS)
    public void testPlanEligibilityRatingInfoConfiguration() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        tdGatherTestData = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ").mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()));
        statutoryDisabilityInsuranceMasterPolicy.initiate(tdGatherTestData);
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(tdGatherTestData, PlanDefinitionTab.class);
        assertSoftly(softly -> {
            LOGGER.info("Test REN-16397 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isDisabled().hasValue(VALUE_TWENTY_ZERO).isRequired();
            LOGGER.info("Test REN-16397 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD)).isDisabled().hasValue(VALUE_TWENTY).isRequired();
            LOGGER.info("Test REN-16397 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isDisabled().hasValue("Weeks").isRequired();
            LOGGER.info("Test REN-16397 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CONTINUOUS_PERIOD_WEEKS)).isAbsent();
            LOGGER.info("Test REN-16397 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS)).hasValue("Percent of Taxable Wage");
            LOGGER.info("Test REN-16398 Precondition select Plan as NY Stat");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NY_STAT);
            LOGGER.info("Test REN-16398 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isDisabled().hasValue(VALUE_TWENTY_ZERO).isRequired();
            LOGGER.info("Test REN-16398 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD)).isDisabled().hasValue(VALUE_FOUR).isRequired();
            LOGGER.info("Test REN-16398 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isDisabled().hasValue("Weeks").isRequired();
            LOGGER.info("Test REN-16398 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CONTINUOUS_PERIOD_WEEKS)).isAbsent();
            LOGGER.info("Test REN-16398 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS)).hasValue(PER_EMPLOYEE_PER_MONTH);
            LOGGER.info("Test REN-16399 Step 1 set coverage as Enhanced NY");
            planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE).click();
            PlanDefinitionTab.changeCoverageTo(STAT_NY);
            planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(ENHANCED_NY);
            LOGGER.info("Test REN-16399 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isDisabled().hasValue(new Currency("0").toPlainString()).isRequired();
            LOGGER.info("Test REN-16399 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD)).isDisabled().hasValue(VALUE_FOUR).isRequired();
            LOGGER.info("Test REN-16399 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isDisabled().hasValue("Weeks").isRequired();
            LOGGER.info("Test REN-16399 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CONTINUOUS_PERIOD_WEEKS)).isAbsent();
            LOGGER.info("Test REN-16399 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS)).hasValue(PER_EMPLOYEE_PER_MONTH);
            LOGGER.info("Test REN-16543 Step 1 set coverage as PFL NY");
            planDefinitionTab.getAssetList().getAsset(ADD_COVERAGE).click();
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NY_STAT);
            planDefinitionTab.getAssetList().getAsset(COVERAGE_NAME).setValue(PFL_NY);
            LOGGER.info("Test REN-16543 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isDisabled().hasValue(VALUE_TWENTY_ZERO).isRequired();
            LOGGER.info("Test REN-16543 Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD)).isAbsent();
            LOGGER.info("Test REN-16543 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isAbsent();
            LOGGER.info("Test REN-16543 Step 5,6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CONTINUOUS_PERIOD_WEEKS)).hasValue("26").isDisabled();
            LOGGER.info("Test REN-16543 Step 7");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS)).hasValue("Percent of Covered Payroll");
        });
    }
}
