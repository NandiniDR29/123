/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.tabs.EmployeeInfoTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerParticipantMembershipRemoveOnUpdate extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24898", component = CRM_CUSTOMER)
    public void testCustomerParticipantMembershipRemoveOnUpdate() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerNonIndividual.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Membership"));
        assertThat(CustomerSummaryPage.tableMembershipCensus).hasRows(1);

        LOGGER.info("TEST: Remove Participant on Update for Customer # " + customerNumber);
        customerNonIndividual.updateParticipantMembership().start(1);

        EmployeeInfoTab.buttonRemoveRelationshipToSponsor.click();
        Page.dialogConfirmation.confirm();
        EmployeeInfoTab.buttonNext.click();
        EmployeeInfoTab.buttonDone.click();

        assertThat(CustomerSummaryPage.tableMembershipCensus).isAbsent();
    }
}
