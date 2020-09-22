/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.catastrophe.action;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.general.catastrophe.pages.CatastrophePage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;

public class CatastropheUpdateAction implements Action {

    @Override
    public String getName() {
        return "Catastrophe Update Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(CatastropheCreateAction.class).getWorkspace();
    }

    public Action start(String elementName) {
        CatastrophePage.tableSearchResult
                .getRow(AdminConstants.AdminCatastropheSearchTable.CATASTROPHE_ID, elementName)
                .getCell(AdminConstants.AdminCatastropheSearchTable.ACTIONS).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Action submit() {
        Tab.buttonSave.click();
        return this;
    }

    public Action perform(String elementName, TestData td) {
        start(elementName);
        getWorkspace().fill(td);
        return submit();
    }
}
