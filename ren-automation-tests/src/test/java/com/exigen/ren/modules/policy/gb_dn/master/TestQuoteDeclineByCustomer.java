/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.DeclineByCustomerActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.DeclineByCustomerActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteDeclineByCustomer extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24458", "REN-3563", "REN-12502"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteDeclineByCustomer() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());

        // Asserts for REN-12502/#3
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            softly.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.PROPOSE);
        });

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Propose Quote #" + policyNumber);
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("TEST: Decline by Company Quote #" + policyNumber);
        // Asserts for REN-3563/#2
        groupDentalMasterPolicy.declineByCustomerQuote().start();
        assertThat(groupDentalMasterPolicy.declineByCustomerQuote().getWorkspace().getTab(DeclineByCustomerActionTab.class).getAssetList().getAsset(DeclineByCustomerActionTabMetaData.DECLINE_REASON))
                .containsAllOptions(PolicyConstants.PolicyDeclineReason.DECLINE_BY_CUSTOMER_REASONS);

        groupDentalMasterPolicy.declineByCustomerQuote().getWorkspace().fill(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_CUSTOMER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupDentalMasterPolicy.declineByCustomerQuote().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
    }
}
