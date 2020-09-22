/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerRelationshipUpdate extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24914", component = CRM_CUSTOMER)
    public void testCustomerRelationshipUpdate() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        LOGGER.info("TEST: Add Relationship for Customer # " + customerNumber);
        customerNonIndividual.addRelationshipContact().perform(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Relationship"));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        LOGGER.info("TEST: Update Relationship for Customer # " + customerNumber);
        customerNonIndividual.updateRelationshipContact().perform(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipUpdate"));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.buttonEditRelationship.click();

        assertThat(RelationshipTab.comboBoxRelationshipRole).hasValue(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipUpdate")
                .getTestData(RelationshipTab.class.getSimpleName()).getValue(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()));
    }
}
