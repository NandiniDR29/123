/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;


public class ManageInvoicingCalendarsActionTab extends ActionTab {
    public static Button buttonAddInvoicingCalendar = new Button(By.xpath("//input[@value='Add Invoicing Calendar' and not(contains(@style, 'none'))]"));
    public static TableExtended<InvoicingCalendars> tableInvoicingCalendars = new TableExtended<>("Invoicing Calendars", By.xpath("//*[@id='manageInvoiceCalendarsForm:allCalendars']//table"));

    public ManageInvoicingCalendarsActionTab() {
        super(ManageInvoicingCalendarsActionTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void addSection(int index, int size) {buttonAddInvoicingCalendar.click();}
            @Override
            protected void selectSection(int index) {}

            @Override
            public void fill(TestData td) {
                if (td.containsKey(name)) {
                    fillSection(td);
                }
            }

            private void fillSection(TestData td) {
                for (TestData block : td.getTestDataList(name)) {
                    List<TestData> list = new ArrayList<>();
                    list.add(block);
                    setValue(list);
                    Tab.buttonSave.click();
                }
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    public enum InvoicingCalendars implements Named {
        CALENDAR_NAME("Calendar Name"),
        GROUP_TYPE("Group Type"),
        PRODUCTS("Products"),
        GEOGRAPHY("Geography"),
        CALENDAR_STATUS("Calendar Status"),
        INVOICING_FREQUENCY("Invoicing Frequency"),
        INVOICING_RULE("Invoicing Rule"),
        ACTIONS("Actions");

        private String name;

        InvoicingCalendars(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
