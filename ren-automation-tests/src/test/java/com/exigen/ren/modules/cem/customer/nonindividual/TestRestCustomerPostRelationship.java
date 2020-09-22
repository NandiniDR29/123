/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.exigen.ren.rest.customer.model.RelationshipModel;
import org.testng.annotations.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPostRelationship extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26521", component = CRM_CUSTOMER)
    public void testRestCustomerPostRelationship() {

        LOGGER.info("STEP: Creating customers");
        mainApp().open();
        CustomerModel createdCustomer = customerRestClient.postCustomers(tdCustomerNonIndividual.getTestData("DataGather", TEST_DATA_KEY));
        CustomerModel unQualifiedCustomer = customerRestClient.postCustomers(tdCustomerNonIndividual.getTestData("DataGather", TEST_DATA_KEY).adjust(
                customerNonIndividual.getDefaultTestData("DataGather", "Adjustment_Unqualified")));
        TestData relationShipData = customerNonIndividual.getDefaultTestData("DataGather", "TestData_WithRelationshipTypes")
                .getTestDataList(RelationshipTab.class.getSimpleName()).get(0);
        RelationshipModel relationshipModel = new RelationshipModel(relationShipData);
        relationshipModel.setRelationshipDescription(relationShipData.getValue(RelationshipTabMetaData.RELATIONSHIP_DESCRIPTION.getLabel()));
        relationshipModel.setRelationshipCustomerNumber(unQualifiedCustomer.getCustomerNumber());

        LOGGER.info("TEST: Check POST/customers/{customerNumber}/relationships/ response and content");
        Response response = customerRestClient.postRelationship(createdCustomer.getCustomerNumber(), relationshipModel);
        assertThat(response).hasStatus(200);
        assertThat(response.readEntity(new GenericType<RelationshipModel>() {
            })).isEqualToIgnoringGivenFields(relationshipModel, "id", "extensionFields");

        LOGGER.info("TEST: Check relationship is added");
        MainPage.QuickSearch.search(createdCustomer.getCustomerNumber());
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        List<String> actualRelationships = CustomerSummaryPage.getRelationshipsSectionsNames();
        assertThat(actualRelationships).hasSize(1);
        assertThat(CustomerSummaryPage.getRelationshipsSectionsNames()).allMatch(text -> text.contains(unQualifiedCustomer.getDisplayValue()))
                .allMatch(text -> text.contains(relationshipModel.getUIRelationshipRole()));
    }
}
