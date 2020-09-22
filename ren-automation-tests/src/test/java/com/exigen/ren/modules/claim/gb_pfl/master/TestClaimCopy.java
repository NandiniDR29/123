/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCopy extends ClaimGroupBenefitsPFLBaseTest {
    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23857", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCopy() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultPaidFamilyLeaveClaimForMasterPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST: Copy Claim #" + claimNumber);
        claim.claimCopy().perform(tdClaim.getTestData("ClaimCopy", "TestData"));

        claim.navigateToClaim();
        assertThat(ClaimSummaryPage.dialogConfirmation.isPresent()).isFalse();

        List<Integer> columns = IntStream.rangeClosed(2, 9).boxed().collect(Collectors.toList());
        assertThat(ClaimSummaryPage.tableListOfNonDentalClaims.getRow(1).getPartialValueByIndex(columns))
                .isEqualTo(ClaimSummaryPage.tableListOfNonDentalClaims.getRow(TableConstants.LifeAndDisabilityClaims.CLAIM_NUM.getName(), claimNumber)
                        .getPartialValueByIndex(columns));
    }
}
