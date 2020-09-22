/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.RadioButton;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.AddBulkPaymentActionTabMetaData;
import org.openqa.selenium.By;

public class AddBulkPaymentActionTab extends ActionTab {

    public static RadioButton radioButtonAllacationAction = new RadioButton(By.id("bulkPaymentForm:allocation_Action_radio:0"));

    public AddBulkPaymentActionTab() {
        super(AddBulkPaymentActionTabMetaData.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.getTestData(getMetaKey()).containsKey(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATION.getLabel())) {
            radioButtonAllacationAction.setValue(true);
        }
        return super.fillTab(td);
    }
}
