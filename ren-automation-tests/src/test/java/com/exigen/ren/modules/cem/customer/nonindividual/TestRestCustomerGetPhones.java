/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.ContactMethodPhoneModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetPhones extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26394", component = CRM_CUSTOMER)
    public void testRestCustomerGetPhones() {

        LOGGER.info("STEP: Creating customer with phone data");
        mainApp().open();
        TestData testData = tdCustomerNonIndividual.getTestData("DataGather", "TestData");
        customerNonIndividual.create(testData);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        LOGGER.info("Created Individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        LOGGER.info("TEST: Check GET/customers/phones response ");
        ResponseContainer<List<ContactMethodPhoneModel>> phonesResponse = customerRestClient.getCustomersPhonesItem(customerNumber);
        CustomAssertions.assertThat(phonesResponse.getResponse()).hasStatus(200);
        ContactMethodPhoneModel contactMethodPhoneModel = phonesResponse.getModel().get(0);
        TestData phoneData = testData.getTestData("GeneralTab").getTestData("Phone Details");
        assertSoftly(softly -> {
            softly.assertThat(contactMethodPhoneModel.getContactType()).isEqualTo("BMOB");
            softly.assertThat(contactMethodPhoneModel.getPhoneNumber()).isEqualTo(phoneData.getValue("Phone Number"));
            softly.assertThat(contactMethodPhoneModel.getConsentStatus()).isEqualTo(ConsentStatus.NOT_REQUESTED.name());
            softly.assertThat(contactMethodPhoneModel.getContactMethod()).isEqualTo(ContactMethodType.PHONE.name());
            softly.assertThat(contactMethodPhoneModel.getDoNotSolicitInd()).isFalse();
        });
    }
}
