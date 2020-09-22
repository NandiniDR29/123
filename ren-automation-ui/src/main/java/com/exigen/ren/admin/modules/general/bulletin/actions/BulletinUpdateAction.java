/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.bulletin.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.general.bulletin.pages.BulletinPage;
import com.exigen.ren.admin.modules.general.bulletin.tabs.BulletinTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BulletinConstants;

public class BulletinUpdateAction implements Action {
    @Override
    public String getName() {
        return "Update Bulletin Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(BulletinCreateAction.class).getWorkspace();
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    public Action start(String bulletinName) {
        BulletinPage.tableSearchResults.getRow(BulletinConstants.BulletinDeailsTable.BULLETIN_NAME, bulletinName).getCell(BulletinPage.tableSearchResults.getColumnsCount())
                .controls.links.get(ActionConstants.CHANGE).click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method without parameters is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
    }

    public Action perform(String bulletinName, TestData td, boolean isSubmeted) {
        start(bulletinName);
        getWorkspace().fill(td);
        return submit(isSubmeted);
    }

    @Override
    public Action submit() {
        throw new UnsupportedOperationException("submit() method without parameters is not supported for this action. Use submit(boolean isSubmeted) instead.");
    }

    public Action submit(boolean isSubmitted) {
        if (isSubmitted) {
            BulletinTab.buttonSubmit.click();
        } else {
            BulletinTab.buttonSave.click();
        }
        Page.dialogConfirmation.confirm();
        return this;
    }

}
