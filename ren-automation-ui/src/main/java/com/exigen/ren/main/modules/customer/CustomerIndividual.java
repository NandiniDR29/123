/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.SearchPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.CustomerTypeMetaData;
import com.exigen.ren.main.modules.customer.tabs.CustomerTypeTab;

public class CustomerIndividual extends Customer {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/cem/customer/individual");

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    public void initiate() {
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.buttonCreateCustomer.click();
        getDefaultWorkspace().getTab(CustomerTypeTab.class).getAssetList().getAsset(CustomerTypeMetaData.CUSTOMER_TYPE).setValue(CustomerConstants.INDIVIDUAL);
        getDefaultWorkspace().getTab(CustomerTypeTab.class).submitTab();
    }
}
