/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerQualifyUnqualified extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24909", component = CRM_CUSTOMER)
    public void testCustomerQualifyUnqualified() {
        mainApp().open();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData_WithoutDivisionsTab").adjust(
                tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Unqualified")));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        assertThat(CustomerSummaryPage.labelCustomerLeadStatus).hasValue(CustomerConstants.UNQUALIFIED);
        assertThat(CustomerSummaryPage.buttonAddQuote).isDisabled();

        NavigationPage.toMainTab("Quote");
        assertThat(QuoteSummaryPage.buttonAddNewQuote).isDisabled();

        NavigationPage.toMainTab("Customer");

        LOGGER.info("TEST: Qualify Customer # " + customerNumber);
        customerNonIndividual.qualify().perform();
        assertThat(CustomerSummaryPage.labelCustomerLeadStatus).hasValue(CustomerConstants.QUALIFIED);
    }
}
