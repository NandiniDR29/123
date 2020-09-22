/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerNewContactsVersionTimelineVerification extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24878", component = CRM_CUSTOMER)
    public void testCustomerNewContactsVersionTimelineVerification() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP: Inquiry Customer # " + customerNumber);
        customerNonIndividual.inquiry().perform();

        assertThat(CustomerSummaryPage.buttonTimeLine).isAbsent();

        GeneralTab.buttonTopCancel.click();

        LOGGER.info("TEST: Update Contacts Details for Customer and created new version and Timeline Verification # " + customerNumber);
        NavigationPage.toSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customerNonIndividual.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateAddressType")), 1);

        LOGGER.info("STEP: Inquiry Customer # " + customerNumber);
        customerNonIndividual.inquiry().perform();

        assertThat(CustomerSummaryPage.buttonTimeLine).isPresent();

        CustomerSummaryPage.buttonTimeLine.click();

        // Check that, Version 2 is not equals Version 1
        verifyVersionCompare();
    }

    private static void verifyVersionCompare() {
        assertThat(CustomerSummaryPage.tableAddresslInfo.getRow(1).getCell(2)).valueContains("V.2");
        assertThat(CustomerSummaryPage.tableAddresslInfo.getRow(1).getCell(3)).valueContains("V.1");

        assertSoftly(softly -> {
            Row rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.ZIP_CODE);
            softly.assertThat(rowChanged.getCell(2).getValue()).isNotEqualToIgnoringCase(rowChanged.getCell(3).getValue());

            rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.CITY);
            softly.assertThat(rowChanged.getCell(2).getValue()).isNotEqualToIgnoringCase(rowChanged.getCell(3).getValue());

            rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.ADDRESS_LINE_1);
            softly.assertThat(rowChanged.getCell(2).getValue()).isNotEqualToIgnoringCase(rowChanged.getCell(3).getValue());
        });
    }
}
