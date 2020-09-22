package com.exigen.ren.main.modules.claim.gb_dn.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.actions.PostRecoveryAction;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import org.openqa.selenium.By;

public class DentalClaimPostRecoveryAction extends PostRecoveryAction {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RecoveryDetailsActionTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        new Button(By.id("productConsolidatedViewForm:postRecoveryClaimsDentalPaymentRoot")).click();
        return this;
    }

    @Override
    public Action submit() {
        new Button(By.id("policyDataGatherForm:postRecoveryBtn_ClaimsDentalRecoveryPostAction")).click();
        return this;
    }
}