package com.exigen.ren.main.modules.claim.gb_dn.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.actions.ApprovePaymentAction;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;

public class DentalClaimApprovePaymentAction extends ApprovePaymentAction {

    public Action start(int rowNumber) {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowNumber).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        new Button(COMMON_LINK_WITH_TEXT_LOCATOR.format(getName())).click();
        return this;
    }

}
