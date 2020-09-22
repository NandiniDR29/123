/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class GeneralRoleMetaData extends MetaData {
    public static final AssetDescriptor<StaticElement> CHANNEL = declare("Channel", StaticElement.class);
    public static final AssetDescriptor<TextBox> ROLE_NAME = declare("Role Name", TextBox.class);
    public static final AssetDescriptor<TextBox> CATEGORY = declare("Category", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> PRIVILEGES = declare("Privileges", AdvancedSelector.class,
            By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='roleForm:select_privileges']]"));
    public static final AssetDescriptor<StaticElement> ROLES_PRIVILEGES = declare("Role's Privileges", StaticElement.class, By.id("roleForm:privileges"));
}
