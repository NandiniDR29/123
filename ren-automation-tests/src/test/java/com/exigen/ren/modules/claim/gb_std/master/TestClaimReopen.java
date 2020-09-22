/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimReopen extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23859", component = CLAIMS_GROUPBENEFITS)
    public void testClaimReopen() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_STD);

        createDefaultShortTermDisabilityClaimForMasterPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("Close Claim #" + claimNumber);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.CLOSED);

        LOGGER.info("TEST: Reopen Claim to initial status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust("ClaimReopenActionTab|Reopen to (Status):", "Initial"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.INITIAL);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));

        LOGGER.info("TEST: Reopen Claim to notification status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust("ClaimReopenActionTab|Reopen to (Status):", "Notification"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));

        LOGGER.info("TEST: Reopen Claim to open status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust("ClaimReopenActionTab|Reopen to (Status):", "Open"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }
}
