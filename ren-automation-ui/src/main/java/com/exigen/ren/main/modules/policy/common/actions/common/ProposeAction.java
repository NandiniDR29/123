/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.actions.common;

import com.exigen.istf.data.TestData;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.google.common.collect.ImmutableList;

public class ProposeAction extends CommonProposeAction {
    @Override
    public String getName() {
        return "Propose";
    }

    /**
     * Perform Proposal for first quote without any additional data
     */
    @Override
    public Action perform() {
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        start();
        QuotesSelectionActionTab.selectQuoteByNumber(ImmutableList.of(policyNumber));
        QuotesSelectionActionTab.textBoxProposalName.setValue("ProposalName");
        Tab.buttonNext.click();
        getWorkspace().getTab(ProposalActionTab.class).submitTab();
        CustomAssertions.assertThat(CaseProfileSummaryPage.tableProposal).as("Proposal action was not finished").isPresent();
        MainPage.QuickSearch.search(policyNumber);
        return this;
    }

    /**
     * Perform Proposal with custom data
     */
    @Override
    public Action perform(TestData td) {
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        start(td);
        getWorkspace().fill(td);
        CustomAssertions.assertThat(CaseProfileSummaryPage.tableProposal).as("Proposal action was not finished").isPresent();
        MainPage.QuickSearch.search(policyNumber);
        return this;
    }
}
