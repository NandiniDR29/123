/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.par.tabs;

import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.security.SecurityDefaultTab;
import com.exigen.ren.admin.modules.security.par.metadata.PARSearchMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class PARSearchTab extends SecurityDefaultTab {
    public static Table tableSearchResults = new Table(By.id("roleSearchForm:body_searchResult"));
    public static final Table tableRoles = new Table(By.id("roleSearchForm:body_searchResult"));

    public PARSearchTab() {
        super(PARSearchMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
