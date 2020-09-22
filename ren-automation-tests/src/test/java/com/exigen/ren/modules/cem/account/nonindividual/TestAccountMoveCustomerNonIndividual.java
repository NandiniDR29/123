/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.account.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.main.modules.account.actiontabs.MoveCustomerSearchActionTab;
import com.exigen.ren.main.modules.account.metadata.MoveCustomerSearchActionTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.account.AccountBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_ACCOUNT;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAccountMoveCustomerNonIndividual extends AccountBaseTest {

    @Test(groups = {ACCOUNT, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24807", component = CRM_ACCOUNT)
    public void testAccountMoveCustomerNonIndividual() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String firstCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultNonIndividualCustomer();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String secondCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Move Customer # " + secondCustomerNumber + " to Customer # " + firstCustomerNumber);
        accountNonIndividual.moveCustomer().perform(tdAccountNonIndividual.getTestData("MoveCustomer", "TestData").adjust(
                TestData.makeKeyPath(MoveCustomerSearchActionTab.class.getSimpleName(),
                        MoveCustomerSearchActionTabMetaData.CUSTOMER.getLabel()),
                firstCustomerNumber));
        assertThat(CustomerSummaryPage.tableCustomers).hasRows(2);
    }
}
