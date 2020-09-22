/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.Users;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerBrand extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-8164", component = CUSTOMER_REST)
    public void testRestCustomerBrand() {
        customerRestClient = new CustomerRestService(Users.CUSTOMER_RS_USER);

        mainApp().open(Users.CUSTOMER_RS_USER.getLogin(), Users.CUSTOMER_RS_USER.getPassword());
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", TEST_DATA_KEY)
            .adjust(tdCustomerIndividual.getTestData("DataGather", "Adjustment_WithoutAgency")));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        ResponseContainer<CustomerModel> customerResponse = customerRestClient.getCustomersItem(customerId);
        CustomerModel customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Brand Name step 1 check
        assertThat(customer.getBrandCd()).isEqualTo(null).as("Customer brand is not null");
        customer.setBrandCd("001");
        customer = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //Brand Name step 2 check
        assertThat(customer.getBrandCd()).isEqualToIgnoringCase("001").as("Customer brand is not 001");
        customerResponse = customerRestClient.getCustomersItem(customerId);
        customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Brand Name step 4 check (step 3 skipped as not related to REST check)
        assertThat(customer.getBrandCd()).isEqualToIgnoringCase("001").as("Customer brand is not 001");
        customer.setBrandCd(null);
        customer = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //Brand Name step 5 check
        assertThat(customer.getBrandCd()).isEqualTo(null).as("Customer brand is not null");
        customerResponse = customerRestClient.getCustomersItem(customerId);
        customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Brand Name step 6 check
        assertThat(customer.getBrandCd()).isEqualTo(null).as("Customer brand is not null");
        customer.setBrandCd("002");
        customer =
                customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //Brand Name step 7 check
        assertThat(customer.getBrandCd()).isEqualToIgnoringCase("002").as("Customer brand is not 002");
        customerResponse = customerRestClient.getCustomersItem(customerId);
        CustomerModel customerWrapper =  customerResponse.getModel();

        LOGGER.info("Response:\n" + customerWrapper);
        //Brand Name step 8 check
        assertThat(customerWrapper.getErrorCode()).isEqualToIgnoringCase("CUSTOMER_NOT_FOUND").as("Error code does not match");
        //Brand Name step 8 check
        assertThat(customerWrapper.getMessage()).isEqualToIgnoringCase(String.format("Customer with number %1$s not found.", customerId)).as("Message does not match");

        customer.setCustomerNumber(null);

        customer.getAddresses().forEach(address ->  address.setId(null));
        customer.getIndividualDetails().setFirstName("CustomerRESTTest" + RandomStringUtils.randomNumeric(5));
        customer.getIndividualDetails().setLastName("LastName" + RandomStringUtils.randomNumeric(5));
        customer.setDisplayValue(customer.getIndividualDetails().getFirstName() + " " + customer.getIndividualDetails().getLastName());
        customer.setBrandCd("001");

        customerResponse = customerRestClient.postCustomer(customer);
        CustomerModel newCustomer = customerResponse.getModel();

        LOGGER.info("Response:\n" + newCustomer);
        //Brand Name step 9 check
        assertThat(customerId).isNotEqualToIgnoringCase(newCustomer.getCustomerNumber()).as("Customer number of existing client is the same as for newly created via REST");
        customer.setBrandCd(null);
        customerResponse = customerRestClient.postCustomer(customer);
        newCustomer = customerResponse.getModel();
        LOGGER.info("Response:\n" + newCustomer);
        //Brand Name step 10 check
        assertThat(customerId).isNotEqualToIgnoringCase(newCustomer.getCustomerNumber()).as("Customer number of existing client is the same as for newly created via REST");
        customer.setBrandCd("002");
        customerResponse = customerRestClient.postCustomer(customer);
        newCustomer = customerResponse.getModel();
        LOGGER.info("Response:\n" + newCustomer);
        //Brand Name step 11 check
        assertThat(newCustomer.getCustomerNumber()).isNotEqualTo(null).as("New Customer id is null");
        customerResponse = customerRestClient.getCustomersItem(newCustomer.getCustomerNumber());
        customerWrapper = customerResponse.getModel();
        LOGGER.info("Response:\n" + customerWrapper);
        //Brand Name step 12 check
        assertThat(customerWrapper.getErrorCode()).isEqualToIgnoringCase("CUSTOMER_NOT_FOUND").as("Error code does not match");
        //Brand Name step 12 check
        assertThat(customerWrapper.getMessage()).isEqualToIgnoringCase(String.format("Customer with number %1$s not found.", newCustomer.getCustomerNumber())).as("Message does not match");
    }
}
