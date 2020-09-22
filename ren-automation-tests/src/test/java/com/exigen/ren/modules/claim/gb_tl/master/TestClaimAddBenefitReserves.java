/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitReservesActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddBenefitReserves extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25738", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddBenefitReserves() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultSelfAdminTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceClaimForMasterPolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_Death_OtherValues"));

        LOGGER.info("Test: Add New Benefit Reserves for Benefit to Claim #" + claimNumber);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 2);

        assertThat(ClaimAdjudicationBenefitPage.tableBenefitReserves).hasRows(3);
        Currency indemnityReserve = new Currency(tdClaim.getTestData("BenefitReserves", "TestData")
                .getValue(BenefitReservesActionTab.class.getSimpleName(), BenefitReservesActionTabMetaData.INDEMNITY_RESERVE.getLabel()));
        Currency expenseReserve = new Currency(tdClaim.getTestData("BenefitReserves", "TestData")
                .getValue(BenefitReservesActionTab.class.getSimpleName(), BenefitReservesActionTabMetaData.EXPENSE_RESERVE.getLabel()));
        Currency recoveryReserve = new Currency(tdClaim.getTestData("BenefitReserves", "TestData")
                .getValue(BenefitReservesActionTab.class.getSimpleName(), BenefitReservesActionTabMetaData.RECOVERY_RESERVE.getLabel()));

        verifyBenefitReservesTable(indemnityReserve, expenseReserve, recoveryReserve);
    }

    private void verifyBenefitReservesTable(Currency indemnityReserve, Currency expenseReserve, Currency recoveryReserve) {
        Map<Object, String> row1 = new HashMap<>();
        row1.put(ClaimConstants.ClaimBenefitReservesTable.RESERVE, "Indemnity");
        row1.put(ClaimConstants.ClaimBenefitReservesTable.UNALLOCATED_AMOUNT, indemnityReserve.toString());
        row1.put(ClaimConstants.ClaimBenefitReservesTable.ALLOCATED_AMOUNT, "$0.00");
        row1.put(ClaimConstants.ClaimBenefitReservesTable.TOTAL_AMOUNT, indemnityReserve.toString());
        assertThat(ClaimAdjudicationBenefitPage.tableBenefitReserves.getRow(1)).hasCells(row1);

        Map<Object, String> row2 = new HashMap<>();
        row2.put(ClaimConstants.ClaimBenefitReservesTable.RESERVE, "Expense");
        row2.put(ClaimConstants.ClaimBenefitReservesTable.UNALLOCATED_AMOUNT, expenseReserve.toString());
        row2.put(ClaimConstants.ClaimBenefitReservesTable.ALLOCATED_AMOUNT, "$0.00");
        row2.put(ClaimConstants.ClaimBenefitReservesTable.TOTAL_AMOUNT, expenseReserve.toString());
        assertThat(ClaimAdjudicationBenefitPage.tableBenefitReserves.getRow(2)).hasCells(row2);

        Map<Object, String> row3 = new HashMap<>();
        row3.put(ClaimConstants.ClaimBenefitReservesTable.RESERVE, "Recovery");
        row3.put(ClaimConstants.ClaimBenefitReservesTable.UNALLOCATED_AMOUNT, recoveryReserve.toString());
        row3.put(ClaimConstants.ClaimBenefitReservesTable.ALLOCATED_AMOUNT, "$0.00");
        row3.put(ClaimConstants.ClaimBenefitReservesTable.TOTAL_AMOUNT, recoveryReserve.toString());
        assertThat(ClaimAdjudicationBenefitPage.tableBenefitReserves.getRow(3)).hasCells(row3);
    }
}
