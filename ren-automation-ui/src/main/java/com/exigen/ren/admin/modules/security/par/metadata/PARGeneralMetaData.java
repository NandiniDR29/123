/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.par.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class PARGeneralMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> ROLE_CODE = declare("Role Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ROLE_NAME = declare("Role Name", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> PRODUCTS = declare("Products", AdvancedSelector.class, By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='roleForm:select_products']]"));
    public static final AssetDescriptor<AdvancedSelector> PRIVILEGES = declare("Product Specific Privileges", AdvancedSelector.class,
            By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='roleForm:select_privileges']]"));
}
