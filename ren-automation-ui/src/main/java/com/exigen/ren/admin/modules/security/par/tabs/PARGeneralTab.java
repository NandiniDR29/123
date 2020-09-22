/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.par.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.security.SecurityDefaultTab;
import com.exigen.ren.admin.modules.security.par.metadata.PARGeneralMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class PARGeneralTab extends SecurityDefaultTab {
    public static Button buttonUpdate = new Button(By.id("roleForm:update_footer"));
    public static Button buttonAddPAR = new Button(By.id("roleSearchForm:add-role"));

    public PARGeneralTab() {
        super(PARGeneralMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
