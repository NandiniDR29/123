/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.rules.write_off.benefits.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.metadata.WriteOffBenefitsSearchMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class WriteOffBenefitsSearchTab extends DefaultTab {


    public static final Button buttonSearch = new Button(By.id("writeOffSearchForm:searchButton_footer"));

    public WriteOffBenefitsSearchTab()  {
        super(WriteOffBenefitsSearchMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonSearch.click();
        return this;
    }
}