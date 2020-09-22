/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.billing.paymentsmaintenance;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.actions.*;

public class PaymentsMaintenance implements IPaymentsMaintenance {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/billing/billingaccount/paymentsmaintenance");

    @Override
    public AddBulkPaymentAction addBulkPayment() {
        return AutomationContext.getAction(AddBulkPaymentAction.class);
    }

    @Override
    public AddSuspenseAction addSuspense() {
        return AutomationContext.getAction(AddSuspenseAction.class);
    }

    @Override
    public ViewSuspenseAction viewSuspense() {
        return AutomationContext.getAction(ViewSuspenseAction.class);
    }

    @Override
    public AddPaymentBatchAction addPaymentBatch() {
        return AutomationContext.getAction(AddPaymentBatchAction.class);
    }

    @Override
    public ClearSuspenseAction clearSuspense() {
        return AutomationContext.getAction(ClearSuspenseAction.class);
    }

    @Override
    public ClearSuspenseGBAction clearSuspenseGB() {
        return AutomationContext.getAction(ClearSuspenseGBAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }


}