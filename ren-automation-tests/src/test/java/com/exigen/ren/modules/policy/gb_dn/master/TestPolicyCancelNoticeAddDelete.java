/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCancelNoticeAddDelete extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24531", component = POLICY_GROUPBENEFITS)
    public void testPolicyCancelNoticeAddDelete() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterPolicy(groupDentalMasterPolicy.getType());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Add Cancel Notice for Policy #" + policyNumber);
        groupDentalMasterPolicy.cancelNotice().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.CANCEL_NOTICE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();

        LOGGER.info("TEST: Delete Cancel Notice for Policy #" + policyNumber);
        groupDentalMasterPolicy.removeCancelNotice().perform(new SimpleDataProvider());
        assertThat(PolicySummaryPage.labelCancelNotice).isAbsent();
    }
}
