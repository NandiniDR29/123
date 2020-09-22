/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;

public class InquiryPaymentSeriesAction implements Action {

    @Override
    public String getName() {
        return "Inquiry Payment Series";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(CreatePaymentSeriesAction.class).getWorkspace();
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    public Action start(int rowNumber) {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PAYMENTS.get());
        ClaimSummaryPage.tableSummaryOfClaimPaymentSeries.getRow(rowNumber).getCell(ClaimConstants.ClaimSummaryOfPaymentSeriesTable.SERIES_NUMBER).controls.links.getFirst().click();
        new Button(COMMON_LINK_WITH_TEXT_LOCATOR.format(getName())).click();
        return this;
    }

}
