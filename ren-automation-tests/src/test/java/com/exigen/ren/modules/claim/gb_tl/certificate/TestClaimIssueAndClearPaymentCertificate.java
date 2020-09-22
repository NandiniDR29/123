/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimIssueAndClearPaymentCertificate extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25748", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIssueAndClearPayment() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_TL);

        createDefaultTermLifeInsuranceClaimForCertificatePolicy();
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_PremiumWaiver"));
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 2);

        LOGGER.info("TEST: Post Partial Payment for Claim #" + claimNumber);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment"));

        LOGGER.info("TEST: Issue Payment for Claim #" + claimNumber);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Issued");

        LOGGER.info("TEST: Clear Payment for Claim #" + claimNumber);
        claim.clearPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ClearPayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Cleared");
    }
}
