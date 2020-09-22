/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.actions.AcceptPaymentAction;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;

public class AcceptPaymentActionTab extends ActionTab {

    public static final RadioButton allocateExistingSuspense = new RadioButton(By.id("paymentForm:createNewPayment_radio:1"));
    public static final RadioGroup suspendRemaining = new RadioGroup(By.id("paymentForm:ren_suspendRemainingAmount_radio"));
    public static final Button buttonClear = new Button(By.xpath("//input[@id='paymentForm:clearButton_footer']"));
    public static final TextBox checkDate = new TextBox(By.id("paymentForm:chequeDateInputDate"));
    public static final Table tableAllocations = new Table(By.xpath("//div[@id='paymentForm:invoicesDistributionsTable']//table"));
    public static final Table tableAllocationsIntoBillingGroups = new Table(By.xpath("//div[@id='advAllocationForm:invoiceDistributionsTable_0']/div/table"));
    public static final Table tablePaymentAllocation = new Table(By.xpath("//div[@id='advAllocationForm:invoice_items_info_table']/div/table"));
    public static StaticElement allocateErrorMessage = new StaticElement(By.xpath("//span[@class='error_message']"));
    private static final String ALLOCATIONS_BILLING_GROUPS_WITH_POLICY_ID_LOCATOR = "(//td//a[contains(text(), '%s')]/ancestor::table)[last()]";
    public static final TableExtended<AcceptPaymentAction.PaymentMethods> tablePaymentMethods = new TableExtended<>("Payment Methods", By.xpath("//form[@id='paymentMethodListTable']/table//table"));

    public AcceptPaymentActionTab() {
        super(AcceptPaymentActionTabMetaData.class);
    }

    @Override
    public Tab fillTab(TestData td) {

        String allocationsLabel = AcceptPaymentActionTabMetaData.ALLOCATIONS.getLabel();
        TestData tabTestData = td.getTestData(getMetaKey());
        if (tabTestData.containsKey(allocationsLabel)) {

            td.mask(TestData.makeKeyPath(getMetaKey(), allocationsLabel));

            super.fillTab(td);

            int i = 0;
            for (String value : tabTestData.getList(allocationsLabel)) {
                new TextBox(By.xpath(String.format("//input[@id='paymentForm:amount_%d' or @id='paymentForm:policyAllocations:%d:amount_' " +
                        "or @id='paymentForm:invoicesDistributionsTable:%d:amount']", i, i, i))).setValue(value);
                i++;
            }
            if (checkDate.isPresent()) {
                checkDate.setValue(tabTestData.getValue(AcceptPaymentActionTabMetaData.CHECK_DATE.getLabel()));
            }
            if (suspendRemaining.isPresent() && suspendRemaining.isEnabled()) {
                suspendRemaining.setValue(VALUE_YES);
            }
            td.ksam(TestData.makeKeyPath(getMetaKey(), allocationsLabel));
        } else {
            super.fillTab(td);
        }

        return this;
    }

    public static TableExtended<AllocationsBilling> getAllocationsBillingGroupsTableForCurrentPolicy(String policyId) {
        return new TableExtended<>("Allocations", ByT.xpath(ALLOCATIONS_BILLING_GROUPS_WITH_POLICY_ID_LOCATOR).format(policyId));
    }

    public enum AllocationsBilling implements Named {
        PREVIOUS("Previous"),
        CURRENT("Current"),
        BILLING_GROUP("Billing Group"),
        COVERAGES("Coverages"),
        REMAINING_DUE("Remaining Due");
        private String name;

        AllocationsBilling(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}