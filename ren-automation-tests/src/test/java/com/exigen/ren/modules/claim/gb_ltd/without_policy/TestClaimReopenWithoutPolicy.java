/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.without_policy;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants.ClaimStatus;
import com.exigen.ren.main.modules.claim.common.metadata.ClaimResolutionActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimResolutionActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimReopenWithoutPolicy extends ClaimGroupBenefitsLTDBaseTest {

    private static final List<String> TYPE_OF_RESOLUTION_VALUES = ImmutableList.of("", "Closed", "Denied", "Withdrawn", "Incident Only", "Paid", "Partially Denied", "Contested");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-23859", "REN-12928"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimReopenWithoutPolicy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultLongTermDisabilityClaimWithoutPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);

        LOGGER.info("Close Claim #" + claimNumber);
        LOGGER.info("Assertion for REN-12928");
        claim.claimClose().start();
        assertThat(new ClaimResolutionActionTab().getAssetList().getAsset(ClaimResolutionActionTabMetaData.TYPE_OF_RESOLUTION)).hasOptions(TYPE_OF_RESOLUTION_VALUES);
        claim.claimClose().getWorkspace().fill(tdClaim.getTestData("ClaimClose", "TestData"));
        claim.claimClose().submit();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.CLOSED);

        LOGGER.info("TEST: Reopen Claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);
    }
}
