/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ac.master;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimSIUActions extends ClaimGroupBenefitsACBaseTest {
    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23856", component = CLAIMS_GROUPBENEFITS)
    public void testClaimSIUActions() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_AC);

        createDefaultGroupAccidentClaimForMasterPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST: SIU Potential on Claim #" + claimNumber);
        claim.flagFraudPotential().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SIU_POTENTIAL);

        LOGGER.info("TEST: SIU Review on Claim #" + claimNumber);
        claim.reviewFraud().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SIU_REVIEW);

        LOGGER.info("TEST: SIU Clear on Claim #" + claimNumber);
        claim.clearFraud().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.INITIAL);
    }
}
