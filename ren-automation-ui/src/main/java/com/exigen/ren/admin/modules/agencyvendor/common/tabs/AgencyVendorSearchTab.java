/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.common.tabs;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.agencyvendor.AgencyVendorDefaultTab;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.common.Tab;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

public class AgencyVendorSearchTab extends AgencyVendorDefaultTab {
    public static Button buttonAddNewAgency = new Button(By.id("brokerManagementForm:addButton"));
    public static Table tableAgencies = new Table(By.id("brokerManagementForm:body_brokerInfoTable"));

    public AgencyVendorSearchTab() {
        super(AgencyVendorSearchMetaData.class);
    }

    public TestData getSearchTestData(String fieldLabel, String fieldValue) {
        return new SimpleDataProvider().adjust(AgencyVendorSearchTab.class.getSimpleName(), new SimpleDataProvider().adjust("Status", "index=0")
                .adjust(fieldLabel, fieldValue));
    }

    public TestData getSearchTestData(TestData testData) {
        return new SimpleDataProvider().adjust(AgencyVendorSearchTab.class.getSimpleName(), testData);
    }

    public String searchMessage() {
        return new StaticElement(By.xpath("//span[@id='brokerManagementForm:brokerSearchMessage']//td")).getValue();
    }

    public void openFirst() {
        if (searchMessage().equals("Search item not found")) {
            try {
                RetryService.run(predicate -> searchMessage().equals("Search item not found"),
                        StopStrategies.stopAfterAttempt(5), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
            } catch (RuntimeException e) {
                throw new IstfException("Agency not found", e);
            }
        }
        tableAgencies.getRow(1).getCell(2).controls.links.getFirst().click();
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
