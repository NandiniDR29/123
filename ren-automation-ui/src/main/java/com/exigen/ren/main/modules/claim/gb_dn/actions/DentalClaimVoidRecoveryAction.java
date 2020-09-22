package com.exigen.ren.main.modules.claim.gb_dn.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.actions.VoidRecoveryAction;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsTab;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;

public class DentalClaimVoidRecoveryAction extends VoidRecoveryAction{

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RecoveryDetailsActionTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start(int rowNumber) {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowNumber).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        RecoveryDetailsTab.buttonVoidRecovery.click();
        return this;
    }

    @Override
    public Action submit() {
        new Button(By.id("policyDataGatherForm:ok_ClaimsDentalDeclineRecoveryAction_footer")).click();
        return this;
    }
}