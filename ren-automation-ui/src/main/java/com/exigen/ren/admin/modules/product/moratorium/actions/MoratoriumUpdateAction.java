/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.moratorium.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.product.moratorium.pages.MoratoriumPage;
import com.exigen.ren.admin.modules.product.moratorium.tabs.MoratoriumTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;

import static com.exigen.ren.main.enums.ActionConstants.ACTIONS;

public class MoratoriumUpdateAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(MoratoriumTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Moratorium Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action perform(TestData td, int rowIndex) {
        start(rowIndex);
        getWorkspace().fill(td);
        return submit();
    }

    public Action start(int rowIndex) {
        MoratoriumPage.tableSearchResult.getRow(rowIndex + 1).getCell(ACTIONS).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Action submit() {
        Tab.buttonSave.click();
        return this;
    }
}
