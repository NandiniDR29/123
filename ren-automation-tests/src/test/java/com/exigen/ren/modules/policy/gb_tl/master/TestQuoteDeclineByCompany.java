/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.DeclineByCustomerActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.DeclineByCompanyActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteDeclineByCompany extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24457", "REN-3563"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteDeclineByCompany() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterQuote(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        termLifeInsuranceMasterPolicy.propose().perform();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("TEST: Decline by Company Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        // Asserts for REN-3563/#3
        termLifeInsuranceMasterPolicy.declineByCompanyQuote().start();
        assertThat(termLifeInsuranceMasterPolicy.declineByCompanyQuote().getWorkspace().getTab(DeclineByCompanyActionTab.class).getAssetList().getAsset(DeclineByCustomerActionTabMetaData.DECLINE_REASON))
                .containsAllOptions(PolicyConstants.PolicyDeclineReason.DECLINE_BY_COMPANY_REASONS);

        termLifeInsuranceMasterPolicy.declineByCompanyQuote().getWorkspace().fill(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DECLINE_BY_COMPANY, TestDataKey.DEFAULT_TEST_DATA_KEY));
        termLifeInsuranceMasterPolicy.declineByCompanyQuote().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.COMPANY_DECLINED);
    }
}
