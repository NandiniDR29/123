/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVoidPayment extends ClaimGroupBenefitsSTDBaseTest {

    private TestData paymentData = tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment");
    private String reverseType = paymentData.getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", "TestData").getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25743", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVoidPayment() {

        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_STD);

        createDefaultShortTermDisabilityClaimForMasterPolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();


        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), 1);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 1);

        LOGGER.info("TEST: Post Partial Payment for Claim #" + claimNumber);
        Currency paymentAmount = reverseAmount.subtract(new Currency("1.0"));
        claim.addPayment().perform(paymentData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks());

        LOGGER.info("TEST: Create Void Payment for Claim #" + claimNumber);
        claim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Voided");
    }
}
