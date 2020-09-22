/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.tabs.BusinessEntityTab;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.exigen.ren.rest.model.RestError;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerAssociateBusinessEntities extends RestBaseTest {


    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-9322", component = CUSTOMER_REST)
    public void testRestCustomerAssociateBusinessEntities() {

        mainApp().open();
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", TEST_DATA_KEY)
                .mask(BusinessEntityTab.class.getSimpleName())
                .adjust(GeneralTab.class.getSimpleName(), tdCustomersRest.getTestData(GeneralTab.class.getSimpleName())));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        ResponseContainer<CustomerModel> customerResponse = customerRestClient.getCustomersItem(customerId);
        CustomerModel customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Check for Nickname step 1
        assertThat(customer.getIndividualDetails().getNickname()).as("Customer nick Name is not null or Empty").isNullOrEmpty();
        //Check Associate Business Entities step 1
        assertThat(customer.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("false");
        customer.getIndividualDetails().setNickname(RandomStringUtils.randomAlphabetic(51));
        CustomerModel customerWrapper = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customerWrapper);
        //Checks for Nickname step 2
        assertThat(new RestError("422", "Should not exceed 50 symbols", "nickname")).isEqualTo(customerWrapper.getErrors().get(0)).as("Incorrect error object");
        assertThat(customerWrapper.getErrorCode()).isEqualToIgnoringCase("422").as("Incorrect errorCode");
        customerResponse = customerRestClient.getCustomersItem(customerId);
        customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Check for Nickname step 2
        assertThat(customer.getIndividualDetails().getNickname()).as("Customer nick Name is not null or Empty").isNullOrEmpty();
        String correctNickName = RandomStringUtils.randomAlphabetic(50);
        customer.getIndividualDetails().setNickname(correctNickName);
        customer.getIndividualDetails().setAssociateBusinessEntity("true");
        customer = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //Check for Nickname step 3
        assertThat(correctNickName).isEqualToIgnoringCase(customer.getIndividualDetails().getNickname()).as("Customer nick Name is incorrect");
        //Check Associate Business Entities step 2
        assertThat(customer.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("true").as("Associate Business Entity is not true");
        customerResponse = customerRestClient.getCustomersItem(customerId);
        customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Check for Nickname step 4
        assertThat(correctNickName).isEqualToIgnoringCase(customer.getIndividualDetails().getNickname()).as("Customer nick Name is incorrect");
        //Check Associate Business Entities step 3
        assertThat(customer.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("true").as("Associate Business Entity is not true");
        customer.getIndividualDetails().setNickname(null);
        customer.getIndividualDetails().setAssociateBusinessEntity("false");
        customer = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //Check for Nickname step 5
        assertThat(customer.getIndividualDetails().getNickname()).isEqualTo(null).as("Customer nick Name is not null");
        //Check Associate Business Entities step 4
        assertThat(customer.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("false").as("Associate Business Entity is not false");
        customerResponse = customerRestClient.getCustomersItem(customerId);
        customer = customerResponse.getModel();
        LOGGER.info("Response:\n" + customer);
        //Check for Nickname step 6
        assertThat(customer.getIndividualDetails().getNickname()).isEqualTo(null).as("Customer nick Name is not null");
        //Check Associate Business Entities step 5
        assertThat(customer.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("false").as("Associate Business Entity is not false");

        customer.setCustomerNumber(null);
        customer.getAddresses().get(0).setId(null);
        customer.getIndividualDetails().setNickname(RandomStringUtils.randomAlphabetic(51));
        customerResponse = customerRestClient.postCustomer(customer);
        customerWrapper = customerResponse.getModel();
        LOGGER.info("Response:\n" + customerWrapper);
        //Check for Nickname step 7
        assertThat(new RestError("422", "Should not exceed 50 symbols", "nickname")).isEqualTo(customerWrapper.getErrors().get(0)).as("Incorrect error object");
        //Check for Nickname step 7
        assertThat(customerWrapper.getErrorCode()).isEqualToIgnoringCase("422").as("Incorrect errorCode");

        customer.getIndividualDetails().setNickname(null);
        customer.getIndividualDetails().setAssociateBusinessEntity("false");
        customerResponse = customerRestClient.postCustomer(customer);
        CustomerModel newCustomer = customerResponse.getModel();
        LOGGER.info("Response:\n" + newCustomer);
        //Check for Nickname step 8
        assertThat(newCustomer.getCustomerNumber()).isNotEqualTo(null).as("New Customer id is null");
        //Check Associate Business Entities step 6
        assertThat(newCustomer.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("false").as("Associate Business Entity is not false");
        customer.getIndividualDetails().setNickname(correctNickName);
        customer.getIndividualDetails().setAssociateBusinessEntity("true");
        customerResponse = customerRestClient.postCustomer(customer);
        CustomerModel newCustomer2 = customerResponse.getModel();
        LOGGER.info("Response:\n" + newCustomer2);
        //Check for Nickname step 9
        assertThat(newCustomer2.getCustomerNumber()).isNotEqualTo(null).as("New Customer id is null");
        //Check Associate Business Entities step 7
        assertThat(newCustomer2.getIndividualDetails().getAssociateBusinessEntity()).isEqualToIgnoringCase("true").as("Associate Business Entity is not true");
    }
}
