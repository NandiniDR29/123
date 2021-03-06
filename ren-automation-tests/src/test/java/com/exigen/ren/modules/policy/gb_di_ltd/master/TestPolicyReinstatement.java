/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyReinstatement extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24480", component = POLICY_GROUPBENEFITS)
    public void testPolicyReinstatement() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterPolicy(longTermDisabilityMasterPolicy.getType());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Cancelling Policy #" + policyNumber);
        longTermDisabilityMasterPolicy.cancel().perform(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        LOGGER.info("TEST: Reinstate Policy #" + policyNumber);
        longTermDisabilityMasterPolicy.reinstate().perform(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.REINSTATEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }
}
