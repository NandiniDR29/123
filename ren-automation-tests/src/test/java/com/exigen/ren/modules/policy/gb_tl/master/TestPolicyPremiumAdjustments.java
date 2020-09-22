/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.ADD;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.COVERAGE_NAME;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyPremiumAdjustments extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24708", component = POLICY_GROUPBENEFITS)
    public void testPolicyPremiumAdjustments() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_AutoSubGroup"), termLifeInsuranceMasterPolicy.getType());
        assertThat(CaseProfileSummaryPage.tableFileIntakeManagement).hasMatchingRows(TableConstants.FileIntakeManagement.STATUS.getName(), "Active");

        termLifeInsuranceMasterPolicy.createQuote(termLifeInsuranceMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", "TestData_UseCensusFile"));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Issue Quote #" + policyNumber);
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicySelfAdminData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicySelfAdminData());
        termLifeInsuranceMasterPolicy.issue().perform(getDefaultTLMasterPolicySelfAdminData());
        Currency policyAnnualPremium = new Currency(PolicySummaryPage.tableCoverageSummary.getRow(COVERAGE_NAME, ADD).getCell(ANNUAL_PREMIUM).getValue());

        termLifeInsuranceMasterPolicy.premiumAdjustment().perform(termLifeInsuranceMasterPolicy.getDefaultTestData("PremiumAdjustment", TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        assertThat(new Currency(PolicySummaryPage.tableCoverageSummary.getRow(COVERAGE_NAME, ADD).getCell(ANNUAL_PREMIUM).getValue()).moreThan(policyAnnualPremium)).isTrue();
    }
}
