/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants.ProductsOwned;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerProductsOwned extends CustomerBaseTest {
    private static final String NEW_CARRIER_NAME = "CHUBB";

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-11968", component = CRM_CUSTOMER)
    public void testCustomerProductsOwned() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.PORTFOLIO.get());
        assertThat(CustomerSummaryPage.tableProductsOwned).isAbsent();

        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Add2Products"));

        assertSoftly(softly -> {
            softly.assertThat(CustomerSummaryPage.tableProductsOwned).isPresent();
            softly.assertThat(CustomerSummaryPage.tableProductsOwned).containsMatchingRow(1, ImmutableMap.of(
                    ProductsOwned.POLICY_TYPE.getName(),
                    tdSpecific().getTestData("TestData_AutoProduct").getValue(GeneralTabMetaData.AddNewProductDetailsMetaData.POLICY_TYPE.getLabel()),
                    ProductsOwned.CARRIER_NAME.getName(),
                    tdSpecific().getTestData("TestData_AutoProduct").getValue(GeneralTabMetaData.AddNewProductDetailsMetaData.CARRIER_NAME.getLabel())));
            softly.assertThat(CustomerSummaryPage.tableProductsOwned).containsMatchingRow(2, ImmutableMap.of(
                    ProductsOwned.POLICY_TYPE.getName(),
                    tdSpecific().getTestData("TestData_BenefitsProduct").getValue(GeneralTabMetaData.AddNewProductDetailsMetaData.POLICY_TYPE.getLabel()),
                    ProductsOwned.CARRIER_NAME.getName(),
                    tdSpecific().getTestData("TestData_BenefitsProduct").getValue(GeneralTabMetaData.AddNewProductDetailsMetaData.CARRIER_NAME.getLabel())));
        });

        customerNonIndividual.update().start();
        GeneralTab.linkChangeProductOwned.click();
        new GeneralTab.ProductOwned(By.id("id"), GeneralTabMetaData.AddNewProductDetailsMetaData.class)
                .getAsset(GeneralTabMetaData.AddNewProductDetailsMetaData.CARRIER_NAME, 0).setValue(NEW_CARRIER_NAME);
        GeneralTab.buttonAddAllProducts.click();

        GeneralTab.linkRemoveProductOwned.click();
        CustomerSummaryPage.dialogConfirmation.confirm();
        GeneralTab.buttonSaveAndExit.click();

        verifyUpdatedTableProductsOwned(CustomerSummaryPage.tableProductsOwned);

        customerNonIndividual.inquiry().start();
        verifyUpdatedTableProductsOwned(GeneralTab.tableProductsOwned);
    }

    private void verifyUpdatedTableProductsOwned(Table nameTable) {
        assertSoftly(softly -> {
            softly.assertThat(nameTable).hasRows(1);
            softly.assertThat(nameTable).containsMatchingRow(1, ImmutableMap.of(
                    ProductsOwned.POLICY_TYPE.getName(),
                    tdSpecific().getTestData("TestData_AutoProduct").getValue(GeneralTabMetaData.AddNewProductDetailsMetaData.POLICY_TYPE.getLabel()),
                    ProductsOwned.CARRIER_NAME.getName(), NEW_CARRIER_NAME));
        });
    }
}
