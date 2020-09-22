/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.AddBulkPaymentActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;

public class AddBulkPaymentAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(AddBulkPaymentActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Add Bulk Payment";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
        PaymentsAndBillingMaintenancePage.buttonAddBulkPayment.click();
        return this;
    }

    public Action perform(TestData td, String bulkAmount, String policy, String allocatedAmounts) {
        td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.BULK_AMOUNT.getLabel()), bulkAmount).adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATION.getLabel()), policy).adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATED_AMOUNT.getLabel()), allocatedAmounts);
        start();
        getWorkspace().fill(td);
        return submit();
    }

    public Action perform(TestData td, String bulkAmount, String allocatedAmounts) {
        td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.BULK_AMOUNT.getLabel()), bulkAmount).adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATED_AMOUNT.getLabel()), allocatedAmounts);
        start();
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String bulkAmount, String allocatedAmounts) instead.");
    }

    @Override
    public Action submit() {
        AddBulkPaymentActionTab.buttonOk.click();
        return this;
    }

}
