/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyEndorsementFlat extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24523", component = POLICY_GROUPBENEFITS)
    public void testPolicyEndorsementFlat() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(groupVisionMasterPolicy.getType());

        createDefaultGroupVisionCertificatePolicy();

        LOGGER.info("TEST: Flat Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        groupVisionCertificatePolicy.endorse().perform(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupVisionCertificatePolicy.dataGather().perform(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement"));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupVisionCertificatePolicy.issue().perform(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE))
                .hasValue(ProductConstants.TransactionHistoryType.ENDORSEMENT);
    }
}
