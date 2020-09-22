/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.DivisionsTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.DivisionsTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerDivisionsOnUpdateAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24873", component = CRM_CUSTOMER)
    public void testCustomerDivisionsOnUpdateAddRemove() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Divisions for Customer # " + customerNumber);
        customerNonIndividual.update().perform(customerNonIndividual.getDefaultTestData("Update", "TestData")
                .adjust(customerNonIndividual.getDefaultTestData("DataGather", "Adjustment_Divisions")));

        assertThat(CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD))
                .hasValue(tdCustomerNonIndividual.getTestData("DataGather").getTestData("Adjustment_Divisions").getTestData(DivisionsTab.class.getSimpleName())
                        .getValue(DivisionsTabMetaData.BILLING_METHOD.getLabel()));

        LOGGER.info("TEST: Remove Divisions for Customer # " + customerNumber);
        customerNonIndividual.removeDivisions().perform();

        assertThat(CustomerSummaryPage.tableDivisions).isAbsent();
    }
}
