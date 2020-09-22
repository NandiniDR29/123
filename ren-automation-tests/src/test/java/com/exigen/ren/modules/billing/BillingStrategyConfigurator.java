/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.ren.modules.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BillingStrategyConfigurator {
    DBService dbService = DBService.get();
    String SELECT_PAID_TO_DATE_STRATEGY = "select name from BillingStrategyConfiguration where name = 'paidToDate'";
    String SELECT_TRANSACTION_DATE_STRATEGY = "select name from BillingStrategyConfiguration where name = 'transactionDate'";
    String SET_PAID_TO_DATE_STRATEGY_QUERY = "update BillingStrategyConfiguration set name = 'paidToDate' where type like 'cancellationDateStrategy'";
    String SET_TRANSACTION_DATE_STRATEGY_QUERY = "update BillingStrategyConfiguration set name = 'transactionDate' where type like 'cancellationDateStrategy'";
    String SELECT_ACTIVE_BILLS_TEMPLATE = "select recordTo from BillingCycleTemplate where dType = 'BillingCycleBillsTemplate' and recordTo is null";
    String SET_BILLS_TEMPLATE_ACTIVE = "update BillingCycleTemplate set recordTo = null where dType = 'BillingCycleBillsTemplate'";
    String SELECT_SHIFTED_DATES_WITH_LIMIT_STRATEGY = "select name from BillingStrategyConfiguration where type = 'installmentDateStrategy' and name = 'shiftedDatesWithLimit'";
    String SET_SHIFTED_DATES_WITH_LIMIT_STRATEGY = "update BillingStrategyConfiguration set name = 'shiftedDatesWithLimit' where type = 'installmentDateStrategy'";
    String SELECT_SHIFTED_DATES_WITHOUT_LIMIT_STRATEGY = "select name from BillingStrategyConfiguration where type = 'installmentDateStrategy' and name = 'shiftedDatesWithoutLimit'";
    String SET_SHIFTED_DATES_WITHOUT_LIMIT_STRATEGY = "update BillingStrategyConfiguration set name = 'shiftedDatesWithoutLimit' where type = 'installmentDateStrategy'";
    String SELECT_INSTALLMENT_START_DATE_APPLIED = "select billingInstallmentStartDate_id from BillingConfig_billTypes where billingInstallmentStartDate_id <> null";
    String CLEAR_INSTALLMENT_START_DATE_APPLIED = "update BillingConfig_billTypes set billingInstallmentStartDate_id = null";
    String COUNT_INSTALLMENT_START_DATE_CONFIG = "select count(*) from BillingInstallmentStartDate";
    String REMOVE_INSTALLMENT_START_DATE_CONFIG = "delete from BillingInstallmentStartDate";
    Object LOCK = new Object();
    Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    default void setPaidToDateStrategy() {
        synchronized (LOCK) {
            dbService.getValue(SELECT_PAID_TO_DATE_STRATEGY).orElseGet(() -> {
                LOG.info("Set strategy to 'Paind to date'");
                dbService.executeUpdate(SET_PAID_TO_DATE_STRATEGY_QUERY);
                return null;
            });
        }
    }

    default void setTransactionDateStrategy() {
        synchronized (LOCK) {
            dbService.getValue(SELECT_TRANSACTION_DATE_STRATEGY).orElseGet(() -> {
                LOG.info("Set strategy to 'Transaction date'");
                dbService.executeUpdate(SET_TRANSACTION_DATE_STRATEGY_QUERY);
                return null;
            });
        }
    }

    default void setBillsTemplateActive() {
        synchronized (LOCK) {
            dbService.getValue(SELECT_ACTIVE_BILLS_TEMPLATE).orElseGet(() -> {
                dbService.executeUpdate(SET_BILLS_TEMPLATE_ACTIVE);
                return null;
            });
        }
    }

    default void setShiftedDatesWithLimitStrategy() {
        synchronized (LOCK) {
            dbService.getValue(SELECT_SHIFTED_DATES_WITH_LIMIT_STRATEGY).orElseGet(() -> {
                dbService.executeUpdate(SET_SHIFTED_DATES_WITH_LIMIT_STRATEGY);
                return null;
            });
        }
    }

    default void setShiftedDatesWithoutLimitStrategy() {
        synchronized (LOCK) {
            dbService.getValue(SELECT_SHIFTED_DATES_WITHOUT_LIMIT_STRATEGY).orElseGet(() -> {
                dbService.executeUpdate(SET_SHIFTED_DATES_WITHOUT_LIMIT_STRATEGY);
                return null;
            });
        }
    }

    default void clearInstallmentStartDateConfig(){
        synchronized (LOCK) {
            dbService.getValue(SELECT_INSTALLMENT_START_DATE_APPLIED).orElseGet(() -> {
                dbService.executeUpdate(CLEAR_INSTALLMENT_START_DATE_APPLIED);
                return null;
            });
            if(Integer.valueOf(dbService.getValue(COUNT_INSTALLMENT_START_DATE_CONFIG).get()) > 0) {
                dbService.executeUpdate(REMOVE_INSTALLMENT_START_DATE_CONFIG);
            }
        }
    }
}
