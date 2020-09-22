/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.CustomerType;
import com.exigen.ren.main.modules.customer.metadata.CommunicationActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerNumberPutCommunication extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26168", component = CRM_CUSTOMER)
    public void testRestCustomerNumberPutCommunication() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        customerIndividual.addCommunication().perform(tdSpecific().getTestData("TestData_Communication"));

        String communicationId = CommunicationActionTab.tableCommunications.getRow(1).getCell("ID").getValue();

        LOGGER.info("TEST: Update Communication for Customer # " + customerNumber);
        ResponseContainer response = customerRestClient.putCommunication(customerNumber, communicationId,
                tdSpecific().getTestData("REST_UpdateCommunication").adjust("entityReferenceId", customerNumber));
        assertThat(response.getResponse().getStatus()).isEqualTo(204);

        LOGGER.info("TEST: Check Communication for Customer # " + customerNumber);
        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.ACTION).controls.buttons.get("Update").click();

        AssetList communicationAssetList = (AssetList) CustomerType.INDIVIDUAL.get().addCommunication().getWorkspace().getTab(CommunicationActionTab.class).getAssetList();
        List<AssetDescriptor> elementList = Arrays.asList(CommunicationActionTabMetaData.ENTITY_TYPE, CommunicationActionTabMetaData.COMMUNICATION_CHANNEL,
                CommunicationActionTabMetaData.COMMUNICATION_DIRECTION, CommunicationActionTabMetaData.SUBJECT, CommunicationActionTabMetaData.STATUS,
                CommunicationActionTabMetaData.COMMUNICATION_DESCRIPTION, CommunicationActionTabMetaData.COMMUNICATION_OUTCOME, CommunicationActionTabMetaData.CATEGORY);
        TestData tdCheck = tdSpecific().getTestData("TestData_Check");
        elementList.forEach(e -> assertThat(communicationAssetList.getAsset(e).getValue()).isEqualTo(tdCheck.getValue(e.getLabel())));

        Tab.buttonSave.click();
        assertThat(CommunicationActionTab.tableCommunications).isPresent();
    }

}
