/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyEndorsementDeclineByCustomer extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24490", component = POLICY_GROUPBENEFITS)
    public void testPolicyEndorsementDeclineByCustomer() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterPolicy(groupVisionMasterPolicy.getType());

        LOGGER.info("TEST: Decline By Customer Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        groupVisionMasterPolicy.endorse().perform(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();

        groupVisionMasterPolicy.declineByCustomerQuote().perform(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_CUSTOMER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();

        assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell(3)).hasValue(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
    }
}
