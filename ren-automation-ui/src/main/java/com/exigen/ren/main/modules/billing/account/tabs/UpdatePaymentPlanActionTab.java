/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.metadata.UpdatePaymentPlanActionTabMetaData;
import org.openqa.selenium.By;

public class UpdatePaymentPlanActionTab extends ActionTab {
    public TableExtended<TableConstants.BillingNewInstallmentsSchedule> tableNewInstallmentsShedule =
            new TableExtended<>("New Installments Schedule", By.id("billingActionForm:billing_installments_info_table2"));
    public static StaticElement textBoxRemainingMinimumRequiredToCollect = new StaticElement(By.id("billingActionForm:downpaymentComponent_remainingBalanceValue"));
    public static Button buttonCancel = new Button(By.id("billingActionForm:cancelButton_footer"));

    public UpdatePaymentPlanActionTab() { super(UpdatePaymentPlanActionTabMetaData.class); }
}
