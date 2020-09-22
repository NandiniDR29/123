/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerUpdate extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24918", component = CRM_CUSTOMER)
    public void testCustomerUpdate() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Update Customer # " + customerNumber);
        customerIndividual.update().perform(tdSpecific().getTestData("TestData"));
        assertThat(CustomerSummaryPage.labelCustomerName.getValue()).isEqualToIgnoringWhitespace(getUpdatedLastNameFirstName());
    }

    private String getUpdatedLastNameFirstName() {
        return tdSpecific().getTestData("TestData").getValue(GeneralTab.class.getSimpleName(), GeneralTabMetaData.FIRST_NAME.getLabel())
                + tdSpecific().getTestData("TestData").getValue(GeneralTab.class.getSimpleName(), GeneralTabMetaData.LAST_NAME.getLabel());
    }
}
