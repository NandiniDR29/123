/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.AddPaymentBatchActionTabMetaData;

public class AllocationsActionTab extends ActionTab {

    public AllocationsActionTab() {
        super(AddPaymentBatchActionTabMetaData.AllocationSearchMetaData.class);
    }

}
