/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_CANCELLED;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyReinstatement extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24480", component = POLICY_GROUPBENEFITS)
    public void testPolicyReinstatement() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterPolicy(statutoryDisabilityInsuranceMasterPolicy.getType());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Cancelling Policy #" + policyNumber);
        statutoryDisabilityInsuranceMasterPolicy.cancel().perform(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_CANCELLED);

        LOGGER.info("TEST: Reinstate Policy #" + policyNumber);
        statutoryDisabilityInsuranceMasterPolicy.reinstate().perform(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.REINSTATEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }
}
