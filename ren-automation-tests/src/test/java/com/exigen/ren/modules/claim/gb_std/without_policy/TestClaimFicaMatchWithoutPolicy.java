/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.without_policy;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationSponsorTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimFicaMatchWithoutPolicy extends ClaimGroupBenefitsSTDBaseTest {

    private final static List<String> FICA_MATCH_VALUES = ImmutableList.of("", "None", "Reimbursement", "Embedded");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13550", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFicaMatch() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        LOGGER.info("REN-13550, Steps 1-3");
        TestData defaultTd = disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER_WITHOUT_POLICY,
                TestDataKey.DEFAULT_TEST_DATA_KEY);
        disabilityClaim.initiateWithoutPolicy(defaultTd);
        disabilityClaim.getDefaultWorkspace().fillUpTo(defaultTd, PolicyInformationParticipantParticipantCoverageTab.class, true);

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.FICA_MATCH)).hasOptions(FICA_MATCH_VALUES);
        policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.FICA_MATCH).setValueByIndex(2);

        policyInformationParticipantParticipantCoverageTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFromTo(defaultTd, PolicyInformationSponsorTab.class, CompleteNotificationTab.class, true);
        completeNotificationTab.submitTab();

        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }
}
