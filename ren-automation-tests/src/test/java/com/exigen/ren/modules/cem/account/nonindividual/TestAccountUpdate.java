/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.account.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.AccountConstants;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.account.AccountBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_ACCOUNT;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAccountUpdate extends AccountBaseTest {

    @Test(groups = {ACCOUNT, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24808", component = CRM_ACCOUNT)
    public void testAccountUpdate() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        assertThat(CustomerSummaryPage.labelConfidential).hasValue(AccountConstants.AccountConfidential.NO);

        LOGGER.info("TEST: Update Account # " + accountNumber);
        accountNonIndividual.update().perform(tdSpecific().getTestData("TestData"));
        assertThat(CustomerSummaryPage.labelConfidential).hasValue(AccountConstants.AccountConfidential.YES);
    }
}
