/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentActionTabMetaData;
import org.openqa.selenium.By;

public class PaymentActionTab extends ActionTab {
    public PaymentActionTab() {
        super(PaymentActionTabMetaData.class);
    }

    public static final TableExtended<ClaimPaymentAllocationView> tableClaimPaymentAllocationView = new TableExtended<>("Claim Payment Allocation View",
            By.xpath("//div[@id='componentInstancesTable_ClaimsPaymentAllocationView']//table"));

    public static final TableExtended<ClaimPaymentView> tableClaimPaymentView = new TableExtended<>("Claim Payment View",
            By.xpath("//div[@id='policyDataGatherForm:transactionInstanceTable_ClaimsPaymentView']//table"));

    public static final TableExtended<PaymentBalanceHistory> tablePaymentBalanceHistory = new TableExtended<>("Payment Balance History",
            By.xpath("//div[@id='policyDataGatherForm:paymentBalanceHistoryTable_PrecClaimsPaymentBalanceHistory']//table"));


    public enum ClaimPaymentAllocationView implements Named {
        BENEFIT("Benefit"),
        RESERVE_TYPE("Reserve Type"),
        ALLOCATION_AMOUNT("Allocation Amount"),
        ALLOCATION_TYPE("Allocation Type"),
        FINAL_ALLOCATION("Final Allocation"),
        SUPPLEMENTAL_ALLOCATION("Supplemental Allocation"),
        NOTE("Note");

        private String name;

        ClaimPaymentAllocationView(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimPaymentView implements Named {
        PAYMENT_NUMBER("Payment Number"),
        GROSS_AMOUNT("Gross Amount"),
        PAYMENT_AMOUNT("Payment Amount"),
        ADJUSTED_GROSS_BENEFIT_AMOUNT("Adjusted Gross Benefit Amount"),
        OFFSET_AMOUNT("Offset Amount"),
        TAX_AMOUNT("Tax Amount");

        private String name;

        ClaimPaymentView(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PaymentBalanceHistory implements Named {
        DATE_OF_TRANSACTION("Date of Transaction"),
        BALANCE_AMOUNT("Balance Amount"),
        TRANSACTION("Transaction"),
        TRANSACTION_COMMENT("Transaction Comment");

        private String name;

        PaymentBalanceHistory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
