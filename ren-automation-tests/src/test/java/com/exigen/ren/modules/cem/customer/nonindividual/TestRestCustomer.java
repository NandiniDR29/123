/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.customer.CustomerType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomer extends CustomerBaseTest {

    private static final String LEGAL_NAME = "Google";

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24991", component = CRM_CUSTOMER)
    public void testCustomerCreate() {
        tdCustomerNonIndividual = testDataManager.customer.get(CustomerType.NON_INDIVIDUAL);
        mainApp().open();
        customerNonIndividual.createViaREST(tdCustomerIndividual.getTestData(TestDataKey.DATA_GATHER,TestDataKey.DEFAULT_TEST_DATA_KEY));

        assertThat(CustomerSummaryPage.labelCustomerName).isPresent();
    }

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24992", component = CRM_CUSTOMER)
    public void testCustomerUpdate() {
        mainApp().open();

        customerNonIndividual.createViaREST(tdCustomerNonIndividual.getTestData(TestDataKey.DATA_GATHER,TestDataKey.DEFAULT_TEST_DATA_KEY));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomerRestService customerRestClient = RESTServiceType.CUSTOMERS.get();
        ResponseContainer<CustomerModel> customerResponse = customerRestClient.getCustomersItem(customerNumber);
        CustomerModel customer = customerResponse.getModel();
        customer.getBusinessDetails().setLegalName(LEGAL_NAME);
        customerNonIndividual.updateNonIndividualViaREST(customerNumber, customer);
        MainPage.QuickSearch.search(customerNumber);

        assertThat(CustomerSummaryPage.labelCustomerName).valueContains(LEGAL_NAME);
    }
}
