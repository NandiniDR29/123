/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role.pages;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import com.exigen.ren.common.components.Dialog;
import org.openqa.selenium.By;

public class RolePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.xpath("//form[contains(@id, 'SearchForm')]"), RoleSearch.class);

    public static Button buttonAddNewRole = new Button(By.id("roleSearchForm:add-role"));
    public static Button buttonReturn = new Button(By.id("roleForm:cancel_footer"));

    public static Table tableSecurityRole = new Table(By.id("roleSearchForm:roleSearch_roleDomain"));
    public static Table tableRolesSearchResult = new Table(By.id("roleSearchForm:body_rolesSearchResult"));

    public static Dialog dialogRemoveRoleConfirmation = new Dialog("//div[@id='deleteRoleConfirmDialogDialog_container']");

    public static StaticElement labelItemNotFound = new StaticElement(By.xpath("//form[@id='roleSearchForm']"));
    public static StaticElement rolesPrivileges = new StaticElement(By.xpath("//*[@id='roleForm:privileges']"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public static final class RoleSearch extends MetaData {
        public static final AssetDescriptor<TextBox> ROLE_NAME = declare("Role Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> BUSINESS_DOMAIN = declare("Business Domain", ComboBox.class);
    }
}
