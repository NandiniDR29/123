/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteIssue extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24448", component = POLICY_GROUPBENEFITS)
    public void testQuoteIssue_1NJ() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        
        // create Stat NJ policy
        statutoryDisabilityInsuranceMasterPolicy.createQuote(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ").mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("TEST: Issue Stat NJ Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        statutoryDisabilityInsuranceMasterPolicy.issue().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        assertThat(PolicySummaryPage.buttonAddCertificatePolicy).isDisabled();

        LOGGER.info("------------------------------------------------------------------------");
        LOGGER.info("TEST: Stat NJ Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        LOGGER.info("------------------------------------------------------------------------");
    }


    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24448", component = POLICY_GROUPBENEFITS)
    public void testQuoteIssue_2NY() {
        mainApp().open();
        
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        
        // create Stat NY policy
        statutoryDisabilityInsuranceMasterPolicy.createQuote(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NY"));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("TEST: Issue Stat NY Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        statutoryDisabilityInsuranceMasterPolicy.issue().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        assertThat(PolicySummaryPage.buttonAddCertificatePolicy).isDisabled();
        
        LOGGER.info("------------------------------------------------------------------------");
        LOGGER.info("TEST: Stat NY Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        LOGGER.info("------------------------------------------------------------------------");
        
    }
}
