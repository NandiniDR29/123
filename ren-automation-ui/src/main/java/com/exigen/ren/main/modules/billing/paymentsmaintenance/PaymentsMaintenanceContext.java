/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.billing.paymentsmaintenance;

import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.AddPaymentBatchActionTab;

public interface PaymentsMaintenanceContext {
    PaymentsMaintenance paymentsMaintenance = AutomationContext.getService(PaymentsMaintenance.class);

    Tab addPaymentBatchTab = paymentsMaintenance.addPaymentBatch().getWorkspace().getTab(AddPaymentBatchActionTab.class);
}
