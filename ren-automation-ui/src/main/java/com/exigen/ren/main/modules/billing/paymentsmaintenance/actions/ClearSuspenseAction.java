/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.ClearSuspenseActionTab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.SearchSuspenseActionTab;
import com.exigen.ren.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;

public class ClearSuspenseAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(SearchSuspenseActionTab.class)
            .registerTab(ClearSuspenseActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Clear Suspense";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        PaymentsAndBillingMaintenancePage.buttonClearSuspense.click();
        return this;
    }

    @Override
    public Action submit() {
        ClearSuspenseActionTab.buttonOk.click();
        return this;
    }

    public Action perform(TestData td, String policy, String suspenseReference, String paymentAmount) {
        td.adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.SearchSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.SearchSuspenseActionTab.SUSPENSE_REFERENCE.getLabel()), suspenseReference).adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.ClearSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.ClearSuspenseActionTab.ALLOCATION.getLabel(), PaymentsMaintenanceMetaData.ClearSuspenseActionTab.AllocationMultiSelector.POLICY_NUMBER.getLabel()), policy).adjust(TestData.makeKeyPath(PaymentsMaintenanceMetaData.ClearSuspenseActionTab.class.getSimpleName(), "Payment Amount"), paymentAmount);
        start();
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
    }

}
