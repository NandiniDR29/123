/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.rules.payment_tolerance.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.billing.rules.payment_tolerance.metadata.PaymentToleranceMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;


public class PaymentToleranceTab extends DefaultTab {

    public static final Button buttonAddUpdate = new Button(By.id("paymentToleranceForm:addUpdateBtn_footer"));
    public static TableExtended<PaymentTolerance> tablePaymentTolerance = new TableExtended<>("Payment Tolerance", By.xpath("//table[@id='paymentToleranceResultForm:paymentTolerancesTable']"));


    public PaymentToleranceTab()
    {
        super(PaymentToleranceMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {}

            @Override
            protected void selectSection(int index) {}

            @Override
            protected void setSectionValue(int index, TestData value) {
                super.setSectionValue(index, value);
                buttonAddUpdate.click();
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() { return this;}

    public enum PaymentTolerance implements Named {
        EVENT("Event"),
        BROAD_LINE_OF_BUSINESS("Broad Line of Business"),
        PRODUCTS("Products"),
        GEOGRAPHY("Geography"),
        AMOUNT("Amount"),
        ACTIONS("Actions");

        private String name;

        PaymentTolerance(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}