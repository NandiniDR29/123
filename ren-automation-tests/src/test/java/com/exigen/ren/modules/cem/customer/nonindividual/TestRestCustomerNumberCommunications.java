/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.communications.model.CommunicationsModel;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerNumberCommunications extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26167", component = CRM_CUSTOMER)
    public void testRestCustomersCustomerNumberCommunications() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Communication for Customer # " + customerNumber);
        ResponseContainer<CommunicationsModel> opportunityResponse = customerRestClient.postCommunications(customerNumber, tdSpecific().getTestData("Rest_Data")
                .adjust("entityReferenceId", customerNumber));
        assertThat(opportunityResponse.getResponse().getStatus()).isEqualTo(200);

        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        checkCommunications(tdSpecific().getTestData("TestData_New").adjust("Entity Reference ID", customerNumber));

        customerNonIndividual.updateCommunication().perform(1, tdSpecific().getTestData("TestData"));
        checkCommunications(tdSpecific().getTestData("TestData_Update"));
    }

    private void checkCommunications(TestData data) {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(CommunicationActionTab.tableCommunications).hasRows(1);
            softly.assertThat(CommunicationActionTab.tableCommunications).containsMatchingRow(1, getMapFromTestData(data));
        });
    }

    private Map<String, String> getMapFromTestData(TestData td) {
        Map<String, String> testDataMap = new HashMap<>();
        td.getKeys().forEach(key ->
                testDataMap.put(key, td.getValue(key))
        );
        return testDataMap;
    }
}
