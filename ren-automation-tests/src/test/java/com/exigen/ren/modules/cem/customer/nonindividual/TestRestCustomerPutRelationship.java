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
import com.exigen.ren.rest.customer.model.RelationshipModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPutRelationship extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26417", component = CRM_CUSTOMER)
    public void testRestCustomerPutRelationship() {

        LOGGER.info("STEP: Creating customer with relationships");
        mainApp().open();
        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData_WithRelationshipTypes"));
        String customerNumber = Customer.CustomerData.getCustomerNumber();

        List<RelationshipModel> relationshipModels = customerRestClient.getRelationships(customerNumber).getModel();

        LOGGER.info("TEST: PUT/customers/{customerNumber}/relationships/{relationshipId}");
        putRelationship(customerNumber, relationshipModels.get(0), "Other");
        putRelationship(customerNumber, relationshipModels.get(1), "Partner");

        LOGGER.info("TEST: Relationships Are Updated");
        MainPage.QuickSearch.search(Customer.CustomerData.getCustomerNumber());
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        List<String> actualRelationships = CustomerSummaryPage.getRelationshipsSectionsNames();
        assertThat(actualRelationships).hasSize(2);
        assertThat(actualRelationships.stream().filter(text -> text.contains("Individual") && text.contains("Other"))).hasSize(1);
        assertThat(actualRelationships.stream().filter(text -> text.contains("Non-Individual") && text.contains("Partner"))).hasSize(1);
    }

    private void putRelationship(String customerNumber, RelationshipModel relationshipModel, String role) {
        relationshipModel.setRelationshipRole(role);
        assertThat(customerRestClient.putRelationships(customerNumber, Integer.toString(relationshipModel.getId()), relationshipModel).getResponse()).hasStatus(200);
    }
}