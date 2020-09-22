/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.note;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.general.IGeneral;
import com.exigen.ren.admin.modules.general.note.action.NoteCreateAction;
import com.exigen.ren.admin.modules.general.note.action.NoteUpdateAction;
import com.exigen.ren.admin.modules.general.note.pages.NotePage;
import com.exigen.ren.admin.modules.general.note.tabs.NoteCategoryTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;

public class Note implements IGeneral {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/general/note");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(NoteCreateAction.class).getWorkspace();
    }

    @Override
    public void initiate() {
        navigate();
        NotePage.buttonAddNoteCategory.click();
    }

    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.GENERAL);
        NavigationPage.toLeftMenuTab(NavigationEnum.GeneralTabLeftMenu.NOTES);
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.NOTE);
        NoteCategoryTab.buttonSave.click();
        LOGGER.info("Created Note " + entity);
    }

    public void search(TestData td) {
        navigate();
        NotePage.search(td);
    }

    public void delete(int rowNumber) {
        NotePage.tableSearchResults.getRow(rowNumber).getCell(NotePage.tableSearchResults.getColumnsCount()).controls.links.get(ActionConstants.DELETE).click();
        Page.dialogConfirmation.confirm();
    }

    public void disable(int rowNumber) {
        NotePage.tableSearchResults.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        NotePage.buttonDisableCategory.click();
    }

    public NoteUpdateAction update() {
        return AutomationContext.getAction(NoteUpdateAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
