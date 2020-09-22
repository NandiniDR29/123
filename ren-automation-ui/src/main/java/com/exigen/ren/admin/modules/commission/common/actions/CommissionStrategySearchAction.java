/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.common.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.commission.common.tabs.CommissionSearchTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import org.openqa.selenium.By;

public class CommissionStrategySearchAction implements Action {
    public static Button buttonSearch = new Button(By.xpath("//*[contains(@id, 'searchBtn_footer')]"));
    private Workspace workspace = new Workspace.Builder()
            .registerTab(CommissionSearchTab.class)
            .build();

    @Override
    public String getName() {
        return "Search Commission Strategy";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        return this;
    }

    @Override
    public Action submit() {
        buttonSearch.click();
        return this;
    }
}
