/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerContactsTable.CONTACT_TYPE;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerContactDetailsEmailAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24863", component = CRM_CUSTOMER)
    public void testCustomerContactDetailsEmailAddRemove() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add New email for Customer # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customerNonIndividual.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewEmail")));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts.getRow(2)).hasCellWithValue(CONTACT_TYPE.getName(), tdCustomerNonIndividual.getTestData(
                "ContactsDetails", "Adjustment_NewEmail", GeneralTab.class.getSimpleName(),
                GeneralTabMetaData.EMAIl_DETAILS.getLabel()).getValue(
                GeneralTabMetaData.EmailDetailsMetaData.EMAIL_TYPE.getLabel()));

        LOGGER.info("TEST: Remove email for Customer # " + customerNumber);
        customerNonIndividual.removeNewContactsDetails().perform(2);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts.getColumn(4).getValue()).doesNotContain(tdCustomerNonIndividual.getTestData(
                "ContactsDetails", "Adjustment_NewEmail", GeneralTab.class.getSimpleName(),
                GeneralTabMetaData.EMAIl_DETAILS.getLabel()).getValue(
                GeneralTabMetaData.EmailDetailsMetaData.EMAIL_TYPE.getLabel()));
    }
}
