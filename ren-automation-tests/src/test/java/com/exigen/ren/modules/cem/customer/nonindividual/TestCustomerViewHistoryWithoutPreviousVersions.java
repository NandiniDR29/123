/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.ViewHistoryActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerViewHistoryWithoutPreviousVersions extends CustomerBaseTest implements CustomerContext {

    private static String errorMessage = "No corresponding prior version exists for this component for selected date";

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24919", component = CRM_CUSTOMER)
    public void testCustomerViewHistoryWithoutPreviousVersions() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: View History for Customer # " + customerNumber);
        customerNonIndividual.viewHistory().perform(tdCustomerNonIndividual.getTestData("ViewHistoryAction", "TestData"));

        assertThat(viewHistoryActionTab.getAssetList().getAsset(ViewHistoryActionTabMetaData.VERSION_DATE)).hasValue(tdCustomerNonIndividual.getValue(
                "ViewHistoryAction", "TestData", viewHistoryActionTab.getMetaKey(), ViewHistoryActionTabMetaData.VERSION_DATE.getLabel()));

        ViewHistoryActionTab.linkHistoryPanel.click();
        assertThat(ViewHistoryActionTab.tableGeneralInfo.getRow(1).getCell(2)).valueContains("Current Version");

        assertThat(ErrorPage.provideLabelErrorMessage(errorMessage)).isPresent();
    }
}
