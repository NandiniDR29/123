/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.module.efolder.defaulttabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
import org.openqa.selenium.By;

public class AddFileTab extends DefaultTab {
    // TODO achykanakov: remove comboBoxType after AddFileAction implemented
    public static ComboBox comboBoxType = new ComboBox(By.id("addDocumentForm:type"));
    public static Button buttonOk = new Button(By.id("addDocumentForm:okBtn"));

    public AddFileTab() {
        super(AddFileTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
