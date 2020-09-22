/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.ContactMethodAddressModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.*;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetAddresses extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26399", component = CRM_CUSTOMER)
    public void testRestCustomerGetAddresses() {
        TestData testData = tdCustomerNonIndividual.getTestData("DataGather", "TestData");
        mainApp().open();

        LOGGER.info("STEP:Create Customer With Address Contact");
        customerNonIndividual.create(testData);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP:Check GET/customers/{customerNumber}/addresses");
        ResponseContainer<List<ContactMethodAddressModel>> response = customerRestClient.getCustomersAddressItem(customerNumber);
        TestData td = testData.getTestData(GeneralTab.class.getSimpleName(), GeneralTabMetaData.ADDRESS_DETAILS.getLabel());
        assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            ContactMethodAddressModel contactMethodAddressModel = response.getModel().get(0);
            softly.assertThat(contactMethodAddressModel.getContactMethod()).isEqualTo("ADDRESS");
            softly.assertThat(contactMethodAddressModel.getContactType()).isEqualTo("legal");
            softly.assertThat(contactMethodAddressModel.getCity()).isEqualTo(td.getValue(CITY.getLabel()));
            softly.assertThat(contactMethodAddressModel.getCountryCd()).isEqualTo("US");
            softly.assertThat(contactMethodAddressModel.getStateProvCd()).isEqualTo(td.getValue(STATE_PROVINCE.getLabel()));
            softly.assertThat(contactMethodAddressModel.getPostalCode()).isEqualTo(td.getValue(ZIP_POST_CODE.getLabel()));
            softly.assertThat(contactMethodAddressModel.getAddressLine1()).isEqualTo(CustomerSummaryPage.labelCustomerAddress.getValue().split(",")[0]);
        });
    }
}
