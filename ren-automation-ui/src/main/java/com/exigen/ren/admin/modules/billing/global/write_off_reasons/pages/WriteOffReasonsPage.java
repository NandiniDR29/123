/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.admin.modules.billing.global.write_off_reasons.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class WriteOffReasonsPage extends AdminPage {

    public static TableExtended<WriteOffReasons> tableWriteOffReasonsPage = new TableExtended<>("Write-Off Reasons",  By.xpath("//*[@id='writeOffReasonsForm:writeOffReasonsTable']//table"));

    public enum WriteOffReasons implements Named {
        CODE("Code"),
        DESCRIPTION("Description"),
        CATEGORY("Category"),
        ACTIONS("Actions");

        private String name;

        WriteOffReasons(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}