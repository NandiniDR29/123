/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.module.efolder.defaulttabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.module.efolder.metadata.ReindexFileTabMetaData;
import org.openqa.selenium.By;

public class ReindexFileTab extends Tab {
    public static Button buttonOk = new Button(By.id("reindexDocumentForm:okBtn"));

    public ReindexFileTab() {
        super(ReindexFileTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
