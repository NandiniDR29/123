/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyPremiumAdjustments extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24708", component = POLICY_GROUPBENEFITS)
    public void testPolicyPremiumAdjustments() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createQuote(groupAccidentMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", TestDataKey.DEFAULT_TEST_DATA_KEY));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Issue Quote #" + policyNumber);
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.issue().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        Currency policyAnnualPremium = new Currency(PolicySummaryPage.tableCoverageSummary.getRow(1).getCell(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM).getValue());

        groupAccidentMasterPolicy.premiumAdjustment().perform(groupAccidentMasterPolicy.getDefaultTestData("PremiumAdjustment", TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        assertThat(new Currency(PolicySummaryPage.tableCoverageSummary.getRow(1).getCell(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM).getValue()))
                .isEqualTo(policyAnnualPremium.multiply(2));
    }
}
