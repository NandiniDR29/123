/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.without_policy;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.REHABILITATION_INCENTIVE_BENEFIT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimRehabilitationIncentiveBenefitWithoutPolicyLTD extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-17460", component = CLAIMS_GROUPBENEFITS)
    public void testClaimRehabilitationIncentiveBenefitWithoutPolicyLTD() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        TestData td = disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER_WITHOUT_POLICY, TestDataKey.DEFAULT_TEST_DATA_KEY);
        initiateClaimWithoutPolicyAndFillToTab(td, policyInformationParticipantParticipantCoverageTab.getClass(), true);

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(REHABILITATION_INCENTIVE_BENEFIT))
                .hasOptions(ImmutableList.of("", "None",  "10% Incentive/Termination",  "20% Incentive/Termination",  "10% Incentive/20% Disincentive", "20% Incentive/20% Disincentive",
                        "5% Incentive/Termination - Disincentive", "5% Incentive/20% Disincentive",  "5%", "10%"));

        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(REHABILITATION_INCENTIVE_BENEFIT).setValue("10% Incentive/Termination");
        policyInformationParticipantParticipantCoverageTab.submitTab();

        disabilityClaim.getDefaultWorkspace().fillFromTo(td, policyInformationSponsorTab.getClass(),completeNotificationTab.getClass(), true);
        completeNotificationTab.submitTab();

        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }
}