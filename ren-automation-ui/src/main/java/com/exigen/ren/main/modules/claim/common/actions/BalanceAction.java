package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import org.openqa.selenium.By;

public class BalanceAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(BalanceActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Balance";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PAYMENTS.get());
        new Button(By.id("productConsolidatedViewForm:updateBalanceClaimsPaymentRoot")).click();
        return this;
    }
}
