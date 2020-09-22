/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.ClaimResolutionActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimResolutionActionTab;
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

public class TestClaimReopenCertificate extends ClaimGroupBenefitsLTDBaseTest {

    private static final List<String> TYPE_OF_RESOLUTION_VALUES = ImmutableList.of("", "Closed", "Denied", "Withdrawn", "Incident Only", "Paid", "Partially Denied", "Contested");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-23859", "REN-12928", "REN-30024"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimReopenCertificate() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        createDefaultLongTermDisabilityMasterPolicy();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultLongTermDisabilityCertificatePolicy();
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultLongTermDisabilityClaimForCertificatePolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        assertSoftly(softly -> {
            LOGGER.info("REN-30024: Step 1");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Master Policy #:"))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Policy #:"))).isPresent();

            LOGGER.info("REN-30024: Step 2");
            softly.assertThat(ClaimSummaryPage.labelClaimWithPolicy).isPresent().hasValue(certificatePolicyNumber);

            LOGGER.info("REN-30024: Step 3");
            softly.assertThat(ClaimSummaryPage.labelClaimWithCertificatePolicy).isPresent().hasValue(masterPolicyNumber);
        });

        LOGGER.info("Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("Close Claim #" + claimNumber);
        LOGGER.info("Assertion for REN-12928");
        claim.claimClose().start();
        assertThat(new ClaimResolutionActionTab().getAssetList().getAsset(ClaimResolutionActionTabMetaData.TYPE_OF_RESOLUTION)).hasOptions(TYPE_OF_RESOLUTION_VALUES);
        claim.claimClose().getWorkspace().fill(tdClaim.getTestData("ClaimClose", "TestData"));
        claim.claimClose().submit();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.CLOSED);

        LOGGER.info("TEST: Reopen Claim #" + claimNumber);
        claim.claimReopen().perform(tdClaim.getTestData("ClaimReopen", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }
}
