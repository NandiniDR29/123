package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.caseprofile.CaseProfileBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.CON;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.APPLICABLE_PAYMENT_MODES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPaymentModesVerification extends CaseProfileBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33756"}, component = POLICY_GROUPBENEFITS)
    public void testPaymentModesVerification_STD() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData(), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(CON));

        LOGGER.info("Steps#2, 5 verification");
        setContributionValueAndFieldsCheck(ImmutableList.of("12"), "12");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33756"}, component = POLICY_GROUPBENEFITS)
    public void testNonDefaultPaymentModesVerification_STD() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();

        LOGGER.info("Scenario#6 Step#3 verification");
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), APPLICABLE_PAYMENT_MODES.getLabel()), ImmutableList.of("1", "4")), shortTermDisabilityMasterPolicy.getType());

        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData(), PlanDefinitionTab.class, false);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(CON));
        setContributionValueAndFieldsCheck(ImmutableList.of(), EMPTY);
    }

    private void setContributionValueAndFieldsCheck(List memberPaymentsMode, String sponsorPaymentMode) {
        assertSoftly(softly -> {
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Voluntary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE)).hasValue(memberPaymentsMode);

            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE)).hasValue(memberPaymentsMode);

            LOGGER.info("Step#5 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PAYMENT_MODE)).hasValue(sponsorPaymentMode);

            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PAYMENT_MODE)).hasValue(sponsorPaymentMode);
        });
    }
}

