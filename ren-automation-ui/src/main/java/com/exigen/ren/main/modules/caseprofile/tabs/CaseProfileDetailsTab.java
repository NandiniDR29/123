/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.caseprofile.tabs;

import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import org.openqa.selenium.By;

public class CaseProfileDetailsTab extends DefaultTab {

    public static Link linkNext = new Link(By.xpath("//a[.='Next']"));

    public CaseProfileDetailsTab() {
        super(CaseProfileDetailsTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        linkNext.click();
        return this;
    }
}
