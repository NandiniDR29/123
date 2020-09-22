/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.BusinessEntityTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.BusinessEntityTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerBusinessEntityAddRemoveWhileCreating extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24834", component = CRM_CUSTOMER)
    public void testCustomerBusinessEntityAddRemoveWhileCreating() {
        TestData dateUpdate = tdCustomerIndividual.getTestData("DataGather", "Adjustment_BusinessEntity").resolveLinks();
        String newDate = dateUpdate.getValue(BusinessEntityTab.class.getSimpleName(), BusinessEntityTabMetaData.DATE_BUSINESS_STARTED.getLabel());
        dateUpdate.adjust(TestData.makeKeyPath(BusinessEntityTab.class.getSimpleName(), BusinessEntityTabMetaData.DATE_BUSINESS_STARTED.getLabel()), newDate);

        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData").adjust(dateUpdate).resolveLinks());

        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Update Customer # " + customerNumber);
        assertThat(CustomerSummaryPage.tableBusinessEntities.getRow(1).getCell(CustomerConstants.CustomerBusinessEntitiesTable.DATE_BUSINESS_STARTED))
                .hasValue(newDate);

        customerIndividual.removeBusinessEntity().perform();
        assertThat(CustomerSummaryPage.tableBusinessEntities.getRow(1)).isAbsent();
    }
}
