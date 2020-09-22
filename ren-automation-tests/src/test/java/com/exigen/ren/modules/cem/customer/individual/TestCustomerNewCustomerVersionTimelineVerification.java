/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerNewCustomerVersionTimelineVerification extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24879", component = CRM_CUSTOMER)
    public void testCustomerNewCustomerVersionTimelineVerification() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Inquiry Customer # " + customerNumber);
        customerIndividual.inquiry().perform();

        assertThat(CustomerSummaryPage.buttonTimeLine).isAbsent();

        GeneralTab.buttonTopCancel.click();

        LOGGER.info("STEP: Update for Customer # " + customerNumber);
        customerIndividual.update().perform(tdCustomerIndividual.getTestData("TestCustomerUpdate", "TestData"));

        LOGGER.info("STEP: Inquiry Customer # " + customerNumber);
        customerIndividual.inquiry().perform();

        assertThat(CustomerSummaryPage.buttonTimeLine).isPresent();

        CustomerSummaryPage.buttonTimeLine.click();

        assertThat(CustomerSummaryPage.tableGeneralInfo.getRow(1).getCell(2)).valueContains("V.2");

        assertThat(CustomerSummaryPage.tableGeneralInfo.getRow(1).getCell(3)).valueContains("V.1");

        assertThat(CustomerSummaryPage.tableGeneralInfo.getRow(4).getCell(2).getValue())
                .isNotEqualToIgnoringCase(CustomerSummaryPage.tableGeneralInfo.getRow(4).getCell(3).getValue());
    }
}
