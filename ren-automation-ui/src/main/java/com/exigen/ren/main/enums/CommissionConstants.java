/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;

public final class CommissionConstants {

    private CommissionConstants() {}

    public static final class CommissionStrategyTable {
        public static final String COMMISSION_STRATEGY_FOR_PRODUCT = "Commission Strategy for Product";
        public static final String ACTION = "Action";
        public static final String GROUP_BENEFITS = "GB - Group Benefits";
        public static final String STATUS = "Status";
        public static final String COMMISSION_TYPE = "Commission Type";
        public static final String SALES_CHANNEL = "Sales Channel";
    }

    public static final class CommissionTemplateTable {
        public static final String ACTIONS = "Actions";
    }

    public static final class CommissionReferralTable {
        public static final String ACTION = "Action";
        public static final String DIRECT_SALES = "Direct Sales";
        public static final String SUBPRODUCER_ID = "Profile/Subproducer ID";
    }

    public static final class CommissionRulesTable {
        public static final String COMMISSION_RATE = "Commission Rate";
        public static final String SUBSEQUENT_TERMS = "Subsequent Terms";
    }

    public static final class CommissionMarketingFeeRulesTable {
        public static final String COMMISSION_RATE = "Commission rate";
        public static final String RECIPIENT_NAME = "Recipient Name";
    }

    public static final class CommissionRatesTable {
        public static final String RATE = "Rate";
    }

    public static final class CommissionBulkAdjustmentTable {
        public static final String ACTION = "Action";
    }

    public static final class CommissionBonusTable {
        public static final String ACTION = "Action";
    }

    public static final class CommissionGroupTable {
        public static final String ACTION = "Action";
    }

    public static final class CommissionStatus {
        public static final String ACTIVE = "Active";
        public static final String PENDING = "Pending";
        public static final String DRAFT = "Draft";
    }

    public static final class CommissionType {
        public static final String HEAP = "Heap";
        public static final String FLAT = "Flat";
        public static final String GRADED = "Graded";
    }

    public static final class CommissionDBColums {
        public static final String AMOUNT = "amount";
        public static final String DTYPE = "dType";
        public static final String TYPE = "type";
        public static final String CODE = "code";
        public static final String RATE_DB = "rate";
        public static final String COMMISSION_AMOUNT = "commissionAmount";
        public static final String BENEF_COMMISSION_PAYABLE_TX_ID = "benefCommissionPayableTXId";
    }

    public static final class CommissionDBValues {
        public static final String DIRECT = "direct";
        public static final String COMMISSION_EARNED_TX = "CommissionEarnedTX";
        public static final String COMMISSION_PAYABLE_TX = "CommissionPayableTX";
    }
}
