/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.metadata.DefaultInvoicingCalendarMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class DefaultInvoicingCalendarTab extends DefaultTab {

    public static final Button buttonAdd = new Button(By.id("defaultInvoicingTemplatesForm:addNewTemplate_footer"));
    public static final TableExtended<DefaultInvoicingCalendar> tableCalendars = new TableExtended<>("Calendars table", By.xpath("//*[@id='defaultInvoicingTemplatesForm:invoiceTable']//table"));

    public DefaultInvoicingCalendarTab()
    {
        super(DefaultInvoicingCalendarMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                buttonAdd.click();
            }

            @Override
            protected void selectSection(int index) {
            }


            @Override
            protected void setSectionValue(int index, TestData value) {
                super.setSectionValue(index, value);
                buttonSave.click();
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() { return this;}

    public enum DefaultInvoicingCalendar implements Named {
        NAME("Name"),
        BILLING_GROUP_TYPE("Billing Group Type"),
        PRODUCTS("Products"),
        GEOGRAPHY("Geography"),
        ACTIONS("Actions");

        private String name;

        DefaultInvoicingCalendar(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}