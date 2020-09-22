package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimApplyRoundingMethordToBenefitAmount extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33615", component = CLAIMS_GROUPBENEFITS)
    public void testClaimApplyRoundingMethordToBenefitAmount() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_STD);

        LOGGER.info("TEST: Step 3");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "109.98"),
                policyInformationParticipantParticipantCoverageTab.getClass(), false);

        policyInformationParticipantParticipantCoverageTab.addCoverage(NC, "STD Core - NC");

        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(BENEFIT_PERCENTAGE).setValue("60");

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(66).toString());
    }
}