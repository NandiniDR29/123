/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.policy.groupbenefits;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.helpers.TableVerifier;
import com.exigen.ren.main.enums.PolicyConstants;

import static com.exigen.istf.verification.CustomAssertions.assertThat;


public class PolicySummaryHelper extends TableVerifier {

    Table table;
    String tableName;

    public PolicySummaryHelper(Table table, String tableName) {

        this.table = table;
        this.tableName = tableName;
    }

    @Override
    protected Table getTable() {
        return this.table;
    }

    @Override
    protected String getTableName() {
        return this.tableName;
    }

    public PolicySummaryHelper setParticipants(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.PARTICIPANTS, value);
        return this;
    }

    public PolicySummaryHelper setVolume(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.VOLUME, value);
        return this;
    }

    public PolicySummaryHelper setVolume(Currency value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.VOLUME, value.toString());
        return this;
    }

    public PolicySummaryHelper setAnnualPremium(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM, value);
        return this;
    }

    public PolicySummaryHelper setAnnualPremium(Currency value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM, value.toString());
        return this;
    }

    public PolicySummaryHelper setModalPremium(String value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM, value);
        return this;
    }

    public PolicySummaryHelper setModalPremium(Currency value) {
        setValue(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM, value.toString());
        return this;
    }

    public void verifyRow(int rowNumber) {
        values.forEach((key, value) -> assertThat(getTable().getRow(rowNumber).getCell(key))
                .as(String.format("Table '%s', Row '%s', Column '%s'", getTableName(), rowNumber, key)).hasValue(value));

    }
}
