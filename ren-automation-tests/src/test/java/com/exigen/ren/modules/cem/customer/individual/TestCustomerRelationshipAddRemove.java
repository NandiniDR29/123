/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerRelationshipAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24912", component = CRM_CUSTOMER)
    public void testCustomerRelationshipAddRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        LOGGER.info("TEST: Add Relationship for Customer # " + customerNumber);
        customerIndividual.addRelationshipContact().perform(tdCustomerIndividual.getTestData("DataGather", "Adjustment_Relationship"));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        assertThat(CustomerSummaryPage.tableRelationshipResult).hasRows(1);
        LOGGER.info("TEST: Remove Relationship for Customer # " + customerNumber);
        customerIndividual.removeRelationshipContact().perform();

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        assertThat(CustomerSummaryPage.tableRelationshipResult).isAbsent();
    }
}
