/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.security.SecurityDefaultTab;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class GeneralRoleTab extends SecurityDefaultTab {
    public static Button buttonUpdate = new Button(By.xpath("(//input[(@value = 'Update' or @value = 'UPDATE') and not(@class = 'hidden') and not(contains(@style,'none'))])[last()]"));
    public static Button buttonReturn = new Button(By.xpath("//input[@value = 'Return' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static Button buttonAddNewRole = new Button(By.id("roleSearchForm:add-role"));
    public static Table tableRolesSearchResult = new Table(By.id("roleSearchForm:body_rolesSearchResult"));
    public static StaticElement rolesPrivileges = new StaticElement(By.xpath("//*[@id='roleForm:privileges']"));

    public static StaticElement labelAddedPriveleges = new StaticElement(By.id("roleForm:privileges"));
    public static StaticElement labelRoleNameInquiry = new StaticElement(By.id("roleForm:role_name"));

    public GeneralRoleTab() {
        super(GeneralRoleMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
