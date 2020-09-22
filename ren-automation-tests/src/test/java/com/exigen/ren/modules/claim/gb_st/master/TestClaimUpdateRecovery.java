/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.RecoveryRecoveryAllocationActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.RecoveryRecoveryAllocationActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateRecovery extends ClaimGroupBenefitsSTBaseTest {

    private Currency recoveryAmount = new Currency(tdClaim.getValue("TestClaimUpdateRecovery", "TestData_UpdateRecovery",
            RecoveryRecoveryAllocationActionTab.class.getSimpleName(),
            RecoveryRecoveryAllocationActionTabMetaData.ALLOCATION_AMOUNT.getLabel()));

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25744", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateRecovery() {

        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);

        createDefaultStatutoryDisabilityInsuranceClaimForMasterPolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 1);

        LOGGER.info("TEST: Post Recovery for Claim #" + claimNumber);
        claim.postRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_PostRecovery"));

        LOGGER.info("TEST: Update Recovery for Claim #" + claimNumber);
        claim.updateRecovery().perform(tdClaim.getTestData("TestClaimUpdateRecovery",
                "TestData_UpdateRecovery"), 1);

        LOGGER.info("TEST: Verify Recovery attributes on Payment Summary View for Claim #" + claimNumber);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS))
                .hasValue("Issued");
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_RECOVERY_AMOUNT))
                .hasValue(recoveryAmount.toString());
 }
}
