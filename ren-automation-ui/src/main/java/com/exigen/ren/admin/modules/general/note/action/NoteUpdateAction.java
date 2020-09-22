/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.note.action;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.general.note.pages.NotePage;
import com.exigen.ren.admin.modules.general.note.tabs.NoteCategoryTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;

public class NoteUpdateAction implements Action {
    @Override
    public String getName() {
        return "Update Note Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(NoteCreateAction.class).getWorkspace();
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    public Action start(String category) {
        NotePage.tableSearchResults.getRow(AdminConstants.AdminNoteSearchTable.NOTE_CATEGORY, category).getCell(AdminConstants.AdminSearchResultsTable.ACTION).controls.links.get("Edit").click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method without parameters is not supported for this action. Use perform(int rowNumber) instead.");
    }

    public Action perform(TestData td, String category) {
        start(category);
        getWorkspace().fill(td);
        submit();
        return this;
    }

    public Action submit() {
        NoteCategoryTab.buttonSave.click();
        return this;
    }
}
