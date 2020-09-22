/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.tabs.PartyResponsibilityActionTab;
import org.openqa.selenium.By;

public class SubrogationAddResponsiblePartyAction implements Action {
    public static Button buttonUpdate = new Button(By.xpath("//input[(@value = 'Update' or @value = 'UPDATEE') and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    private Workspace workspace = new Workspace.Builder()
            .registerTab(PartyResponsibilityActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Add Responsible Party";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        new Link(By.xpath("//a[text()='Add Responsible Party']")).click();
        return this;
    }

    @Override
    public Action submit() {
        if (buttonUpdate.isPresent()) {
            buttonUpdate.click();
        } else {
            Tab.buttonSaveAndExit.click();
        }
        return this;
    }
}
