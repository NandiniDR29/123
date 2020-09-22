/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.global.billingholdmoratoriumreasons.pages;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class BillingHoldAndMoratoriumReasonsPage extends AdminPage {

    public static Button buttonAddUpdate = new Button(By.id("reasonAddEditForm:addUpdate_footer"));
    private static AssetList assetListCreateBillingHold = new AssetList(By.id("reasonAddEditForm"), BillingHoldAndMoratoriumReasonsTab.class);

    public static Table tableBillingHoldAndMoratoriumReasons = new Table(By.xpath("//table[@id='reasonAddEditForm:txSearchResult']"));


    public static class BillingHoldAndMoratoriumReasonsTab extends MetaData {
        public static AssetDescriptor<TextBox> BILLING_HOLD_REASON_CODE = declare("Billing Hold Reason Code", TextBox.class);
        public static AssetDescriptor<TextBox> BILLING_HOLD_REASON_DESCRIPTION = declare("Billing Hold Reason Description", TextBox.class);
        public static AssetDescriptor<ComboBox> REASON_CATEGORY = declare("Reason Category", ComboBox.class);
    }

    public static void createBillingHold(TestData td) {
        assetListCreateBillingHold.fill(td);
    }
}
