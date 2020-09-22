package com.exigen.ren.main.modules.policy.common.actions.common;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.google.common.collect.ImmutableList;

public class PreProposalAction extends CommonProposeAction {
    @Override
    public String getName() {
        return "Pre-Proposal";
    }

    /**
     * Perform Pre-Proposal for first quote without any additional data
     */
    @Override
    public Action perform() {
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        start();
        QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
        QuotesSelectionActionTab.textBoxProposalName.setValue("ProposalName");
        Tab.buttonNext.click();
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGeneratePreProposal.click();
        Page.dialogConfirmation.confirm();
        MainPage.QuickSearch.search(policyNumber);
        return this;
    }
}
