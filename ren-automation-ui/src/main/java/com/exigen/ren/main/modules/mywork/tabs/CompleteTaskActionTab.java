/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.mywork.tabs;

import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.mywork.metadata.CompleteTaskActionTabMetaData;
import org.openqa.selenium.By;

public class CompleteTaskActionTab extends ActionTab {

    public static Button buttonComplete = new Button(By.id("taskCompleteForm:submitComplete_footer"));
    public static Button buttonCancel = new Button(By.id("taskCompleteForm:navigateBack_footer"));
    public static Button buttonAddService = new Button(By.xpath("//button[@id='taskCompleteForm:addService']"));
    public static Button buttonAddInvoice = new Button(By.xpath("//button[@id='taskCompleteForm:addInvoice']"));
    public static final ByT SUBMITTED_SERVICES_SECTION_LOCATOR_TEMPLATE = ByT.xpath(".//tbody[contains(@id, 'services_data')]/tr[@data-ri='%s']");
    public static final ByT INVOICE_SECTION_LOCATOR_TEMPLATE = ByT.xpath("//div[@id='taskCompleteForm:invoices:%s:invoicePanel']");

    public CompleteTaskActionTab() {
        super(CompleteTaskActionTabMetaData.class);
    }

    public static class SubmittedServicesRepeatAssetList extends RepeatAssetList {

        public SubmittedServicesRepeatAssetList(ByT locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(SUBMITTED_SERVICES_SECTION_LOCATOR_TEMPLATE, metaDataClass);
        }

        public SubmittedServicesRepeatAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(SUBMITTED_SERVICES_SECTION_LOCATOR_TEMPLATE, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            buttonAddService.click(Waiters.AJAX);
        }
    }

    public final static class InvoiceRepeatAssetList extends RepeatAssetList {

        public InvoiceRepeatAssetList(ByT locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(locatorTemplate, metaDataClass);
        }

        public InvoiceRepeatAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(INVOICE_SECTION_LOCATOR_TEMPLATE, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            if(index > 0) {
                buttonAddInvoice.click(Waiters.AJAX);
            }
        }
    }
}
