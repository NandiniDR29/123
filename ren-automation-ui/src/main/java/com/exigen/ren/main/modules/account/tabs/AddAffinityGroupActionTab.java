/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.account.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.account.metadata.AddAffinityGroupActionTabMetaData;
import org.openqa.selenium.By;

public class AddAffinityGroupActionTab extends ActionTab {

    public static Button buttonAddAffinityGroup = new Button(By.xpath("//input[@value='Add Affinity Group']"));

    public AddAffinityGroupActionTab() {
        super(AddAffinityGroupActionTabMetaData.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        assetList.fill(td);
        buttonAddAffinityGroup.click();
        return this;
    }
}
