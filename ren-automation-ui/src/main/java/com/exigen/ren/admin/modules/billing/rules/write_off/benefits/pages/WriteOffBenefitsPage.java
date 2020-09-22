/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.admin.modules.billing.rules.write_off.benefits.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class WriteOffBenefitsPage extends AdminPage {
    public static final Button buttonAddWriteOff = new Button(By.id("writeOffSearchForm:addButton_footer"));
    public static TableExtended<WriteOffTypes> tableWriteOffReasonsPage = new TableExtended<>("Write-Off Types",  By.xpath("//*[@id='writeOffSearchForm:writeOffSearchResult']//table"));

    public enum WriteOffTypes implements Named {
        DESCRIPTION("Description"),
        WRITE_OFF_TYPE("Write-Off Type"),
        EFFECTIVE("Effective"),
        EXPIRATION("Expiration"),
        ACTIONS("Actions");

        private String name;

        WriteOffTypes(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
