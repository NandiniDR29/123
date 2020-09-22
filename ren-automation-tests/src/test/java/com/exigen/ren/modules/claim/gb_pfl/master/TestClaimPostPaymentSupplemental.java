/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPostPaymentSupplemental extends ClaimGroupBenefitsPFLBaseTest {

    private String reverseType = tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment").getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", "TestData").getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25749", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPostPaymentSupplemental() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);

        createDefaultPaidFamilyLeaveClaimForMasterPolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", TestDataKey.DEFAULT_TEST_DATA_KEY), 1);

        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 1);

        claim.changeSingleBenefitCalculationStatus().perform(tdClaim.getTestData("FeatureClose", "TestData"), 1);

        LOGGER.info("TEST: Post Supplemental Payment for Claim #" + claimNumber);

        Currency supplementalAmount = reverseAmount.subtract(new Currency("1.0"));

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_SupplementalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        supplementalAmount.toPlainString())
                .resolveLinks());

        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Pending");
                });

        claim.viewSingleBenefitCalculation().perform(1);

        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableBeneficiaryPayments).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableBeneficiaryPayments.getRow(1).getCell(ClaimConstants.ClaimBeneficiaryPaymentsTable.TRANSACTION_STATUS)).hasValue("Pending");
            softly.assertThat(ClaimSummaryPage.tableBeneficiaryPayments.getRow(1).getCell(ClaimConstants.ClaimBeneficiaryPaymentsTable.TOTAL_PAYMENT_AMOUNT)).hasValue(supplementalAmount.toString());
        });
    }
}
