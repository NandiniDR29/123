/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.AdditionalNameModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetAdditionalNames extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-26403", "REN-11058"}, component = CRM_CUSTOMER)
    public void testRestCustomerGetAdditionalNames() {
        TestData tdAdditionalName = customerNonIndividual.getDefaultTestData("AdditionalName", "TestData");

        mainApp().open();

        LOGGER.info("STEP: Create Customer, Add Additional Name");
        customerNonIndividual.createViaREST(customerNonIndividual.getDefaultTestData("DataGather", "TestData"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        // Assert for REN-11058/#2
        assertThat(Integer.parseInt(customerNumber)).as("Customer ID should be between range: 1 to 999999").isBetween(1, 999999);

        customerNonIndividual.update().perform(tdAdditionalName);

        LOGGER.info("TEST: Check GET /customers/{customerNumber}/business-additional-names");
        ResponseContainer<List<AdditionalNameModel>> response = customerRestClient.getCustomersBusinessAdditionalNames(customerNumber);
        assertThat(response.getResponse()).hasStatus(200);
        String expectedNameDBA = tdAdditionalName.getTestData(generalTab.getMetaKey(),
                GeneralTabMetaData.ADDITIONAL_NAMES.getLabel()).getValue(GeneralTabMetaData.AdditionalNameDetailsMetaData.NAME_DBA.getLabel());
        assertThat(response.getModel().get(0).getNameDba()).isEqualTo(expectedNameDBA);

    }
}
