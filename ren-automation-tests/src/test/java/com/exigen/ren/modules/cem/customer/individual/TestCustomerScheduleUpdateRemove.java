/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerScheduleUpdateRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24916", component = CRM_CUSTOMER)
    public void testCustomerScheduleUpdateRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Scheduled Update for Customer # " + customerNumber);
        customerIndividual.scheduledUpdate().perform(tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData"));

        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isPresent();

        LOGGER.info("TEST: Delete Pending Updates for Customer # " + customerNumber);
        customerIndividual.deletePendingUpdates().perform();

        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isAbsent();
    }
}
