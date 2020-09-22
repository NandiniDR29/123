/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.admin.modules.billing.rules.paymentplans.pages;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class PaymentPlansPage extends AdminPage {

    public static Table tablePaymentPlans = new Table(By.id("paymentPlansResultForm:paymentPlansSearchResult"));
    public static Button buttonAddUpdate = new Button(By.xpath("//input[@id='planAddEditForm:addUpdate_footer' or @id='planAddEditForm:addUpdateFake_footer']"));
    public static Button buttonSearch = new Button(By.id("planSearchForm:searchBtn_footer"));

}
