/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.account.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
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

public class TestRestGetCustomerUnderAccount extends RestBaseTest {

    @Test(groups = {Groups.ACCOUNT, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26187", component = CRM_ACCOUNT)
    public void testRestGetCustomerUnderAccount() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        String customerStatus = CustomerSummaryPage.labelLeadStatus.getValue();

        LOGGER.info("Created Non-Individual " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        toMainTab(ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.ACCOUNT));

        LOGGER.info("TEST: Check GET/accounts/{accountNumber}/customers Response ");
        ResponseContainer<List<CustomerModel>> accountCustomerResponse = customerRestClient.getAccountCustomer(accountNumber);
        assertThat(accountCustomerResponse.getResponse().getStatus()).isEqualTo(200);

        CustomerModel customerAccount = accountCustomerResponse.getModel().get(0);
        assertSoftly(softly -> {
            softly.assertThat(customerAccount.getCustomerNumber()).isEqualTo(customerNumber);
            softly.assertThat(customerAccount.getDisplayValue()).isEqualTo(customerName);
            softly.assertThat(customerAccount.getAccountNumber()).isEqualTo(accountNumber);
            softly.assertThat(customerAccount.getCustomerStatus()).isEqualToIgnoringCase(customerStatus);
            softly.assertThat(customerAccount.getBusinessDetails().getGroupSponsorInd()).isEqualTo(true);
        });
    }
}
