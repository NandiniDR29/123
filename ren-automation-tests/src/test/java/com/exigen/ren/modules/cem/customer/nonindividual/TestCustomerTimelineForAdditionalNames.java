/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerTimelineForAdditionalNames extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24939", component = CRM_CUSTOMER)
    public void testCustomerTimelineForAdditionalNames() {
        mainApp().open();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()));

        customerNonIndividual.inquiry().perform();
        assertThat(CustomerSummaryPage.buttonTimeLine).isAbsent();
        GeneralTab.buttonCancel.click();

        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_ChangeAdditionalName"));
        customerNonIndividual.inquiry().perform();
        assertThat(CustomerSummaryPage.buttonTimeLine).isPresent();

        CustomerSummaryPage.buttonTimeLine.click();

        assertThat(CustomerSummaryPage.tableAdditionalNamesInfo.getRow(1).getCell(2)).valueContains("V.2");
        assertThat(CustomerSummaryPage.tableAdditionalNamesInfo.getRow(1).getCell(3)).valueContains("V.1");

        Row rowChanged = CustomerSummaryPage.tableAdditionalNamesInfo.getRowContains(1, CustomerConstants.AdditionalNamesTable.NAME_DBA);

        assertThat(rowChanged.getCell(2))
                .hasValue(tdSpecific().getValue("TestData_ChangeAdditionalName", GeneralTab.class.getSimpleName(),
                        GeneralTabMetaData.ADDITIONAL_NAMES.getLabel(),
                        GeneralTabMetaData.AdditionalNameDetailsMetaData.NAME_DBA.getLabel()));
        assertThat(rowChanged.getCell(3))
                .hasValue(tdSpecific().getValue("TestData", GeneralTab.class.getSimpleName(),
                        GeneralTabMetaData.ADDITIONAL_NAMES.getLabel(),
                        GeneralTabMetaData.AdditionalNameDetailsMetaData.NAME_DBA.getLabel()));
        assertThat(rowChanged.getCell(2).getValue()).isNotEqualTo(rowChanged.getCell(3).getValue());
    }
}
