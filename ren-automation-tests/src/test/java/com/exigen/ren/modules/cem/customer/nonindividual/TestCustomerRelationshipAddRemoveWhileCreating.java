/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

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

public class TestCustomerRelationshipAddRemoveWhileCreating extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24913", component = CRM_CUSTOMER)
    public void testCustomerRelationshipAddRemoveWhileCreating() {
        mainApp().open();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Relationship").resolveLinks()));

        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        LOGGER.info("TEST: Remove Relationship for Customer # " + customerNumber);
        customerNonIndividual.removeRelationshipContact().perform();

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        assertThat(CustomerSummaryPage.tableRelationshipResult).isAbsent();
    }
}
