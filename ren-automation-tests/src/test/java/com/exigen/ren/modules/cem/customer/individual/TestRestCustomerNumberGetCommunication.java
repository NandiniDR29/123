/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.communications.model.CommunicationModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerNumberGetCommunication extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26170", component = CRM_CUSTOMER)
    public void testRestCustomerNumberGetCommunication() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        customerIndividual.addCommunication().perform(tdSpecific().getTestData("TestData_Communication"));

        String communicationId = CommunicationActionTab.tableCommunications.getRow(1).getCell("ID").getValue();

        ResponseContainer<CommunicationModel> response = customerRestClient.getCommunication(customerNumber, communicationId);
        TestData td=tdSpecific().getTestData("TestData_Check");
        CommunicationModel communicationModel = response.getModel();
        assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(communicationModel.getCustomerNumber()).isEqualTo(customerNumber);
            softly.assertThat(communicationModel.getEntityReferenceId()).isEqualTo(customerNumber);
            softly.assertThat(communicationModel.getCategoryCd()).isEqualTo(td.getValue("categoryCd"));
            softly.assertThat(communicationModel.getSubCategoryCd()).isEqualTo(td.getValue("subCategoryCd"));
            softly.assertThat(communicationModel.getSourceCd()).isEqualTo(td.getValue("sourceCd"));
            softly.assertThat(communicationModel.getLanguageCd()).isEqualTo(td.getValue("languageCd"));
            softly.assertThat(communicationModel.getInternalCallerCd()).isEqualTo(td.getValue("internalCallerCd"));
            softly.assertThat(communicationModel.getSubject()).isEqualTo(td.getValue("subject"));
            softly.assertThat(communicationModel.getStatusCd()).isEqualTo(td.getValue("statusCd"));
            softly.assertThat(communicationModel.getChannelCd()).isEqualTo(td.getValue("channelCd"));
            softly.assertThat(communicationModel.getDirectionCd()).isEqualTo(td.getValue("directionCd"));
            softly.assertThat(communicationModel.getEntityTypeCd()).isEqualTo(td.getValue("entityTypeCd"));
            softly.assertThat(communicationModel.getDescription()).isEqualTo(td.getValue("description"));
            softly.assertThat(communicationModel.getOutcome()).isEqualTo(td.getValue("outcome"));
        });
    }

}
