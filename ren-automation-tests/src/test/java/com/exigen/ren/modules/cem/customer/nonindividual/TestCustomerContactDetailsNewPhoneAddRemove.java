/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
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

public class TestCustomerContactDetailsNewPhoneAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24865", component = CRM_CUSTOMER)
    public void testCustomerContactDetailsNewPhoneAddRemove() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add New Contats Details for Customer # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customerNonIndividual.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData"));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts.getRow(3).getCell(CustomerConstants.CustomerContactsTable.CONTACT_TYPE.getName()))
                .hasValue(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData", GeneralTab.class.getSimpleName(),
                        GeneralTabMetaData.PHONE_DETAILS.getLabel()).getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_TYPE.getLabel()));

        LOGGER.info("TEST: Remove New Contats Details for Customer # " + customerNumber);
        customerNonIndividual.removeNewContactsDetails().perform(3);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts.getColumn(4).getValue())
                .doesNotContain(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData", GeneralTab.class.getSimpleName(),
                        GeneralTabMetaData.PHONE_DETAILS.getLabel()).getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_TYPE.getLabel()));
    }
}
