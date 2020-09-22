/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.claim;

import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.helpers.TableVerifier;
import com.exigen.ren.main.pages.summary.claim.ClaimHistoryPage;

import java.time.LocalDateTime;


public class ClaimHistoryVerifier extends TableVerifier {

    public ClaimHistoryVerifier setClaimVersionType(String claimVersionType) {
        setValue("Type", claimVersionType);
        return this;
    }

    public ClaimHistoryVerifier setDate(LocalDateTime date) {
        setValue("Date", date.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public ClaimHistoryVerifier setReason(String reason) {
        setValue("Reason", reason);
        return this;
    }

    public ClaimHistoryVerifier setPerformer(String performer) {
        setValue("Performer", performer);
        return this;
    }

    @Override
    protected Table getTable() {
        return ClaimHistoryPage.tableClaimHistory;
    }

    @Override
    protected String getTableName() {
        return "Claim History";
    }
}
