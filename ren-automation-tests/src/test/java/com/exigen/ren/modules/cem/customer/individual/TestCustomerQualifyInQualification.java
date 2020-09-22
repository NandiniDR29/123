/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerQualifyInQualification extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24908", component = CRM_CUSTOMER)
    public void testCustomerQualifyInQualification() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData_WithoutBusinessEntityTab").adjust(
                tdCustomerIndividual.getTestData("DataGather", "Adjustment_InQualification")));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        assertThat(CustomerSummaryPage.labelCustomerLeadStatus).hasValue(CustomerConstants.IN_QUALIFICATION);

        LOGGER.info("TEST: Qualify Customer # " + customerNumber);
        customerIndividual.qualify().perform();
        assertThat(CustomerSummaryPage.labelCustomerLeadStatus).hasValue(CustomerConstants.QUALIFIED);
    }
}
