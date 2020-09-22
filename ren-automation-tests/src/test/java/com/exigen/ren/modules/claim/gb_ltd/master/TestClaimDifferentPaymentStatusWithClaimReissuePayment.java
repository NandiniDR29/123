package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.GroupBenefitsClaimType;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.scenarios.master.ScenarioTestClaimDifferentPaymentStatusWithClaimReissuePayment;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimContext.disabilityClaim;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimDifferentPaymentStatusWithClaimReissuePayment extends ScenarioTestClaimDifferentPaymentStatusWithClaimReissuePayment {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33882", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityReissuePaymentWithoutPrivilege() {
        TestData tdClaim = disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), PREFERRED_PAYMENT_METHOD.getLabel()), "EFT")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), BANK_NAME.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), BANK_ACCOUNT_NUMBER.getLabel()), "$<rx:\\d{6}>")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), BANK_TRANSIT_ROUTING_NUMBER.getLabel()), "$<rx:\\d{9}>")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), BANK_ACCOUNT_TYPE.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), SUBJECT_TO_INTERNATIONAL_ACH_FORMATTING.getLabel()), "Yes")
                .resolveLinks();

        super.testClaimDifferentPaymentStatusWithClaimReissuePayment(
                GroupBenefitsMasterPolicyType.GB_DI_LTD,
                GroupBenefitsClaimType.CLAIM_DI_LTD,
                getDefaultLTDMasterPolicyData(),
                tdClaim,
                "TestData_LTD");
    }
}