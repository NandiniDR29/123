/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.module.efolder.defaulttabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.module.efolder.metadata.AddExtFileTabMetaData;
import org.openqa.selenium.By;

public class AddExtFileTab extends Tab {
    // TODO achykanakov: remove comboBoxType after AddExtFileAction implemented
    public static ComboBox comboBoxType = new ComboBox(By.id("addExtDocumentForm:type"));
    public static Button buttonOk = new Button(By.id("addExtDocumentForm:okBtn"));

    public AddExtFileTab() {
        super(AddExtFileTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
