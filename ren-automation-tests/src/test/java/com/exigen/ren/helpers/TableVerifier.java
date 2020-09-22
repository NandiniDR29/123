/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.istf.webdriver.controls.composite.table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public abstract class TableVerifier {

    protected Map<String, String> values = new HashMap<>();

    public final VerifyRows rows = new VerifyRows();

    protected abstract Table getTable();

    protected abstract String getTableName();

    public TableVerifier verify(int rowNumber) {
        values.forEach((key, value) -> assertThat(getTable().getRow(rowNumber))
                .as(String.format("Table '%s', Row '%s', Column '%s'", getTableName(), rowNumber, key)).hasCellWithValue(key, value));
        return this;
    }

    public TableVerifier verifyDollar(int rowNumber, String column, Currency value, double precision) {
        Currency actualDollar = new Currency(getTable().getRow(rowNumber).getCell(column).getValue());
        actualDollar.verify.isEqual(value, precision);
        return this;
    }

    public TableVerifier verify(int rowNumberStart, int rowNumberEnd) {
        IntStream.rangeClosed(rowNumberStart, rowNumberEnd).forEach(this::verify);
        return this;
    }

    public TableVerifier setValue(String columnName, String expectedValue) {
        values.put(columnName, expectedValue);
        return this;
    }

    public class VerifyRows {
        protected List<String> excludeColumn = new ArrayList<>();

        public VerifyRows excludeColumn(String columnName) {
            excludeColumn.add(columnName);
            return this;
        }

        public void verify(Row actualRow, Row expectedRow) {
            IntStream.rangeClosed(1, getTable().getColumnsCount()).forEach(index -> {
                if(!excludeColumn.contains(getTable().getHeader().getCell(index).getValue())) {
                    assertThat(actualRow.getCell(index)).as("Table '%s', Column '%s'", getTableName(),
                            getTable().getHeader().getCell(index).getValue()).hasValue(expectedRow.getCell(index).getValue());
                }
            });
        }
    }
}
