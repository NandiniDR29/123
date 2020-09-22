/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.ContactMethodPhoneModel;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.exigen.ren.rest.model.RestError;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerAddressPhoneAdditionalFields extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-12308", component = CUSTOMER_REST)
    public void testRestCustomerAddressPhoneAdditionalFields() {

        mainApp().open();
        customerIndividual.createViaUI(tdCustomersRest.getTestData("CRCustomer1"));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        ResponseContainer<CustomerModel> customerResponse1 = customerRestClient.getCustomersItem(customerId);
        CustomerModel customer = customerResponse1.getModel();
        LOGGER.info("Response:\n" + customer);

        //step 1 check
        ContactMethodPhoneModel phone = customer.getPhones().get(0);
        assertSoftly(softly -> {
                    softly.assertThat(phone.getPhoneExtension()).as("Phone extension is not null or empty").isNullOrEmpty();
                    softly.assertThat(phone.getPreferredDaysToContact()).as("Preferred Day(s) to Contact list is not empty").isEmpty();
                    softly.assertThat(phone.getPreferredTimesToContact()).as("Preferred Time(s) to Contact list is not empty").isEmpty();
                });
        int phoneId = phone.getId();
        phone.setPhoneExtension("380");
        phone.addPreferredDaysToContact(DayOfWeek.MONDAY.name(), DayOfWeek.SATURDAY.name());
        phone.addPreferredTimesToContact(PartOfDay.MORNING.name(), PartOfDay.AFTERNOON.name());

        ContactMethodPhoneModel updatedPhone =
                customerRestClient.putCustomersPhonesItem(phone, customerId, phoneId);
        LOGGER.info("Response:\n" + updatedPhone);
        //step 2 check
        checkPhoneUpdate(phone, updatedPhone, customerId);

        customerResponse1 = customerRestClient.getCustomersItem(customerId);
        customer = customerResponse1.getModel();
        LOGGER.info("Response:\n" + customer);
        //step 3 check
        checkPhoneUpdate(phone, customer.getPhones().get(0), customerId);

        phone.setPhoneExtension(RandomStringUtils.random(5, SPECIAL_SUMBOLS));

        ContactMethodPhoneModel customerErrorWrapper = customerRestClient.putCustomersPhonesItem(phone, customerId, phoneId);
        LOGGER.info("Response:\n" + customerErrorWrapper);
        //step 4 check
        checkRestError(customerErrorWrapper);

        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));

        customerErrorWrapper = customerRestClient.putCustomersPhonesItem(phone, customerId, phoneId);
        LOGGER.info("Response:\n" + customerErrorWrapper);
        //step 5 check
        checkRestError(customerErrorWrapper);

        phone.setPhoneExtension("");
        phone.getPreferredDaysToContact().clear();
        phone.getPreferredTimesToContact().clear();

        updatedPhone = customerRestClient.putCustomersPhonesItem(phone, customerId, phoneId);
        LOGGER.info("Response:\n" + updatedPhone);
        //step 6 check
        checkPhoneUpdate(phone, updatedPhone, customerId);

        //step 7 repeat steps starts
        phone.setPhoneExtension("1");
        phone.addPreferredDaysToContact(DayOfWeek.THURSDAY.name(), DayOfWeek.TUESDAY.name());
        phone.addPreferredTimesToContact(PartOfDay.EVENING.name(), PartOfDay.NIGHT.name());
        customer.getPhones().clear();
        customer.getPhones().add(phone);

        customer = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //step 7 (repeat step 2) check
        checkPhoneUpdate(phone, customer.getPhones().get(0), customerId);
        //step 7 (repeat step 3) ignored, same checked in step 1 and in put method response
        phone.setPhoneExtension(RandomStringUtils.random(5, SPECIAL_SUMBOLS));
        customer.getPhones().clear();
        customer.getPhones().add(phone);
        CustomerModel customerResponse = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customerResponse.toString());
        //step 7 (repeat step 4) check
        checkRestError(customerResponse);
        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));
        customerResponse = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customerResponse);
        //step 7 (repeat step 5) check
        checkRestError(customerResponse);
        phone.setPhoneExtension("");
        phone.getPreferredDaysToContact().clear();
        phone.getPreferredTimesToContact().clear();
        customer = customerRestClient.putCustomer(customer, customerId);
        LOGGER.info("Response:\n" + customer);
        //step 7 (repeat step 6) check
        checkPhoneUpdate(phone, customer.getPhones().get(0), customerId);

        phone.setId(null);
        phone.setPhoneNumber("911");

        ContactMethodPhoneModel addedPhone = customerRestClient.postCustomersPhonesItem(phone, customerId);
        LOGGER.info("Response:\n" + addedPhone);
        //step 8 check
        assertThat(phone).isEqualTo(addedPhone).as(String.format("Newly added contact method phone was not added for customer = %1$s", customerId));

        phone.setPhoneExtension("7");
        addedPhone = customerRestClient.postCustomersPhonesItem(phone, customerId);
        LOGGER.info("Response:\n" + addedPhone);
        //step 9 check
        assertThat(phone).isEqualTo(addedPhone).as(String.format("Newly added contact method phone was not added for customer = %1$s", customerId));

        phone.setPhoneExtension(RandomStringUtils.random(5, SPECIAL_SUMBOLS));
        ContactMethodPhoneModel contactMethod = customerRestClient.postCustomersPhonesItem(phone, customerId);
        LOGGER.info("Response:\n" + contactMethod);
        //step 10 check
        checkRestError(contactMethod);

        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));
        contactMethod = customerRestClient.postCustomersPhonesItem(phone, customerId);
        LOGGER.info("Response:\n" + contactMethod);
        //step 11 check
        checkRestError(contactMethod);

        //step 12 repeat 8-11
        customer.setCustomerNumber(null);
        customer.getAddresses().get(0).setId(null);
        phone.setPhoneNumber("101");
        phone.setPhoneExtension("");
        phone.setId(null);
        customer.getPhones().clear();
        customer.getPhones().add(phone);
        customerResponse1 = customerRestClient.postCustomer(customer);
        CustomerModel newCustomer = customerResponse1.getModel();
        LOGGER.info("Response:\n" + customer.toString());
        //step 12 (repeat step 8) check
        assertThat(customer).isEqualTo(newCustomer).as("Customer was not created");
        phone.setPhoneExtension("3");
        phone.addPreferredDaysToContact(DayOfWeek.FRIDAY.name());
        phone.addPreferredTimesToContact(PartOfDay.EVENING.name());
        customerResponse1 = customerRestClient.postCustomer(customer);
        newCustomer = customerResponse1.getModel();
        LOGGER.info("Response:\n" + customer.toString());
        //step 12 (repeat step 9) check
        assertThat(customer).isEqualTo(newCustomer).as("Customer was not created");
        phone.setPhoneExtension(RandomStringUtils.random(5, SPECIAL_SUMBOLS));
        customerResponse1 = customerRestClient.postCustomer(customer);
        CustomerModel customerErrorResponse = customerResponse1.getModel();

        LOGGER.info("Response:\n" + customerErrorResponse);
        //step 12 (repeat step 10) check
        checkRestError(customerErrorResponse);
        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));
        customerResponse1 = customerRestClient.postCustomer(customer);
        customerErrorResponse = customerResponse1.getModel();
        LOGGER.info("Response:\n" + customerErrorResponse);
        //step 12 (repeat step 11 check)
        checkRestError(customerErrorResponse);
        //steps 13-17 not covered as redudant
    }

    private void checkRestError(RestError response){
        assertThat(new RestError("422", "7 digits should be entered", "phoneExtension").setFieldEqualsContainsEnabled())
                .isEqualTo(response.getErrors().get(0).setFieldEqualsContainsEnabled()).as("Incorrect error object");
        assertThat(response.getErrorCode()).isEqualToIgnoringCase("422").as("Incorrect errorCode");
    }

    private void checkPhoneUpdate(ContactMethodPhoneModel phone, ContactMethodPhoneModel phoneUpdated, String customerId){
        assertThat(phone).isEqualTo(phoneUpdated).as(String.format("Phone was not updated for customer %1$s", customerId));
    }
}
