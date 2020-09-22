/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab.ACCOUNT;
import static com.exigen.ren.common.pages.NavigationPage.toMainTab;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestGetCustomerDetails extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26328", component = CRM_CUSTOMER)
    public void testRestGetCustomerDetails() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        String customerStatus = CustomerSummaryPage.labelLeadStatus.getValue();

        LOGGER.info("Created Non-Individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        toMainTab(ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();

        LOGGER.info("TEST: Check GET/customers/details Response ");
        ResponseContainer<List<CustomerModel>> customerDetailsResponse = customerRestClient.getCustomerDetails(Collections.singletonList(customerNumber));
        assertThat(customerDetailsResponse.getResponse().getStatus()).isEqualTo(200);

        CustomerModel customerDetails = customerDetailsResponse.getModel().get(0);
        assertSoftly(softly -> {
            softly.assertThat(customerDetails.getCustomerNumber()).isEqualTo(customerNumber);
            softly.assertThat(customerDetails.getDisplayValue()).isEqualTo(customerName);
            softly.assertThat(customerDetails.getAccountNumber()).isEqualTo(accountNumber);
            softly.assertThat(customerDetails.getCustomerStatus()).isEqualToIgnoringCase(customerStatus);
            softly.assertThat(customerDetails.getBusinessDetails().getGroupSponsorInd()).isEqualTo(true);
        });
    }
}
