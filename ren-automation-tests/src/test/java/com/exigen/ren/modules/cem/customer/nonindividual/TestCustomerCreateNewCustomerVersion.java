/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerCreateNewCustomerVersion extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24871", component = CRM_CUSTOMER)
    public void testCustomerCreateNewCustomerVersion() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Inquiry Customer # " + customerNumber);
        customerNonIndividual.inquiry().perform();

        assertThat(CustomerSummaryPage.buttonTimeLine).isAbsent();

        GeneralTab.buttonTopCancel.click();

        LOGGER.info("STEP: Update for Customer # " + customerNumber);
        customerNonIndividual.update().perform(tdCustomerNonIndividual.getTestData("TestCustomerUpdate", "TestData"));

        LOGGER.info("STEP: Inquiry Customer # " + customerNumber);
        customerNonIndividual.inquiry().perform();

        assertThat(CustomerSummaryPage.buttonTimeLine).isPresent();
    }
}
