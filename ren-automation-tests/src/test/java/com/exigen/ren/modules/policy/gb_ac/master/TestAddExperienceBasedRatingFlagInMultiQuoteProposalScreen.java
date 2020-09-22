package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddExperienceBasedRatingFlagInMultiQuoteProposalScreen extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-19053", component = POLICY_GROUPBENEFITS)
    public void testAddExperienceBasedRatingFlagAC() {

        LOGGER.info("Test REN-19053 Step 1 and Step 2");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData());
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Test REN-19053 Step 3");
        groupAccidentMasterPolicy.propose().start();
        new QuotesSelectionActionTab().fillTab(getDefaultACMasterPolicyData()).submitTab();

        LOGGER.info("Test REN-19053 Step 4");
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber, "Enhanced 10 Units").getRow(1).getCell(4)).hasValue("Experience Based Rating - No");
    }
}
