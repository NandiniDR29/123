/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.billing.groupbenefits;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.helpers.TableVerifier;
import com.exigen.ren.helpers.billing.BillingHelper;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;

import java.time.LocalDateTime;


public class BillingBillsAndStatementsGBVerifier extends TableVerifier {

    public BillingBillsAndStatementsGBVerifier setDueDate(LocalDateTime value) {
        setValue("Due Date", value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setStatus(String value) {
        setValue("Status", value);
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setCurrentDue(Currency value) {
        setValue("Current Due", value.toString());
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setCurrentDueZero() {
        setValue("Current Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setPriorDue(Currency value) {
        setValue("Prior Due", value.toString());
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setPriorDueZero() {
        setValue("Prior Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setUnallocatedInvoicePremium(Currency value) {
        setValue("Unallocated Invoice Premium", value.toString());
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setUnallocatedInvoicePremiumZero() {
        setValue("Unallocated Invoice Premium", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingBillsAndStatementsGBVerifier setTotal(Currency value) {
        setValue("Total Due", value.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tableBillsAndStatements;
    }

    @Override
    protected String getTableName() {
        return "Bills And Statements";
    }
}
