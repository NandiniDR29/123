/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimPaymentsTab.CALCULATOR;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimPaymentsTab.PAYMENT_SERIES_PROFILE;

public class PaymentSeriesPaymentSeriesProfileActionTab extends ActionTab {
    public PaymentSeriesPaymentSeriesProfileActionTab() {
        super(PaymentSeriesPaymentSeriesProfileActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

    @Override
    public Tab navigateToTab() {
        NavigationPage.toTreeTab(getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return PAYMENT_SERIES_PROFILE.get();
    }
}
