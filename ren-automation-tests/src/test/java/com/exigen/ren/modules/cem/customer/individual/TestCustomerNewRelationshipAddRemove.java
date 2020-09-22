/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
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

public class TestCustomerNewRelationshipAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24880", component = CRM_CUSTOMER)
    public void testCustomerNewRelationshipAddRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerIndividual.getTestData("DataGather", "Adjustment_Relationship").resolveLinks()));

        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Relationship for Customer # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customerIndividual.addNewRelationshipContacts().perform(tdSpecific().getTestData("TestData_AddNewRelationship"));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        RelationshipTab.linkRelationshipsTogglePanel.click();

        String taxIdentificationValue = tdSpecific().getTestData("TestData_AddNewRelationship")
                .getTestDataList(RelationshipTab.class.getSimpleName()).get(1)
                .getValue(RelationshipTabMetaData.SSN_TAX_IDENTIFICATION.getLabel());

        assertThat(RelationshipTab.labelTaxIdentification).hasValue(taxIdentificationValue);

        LOGGER.info("TEST: Remove New Relationship for Customer # " + customerNumber);
        customerIndividual.removeNewRelationshipContacts().perform(1);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(RelationshipTab.labelTaxIdentification).isAbsent();
    }
}
