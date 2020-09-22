/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.billing.account;

import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.billing.account.tabs.ReverseWriteOffActionTab;

public interface BillingAccountContext {
    BillingAccount billingAccount = AutomationContext.getService(BillingAccount.class);
    ReverseWriteOffActionTab reverseWriteOffActionTab = billingAccount.getPaymentsAdjustmentsDefaultWorkspace().getTab(ReverseWriteOffActionTab.class);
}
