/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteCopy extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24460", component = POLICY_GROUPBENEFITS)
    public void testQuoteCopy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());

        paidFamilyLeaveMasterPolicy.initiate(getDefaultPFLMasterPolicyData());

        policyInformationTab.fillTab(getDefaultPFLMasterPolicyData());

        PolicyInformationTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        LOGGER.info("Initiated Quote #" + policyNumber);

        LOGGER.info("TEST: Copy From Quote #" + policyNumber);
        paidFamilyLeaveMasterPolicy.copyQuote().perform(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.COPY_FROM_QUOTE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();
        LOGGER.info("Copied Quote #" + policyNumberCopied);

        assertThat(policyNumber).isNotEqualTo(policyNumberCopied)
                .as("Copied policy %s number is the same as initial policy number %s",
                        policyNumberCopied, policyNumber);
    }
}
