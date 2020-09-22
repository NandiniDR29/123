package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimChangeDateOfLossActionTab;
import org.openqa.selenium.By;

public class ClaimChangeDateOfLossAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(ClaimChangeDateOfLossActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Claim Change Date of Loss";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        new Button(By.xpath("//input[@id='policyDataGatherForm:changeDateOfLossButton_footer']")).click();
        return this;
    }
}
