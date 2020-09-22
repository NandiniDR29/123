/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.OPEN;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.labelClaimStatus;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyCoveredEarningsField extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18533", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyCoveredEarningsField() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_LTD);

        disabilityClaim.initiate(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        disabilityClaim.getDefaultWorkspace().fillUpTo(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY),
                policyInformationParticipantParticipantInformationTab.getClass());

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).isPresent();

        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY),
                policyInformationParticipantParticipantInformationTab.getClass(), completeNotificationTab.getClass());
        completeNotificationTab.submitTab();

        disabilityClaim.claimOpen().perform();
        assertThat(labelClaimStatus).hasValue(OPEN);
    }
}
