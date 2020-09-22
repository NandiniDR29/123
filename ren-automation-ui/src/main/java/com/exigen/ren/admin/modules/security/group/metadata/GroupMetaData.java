/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.group.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class GroupMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> BUSINESS_DOMAIN = declare("Business Domain", ComboBox.class);
    public static final AssetDescriptor<Button> ADD_GROUP = declare("Add Group", Button.class, By.id("groupSearchForm:add-group"));
    public static final AssetDescriptor<TextBox> GROUP_NAME = declare("Group Name", TextBox.class);
    public static final AssetDescriptor<AdvancedSelector> GROUP_ROLES = declare("Group Roles", AdvancedSelector.class);
    public static final AssetDescriptor<AdvancedSelector> GROUP_USERS = declare("Group Users", AdvancedSelector.class);
}
