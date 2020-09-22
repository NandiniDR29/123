/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.billing.rules.payment_tolerance.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class PaymentToleranceMetaData extends MetaData {

    public static final AssetDescriptor<ComboBox> BROAD_LINE_OF_BUSINESS = declare("Broad Line of Business", ComboBox.class);
    public static final AssetDescriptor<ComboBox> EVENT = declare("Event", ComboBox.class);
    public static final AssetDescriptor<AdvancedSelector> PRODUCTS = declare("Products", AdvancedSelector.class);
    public static final AssetDescriptor<DialogAssetList> GEOGRAPHY = declare("Geography", DialogAssetList.class, GeographyMetaData.class, false, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<CheckBox> SELF_ADMINISTERED = declare("Self Administered", CheckBox.class, By.xpath("//input[contains(@id,'paymentToleranceForm:groupType') and @value='SELF_ADMINISTERED']"));
    public static final AssetDescriptor<CheckBox> LIST_BILL = declare("List Bill", CheckBox.class, By.xpath("//input[contains(@id,'paymentToleranceForm:groupType') and @value='LIST_BILL']"));
    public static final AssetDescriptor<CheckBox> FLAT = declare("Flat", CheckBox.class, By.xpath("//input[@value='FLAT']"));
    public static final AssetDescriptor<CheckBox> PERCENTAGE = declare("Percentage", CheckBox.class, By.xpath("//input[@value='PERCENTAGE']"));
    public static final AssetDescriptor<RadioGroup> TOLERANCE_BALANCE = declare("Tolerance Balance", RadioGroup.class);

    public static class GeographyMetaData extends MetaData {
        public static final AssetDescriptor<Button> OPEN_POPUP = declare("Open Popup", Button.class, Waiters.AJAX, By.xpath(".//*[@id='paymentToleranceForm:selectGeographyBtn']"));
        public static final AssetDescriptor<ListBox> COUNTRIES = declare("Countries", ListBox.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:countries']"));
        public static final AssetDescriptor<ListBox> STATES = declare("States", ListBox.class, Waiters.AJAX, false, By.xpath(".//*[@id='gForm_geographyPanel:states']"));
        public static final AssetDescriptor<Button> SUBMIT_POPUP = declare("Submit Popup", Button.class, Waiters.AJAX,false, By.xpath(".//*[@id='gForm_geographyPanel:okBtn_geographyPanel']"));
        public static final AssetDescriptor<Button> CLOSE_POPUP = declare("Close Popup", Button.class, Waiters.AJAX,false, By.xpath(".//*[@id='gForm_geographyPanel:cancelBtn_geographyPanel']"));
    }
}