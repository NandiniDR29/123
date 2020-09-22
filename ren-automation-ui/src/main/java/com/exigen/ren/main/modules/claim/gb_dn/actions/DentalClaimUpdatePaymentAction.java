package com.exigen.ren.main.modules.claim.gb_dn.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.actions.UpdatePaymentAction;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.FinancialPaymentPaymentDetailsActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;

public class DentalClaimUpdatePaymentAction extends UpdatePaymentAction {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(FinancialPaymentPaymentDetailsActionTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start(int rowNumber) {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowNumber).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        ClaimPaymentsPage.buttonUpdatePayment.click();
        return this;
    }

    @Override
    public Action submit() {
        new Button(By.id("policyDataGatherForm:paymentValidateBtn_ClaimsPaymentUpdateAction")).click();
        return this;
    }
}
