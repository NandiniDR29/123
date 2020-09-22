/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.*;
import com.exigen.ren.rest.model.RestError;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestIndividualCustomerWithAllContacts extends RestBaseTest {

    private static final String ERROR_CODE = "422";

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26697", component = CUSTOMER_REST)
    public void testIndividualCustomerWithInvalidCommunicationPreferences() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData_withAllContacts"));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Update Individual Customer # " + customerId + " with invalid communication preferences via PUT");
        CustomerModel customer = populateCustomerModelWithInvalidCommunicationPreferences(customerId);
        CustomerModel createdCustomer = customerRestClient.putCustomer(customer, customerId);
        List<RestError> actualErrors = createdCustomer.getErrors();
        List<RestError> expectedErrors = getExpectedErrors();
        assertThat(createdCustomer.getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(actualErrors).containsAll(expectedErrors).as("All errors are correct and present in response");

        LOGGER.info("TEST: Update Individual Customer # " + customerId + " with invalid communication preferences via POST");
        ResponseContainer<CustomerModel> customerResponse = customerRestClient.postCustomer(customer);
        createdCustomer = customerResponse.getModel();
        actualErrors = createdCustomer.getErrors();
        assertThat(createdCustomer.getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(actualErrors).containsAll(expectedErrors).as("All errors are correct and present in response");
    }

    private CustomerModel populateCustomerModelWithInvalidCommunicationPreferences(String customerId){
        List<String> invalidCommunicationPreferences = Arrays.asList("POLICY_DOC_INVALID");

        ResponseContainer<CustomerModel> customerResponse = customerRestClient.getCustomersItem(customerId);
        CustomerModel customer = customerResponse.getModel();

        ContactMethodAddressModel adresses = customer.getAddresses().get(0);
        adresses.setCommunicationPreferences(invalidCommunicationPreferences);
        customer.setAddresses(Arrays.asList(adresses));

        ContactMethodChatModel chats = customer.getChats().get(0);
        chats.setCommunicationPreferences(invalidCommunicationPreferences);
        customer.setChats(Arrays.asList(chats));

        ContactMethodEmailModel email = customer.getEmails().get(0);
        email.setCommunicationPreferences(invalidCommunicationPreferences);
        customer.setEmails(Arrays.asList(email));

        ContactMethodPhoneModel phone = customer.getPhones().get(0);
        phone.setCommunicationPreferences(invalidCommunicationPreferences);
        customer.setPhones(Arrays.asList(phone));

        ContactMethodWebAddressModel webAddress = customer.getWebAddresses().get(0);
        webAddress.setCommunicationPreferences(invalidCommunicationPreferences);
        customer.setWebAddresses(Arrays.asList(webAddress));

        ContactMethodSocialNetModel socialNet = customer.getSocialNets().get(0);
        socialNet.setCommunicationPreferences(invalidCommunicationPreferences);
        customer.setSocialNets(Arrays.asList(socialNet));
        return customer;
    }

    private List<RestError> getExpectedErrors() {
        List<RestError> errorList = new ArrayList<>();
        String errorMessage = "Wrong lookup code for Communication Preferences";
        String field = "details.communicationInfo.%s[0].communicationPreferences";
        errorList.add(new RestError(ERROR_CODE, errorMessage, String.format(field, "socialNetInfos")));
        errorList.add(new RestError(ERROR_CODE, errorMessage, String.format(field, "phones")));
        errorList.add(new RestError(ERROR_CODE, errorMessage, String.format(field, "webAddressInfos")));
        errorList.add(new RestError(ERROR_CODE, errorMessage, String.format(field, "emails")));
        errorList.add(new RestError(ERROR_CODE, errorMessage, String.format(field, "chats")));
        errorList.add(new RestError(ERROR_CODE, errorMessage, String.format(field, "addressInfos")));
        return errorList;
    }

}