/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.tabs.OpenClaimActionTab;
import org.openqa.selenium.By;

public class ClaimOpenAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(OpenClaimActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Claim Open";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
    }

    public Action perform() {
        start();
        return submit();
    }

    @Override
    public Action submit() {
        new Button(By.xpath("//input[@value='Open Claim' and not(contains(@style, 'none'))]")).click();
        return this;
    }

}
