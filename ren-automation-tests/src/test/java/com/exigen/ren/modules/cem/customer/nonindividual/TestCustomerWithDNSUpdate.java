/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerWithDNSUpdate extends CustomerBaseTest {
    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26427", component = CRM_CUSTOMER)
    public void testCustomerWithDNSUpdate() {
        mainApp().open();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.ADDRESS_DETAILS.getLabel()),
                        tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Address_WithDNS")));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        assertThat(CustomerSummaryPage.labelCustomerAddress).valueContains("(DNS)");

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerNonIndividual.update().perform(tdCustomerNonIndividual.getTestData("DataGather", "TestData_Update"));
        LOGGER.info("Updated Customer # " + customerNumber);

        assertThat(CustomerSummaryPage.labelCustomerAddress).valueContains("(DNS)");
    }
}
