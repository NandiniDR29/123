/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants.ClaimStatus;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCreationCertificate extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23851", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCreationCertificate() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        createDefaultLongTermDisabilityMasterPolicy();

        createDefaultLongTermDisabilityCertificatePolicy();

        createDefaultLongTermDisabilityClaimForCertificatePolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.INITIAL);

        LOGGER.info("TEST: Submit Claim #" + claimNumber);
        claim.claimSubmit().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.NOTIFICATION);

        LOGGER.info("TEST: Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);

        LOGGER.info("TEST: Close Claim #" + claimNumber);
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.CLOSED);
    }
}
