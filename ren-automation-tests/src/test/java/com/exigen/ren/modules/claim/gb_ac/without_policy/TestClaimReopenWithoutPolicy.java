/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ac.without_policy;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants.ClaimStatus;
import com.exigen.ren.main.modules.claim.common.metadata.ClaimResolutionActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimResolutionActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimReopenWithoutPolicy extends ClaimGroupBenefitsACBaseTest {

    private static final List<String> TYPE_OF_RESOLUTION_VALUES = ImmutableList.of("", "Closed", "Denied", "Withdrawn", "Incident Only", "Paid", "Partially Denied", "Contested");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-23859", "REN-12928"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimReopenWithoutPolicy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultGroupAccidentClaimWithoutPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);

        LOGGER.info("Close Claim #" + claimNumber);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.CLOSED);

        LOGGER.info("TEST: Reopen Claim to initial status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust("ClaimReopenActionTab|Reopen to (Status):", "Initial"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.INITIAL);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));

        LOGGER.info("TEST: Reopen Claim to notification status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust("ClaimReopenActionTab|Reopen to (Status):", "Notification"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.NOTIFICATION);

        LOGGER.info("Assertion for REN-12928");
        claim.claimClose().start();
        assertThat(new ClaimResolutionActionTab().getAssetList().getAsset(ClaimResolutionActionTabMetaData.TYPE_OF_RESOLUTION)).hasOptions(TYPE_OF_RESOLUTION_VALUES);
        claim.claimClose().getWorkspace().fill(tdClaim.getTestData("ClaimClose", "TestData"));
        claim.claimClose().submit();

        LOGGER.info("TEST: Reopen Claim to open status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust("ClaimReopenActionTab|Reopen to (Status):", "Open"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);

    }
}
