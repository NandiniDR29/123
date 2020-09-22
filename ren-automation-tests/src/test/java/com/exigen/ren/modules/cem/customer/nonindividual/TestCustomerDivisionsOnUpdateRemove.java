/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.DivisionsTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerDivisionsOnUpdateRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24874", component = CRM_CUSTOMER)
    public void testCustomerDivisionsOnUpdateRemove() {
        mainApp().open();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")).resolveLinks());

        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        assertThat(CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD))
                .hasValue(tdCustomerNonIndividual.getTestData("DataGather").getTestData("Adjustment_Divisions").getTestData("DivisionsTab")
                        .getValue(DivisionsTabMetaData.BILLING_METHOD.getLabel()));

        LOGGER.info("TEST: Remove Divisions for Customer # " + customerNumber);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData"));

        assertThat(CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD))
                .hasValue(tdSpecific().getTestData("TestData").getTestData("DivisionsTab").getValue(DivisionsTabMetaData.BILLING_METHOD.getLabel()));

        LOGGER.info("TEST: Remove Divisions for Customer # " + customerNumber);
        customerNonIndividual.removeDivisions().perform();

        assertThat(CustomerSummaryPage.tableDivisions).isAbsent();
    }
}
