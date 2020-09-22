/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerContactDetailsNewAddressAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24864", component = CRM_CUSTOMER)
    public void testCustomerContactDetailsNewAddressAddRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add New Address for Customer # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customerIndividual.addNewContactsDetails().perform(tdCustomerIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_NewAddress")));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts.getRow(2).getCell(CustomerConstants.CustomerContactsTable.CONTACT_TYPE.getName()))
                .hasValue(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_NewAddress",
                        GeneralTab.class.getSimpleName(), GeneralTabMetaData.ADDRESS_DETAILS.getLabel())
                        .getValue(GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_TYPE.getLabel()));

        LOGGER.info("TEST: Remove Address for Customer # " + customerNumber);
        customerIndividual.removeNewContactsDetails().perform(2);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts.getColumn(4).getValue())
                .doesNotContain(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_NewAddress",
                        GeneralTab.class.getSimpleName(), GeneralTabMetaData.ADDRESS_DETAILS.getLabel())
                        .getValue(GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_TYPE.getLabel()));
    }
}
