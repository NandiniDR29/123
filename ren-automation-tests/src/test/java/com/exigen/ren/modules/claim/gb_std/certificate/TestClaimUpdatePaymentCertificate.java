/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdatePaymentCertificate extends ClaimGroupBenefitsSTDBaseTest {

    private String reverseType = tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment").getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", "TestData").getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25742", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdatePayment() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_STD);

        createDefaultShortTermDisabilityClaimForCertificatePolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), 1);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 1);

        LOGGER.info("TEST: Post Final Payment for Claim #" + claimNumber);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        reverseAmount.toPlainString())
                .resolveLinks());

        Currency updatedPayment = reverseAmount.subtract(new Currency("2.0"));
        claim.updatePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        updatedPayment.toPlainString())
                .resolveLinks(), 1);

        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(updatedPayment.toString());

        claim.viewSingleBenefitCalculation().perform(1);

        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableBeneficiaryPayments).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableBeneficiaryPayments.getRow(1).getCell(ClaimConstants.ClaimBeneficiaryPaymentsTable.TRANSACTION_STATUS)).hasValue(BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED);
            softly.assertThat(ClaimSummaryPage.tableBeneficiaryPayments.getRow(1).getCell(ClaimConstants.ClaimBeneficiaryPaymentsTable.TOTAL_PAYMENT_AMOUNT)).hasValue(updatedPayment.toString());
            softly.assertThat(ClaimSummaryPage.tableSingleBenefitCalculationReserveHistory.getRow(ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.TRANSACTION, "Update Payment")).isPresent();
            softly.assertThat(ClaimSummaryPage.tableSingleBenefitCalculationReserveHistory.getRow(ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.TRANSACTION, "Update Payment").getCell(
                    ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.RESERVE_TYPE)).hasValue(reverseType);
            softly.assertThat(ClaimSummaryPage.tableSingleBenefitCalculationReserveHistory.getRow(ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.TRANSACTION, "Update Payment").getCell(
                    ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.OLD_RESERVE)).hasValue(new Currency(0).toString());
            softly.assertThat(ClaimSummaryPage.tableSingleBenefitCalculationReserveHistory.getRow(ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.TRANSACTION, "Update Payment").getCell(
                    ClaimConstants.ClaimSingleBenefitCalculationReserveHistoryTable.NEW_RESERVE)).hasValue(new Currency(2.0).toString());
        });
    }
}
