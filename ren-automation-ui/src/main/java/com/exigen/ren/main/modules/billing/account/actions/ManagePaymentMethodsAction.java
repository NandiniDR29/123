package com.exigen.ren.main.modules.billing.account.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.billing.account.tabs.PaymentMethodsActionTab;

public class ManagePaymentMethodsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(PaymentMethodsActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Payment Methods";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }


    public Action perform(TestData td) {
        start();
        getWorkspace().fill(td);
        return submit();
    }
}