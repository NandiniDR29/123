/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.account.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.account.model.AccountModel;
import com.exigen.ren.utils.groups.Groups;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab.ACCOUNT;
import static com.exigen.ren.common.pages.NavigationPage.toMainTab;
import static com.exigen.ren.utils.components.Components.CRM_ACCOUNT;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestRestGetAccount extends RestBaseTest {

    @Test(groups = {Groups.ACCOUNT, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26183", component = CRM_ACCOUNT)
    public void testRestGetAccount() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        LOGGER.info("Created Non-Individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        toMainTab(ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.ACCOUNT));

        LOGGER.info("TEST: Check GET/accounts/{accountNumber} Response ");
        ResponseContainer<List<AccountModel>> accountResponse = customerRestClient.getAccount(accountNumber);
        assertThat(accountResponse.getResponse().getStatus()).isEqualTo(200);

        AccountModel accountModel = accountResponse.getModel().get(0);
        assertSoftly(softly -> {
            softly.assertThat(accountModel.getAccountNumber()).isEqualTo(accountNumber);
            softly.assertThat(accountModel.getAccountName()).isEqualTo(CustomerSummaryPage.labelCustomerName.getValue());
            softly.assertThat(accountModel.getSpecialHandling()).isEqualToIgnoringCase(CustomerSummaryPage.labelSpecialHandling.getValue());
            softly.assertThat(accountModel.getConfidential()).isEqualTo(false);
        });
    }
}
