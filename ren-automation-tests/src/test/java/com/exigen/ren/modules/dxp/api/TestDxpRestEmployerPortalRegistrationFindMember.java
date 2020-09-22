/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.RegistrationMembers;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EMAIl_DETAILS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.PHONE_DETAILS;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestEmployerPortalRegistrationFindMember extends RestBaseTest implements CustomerContext {


    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22432", component = CUSTOMER_REST)
    public void testDxpRestEmployerPortalRegistrationFindMember() {

        mainApp().open();
        String ssnForFirstCustomer = RandomStringUtils.randomNumeric(8);
        String ssnForSecondCustomer = RandomStringUtils.randomNumeric(8);

        customerIndividual.createViaUI(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.SSN_TAX_IDENTIFICATION.getLabel()), ssnForFirstCustomer));
        String firstName = getStoredValue("FirstName_REN_22432");
        String lastName = getStoredValue("LastName_REN_22432");
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerIndividual.createViaUI(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.SSN_TAX_IDENTIFICATION.getLabel()), ssnForSecondCustomer));

        TestData td = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY, GeneralTab.class.getSimpleName());
        String email = td.getTestData(EMAIl_DETAILS.getLabel()).getValue(GeneralTabMetaData.EmailDetailsMetaData.EMAIL_ADDRESS.getLabel());
        String firstPhone = td.getTestDataList(PHONE_DETAILS.getLabel()).get(0).getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_NUMBER.getLabel());
        String secondPhone = td.getTestDataList(PHONE_DETAILS.getLabel()).get(1).getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_NUMBER.getLabel());

        ResponseContainer<List<RegistrationMembers>> response = dxpRestService.getEmployerRegistrationMembers(customerNum, firstName, lastName, email, secondPhone, ssnForFirstCustomer, null);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);

        List<RegistrationMembers> model1 = response.getModel();
        assertThat(model1.size()).isEqualTo(1);
        assertSoftly(softly -> {
            softly.assertThat(model1.get(0).getFirstName()).isEqualTo(firstName);
            softly.assertThat(model1.get(0).getLastName()).isEqualTo(lastName);
            softly.assertThat(model1.get(0).getMiddleName()).isNotNull();
            softly.assertThat(model1.get(0).getEmails().get(0).getEmail()).isEqualTo(email);
            softly.assertThat(model1.get(0).getPhones().get(1).getPhone()).isEqualTo(secondPhone);
        });

        ResponseContainer<List<RegistrationMembers>> response2 = dxpRestService.getEmployerRegistrationMembers(customerNum, firstName, lastName, email, firstPhone, null, null);
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);

        List<RegistrationMembers> model2 = response2.getModel();
        assertThat(model2.size()).isEqualTo(2);

        assertSoftly(softly -> {
            softly.assertThat(model2.get(0).getFirstName()).isEqualTo(firstName);
            softly.assertThat(model2.get(0).getLastName()).isEqualTo(lastName);
            softly.assertThat(model2.get(0).getMiddleName()).isNotNull();
            softly.assertThat(model2.get(0).getEmails().get(0).getEmail()).isEqualTo(email);
            softly.assertThat(model2.get(0).getPhones().get(0).getPhone()).isEqualTo(firstPhone);

            softly.assertThat(model2.get(1).getFirstName()).isEqualTo(firstName);
            softly.assertThat(model2.get(1).getLastName()).isEqualTo(lastName);
            softly.assertThat(model2.get(1).getMiddleName()).isNotNull();
            softly.assertThat(model2.get(1).getEmails().get(0).getEmail()).isEqualTo(email);
            softly.assertThat(model2.get(1).getPhones().get(0).getPhone()).isEqualTo(firstPhone);
            softly.assertThat(model2.get(1).getMiddleName()).isNotEqualTo(model2.get(0).getMiddleName());
        });
    }
}
