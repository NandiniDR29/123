/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary.billing;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class PaymentsAndBillingMaintenancePage extends SummaryPage {
    public static TableExtended<SuspenseTable> tableSuspense = new TableExtended<>("Suspense", By.id("backOfficeGeneralForm:currentPeriodSuspenseSummary"));
    public static TableExtended<SuspenseTable> tablePaymentBatches = new TableExtended<>("Payment Batches", By.id("backOfficeGeneralForm:currentPeriodBatchPaymentSummary"));
    public static TableExtended<BatchPaymentList> tablePaymentBatchesList = new TableExtended<>("Payment Batches List", By.id("batchPaymentListForm:batchPaymentsList"));
    public static Table tableSuspensePayments = new Table(By.xpath("//div[@id='suspenseSearch:suspenseSearchResults']//table"));

    public static Button buttonAddPaymentBatch = new Button(By.id("backOfficeGeneralForm:addBatchPaymentBtn"));
    public static Button buttonAddBulkPayment = new Button(By.id("backOfficeGeneralForm:addBulkPaymentBtn"));
    public static Button buttonAddSuspense = new Button(By.id("backOfficeGeneralForm:addSuspenseBtn"));
    public static Button buttonClearSuspense = new Button(By.id("backOfficeGeneralForm:clearSuspenseBtn"));

    public enum SuspenseTable implements Named {
        COMPLETE("# Complete"),
        INCOMPLETE("# Incomplete"),
        SUSPENDED("# Suspended");
        private String name;

        SuspenseTable(String name) {this.name = name;}

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BatchPaymentList implements Named {
        REFERENCE("Reference #"),
        PAYMENT_TYPE("Payment Type"),
        CREATION_DATE("Creation Date"),
        PAYMENTS("# of Payments"),
        TOTAL_AMOUNT("Total Amount"),
        STATUS("Status"),
        ACTION("Action");
        private String name;

        BatchPaymentList(String name) {this.name = name;}

        @Override
        public String getName() {
            return name;
        }
    }


}
