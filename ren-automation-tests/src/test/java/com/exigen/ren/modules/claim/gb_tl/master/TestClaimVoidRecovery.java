/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVoidRecovery extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25745", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVoidRecovery() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultSelfAdminTermLifeInsuranceMasterPolicy();

        createDefaultTermLifeInsuranceClaimForMasterPolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_PremiumWaiver"));

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 2);

        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 2);

        LOGGER.info("TEST: Post Recovery for Claim #" + claimNumber);
        claim.postRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_PostRecovery"));

        LOGGER.info("TEST: Void Recovery for Claim #" + claimNumber);
        claim.voidRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidRecovery"), 1);

        LOGGER.info("TEST: Verify Recovery attributes on Payment Summary View for Claim #" + claimNumber);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS))
                .hasValue("Voided");
   }
}
