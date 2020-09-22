/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.DivisionsTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.DivisionsTab;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerDivisionsOnCreateAddRemove extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24872", component = CRM_CUSTOMER)
    public void testCustomerDivisionsOnCreateAddRemove() {
        mainApp().reopen();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")).resolveLinks());

        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        assertThat(CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD))
                .hasValue(tdCustomerNonIndividual.getTestData("DataGather").getTestData("Adjustment_Divisions").getTestData(DivisionsTab.class.getSimpleName())
                        .getValue(DivisionsTabMetaData.BILLING_METHOD.getLabel()));

        LOGGER.info("TEST: Remove Divisions for Customer # " + customerNumber);
        customerNonIndividual.removeDivisions().perform();

        assertThat(CustomerSummaryPage.tableDivisions).isAbsent();
    }

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24872", component = CRM_CUSTOMER)
    public void testCustomerDivisionsOnAssociateDivisionsUncheck() {
        mainApp().reopen();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")).resolveLinks());

        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        assertThat(CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD))
                .hasValue(tdCustomerNonIndividual.getTestData("DataGather").getTestData("Adjustment_Divisions").getTestData(DivisionsTab.class.getSimpleName())
                        .getValue(DivisionsTabMetaData.BILLING_METHOD.getLabel()));

        LOGGER.info("TEST: Uncheck Associate Divisions and Cancel this action for Customer # " + customerNumber);
        customerNonIndividual.associateDivisions().start();

        DivisionsTab.checkBoxAssociateDivisions.setValue(false);
        Page.dialogConfirmation.reject();
        GeneralTab.buttonSaveAndExit.click();

        assertThat(CustomerSummaryPage.tableDivisions).isPresent();

        LOGGER.info("TEST: Uncheck Associate Divisions for Customer # " + customerNumber);
        customerNonIndividual.associateDivisions().perform(false);

        assertThat(CustomerSummaryPage.tableDivisions).isAbsent();

        NavigationPage.setActionAndGo("Update");
        assertThat(NavigationPage.isSubTabPresent("Divisions")).isFalse();
    }
}
