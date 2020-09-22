package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_NC;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimApplyRoundingMethordToBenefitAmount extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33615", component = CLAIMS_GROUPBENEFITS)
    public void testClaimApplyRoundingMethordToBenefitAmount() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName()+"[1]", BENEFIT_SCHEDULE.getLabel(), MINIMUM_MONTHLY_BENEFIT_AMOUNT.getLabel()), "$50"));

        LOGGER.info("TEST: Step 3");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "109.98"),
                policyInformationParticipantParticipantCoverageTab.getClass(), false);

        policyInformationParticipantParticipantCoverageTab.addCoverage(LTD_NC, "LTD Core - NC");

        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(BENEFIT_PERCENTAGE).setValue("60");

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(66).toString());
    }
}