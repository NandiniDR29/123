/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.groupsinformation.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.cem.groupsinformation.pages.GroupInformationPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.CEMConstants;

public class GroupsInformationUpdateAction implements Action {
    @Override
    public String getName() {
        return "Group Information Create Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(GroupInformationCreateAction.class).getWorkspace();
    }

    public Action start(int rowNumber) {
        GroupInformationPage.tableGroupsInformation.getRow(rowNumber).getCell(CEMConstants.CEMGroupsInformationTable.ACTIONS).controls.buttons.get("Edit").click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
    }

    @Override
    public Action submit() {
        Tab.buttonSave.click();
        return this;
    }
}
