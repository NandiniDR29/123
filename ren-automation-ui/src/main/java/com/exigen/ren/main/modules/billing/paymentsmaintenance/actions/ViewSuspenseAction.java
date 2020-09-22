/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.actions;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.ViewSuspenseActionTabMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.InquirySuspenseActionTab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.ViewSuspenseActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;

import static com.exigen.ren.main.modules.billing.paymentsmaintenance.pages.ViewSuspensePage.buttonSearchSuspense;

public class ViewSuspenseAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ViewSuspenseActionTab.class)
            .registerTab(InquirySuspenseActionTab.class)
            .build();

    @Override
    public String getName() {
        return "View Suspense";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        start(1);
        return this;
    }

    public Action start(int rowNum) {
        if (BillingSummaryPage.buttonPaymentsBillingMaintenance.isPresent()) {
            BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
        }
        PaymentsAndBillingMaintenancePage.tableSuspense.getRow(rowNum).getCell(PaymentsAndBillingMaintenancePage.SuspenseTable.SUSPENDED.getName()).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        start();
        getWorkspace().fill(td);
        submit();
        return this;
    }

    public Action perform(TestData td, String refNum) {
        perform(td.adjust(TestData.makeKeyPath(ViewSuspenseActionTab.class.getSimpleName(), ViewSuspenseActionTabMetaData.REFERENCE.getLabel()), refNum));
        return this;
    }

    public Action perform(TestData td, Currency amountFrom, Currency amountTo) {
        perform(td.adjust(TestData.makeKeyPath(ViewSuspenseActionTab.class.getSimpleName(), ViewSuspenseActionTabMetaData.PAYMENT_DESIGNATION_FROM.getLabel()), amountFrom.toString())
                .adjust(TestData.makeKeyPath(ViewSuspenseActionTab.class.getSimpleName(), ViewSuspenseActionTabMetaData.PAYMENT_DESIGNATION_TO.getLabel()), amountTo.toString()));
        return this;
    }

    public Action perform() {
        perform(new SimpleDataProvider());
        return this;
    }

    @Override
    public Action submit() {
        buttonSearchSuspense.click();
        return this;
    }

}
