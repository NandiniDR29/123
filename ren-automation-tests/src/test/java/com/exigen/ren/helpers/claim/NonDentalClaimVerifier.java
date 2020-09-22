/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.claim;

import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.helpers.TableVerifier;
import com.exigen.ren.main.modules.claim.ClaimType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;


public class NonDentalClaimVerifier extends TableVerifier {

    public NonDentalClaimVerifier setStatus(String claimStatus) {
        setValue("Status", claimStatus);
        return this;
    }

    public NonDentalClaimVerifier setClaimType(ClaimType value) {
        setValue("Type of Claim", value.getName());
        return this;
    }

    public NonDentalClaimVerifier setPolicyType(String value) {
        setValue("Policy Product", value);
        return this;
    }

    @Override
    protected Table getTable() {
        return ClaimSummaryPage.tableListOfNonDentalClaims;
    }

    @Override
    protected String getTableName() {
        return "Life & Disability Claims";
    }
}
