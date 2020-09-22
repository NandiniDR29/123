/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.DATA_GATHERING;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteCopy extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24460", component = POLICY_GROUPBENEFITS)
    public void testQuoteCopy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        PolicyInformationTab policyInformationtab = new PolicyInformationTab();
        policyInformationtab.fillTab(getDefaultLTDMasterPolicyData());
        PolicyInformationTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        LOGGER.info("Initiated Quote #" + policyNumber);

        LOGGER.info("TEST: Copy From Quote #" + policyNumber);
        longTermDisabilityMasterPolicy.copyQuote().perform(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.COPY_FROM_QUOTE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();
        LOGGER.info("Copied Quote #" + policyNumberCopied);

        assertThat(policyNumber).isNotEqualTo(policyNumberCopied).as("Copied quote number %s is the same as initial %s", policyNumberCopied, policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(DATA_GATHERING);
    }
}
