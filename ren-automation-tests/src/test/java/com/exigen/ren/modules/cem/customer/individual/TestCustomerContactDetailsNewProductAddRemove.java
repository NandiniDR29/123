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

public class TestCustomerContactDetailsNewProductAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24866", component = CRM_CUSTOMER)
    public void testCustomerContactDetailsNewProductAddRemove() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add New Product Details for Customer # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customerIndividual.addNewContactsDetails().perform(tdCustomerIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_NewProduct")));

        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.buttonAddNewContactsDetails.click();

        assertThat(CustomerSummaryPage.tableNewProductDetails.getRow(1)
                .getCell(CustomerConstants.CustomerNewProductDetailsTable.POLICY_TYPE))
                .hasValue(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_NewProduct",
                        GeneralTab.class.getSimpleName(), GeneralTabMetaData.ADD_NEW_PRODUCT_DETAILS.getLabel())
                        .getValue(GeneralTabMetaData.AddNewProductDetailsMetaData.POLICY_TYPE.getLabel()));

        LOGGER.info("TEST: Remove New Product Details for Customer # " + customerNumber);
        customerIndividual.removeNewProductDetails().perform(1);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.buttonAddNewContactsDetails.click();
        assertThat(CustomerSummaryPage.tableNewProductDetails).isAbsent();
    }
}
