package com.exigen.ren.main.modules.claim.gb_dn.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PatientHistoryRecordActionTab;

import static com.exigen.ren.main.enums.ActionConstants.UPDATE;

public class UpdatePatientHistoryRecordAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(PatientHistoryRecordActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Patient History Record";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method is not supported for this action. Use start(Row row) instead or implement your own.");
    }

    public Action start(Row row) {
        row.getCell(23).controls.links.get(UPDATE).click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method is not supported for this action. Use perform(Row row, TestData td) instead or implement your own.");
    }

    public Action perform(Row row, TestData td) {
        start(row);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action submit() {
        PatientHistoryRecordActionTab.buttonUpdatePatientHistoryRecord.click();
        return this;
    }

}
