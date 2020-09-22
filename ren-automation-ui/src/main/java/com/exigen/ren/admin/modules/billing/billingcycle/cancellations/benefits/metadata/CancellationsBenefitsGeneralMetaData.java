/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.billing.billingcycle.cancellations.benefits.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.controls.assets.CommunicationAndCancelNoticesAssetList;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class CancellationsBenefitsGeneralMetaData extends MetaData {

    public static final AssetDescriptor<AdvancedSelector> PRODUCTS = declare("Products", AdvancedSelector.class);
    public static final AssetDescriptor<DialogAssetList> GEOGRAPHY = declare("Geography", DialogAssetList.class, GeographyMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<CheckBox> SELF_ADMINISTERED = declare("Self Administered", CheckBox.class, By.xpath("//*[@id='cancelForm:groupTypes']//input[@value='SELF_ADMINISTERED']"));
    public static final AssetDescriptor<CheckBox> LIST_BILL = declare("List Bill", CheckBox.class, By.xpath("//*[@id='cancelForm:groupTypes']//input[@value='LIST_BILL']"));
    public static final AssetDescriptor<ComboBox> BILLING_CALENDAR = declare("Billing Calendar", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BASIS_FOR_COMMUNICATION_SCHEDULE_AND_CANCELLATION_TIMELINES = declare("Basis for communication schedule and cancellation timelines", ComboBox.class);
    public static final AssetDescriptor<TextBox> GRACE_PERIOD_X_DAYS_AFTER_INVOICE_DUE_DATE = declare("Grace period X days after invoice due date", TextBox.class);
    public static final AssetDescriptor<CommunicationAndCancelNoticesAssetList> COMMUNICATION_AND_CANCEL_NOTICES =
            declare("Communication and Cancel Notices", CommunicationAndCancelNoticesAssetList.class, CommunicationAndCancelNoticesMetaData.class);
    public static final AssetDescriptor<TextBox> CANCEL_X_DAYS_AFTER_INVOICE_DUE_DATE = declare("Cancel X days after grace period end date/invoice due date", TextBox.class);
    public static final AssetDescriptor<ComboBox> SET_CANCELLATION_REASON = declare("Set cancellation reason", ComboBox.class, By.xpath(".//*[@id='cancelForm:cancellationsList']"));
    public static final AssetDescriptor<ComboBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", ComboBox.class);

    public static class GeographyMetaData extends MetaData {
        public static final AssetDescriptor<Button> OPEN_POPUP = declare("Open Popup", Button.class, Waiters.AJAX, By.xpath(".//*[@id='cancelForm:selectGeographyBtn']"));
        public static final AssetDescriptor<ListBox> COUNTRIES = declare("Countries", ListBox.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:countries']"));
        public static final AssetDescriptor<ListBox> STATES = declare("States", ListBox.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:states']"));
        public static final AssetDescriptor<Button> SUBMIT_POPUP = declare("Submit Popup", Button.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:okBtn_geographyPanel']"));
        public static final AssetDescriptor<Button> CLOSE_POPUP = declare("Close Popup", Button.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:cancelBtn_geographyPanel']"));
    }

    public static final class CommunicationAndCancelNoticesMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> GENERATE_NOTICE_X_DAYS = declare("Generate notice X days after grace period end date/invoice due date", TextBox.class);
        public static final AssetDescriptor<ComboBox> NOTICE_TYPE = declare("Notice type", ComboBox.class);
    }
}