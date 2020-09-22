/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.scheme.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class ProductsMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> PRODUCT_ASSOCIATION_EFFECTIVE_DATE = declare("Product Association Effective Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class);
    public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, By.id("schemeManagementForm:addBtn"));
}
