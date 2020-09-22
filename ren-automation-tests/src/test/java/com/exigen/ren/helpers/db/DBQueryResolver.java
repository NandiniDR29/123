/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.db;

import com.exigen.istf.config.PropertyProvider;

import java.util.Optional;

public class DBQueryResolver {
    private final String MSSQL;
    private final String oracleQuery;
    private final String postgreQuery;

    public DBQueryResolver(Builder builder) {
        this.MSSQL = builder.MSSQL;
        this.oracleQuery = builder.oracleQuery;
        this.postgreQuery = builder.postgreQuery;
    }

    public static DBQueryResolver.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String MSSQL;
        private String oracleQuery;
        private String postgreQuery;

        private Builder() {
        }

        public Builder setMSSQL(String MSSQL) {
            this.MSSQL = MSSQL;
            return this;
        }

        public Builder setOracle(String oracleQuery) {
            this.oracleQuery = oracleQuery;
            return this;
        }

        public Builder setPostgre(String postgreQuery) {
            this.postgreQuery = postgreQuery;
            return this;
        }

        public Builder setMSSQLAndOracle(String query) {
            this.MSSQL = query;
            this.oracleQuery = query;
            return this;
        }

        public Builder setMSSQLAndPostgre(String query) {
            this.MSSQL = query;
            this.postgreQuery = query;
            return this;
        }

        public Builder setOracleAndPostgre(String query) {
            this.oracleQuery = query;
            this.postgreQuery = query;
            return this;
        }

        public Builder setAll(String query) {
            this.MSSQL = query;
            this.oracleQuery = query;
            this.postgreQuery = query;
            return this;
        }

        public DBQueryResolver build() {
            return new DBQueryResolver(this);
        }
    }

    public String resolve() {
        switch (getType()) {
            case MSSQL: {
                return Optional.of(MSSQL).orElseThrow(() -> new IllegalArgumentException("MS SQL query is null"));
            }
            case ORACLE: {
                return Optional.of(oracleQuery).orElseThrow(() -> new IllegalArgumentException("Oracle query is null"));
            }
            case POSTGRE: {
                return Optional.of(postgreQuery).orElseThrow(() -> new IllegalArgumentException("Postgre query is null"));
            }
        }
        return null;
    }

    private DBType getType() {
        String dbDriver = PropertyProvider.getProperty("driver");
        if (dbDriver.contains("oracle")) {
            return DBType.ORACLE;
        } else if (dbDriver.contains("sqlserver")) {
            return DBType.MSSQL;
        } else if (dbDriver.contains("postgresql")) {
            return DBType.POSTGRE;
        } else {
            throw new IllegalArgumentException("Unknown DB type");
        }
    }

    private enum DBType {
        MSSQL,
        ORACLE,
        POSTGRE
    }
}
