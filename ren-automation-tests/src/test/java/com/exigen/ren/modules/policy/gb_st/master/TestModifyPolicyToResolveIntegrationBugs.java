package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.ENHANCED_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestModifyPolicyToResolveIntegrationBugs extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private final String PMNTH = "Per Employee Per Month";
    private final String P100MP = "Per $100 Monthly Covered Payroll";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37683", component = POLICY_GROUPBENEFITS)
    public void testStatAttributesPlanConfigurationChanges1() {
        createCustomerMpAndCheckRateBasisValue(VALUE_YES, PMNTH);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37683", component = POLICY_GROUPBENEFITS)
    public void testStatAttributesPlanConfigurationChanges2() {
        createCustomerMpAndCheckRateBasisValue(VALUE_NO, PMNTH, P100MP);
    }

    private void createCustomerMpAndCheckRateBasisValue(String underFiftyLives, String... values) {
        LOGGER.info("TC#2 Precondition");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), UNDER_FIFTY_LIVES.getLabel()), underFiftyLives), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(STAT_NY);
        planDefinitionTab.addCoverage(NY_STAT, ENHANCED_NY);
        LOGGER.info("Verify attribute Rate Basis in Rate section and its parameters for each plan");
        assertSoftly(softly -> {
            PlanDefinitionTab.changeCoverageTo(STAT_NY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS)).hasOptions(values);
            PlanDefinitionTab.changeCoverageTo(ENHANCED_NY);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS)).hasOptions(values);
        });
    }
}
