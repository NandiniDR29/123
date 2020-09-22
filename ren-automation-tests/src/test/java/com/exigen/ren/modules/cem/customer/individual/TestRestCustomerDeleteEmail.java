/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerDeleteEmail extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26382", component = CRM_CUSTOMER)
    public void testRestCustomerDeleteEmail() {

        LOGGER.info("STEP: Creating customer with email");
        mainApp().open();
        CustomerModel createdCustomer = customerRestClient.postCustomers(tdCustomerIndividual.getTestData("DataGather", "TestData_withPhoneEmail"));
        LOGGER.info("Created individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER) + " with email contact");

        LOGGER.info("TEST: Check DELETE/customers/{customerNumber}/email/{contactId} response");
        assertThat(customerRestClient.deleteCustomersEmailItem(createdCustomer.getCustomerNumber(), createdCustomer.getEmails().get(0).getId())).hasStatus(204);

        LOGGER.info("TEST: Check email is deleted");
        MainPage.QuickSearch.search(createdCustomer.getCustomerNumber());
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        assertThat(CustomerSummaryPage.tableCustomerContacts).hasMatchingRows(0, ImmutableMap.of("Contact Method", "Email"));
    }
}
