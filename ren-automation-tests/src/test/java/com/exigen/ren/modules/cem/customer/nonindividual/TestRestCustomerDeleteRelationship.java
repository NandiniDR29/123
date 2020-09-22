/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.Customer;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.RelationshipModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerDeleteRelationship extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26393", component = CRM_CUSTOMER)
    public void testRestCustomerDeleteRelationship() {

        LOGGER.info("STEP: Creating customer with relationships");
        mainApp().open();
        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData_WithRelationshipTypes"));

        LOGGER.info("TEST: Check DELETE/customers/{customerNumber}/relationships/{relationshipId} response and result for individual relationship");
        ResponseContainer<List<RelationshipModel>> relationShipsResponse = customerRestClient.getRelationships(Customer.CustomerData.getCustomerNumber());
        deleteAndAssert(relationShipsResponse, 0, 1);
        assertThat(CustomerSummaryPage.getRelationshipsSectionsNames()).allMatch(text -> text.contains("Non-Individual"));

        LOGGER.info("TEST: Check DELETE/customers/{customerNumber}/relationships/{relationshipId} response and result for nonindividual relationship");
        deleteAndAssert(relationShipsResponse, 1, 0);
    }

    private void deleteAndAssert(ResponseContainer<List<RelationshipModel>> relationShipsResponse, int numToDelete, int countOfLeftovers) {
        assertThat(customerRestClient.deleteRelationship(Customer.CustomerData.getCustomerNumber(), relationShipsResponse.getModel().get(numToDelete).getId())).hasStatus(204);
        MainPage.QuickSearch.search(Customer.CustomerData.getCustomerNumber());
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        List<String> actualRelationships = CustomerSummaryPage.getRelationshipsSectionsNames();
        assertThat(actualRelationships).hasSize(countOfLeftovers);
    }
}