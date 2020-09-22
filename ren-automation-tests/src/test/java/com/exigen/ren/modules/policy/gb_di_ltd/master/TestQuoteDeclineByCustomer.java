/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.DeclineByCustomerActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.DeclineByCustomerActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteDeclineByCustomer extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24458", "REN-5654", "REN-3563"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteDeclineByCustomer() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterQuote(longTermDisabilityMasterPolicy.getType());

        // Assert for REN-5654/#2
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.PROPOSE);

        LOGGER.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        longTermDisabilityMasterPolicy.propose().perform();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("TEST: Decline by Customer Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        // Assert for REN-3563/#2
        longTermDisabilityMasterPolicy.declineByCustomerQuote().start();
        assertThat(longTermDisabilityMasterPolicy.declineByCustomerQuote().getWorkspace().getTab(DeclineByCustomerActionTab.class).getAssetList().getAsset(DeclineByCustomerActionTabMetaData.DECLINE_REASON))
                .containsAllOptions(PolicyConstants.PolicyDeclineReason.DECLINE_BY_CUSTOMER_REASONS);

        longTermDisabilityMasterPolicy.declineByCustomerQuote().getWorkspace().fill(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_CUSTOMER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        longTermDisabilityMasterPolicy.declineByCustomerQuote().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
    }
}
