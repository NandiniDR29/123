/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.dblookups.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.general.dblookups.metadata.DBLookupsMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class DBLookupsTab extends DefaultTab {

    public static Button buttonUpdate = new Button(By.id("editValueForm:updateBtn_footer"));


    public DBLookupsTab() {
        super(DBLookupsMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
