/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class AdjustPremiumPage extends SummaryPage {

    public static TableExtended<TableConstants.BillingAdjustPremiumGB> tableAdjustPremium = new TableExtended<>("Adjust Premium", By.xpath("//div[@id='invoicePremiumUpdateForm:rootInvoicePremiumAdjustment']//table"),
            By.xpath(".//*[@id='invoicePremiumUpdateForm:rootInvoicePremiumAdjustment_paginator_bottom']"));
    public static StaticElement labelAmountBilled = new StaticElement(By.id("invoicePremiumUpdateForm:rootInvoicePremiumAdjustment:totalAdjustedAmounts"));
    public static Button buttonCancelBackUp = new Button(By.id("invoicePremiumUpdateForm:cancel_footer"));
    public static Button buttonSubmitAdjustments = new Button(By.id("invoicePremiumUpdateForm:submitButton_footer"));
    public static Button buttonApplyMinimumAnnualCharge = new Button(By.id("invoicePremiumUpdateForm:applyMinimumAnnualChargeButton_footer"));

    public static void submitAdjustments(){
        buttonSubmitAdjustments.click();
        Page.dialogConfirmation.confirm();
    }

}
