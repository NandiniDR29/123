package com.exigen.ren.main.modules.caseprofile.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.tabs.CreateEnrolledCensusFileIntakeManagementTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;

import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CASE;

public class CaseCreateEnrolledCensus implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(CreateEnrolledCensusFileIntakeManagementTab.class)
            .build();

    @Override
    public String getName() {
        return "Create Enrolled Census";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action perform(TestData testData) {
        start();
        getWorkspace().getTab(CreateEnrolledCensusFileIntakeManagementTab.class).fillTab(testData);
        close();
        return this;
    }

    @Override
    public Action start() {
        NavigationPage.toMainTab(CASE);
        CaseProfileSummaryPage.tableSelectCaseProfile.getColumn(CaseProfileSummaryPage.CaseProfilesTable.CASE_PROFILE_NAME.getName()).getCell(1).controls.links.getFirst().click();
        Action.super.start();
        return this;
    }

    public void close() {
        CreateEnrolledCensusFileIntakeManagementTab.exitCreateEnrolledCensus();
    }
}
