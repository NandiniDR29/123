/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.account;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.modules.customer.CustomerContext;
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

public class TestRestCustomerGetAccounts extends RestBaseTest implements CustomerContext {

    @Test(groups = {Groups.ACCOUNT, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26188", component = CRM_ACCOUNT)
    public void testRestCustomerGetAccounts() {
        mainApp().open();

        createDefaultIndividualCustomer();
        String customerNumberOne = CustomerSummaryPage.labelCustomerNumber.getValue();
        LOGGER.info("Created Individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        toMainTab(ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.ACCOUNT));

        customerNonIndividual.createWithExistingAccount(customerNonIndividual.getDefaultTestData("DataGather", "TestData"));
        String customerNumberTwo = CustomerSummaryPage.labelCustomerNumber.getValue();
        LOGGER.info("Created Non-Individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        toMainTab(ACCOUNT.get());
        LOGGER.info("TEST: Check GET/accounts (Individual) Response ");
        ResponseContainer<List<AccountModel>> accountsResponse = customerRestClient.getAccounts(accountNumber, customerNumberOne);
        assertThat(accountsResponse.getResponse().getStatus()).isEqualTo(200);

        AccountModel accountModelIndividual = accountsResponse.getModel().get(0);
        assertSoftly(softly -> {
            softly.assertThat(accountModelIndividual.getAccountNumber()).isEqualTo(accountNumber);
            softly.assertThat(accountModelIndividual.getAccountName()).isEqualTo(CustomerSummaryPage.labelCustomerName.getValue());
            softly.assertThat(accountModelIndividual.getSpecialHandling()).isEqualToIgnoringCase(CustomerSummaryPage.labelSpecialHandling.getValue());
            softly.assertThat(accountModelIndividual.getConfidential()).isEqualTo(false);
        });

        LOGGER.info("TEST: Check GET/accounts (Non-Individual) Response ");
        accountsResponse = customerRestClient.getAccounts(accountNumber, customerNumberTwo);
        assertThat(accountsResponse.getResponse().getStatus()).isEqualTo(200);
        AccountModel accountModelNonIndividual = accountsResponse.getModel().get(0);

        assertSoftly(softly -> {
            softly.assertThat(accountModelNonIndividual.getAccountNumber()).isEqualTo(accountNumber);
            softly.assertThat(accountModelNonIndividual.getAccountName()).isEqualTo(CustomerSummaryPage.labelCustomerName.getValue());
            softly.assertThat(accountModelNonIndividual.getSpecialHandling()).isEqualToIgnoringCase(CustomerSummaryPage.labelSpecialHandling.getValue());
            softly.assertThat(accountModelIndividual.getConfidential()).isEqualTo(false);
        });
    }
}