/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.ClaimResolutionActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimReopenActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimResolutionActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimReopen extends ClaimGroupBenefitsLTDBaseTest {

    private static final List<String> TYPE_OF_RESOLUTION_VALUES = ImmutableList.of("", "Closed", "Denied", "Withdrawn", "Incident Only", "Paid", "Partially Denied", "Contested");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-23859", "REN-12928", "REN-30024"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimReopen() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_LTD);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultLongTermDisabilityClaimForMasterPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        assertSoftly(softly -> {
            LOGGER.info("REN-30024: Step 1");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Policy #:"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Master Policy #:"))).isPresent();
            LOGGER.info("REN-30024: Step 3");
            softly.assertThat(ClaimSummaryPage.labelClaimWithPolicy).isPresent().hasValue(policyNumber);
        });

        LOGGER.info("Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("Close Claim #" + claimNumber);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.CLOSED);

        LOGGER.info("TEST: Reopen Claim to initial status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust(
                TestData.makeKeyPath(ClaimReopenActionTab.class.getSimpleName(), "Reopen to (Status):"), "Initial"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.INITIAL);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));

        LOGGER.info("TEST: Reopen Claim to notification status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust(
                TestData.makeKeyPath(ClaimReopenActionTab.class.getSimpleName(), "Reopen to (Status):"), "Notification"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        LOGGER.info("Assertion for REN-12928");
        claim.claimClose().start();
        assertThat(new ClaimResolutionActionTab().getAssetList().getAsset(ClaimResolutionActionTabMetaData.TYPE_OF_RESOLUTION)).hasOptions(TYPE_OF_RESOLUTION_VALUES);
        claim.claimClose().getWorkspace().fill(tdClaim.getTestData("ClaimClose", "TestData"));
        claim.claimClose().submit();

        LOGGER.info("TEST: Reopen Claim to open status, claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData").adjust(
                TestData.makeKeyPath(ClaimReopenActionTab.class.getSimpleName(), "Reopen to (Status):"), "Open"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }
}
