/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.SearchSuspenseActionTabMetaData;
import org.openqa.selenium.By;

public class SearchSuspenseActionTab extends ActionTab {


    public static TableExtended<BillingSuspenseSearch> tableSuspenseSearchResults  =
            new TableExtended<>("Suspense Search", By.xpath("//div[@id='suspenseSearch:suspenseSearchResults']//table"));
    public static Button buttonSearchSuspense =  new Button(By.id("suspenseSearch:searchBtn"));

    public SearchSuspenseActionTab() {
        super(SearchSuspenseActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonSearchSuspense.click();
        if (tableSuspenseSearchResults.getRow(1).getCell(BillingConstants.BillingSuspenseSearchResultsTable.ACTION).getValue().contains("Clear")) {
            tableSuspenseSearchResults.getRow(1).getCell(BillingConstants.BillingSuspenseSearchResultsTable.ACTION).controls.links.get(ActionConstants.CLEAR).click();
        }
        return this;
    }


    public enum BillingSuspenseSearch implements Named {
        REFERENCE("Reference #");
        private String name;

        BillingSuspenseSearch(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
