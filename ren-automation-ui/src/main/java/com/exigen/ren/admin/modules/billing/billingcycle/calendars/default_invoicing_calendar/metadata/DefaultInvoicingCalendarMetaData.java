/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class DefaultInvoicingCalendarMetaData extends MetaData {

    public static final AssetDescriptor<ComboBox> BILLING_CALENDAR = declare("Billing Calendar", ComboBox.class);
    public static final AssetDescriptor<TextBox> CALENDAR_NAME = declare("Calendar Name", TextBox.class);
    public static final AssetDescriptor<CheckBox> SELF_ADMINISTERED = declare("Self Administered", CheckBox.class,  By.xpath("//*[contains(@id,'groupType:0')]"));
    public static final AssetDescriptor<CheckBox> LIST_BILL = declare("List Bill", CheckBox.class,  By.xpath("//*[contains(@id,'groupType:1')]"));
    public static final AssetDescriptor<CheckBox> NON_PREMIUM = declare("Non Premium", CheckBox.class,  By.xpath("//*[contains(@id,'groupType:2')]"));
    public static final AssetDescriptor<AdvancedSelector> PRODUCTS = declare("Products", AdvancedSelector.class);
    public static final AssetDescriptor<DialogAssetList> GEOGRAPHY = declare("Geography", DialogAssetList.class, GeographyMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final AssetDescriptor<ComboBox> INVOICING_FREQUENCY = declare("Invoicing Frequency", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INVOICING_RULE = declare("Invoicing Rule", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BILLING_PERIOD_OFFSET = declare("Billing Period Offset", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INVOICE_DUE_DAY = declare("Invoice Due Day", ComboBox.class);
    public static final AssetDescriptor<TextBox> GENERATION_DATE_RULE= declare("Generation Date Rule", TextBox.class);

    public static class GeographyMetaData extends MetaData {
        public static final AssetDescriptor<Button> OPEN_POPUP = declare("Open Popup", Button.class, Waiters.AJAX, By.xpath(".//*[@id='defaultInvoicingCalendarForm:selectGeographyBtn']"));
        public static final AssetDescriptor<ListBox> COUNTRIES = declare("Countries", ListBox.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geography:countries']"));
        public static final AssetDescriptor<ListBox> STATES = declare("States", ListBox.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:states']"));
        public static final AssetDescriptor<Button> SUBMIT_POPUP = declare("Submit Popup", Button.class, Waiters.AJAX,false, By.xpath(".//*[@id='gForm_geography:okBtn_geography']"));
        public static final AssetDescriptor<Button> CLOSE_POPUP = declare("Close Popup", Button.class, Waiters.AJAX,false, By.xpath(".//*[@id='gForm_geography:cancelBtn_geography']"));
    }
}