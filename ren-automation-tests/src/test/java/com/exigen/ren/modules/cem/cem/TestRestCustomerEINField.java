/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerEINField extends RestBaseTest {

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-19107", component = CUSTOMER_REST)
    public void testRestCustomerEINField() {
        assertSoftly(softly -> {

            mainApp().open();
            createDefaultNonIndividualCustomer();
            String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
            ResponseContainer<CustomerModel> customerResponse = customerRestClient.getCustomersItem(customerId);
            softly.assertThat(customerResponse.getResponse().getStatus()).isEqualTo(200);

            LOGGER.info("---=={Step 4}==---");
            CustomerModel customer = customerResponse.getModel();
            customer.getBusinessDetails().setGroupSponsorInd(false);
            customer.getBusinessDetails().setLegalId("");

            customerResponse = customerRestClient.postCustomer(customer);
            softly.assertThat(customerResponse.getResponse().getStatus()).isEqualTo(422);
            softly.assertThat(customerResponse.getModel().getErrors().get(0).getMessage()).isEqualTo("EIN is required");
            softly.assertThat(customerResponse.getModel().getErrors().get(0).getField()).isEqualTo("legalId");

            LOGGER.info("---=={Step 5}==---");
            customer.getBusinessDetails().setGroupSponsorInd(true);
            customerResponse = customerRestClient.postCustomer(customer);
            CustomerModel newCustomer = customerResponse.getModel();
            softly.assertThat(customerResponse.getResponse().getStatus()).isEqualTo(200);

            customerNonIndividual.update().start();
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.GROUP_SPONSOR)).hasValue(true);

            LOGGER.info("---=={Step 8-9}==---");
            String newCustomerId = newCustomer.getCustomerNumber();
            newCustomer.getBusinessDetails().setLegalName("TestDisplayValue");

            customerResponse = customerRestClient.putNonIndividualCustomer(newCustomerId, newCustomer);
            softly.assertThat(customerResponse.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(customerResponse.getModel().getDisplayValue()).isEqualTo("TestDisplayValue");

            LOGGER.info("---=={Step 10}==---");
            newCustomer.getBusinessDetails().setGroupSponsorInd(false);
            newCustomer.setDisplayValue("TestDisplayValueUpdate");
            customerResponse = customerRestClient.postCustomer(newCustomer);
            softly.assertThat(customerResponse.getResponse().getStatus()).isEqualTo(422);
            softly.assertThat(customerResponse.getModel().getErrors().get(0).getMessage()).isEqualTo("EIN is required");
            softly.assertThat(customerResponse.getModel().getErrors().get(0).getField()).isEqualTo("legalId");
        });
    }
}
