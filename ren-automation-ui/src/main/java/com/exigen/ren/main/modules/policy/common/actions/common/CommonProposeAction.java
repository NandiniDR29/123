package com.exigen.ren.main.modules.policy.common.actions.common;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.google.common.collect.ImmutableMap;

import static com.exigen.ren.main.enums.CaseProfileConstants.CaseProfileTable.CASE_PROFILE_NAME;

public abstract class CommonProposeAction implements Action {
    public static final String SELECT_CASE_BY_NAME = "CaseName";

    protected Workspace workspace = new Workspace.Builder()
            .registerTab(QuotesSelectionActionTab.class)
            .registerTab(ProposalActionTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CASE.get());
        CaseProfileSummaryPage.tableSelectCaseProfile.getRow(1).getCell(CASE_PROFILE_NAME).controls.links.getFirst().click();
        NavigationPage.toSubTab(NavigationEnum.CaseProfileTab.PROPOSALS.get());
        CaseProfileSummaryPage.buttonAddNewProposal.click();
        return this;
    }

    public Action start(TestData td) {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CASE.get());
        if (td.getTestData(QuotesSelectionActionTab.class.getSimpleName()).containsKey(SELECT_CASE_BY_NAME)) {
            CaseProfileSummaryPage.tableSelectCaseProfile.getRow(ImmutableMap.of(CaseProfileSummaryPage.CaseProfilesTable.CASE_PROFILE_NAME.getName(),
                    td.getValue(QuotesSelectionActionTab.class.getSimpleName(), SELECT_CASE_BY_NAME))).getCell(CASE_PROFILE_NAME).controls.links.getFirst().click();
        } else {
            CaseProfileSummaryPage.tableSelectCaseProfile.getRow(1).getCell(CASE_PROFILE_NAME).controls.links.getFirst().click();
        }
        NavigationPage.toSubTab(NavigationEnum.CaseProfileTab.PROPOSALS.get());
        CaseProfileSummaryPage.buttonAddNewProposal.click();
        return this;
    }

    @Override
    public Action submit() {
        Tab.buttonOk.click();
        return this;
    }
}
