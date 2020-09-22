/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary.claim;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class ClaimHistoryPage extends SummaryPage {

    public static TableExtended<ClaimHistory> tableClaimHistory = new TableExtended<>("Claim History",  By.xpath("//div[@id='claimsHistoryInnerForm:versionsTable']//table"));

    public enum ClaimHistory implements Named {
        CLAIM_VERSION_TYPE("Type"),
        DATE("Date"),
        REASON("Reason"),
        PERFORMER("Performer"),
        DATE_AND_TIME("Date and Time"),
        REFERENCE("Reference"),
        COMPONENT("Component"),
        ATTRIBUTE("Attribute"),
        ORIGINAL("Original"),
        NEW("New"),
        ADJUSTED_BY("Adjusted by");

        private String name;

        ClaimHistory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
