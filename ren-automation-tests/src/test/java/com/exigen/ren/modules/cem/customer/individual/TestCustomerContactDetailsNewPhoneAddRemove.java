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
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerContactDetailsNewPhoneAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24865", component = CRM_CUSTOMER)
    public void testCustomerContactDetailsNewPhoneAddRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add New Contats Details for Customer # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS);
        customerIndividual.addNewContactsDetails().perform(tdCustomerIndividual.getTestData("ContactsDetails", "TestData"));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS);

        assertThat(CustomerSummaryPage.tableCustomerContacts.getRow(3).getCell(CustomerConstants.CustomerContactsTable.CONTACT_TYPE.getName()))
                .hasValue(tdCustomerIndividual.getTestData("ContactsDetails", "TestData", generalTab.getMetaKey(),
                        GeneralTabMetaData.PHONE_DETAILS.getLabel())
                        .getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_TYPE.getLabel()));

        LOGGER.info("TEST: Remove New Contats Details for Customer # " + customerNumber);
        customerIndividual.removeNewContactsDetails().perform(3);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS);

        assertThat(CustomerSummaryPage.tableCustomerContacts.getColumn(4).getValue())
                .doesNotContain(tdCustomerIndividual.getTestData("ContactsDetails", "TestData",
                        generalTab.getMetaKey(), GeneralTabMetaData.PHONE_DETAILS.getLabel())
                        .getValue(GeneralTabMetaData.PhoneDetailsMetaData.PHONE_TYPE.getLabel()));
    }
}
