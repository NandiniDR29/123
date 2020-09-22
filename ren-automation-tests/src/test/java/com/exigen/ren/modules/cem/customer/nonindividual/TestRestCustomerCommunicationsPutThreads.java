/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
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
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerCommunicationsPutThreads extends RestBaseTest{

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26169", component = CRM_CUSTOMER)
    public void testRestCustomerCommunicationsPutThreads() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Add Communication for Customer # " + customerNumber);
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.COMMUNICATION.get());
        customerNonIndividual.addCommunication().perform(tdCustomerNonIndividual.getTestData("Communication", "TestData"));

        LOGGER.info("STEP: Add Communication Thread for Customer # " + customerNumber);
        customerNonIndividual.addCommunicationThread().perform(tdSpecific().getTestData("TestData_CommunicationThread"), 1);

        String communicationId = CommunicationActionTab.tableCommunications.getRow(2).getCell("ID").getValue();

        LOGGER.info("TEST: Update Communication Thread for Customer # " + customerNumber);
        Response response = customerRestClient.putCommunicationsThreads(customerNumber, communicationId, "1",
                tdSpecific().getTestData("REST_UpdateCommunicationThread").adjust("entityReferenceId", customerNumber));
        assertThat(response.getStatus()).isEqualTo(204);

        CommunicationActionTab.tableCommunications.getRow(1).getCell(CustomerConstants.CustomerCommunicationsTable.ACTION).controls.buttons.get("Update").click();

        TestData tdCheck = tdSpecific().getTestData("TestData_Check");
        AssetList communicationAssetList = (AssetList) CustomerType.NON_INDIVIDUAL.get().addCommunication().getWorkspace().getTab(CommunicationActionTab.class).getAssetList();
        List<AssetDescriptor> elementList = Arrays.asList(CommunicationActionTabMetaData.CATEGORY, CommunicationActionTabMetaData.SUB_CATEGORY, CommunicationActionTabMetaData.SOURCE,
                CommunicationActionTabMetaData.LANGUAGE,CommunicationActionTabMetaData.INTERNAL_CALLER_ID, CommunicationActionTabMetaData.SUBJECT,
                CommunicationActionTabMetaData.STATUS, CommunicationActionTabMetaData.ENTITY_TYPE, CommunicationActionTabMetaData.COMMUNICATION_DESCRIPTION,
                CommunicationActionTabMetaData.COMMUNICATION_OUTCOME);
        elementList.forEach(e -> assertThat(communicationAssetList.getAsset(e).getValue())
                .describedAs("element: " + e.getLabel()).isEqualTo(tdCheck.getValue(e.getLabel())));

        Tab.buttonSave.click();
        CustomAssertions.assertThat(CommunicationActionTab.tableCommunications).isPresent();
    }
}
