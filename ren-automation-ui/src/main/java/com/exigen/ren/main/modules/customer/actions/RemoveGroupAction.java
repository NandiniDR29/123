/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;

public class RemoveGroupAction implements Action {
    @Override
    public String getName() {
        return "Update";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(DeleteCustomerAction.class).getWorkspace();
    }

    /**
     * Delete existing group. Indexing starts with 1.
     *
     * @param index number of group to be removed
     * @return
     */
    public Action perform(int index) {
        start();
        new Button(ByT.id("crmForm:removeGroupInfoBtn_%s").format(index - 1)).click();
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
    }

    @Override
    public Action submit() {
        RelationshipTab.buttonSaveAndExit.click();
        return this;
    }
}
