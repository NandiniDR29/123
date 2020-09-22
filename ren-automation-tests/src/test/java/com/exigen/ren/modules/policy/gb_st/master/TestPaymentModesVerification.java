package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.APPLICABLE_PAYMENT_MODES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPaymentModesVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-33756", component = POLICY_GROUPBENEFITS)
    public void testPaymentModesVerification_ST() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks(), PlanDefinitionTab.class, true);

        LOGGER.info("Steps#2, 5 verification");
        setContributionValueAndFieldsCheck(ImmutableList.of("12"), "12");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-33756", component = POLICY_GROUPBENEFITS)
    public void testNonDefaultPaymentModesVerification_ST() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();

        LOGGER.info("Scenario#6 Step#2 verification");
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), APPLICABLE_PAYMENT_MODES.getLabel()), ImmutableList.of("1", "4")), statutoryDisabilityInsuranceMasterPolicy.getType());

        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks(), PlanDefinitionTab.class, true);
        setContributionValueAndFieldsCheck(ImmutableList.of(), EMPTY);
    }

    private void setContributionValueAndFieldsCheck(List memberPaymentsMode, String sponsorPaymentMode) {
        tableCoverageDefinition.getRows().forEach(row -> {
            row.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            assertSoftly(softly -> {

                LOGGER.info("Step#2 verification");
                planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Mandatory");
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE)).hasValue(memberPaymentsMode);

                LOGGER.info("Step#5 verification");
                planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Non-contributory");
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE)).isAbsent();
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PAYMENT_MODE)).hasValue(sponsorPaymentMode);
            });
        });
    }
}
