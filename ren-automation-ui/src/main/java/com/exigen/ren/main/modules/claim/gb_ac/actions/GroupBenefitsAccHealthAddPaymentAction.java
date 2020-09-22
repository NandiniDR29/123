/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ac.actions;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.actions.AddPaymentAction;
import org.openqa.selenium.By;

public class GroupBenefitsAccHealthAddPaymentAction extends AddPaymentAction {

    @Override
    public Action submit() {
        NavigationPage.toLeftMenuTab("Payment Allocation");
        new Button(By.id("policyDataGatherForm:paymentValidateBtn_ClaimsPaymentPostAction")).click();
        if (Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.confirm();
        }
        return this;
    }

}
