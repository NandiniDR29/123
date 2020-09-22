/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class ViewSuspensePage extends SummaryPage {
    public static TableExtended<SuspenseListTable> tableSuspense = new TableExtended<>("Suspense", By.xpath("//div[@id='suspenseListForm:suspensesList']//table"));
    public static Button buttonSearchSuspense = new Button(By.id("suspenseListForm:searchBtn"));

    public enum SuspenseListTable implements Named {
        REFERENCE("Reference #"),
        BILLING_ACCOUNTS("Billing Accounts"),
        AMOUNT("Amount");
        private String name;

        SuspenseListTable(String name) {this.name = name;}

        @Override
        public String getName() {
            return name;
        }
    }
}
