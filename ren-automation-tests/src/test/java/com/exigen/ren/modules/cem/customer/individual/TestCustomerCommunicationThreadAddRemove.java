/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerCommunicationThreadAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24842", component = CRM_CUSTOMER)
    public void testCustomerCommunicationThreadAddRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.COMMUNICATION.get());
        customerIndividual.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData"));
        int communicationsCount = CommunicationActionTab.tableCommunications.getRowsCount();

        LOGGER.info("TEST: Add Communication Thread for Customer # " + customerNumber);
        customerIndividual.addCommunicationThread().perform(tdCustomerIndividual.getTestData("CommunicationThread", "TestData"), 1);
        assertThat(CommunicationActionTab.tableCommunications).hasRows(communicationsCount + 1);
        assertThat(CommunicationActionTab.tableCommunications).containsMatchingRows(1, 2,
                        ImmutableMap.of(CustomerConstants.CustomerCommunicationsTable.ENTITY_REFERENCE_ID, CustomerSummaryPage.labelCustomerNumber.getValue()));

        LOGGER.info("TEST: Remove Communication Thread for Customer # " + customerNumber);
        customerIndividual.removeCommunication().perform(1);
        assertThat(CommunicationActionTab.tableCommunications).hasRows(communicationsCount);
    }
}
