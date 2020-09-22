/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.AddPaymentBatchActionTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLQualifyingEventTab;
import org.openqa.selenium.By;

public class AddPaymentBatchActionTab extends ActionTab {
    public static Button buttonStartAllocations = new Button(By.id("paymentBatchForm:startAllocationsBtn_footer"));
    public static StaticElement numberOfPayments = new StaticElement(By.id("paymentBatchForm:generalPaymentBatch_itemCount"));
    public static StaticElement totalAmount = new StaticElement(By.id("paymentBatchForm:generalPaymentBatch_totalAmount"));
    public static StaticElement paymentDesignation = new StaticElement(By.id("paymentBatchForm:paymentItem_paymentDesignation"));
    public static StaticElement billingAccount = new StaticElement(By.id("batchPaymentItemForm:billingAccountSelection"));
    public static final TableExtended<PaymentListColumns> tablePaymentList = new TableExtended<>("Payment List", By.xpath("//div[@id='paymentBatchForm:batchPaymentsList']/div/table"));

    public AddPaymentBatchActionTab() {
        super(AddPaymentBatchActionTabMetaData.class);
    }

    public static class PaymentList extends RepeatAssetList {
        public PaymentList(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//tbody[@id='paymentBatchForm:batchPaymentsList_data']/tr[contains(@class,'ui-widget-content')][%s]"), metaDataClass);
        }

        public PaymentList(BaseElement<?, ?> parent, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//tbody[@id='paymentBatchForm:batchPaymentsList_data']/tr[contains(@class,'ui-widget-content')][%s]"), metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) { }

        protected void setSectionValue(int sectionIndex, TestData value) {
            super.setSectionValue(++sectionIndex, value);
        }
    }

    public enum PaymentListColumns implements Named {
        AMOUNT("Amount"),
        PAYOR("Payor"),
        CHECK_NUMBER("Check Number"),
        CHECK_DATE("Check Date"),
        ACTION("Action");

        private String name;

        PaymentListColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
