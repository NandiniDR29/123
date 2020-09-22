/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerPendingUpdate extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-11938", component = CRM_CUSTOMER)
    public void testCustomerPendingUpdate() {
        mainApp().open();

        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")));

        customerNonIndividual.scheduledUpdate().perform(tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData_Division"));
        CustomerSummaryPage.linkPendingUpdatesPanel.click();

        assertThat(CustomerSummaryPage.labelPendingUpdatesScheduled.getValue()).contains(String.format("%d Pending Updates Scheduled for %s", 3,
                tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData_Division", "ScheduledUpdateActionTab")
                        .getValue("Update Effective Date")));

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        CustomerSummaryPage.buttonCompareAll.click();

        assertThat(ViewHistoryActionTab.getTableAndExpand("General Info").getRow(4))
                .hasValue(Arrays.asList(CustomerConstants.NAME_LEGAL, customerName, tdCustomerNonIndividual.getTestData("ScheduleUpdateAction",
                        "TestData_Division", "GeneralTab").getValue(CustomerConstants.NAME_LEGAL)));

        assertThat(ViewHistoryActionTab.getTableAndExpand(String.format("Division %s", tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions", "DivisionsTab")
                .getValue(CustomerConstants.DIVISION_NAME))).getRow(4))
                .hasValue(Arrays.asList(CustomerConstants.DIVISION_NAME, tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions", "DivisionsTab")
                        .getValue(CustomerConstants.DIVISION_NAME), tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData_Division")
                        .getTestDataList("DivisionsTab").get(0).getValue(CustomerConstants.DIVISION_NAME)));

        assertThat(ViewHistoryActionTab.getTableAndExpand(String.format("Division %s", tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData_Division")
                .getTestDataList("DivisionsTab").get(1).getValue(CustomerConstants.DIVISION_NAME))).getRow(4))
                .hasCellWithValue(3, tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData_Division")
                        .getTestDataList("DivisionsTab").get(1).getValue(CustomerConstants.DIVISION_NAME));
    }
}
