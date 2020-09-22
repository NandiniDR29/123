/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_tl.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.actions.AddPaymentAction;

import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;

public class TermLifeClaimAddPaymentAction extends AddPaymentAction {

    @Override
    public Action submit() {
        NavigationPage.toLeftMenuTab("Payment Allocation");
        buttonPostPayment.click();
        if (Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.confirm();
        }
        return this;
    }

}
