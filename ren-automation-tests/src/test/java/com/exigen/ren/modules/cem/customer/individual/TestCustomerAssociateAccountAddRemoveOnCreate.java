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

public class TestCustomerAssociateAccountAddRemoveOnCreate extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24827", component = CRM_CUSTOMER)
    public void testCustomerAssociateAccountAddRemoveOnCreate() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerIndividual.getTestData("DataGather", "Adjustment_WithAssociateAccount").resolveLinks()));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        customerIndividual.update().start();

        assertThat(CustomerSummaryPage.tableMajorLargeAccount.getRow(1).getCell(2))
                .hasValue(tdCustomerIndividual.getTestData("DataGather", "Adjustment_WithAssociateAccount")
                        .getTestData(GeneralTab.class.getSimpleName(), GeneralTabMetaData.ASSOCIATE_ACCOUNT.getLabel())
                        .getTestData(GeneralTabMetaData.AssociateAccountMetaData.ASSOCIATE_NEW_ACCOUNT.getLabel())
                        .getValue(GeneralTabMetaData.AssociateNewAccountMetaData.ACCOUNT_DESIGNATION_TYPE.getLabel()));

        CustomerSummaryPage.buttonRemoveThisAccount.click();

        assertThat(CustomerSummaryPage.tableMajorLargeAccount.getRow(1)).isAbsent();
    }
}
