/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class CurrentPeriodPage extends SummaryPage {
    public static Button buttonBack = new Button(By.id("benefitsInvoiceViewForm:backButton_footer"));
    public static TableExtended<TableConstants.BillingCurrentPeriod> tableCurrentPeriod = new TableExtended<>("Current Period", By.xpath("//div[@id='benefitsInvoiceViewForm:currentPeriodTable']//table"));
    public static TableExtended<TableConstants.BillingCurrentPeriod> tablePriorPeriodCharges = new TableExtended<>("Prior Period Charges", By.xpath("//div[contains(@id, 'priorPeriodTable')]//table"));

}
