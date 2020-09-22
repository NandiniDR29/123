/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.communications.model.CommunicationsModelThread;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRESTCustomersCommunicationsPOSTThreads extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26172", component = CRM_CUSTOMER)
    public void testRESTCustomersCommunicationsPOSTThreads() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        customerNonIndividual.addCommunication().perform(tdCustomerNonIndividual.getTestData("Communication", "TestData"));
        String communicationId = CommunicationActionTab.tableCommunications.getRow(1).getCell("ID").getValue();

        LOGGER.info("STEP: Add Communication Thread for Customer # " + customerNumber);
        ResponseContainer<CommunicationsModelThread> response =
                customerRestClient.postCommunicationsThreads(customerNumber, communicationId, tdSpecific().getTestData("TestData")
                        .adjust("entityReferenceId", customerNumber).resolveLinks());
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            checkCommunicationThread(softly, response.getModel(), tdSpecific().getTestData("TestData_Check"));
        });

        MainPage.QuickSearch.search(customerNumber);
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        assertThat(CommunicationActionTab.tableCommunications).hasRows(2);
    }

    private void checkCommunicationThread(ETCSCoreSoftAssertions softly, CommunicationsModelThread communicationsThread, TestData data) {
        softly.assertThat(communicationsThread.getThreadId()).isEqualTo(data.getValue("threadId"));
        softly.assertThat(communicationsThread.getCategoryCd()).isEqualTo(data.getValue("categoryCd"));
        softly.assertThat(communicationsThread.getSubCategoryCd()).isEqualTo(data.getValue("subCategoryCd"));
        softly.assertThat(communicationsThread.getSourceCd()).isEqualTo(data.getValue("sourceCd"));
        softly.assertThat(communicationsThread.getStatusCd()).isEqualTo(data.getValue("statusCd"));
        softly.assertThat(communicationsThread.getEntityTypeCd()).isEqualTo(data.getValue("entityTypeCd"));
        softly.assertThat(communicationsThread.getDirectionCd()).isEqualTo(data.getValue("directionCd"));
        softly.assertThat(communicationsThread.getPerformerDescription()).isEqualTo(data.getValue("performerDescription"));
        softly.assertThat(communicationsThread.getCommunicationTypeCd()).isEqualTo(data.getValue("communicationTypeCd"));
    }
}
