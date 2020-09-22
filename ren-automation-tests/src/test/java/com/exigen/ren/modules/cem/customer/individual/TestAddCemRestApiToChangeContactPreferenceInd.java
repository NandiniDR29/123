package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.ContactMethodAddressModel;
import com.exigen.ren.rest.customer.model.ContactMethodEmailModel;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddCemRestApiToChangeContactPreferenceInd extends RestBaseTest {

    private final static String CLAIMS = "CLAIMS";
    private final static String ADMINISTRATIVE = "ADMINISTRATIVE";

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-43698", component = CRM_CUSTOMER)
    public void testApiVerificationPutCustomersCustomerNumberContacts() {
        mainApp().open();
        customerIndividual.createViaUI(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("REN-43698 TC#1 Step 1");
        ResponseContainer<CustomerModel> customersItem = customerRestClient.getCustomersItem(customerId);
        Integer residenceAddressId = customersItem.getModel().getAddresses().stream().filter(address -> address.getContactType().equals("residence")).findFirst().get().getId();
        Integer firstMailingAddressId = customersItem.getModel().getAddresses().stream().filter(address -> address.getContactType().equals("mailing")).findFirst().get().getId();
        Integer secondMailingAddressId = customersItem.getModel().getAddresses().stream().filter(address -> address.getContactType().equals("mailing")).skip(1).findAny()
                .get().getId();

        Integer emailNullId = customersItem.getModel().getEmails().stream().filter(email -> email.getContactType() == null).findFirst().get().getId();
        Integer emailPersId = customersItem.getModel().getEmails().stream().filter(email -> email.getContactType() != null && email.getContactType().equals("PERS"))
                .findFirst().get().getId();
        Integer emailWorkId = customersItem.getModel().getEmails().stream().filter(email -> email.getContactType() != null && email.getContactType().equals("WORK"))
                .findFirst().get().getId();

        CustomerModel clearCustomerModel = createClearCustomerModel();
        clearCustomerModel.setAddresses(getListAddresses(customersItem.getModel()));
        clearCustomerModel.setEmails(getListEmails(customersItem.getModel()));

        ResponseContainer<CustomerModel> customerModelResponseContainer = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel);

        LOGGER.info("REN-43698 TC#1 Step 2");
        assertSoftly(softly -> {
            softly.assertThat(clearCustomerModel.getAddresses()).isEqualTo(customerModelResponseContainer.getModel().getAddresses());
            softly.assertThat(clearCustomerModel.getEmails()).isEqualTo(customerModelResponseContainer.getModel().getEmails());
        });

        LOGGER.info("REN-43698 TC#2 Step 1");
        CustomerModel clearCustomerModel2 = createClearCustomerModel();
        clearCustomerModel2.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS)));
        clearCustomerModel2.setEmails(setUpEmailsList(customersItem.getModel()));
        ResponseContainer<CustomerModel> customerModelResponseContainer2 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel2);
        checkResponseAndErrorMessage(customerModelResponseContainer2);

        LOGGER.info("REN-43698 TC#2 Step 2");
        CustomerModel clearCustomerModel3 = createClearCustomerModel();
        clearCustomerModel3.setAddresses(setUpAddressesList(customersItem.getModel(), secondMailingAddressId, ImmutableList.of(ADMINISTRATIVE)));
        clearCustomerModel3.setEmails(setUpEmailsList(customersItem.getModel()));
        ResponseContainer<CustomerModel> customerModelResponseContainer3 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel3);
        checkResponseAndErrorMessage(customerModelResponseContainer3);

        LOGGER.info("REN-43698 TC#2 Step 3");
        CustomerModel clearCustomerModel4 = createClearCustomerModel();
        clearCustomerModel4.setAddresses(setUpAddressesList(customersItem.getModel()));
        clearCustomerModel4.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(CLAIMS)));
        ResponseContainer<CustomerModel> customerModelResponseContainer4 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel4);
        checkResponseAndErrorMessage(customerModelResponseContainer4);

        LOGGER.info("REN-43698 TC#2 Step 4");
        CustomerModel clearCustomerModel5 = createClearCustomerModel();
        clearCustomerModel5.setAddresses(setUpAddressesList(customersItem.getModel()));
        clearCustomerModel5.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer5 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel5);
        checkResponseAndErrorMessage(customerModelResponseContainer5);

        LOGGER.info("REN-43698 TC#2 Step 5");
        CustomerModel clearCustomerModel6 = createClearCustomerModel();
        clearCustomerModel6.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS, ADMINISTRATIVE)));
        clearCustomerModel6.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(CLAIMS)));
        ResponseContainer<CustomerModel> customerModelResponseContainer6 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel6);
        checkResponseAndErrorMessage(customerModelResponseContainer6);

        LOGGER.info("REN-43698 TC#2 Step 6");
        CustomerModel clearCustomerModel7 = createClearCustomerModel();
        clearCustomerModel7.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(ADMINISTRATIVE)));
        clearCustomerModel7.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(CLAIMS, ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer7 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel7);
        checkResponseAndErrorMessage(customerModelResponseContainer7);

        LOGGER.info("REN-43698 TC#3 Step 1");
        CustomerModel clearCustomerModel8 = createClearCustomerModel();
        clearCustomerModel8.setAddresses
                (setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS), secondMailingAddressId, ImmutableList.of(ADMINISTRATIVE)));
        clearCustomerModel8.setEmails(setUpEmailsList(customersItem.getModel()));
        ResponseContainer<CustomerModel> customerModelResponseContainer8 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel8);
        checkResponseAndErrorMessage(customerModelResponseContainer8);

        LOGGER.info("REN-43698 TC#3 Step 2");
        CustomerModel clearCustomerModel9 = createClearCustomerModel();
        clearCustomerModel9.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS)));
        clearCustomerModel9.setEmails(setUpEmailsList(customersItem.getModel(), emailWorkId, ImmutableList.of(ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer9 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel9);
        checkResponseAndErrorMessage(customerModelResponseContainer9);

        LOGGER.info("REN-43698 TC#3 Step 3");
        CustomerModel clearCustomerModel10 = createClearCustomerModel();
        clearCustomerModel10.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS)));
        clearCustomerModel10.setEmails(setUpEmailsList(customersItem.getModel(), emailNullId, ImmutableList.of(ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer10 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel10);
        checkResponseAndErrorMessage(customerModelResponseContainer10);

        LOGGER.info("REN-43698 TC#3 Step 4");
        CustomerModel clearCustomerModel11 = createClearCustomerModel();
        clearCustomerModel11.setAddresses(setUpAddressesList(customersItem.getModel(), residenceAddressId, ImmutableList.of(CLAIMS)));
        clearCustomerModel11.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer11 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel11);
        checkResponseAndErrorMessage(customerModelResponseContainer11);

        LOGGER.info("REN-43698 TC#3 Step 5");
        CustomerModel clearCustomerModel12 = createClearCustomerModel();
        clearCustomerModel12.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS)));
        clearCustomerModel12.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer12 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel12);
        CustomAssertions.assertThat(customerModelResponseContainer12.getResponse().getStatus()).isEqualTo(200);

        LOGGER.info("REN-43698 TC#3 Step 6");
        CustomerModel clearCustomerModel13 = createClearCustomerModel();
        clearCustomerModel13.setAddresses(setUpAddressesList(customersItem.getModel(), firstMailingAddressId, ImmutableList.of(CLAIMS, ADMINISTRATIVE)));
        clearCustomerModel13.setEmails(setUpEmailsList(customersItem.getModel()));
        ResponseContainer<CustomerModel> customerModelResponseContainer13 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel13);
        CustomAssertions.assertThat(customerModelResponseContainer13.getResponse().getStatus()).isEqualTo(200);

        LOGGER.info("REN-43698 TC#3 Step 7");
        CustomerModel clearCustomerModel14 = createClearCustomerModel();
        clearCustomerModel14.setAddresses(setUpAddressesList(customersItem.getModel()));
        clearCustomerModel14.setEmails(setUpEmailsList(customersItem.getModel(), emailPersId, ImmutableList.of(CLAIMS, ADMINISTRATIVE)));
        ResponseContainer<CustomerModel> customerModelResponseContainer14 = renCustomerRestService.updatesContactsForCustomer(customerId, clearCustomerModel14);
        CustomAssertions.assertThat(customerModelResponseContainer14.getResponse().getStatus()).isEqualTo(200);
    }

    private void checkResponseAndErrorMessage(ResponseContainer<CustomerModel> customerModelResponseContainer3) {
        assertSoftly(softly -> {
            softly.assertThat(customerModelResponseContainer3.getModel().getErrorCode()).isEqualTo("422");
            softly.assertThat(customerModelResponseContainer3.getModel().getErrors().get(0).getMessage()).isEqualTo("(1) Each of the two communication preferences types - i.e. 'Claims' and 'Administrative' must be used at least once and no more than once on an individual customer.(example #1 - 'Claims' is set on Address 123 main street while 'Administrative' is set neither on the Mailing address nor on Email address is NOT a valid case;example #2 'Administrative' is set on Address 123 main street and on xxx@yyy.com is NOT a valid case).(2) Two communication preferences types i.e. 'Claims' and 'Administrative' can not be distributed between 2 Mailing addresses(example #1 - 'Claims' is set on Address 123 main street and 'Administrative' is set on Address 456 main street is NOT a valid case(3) Only one Email address for each type is allowed for an individual customer (\"Personal\" to be used for INDV as Member/Participant)");
        });
    }

    private List<ContactMethodEmailModel> getListEmails(CustomerModel model) {
        List<ContactMethodEmailModel> contactMethodEmailModelList = new ArrayList<>();
        model.getEmails().forEach(email -> {
            ContactMethodEmailModel contactMethodEmailModel = new ContactMethodEmailModel();
            contactMethodEmailModel.setId(email.getId());
            contactMethodEmailModel.setCommunicationPreferences(email.getCommunicationPreferences());
            contactMethodEmailModelList.add(contactMethodEmailModel);
        });
        return contactMethodEmailModelList;
    }

    private List<ContactMethodAddressModel> getListAddresses(CustomerModel customerModel) {
        List<ContactMethodAddressModel> contactMethodAddressModelList = new ArrayList<>();
        customerModel.getAddresses().forEach(address -> {
            ContactMethodAddressModel contactMethodAddressModel = new ContactMethodAddressModel();
            contactMethodAddressModel.setId(address.getId());
            contactMethodAddressModel.setCommunicationPreferences(address.getCommunicationPreferences());
            contactMethodAddressModelList.add(contactMethodAddressModel);
        });
        return contactMethodAddressModelList;
    }

    private List<ContactMethodEmailModel> setUpEmailsList(CustomerModel customerModelOriginal, int id, List<String> values) {
        List<ContactMethodEmailModel> contactMethodEmailModelList = new ArrayList<>();
        for (int i = 0; i < customerModelOriginal.getEmails().size(); i++) {
            ContactMethodEmailModel contactMethodEmailModel = new ContactMethodEmailModel();
            contactMethodEmailModel.setId(customerModelOriginal.getEmails().get(i).getId());
            contactMethodEmailModelList.add(contactMethodEmailModel);
        }
        contactMethodEmailModelList.stream().filter(p -> p.getId().equals(id)).findFirst().get().setCommunicationPreferences(values);
        return contactMethodEmailModelList;
    }

    private List<ContactMethodEmailModel> setUpEmailsList(CustomerModel customerModelOriginal) {
        List<ContactMethodEmailModel> contactMethodEmailModelList = new ArrayList<>();
        for (int i = 0; i < customerModelOriginal.getEmails().size(); i++) {
            ContactMethodEmailModel contactMethodEmailModel = new ContactMethodEmailModel();
            contactMethodEmailModel.setId(customerModelOriginal.getEmails().get(i).getId());
            contactMethodEmailModelList.add(contactMethodEmailModel);
        }
        return contactMethodEmailModelList;
    }

    private List<ContactMethodAddressModel> setUpAddressesList(CustomerModel customerModelOriginal, int id, List<String> values) {
        List<ContactMethodAddressModel> contactMethodAddressModelList = new ArrayList<>();
        for (int i = 0; i < customerModelOriginal.getAddresses().size(); i++) {
            ContactMethodAddressModel contactMethodAddressModel = new ContactMethodAddressModel();
            contactMethodAddressModel.setId(customerModelOriginal.getAddresses().get(i).getId());
            contactMethodAddressModelList.add(contactMethodAddressModel);
        }
        contactMethodAddressModelList.stream().filter(p -> p.getId().equals(id)).findFirst().get().setCommunicationPreferences(values);
        return contactMethodAddressModelList;
    }

    private List<ContactMethodAddressModel> setUpAddressesList(CustomerModel customerModelOriginal, int id1, List<String> values1, int id2, List<String> values2) {
        List<ContactMethodAddressModel> contactMethodAddressModelList = new ArrayList<>();
        for (int i = 0; i < customerModelOriginal.getAddresses().size(); i++) {
            ContactMethodAddressModel contactMethodAddressModel = new ContactMethodAddressModel();
            contactMethodAddressModel.setId(customerModelOriginal.getAddresses().get(i).getId());
            contactMethodAddressModelList.add(contactMethodAddressModel);
        }
        contactMethodAddressModelList.stream().filter(p -> p.getId().equals(id1)).findFirst().get().setCommunicationPreferences(values1);
        contactMethodAddressModelList.stream().filter(p -> p.getId().equals(id2)).findFirst().get().setCommunicationPreferences(values2);
        return contactMethodAddressModelList;
    }

    private List<ContactMethodAddressModel> setUpAddressesList(CustomerModel customerModelOriginal) {
        List<ContactMethodAddressModel> contactMethodAddressModelList = new ArrayList<>();
        for (int i = 0; i < customerModelOriginal.getAddresses().size(); i++) {
            ContactMethodAddressModel contactMethodAddressModel = new ContactMethodAddressModel();
            contactMethodAddressModel.setId(customerModelOriginal.getAddresses().get(i).getId());
            contactMethodAddressModelList.add(contactMethodAddressModel);
        }
        return contactMethodAddressModelList;
    }

    private CustomerModel createClearCustomerModel() {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setCustomerStatus(null);
        customerModel.setSourceCd(null);
        customerModel.setRatingCd(null);
        customerModel.setPreferredSpokenLanguageCd(null);
        customerModel.setPreferredWrittenLanguageCd(null);
        customerModel.setPaperless(null);
        return customerModel;
    }

}
