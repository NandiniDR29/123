/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.majorlargeaccount;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.admin.modules.cem.majorlargeaccount.metadata.MajorLargeAccountMetaData;
import com.exigen.ren.admin.modules.cem.majorlargeaccount.pages.MajorLargeAccountPage;
import com.exigen.ren.admin.modules.cem.majorlargeaccount.tabs.MajorLargeAccountTab;
import com.exigen.ren.main.enums.CEMConstants;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMajorLargeAccountAddUpdate extends CemBaseTest {

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24922", component = CRM_CUSTOMER)
    public void testMajorLargeAccountAddUpdate() {
        adminApp().open();

        LOGGER.info("TEST: Create Update New Major/Large Account");
        String accountId = majorLargeAccount.createWithId(tdMajorLargeAccount.getTestData("MajorLargeAccount", "TestData"));

        majorLargeAccount.search(tdMajorLargeAccount.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.ACCOUNT_ID.getLabel()), accountId));

        assertThat(MajorLargeAccountPage.tableMajorLargeAccount.getRow(1).getCell(CEMConstants.CEMMajorLargeAccountTable.DESIGNATION_TYPE)).hasValue(tdMajorLargeAccount.getTestData(
                "MajorLargeAccount", "TestData", MajorLargeAccountTab.class.getSimpleName()).getValue(
                MajorLargeAccountMetaData.ACCOUNT_DESIGNATION_TYPE.getLabel()));

        LOGGER.info("TEST: Update Major/Large Account");
        majorLargeAccount.editMajorLargeAccount().perform(tdMajorLargeAccount.getTestData("MajorLargeAccountUpdate", "TestData"), 1);

        majorLargeAccount.search(tdMajorLargeAccount.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.ACCOUNT_ID.getLabel()), accountId));

        assertThat(MajorLargeAccountPage.tableMajorLargeAccount.getRow(1).getCell(CEMConstants.CEMMajorLargeAccountTable.DESIGNATION_TYPE)).hasValue(tdMajorLargeAccount.getTestData(
                "MajorLargeAccountUpdate", "TestData", MajorLargeAccountTab.class.getSimpleName())
                .getValue(MajorLargeAccountMetaData.ACCOUNT_DESIGNATION_TYPE.getLabel()));

        assertThat(MajorLargeAccountPage.tableMajorLargeAccount.getRow(1)).isPresent();
    }
}
