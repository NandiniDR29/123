/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.RelationshipModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetRelationships extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26400", component = CRM_CUSTOMER)
    public void testRestCustomerGetRelationships() {

        mainApp().open();

        LOGGER.info("STEP:Create Customer With Two Relationship(Ind and NonInd )");
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData_WithRelationshipTypes"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP:Check GET/customers/{customerNumber}/relationships");
        ResponseContainer<List<RelationshipModel>> response = customerRestClient.getCustomersRelationships(customerNumber);
        assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            List<RelationshipModel> relationshipModelList = response.getModel();
            softly.assertThat(relationshipModelList.get(0).getUIRelationshipRole()).isEqualTo(RelationshipModel.RelationRole.RELATIVE.getUIName());
            softly.assertThat(relationshipModelList.get(0).getRelationshipDescription()).isNull();
            softly.assertThat(relationshipModelList.get(1).getUIRelationshipRole()).isEqualTo(RelationshipModel.RelationRole.EMPLOYER.getUIName());
            softly.assertThat(relationshipModelList.get(1).getRelationshipDescription()).isNull();
        });
    }
}
