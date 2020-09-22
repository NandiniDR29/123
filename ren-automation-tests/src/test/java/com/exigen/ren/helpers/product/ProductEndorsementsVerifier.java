/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.product;

import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.helpers.TableVerifier;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;


public class ProductEndorsementsVerifier extends TableVerifier {

    public ProductEndorsementsVerifier setStatus(String policyStatus) {
        setValue("Status", policyStatus.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return PolicySummaryPage.tableEndorsements;
    }

    @Override
    protected String getTableName() {
        return "Endorsements";
    }
}
