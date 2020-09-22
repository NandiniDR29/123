/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.communications.model.CommunicationModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerNumberGetCommunications extends RestBaseTest {
    private String customerNumber;
    private String communicationId;

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26166", component = CRM_CUSTOMER)
    public void testRestCustomerNumberGetCommunications() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        customerIndividual.addCommunication().perform(tdCustomerIndividual.getTestData("Communication", "TestData"));

        LOGGER.info("STEP: Add Communication Thread for Customer # " + customerNumber);
        customerIndividual.addCommunicationThread().perform(tdCustomerNonIndividual.getTestData("CommunicationThread", "TestData"), 1);

        communicationId = CommunicationActionTab.tableCommunications.getRow(2).getCell("ID").getValue();

        TestData td = tdSpecific().getTestData("TestData");
        ResponseContainer<List<CommunicationModel>> response = customerRestClient.getCommunications(customerNumber);
        assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel()).hasSize(2);
            checkCommunication(softly, response.getModel().get(0), td, false);
            checkCommunication(softly, response.getModel().get(1), td, true);
        });
    }

    private void checkCommunication(ETCSCoreSoftAssertions function, CommunicationModel communicationModel, TestData td, Boolean thread) {
        function.assertThat(communicationModel.getCustomerNumber()).isEqualTo(customerNumber);
        function.assertThat(communicationModel.getPerformerDescription()).isEqualTo(td.getValue("Performer"));
        function.assertThat(communicationModel.getDirectionCd()).isEqualTo(td.getValue("Direction"));
        function.assertThat(communicationModel.getChannelCd()).isEqualTo(td.getValue("Channel"));
        function.assertThat(communicationModel.getCommunicationTypeCd()).isEqualTo(td.getValue("Type"));
        function.assertThat(communicationModel.getEntityTypeCd()).isEqualTo(td.getValue("Entity Type"));
        function.assertThat(communicationModel.getCommunicationId()).isEqualTo(communicationId);
        if (thread) {
            function.assertThat(communicationModel.getThreadId()).isEqualTo("1");
        } else {
            function.assertThat(communicationModel.getThreadId()).isEqualTo("0");
        }
    }
}
