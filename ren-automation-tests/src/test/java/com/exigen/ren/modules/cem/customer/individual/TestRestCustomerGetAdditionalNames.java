/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.AdditionalNameModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetAdditionalNames extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-26403", "REN-11058"}, component = CRM_CUSTOMER)
    public void testRestCustomerGetAdditionalNames() {
        TestData tdAdditionalName = tdCustomerIndividual.getTestData("AdditionalName", "TestData");

        mainApp().open();

        LOGGER.info("STEP: Create Customer, Add Additional Name");
        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        // Assert for REN-11058/#3
        assertThat(Integer.parseInt(customerNumber)).as("Customer ID should be between range: 1 to 999999").isBetween(1, 999999);

        customerIndividual.update().perform(tdAdditionalName);

        LOGGER.info("TEST: Check GET /customers/{customerNumber}/individual-additional-names");
        ResponseContainer<List<AdditionalNameModel>> response = customerRestClient.getCustomersAdditionalNames(customerNumber);
        TestData td = tdAdditionalName.getTestData(GeneralTab.class.getSimpleName(), GeneralTabMetaData.ADDITIONAL_NAMES.getLabel());
        assertSoftly(softly -> {
            assertThat(response.getResponse()).hasStatus(200);
            AdditionalNameModel additionalNameModel = response.getModel().get(0);
            softly.assertThat(additionalNameModel.getFirstName()).isEqualTo(td.getValue(GeneralTabMetaData.FIRST_NAME.getLabel()));
            softly.assertThat(additionalNameModel.getMiddleName()).isEqualTo(td.getValue(GeneralTabMetaData.MIDDLE_NAME.getLabel()));
            softly.assertThat(additionalNameModel.getLastName()).isEqualTo(td.getValue(GeneralTabMetaData.LAST_NAME.getLabel()));
            softly.assertThat(additionalNameModel.getSuffixNumValue()).isEqualTo(1);
            softly.assertThat(additionalNameModel.getDesignationCd()).isEqualTo(td.getValue(GeneralTabMetaData.DESIGNATION.getLabel()).toUpperCase());
        });
    }
}
