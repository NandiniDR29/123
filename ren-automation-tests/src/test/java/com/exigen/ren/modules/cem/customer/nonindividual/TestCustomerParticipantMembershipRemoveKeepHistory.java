/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerParticipantMembershipRemoveKeepHistory extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24896", component = CRM_CUSTOMER)
    public void testCustomerParticipantMembershipRemoveKeepHistory() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerNonIndividual.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Membership"));
        String participantId = CustomerSummaryPage.tableMembershipCensus.getRow(1).getCell(CustomerConstants.CustomerMembershipCensusTable.PARTICIPANT_ID).getValue();

        LOGGER.info("TEST: Remove Participant but Keep History for  # " + participantId);
        customerNonIndividual.removeParticipantMembership().perform(tdCustomerNonIndividual.getTestData("RemoveParticipant", "TestData").adjust(
                tdCustomerNonIndividual.getTestData("RemoveParticipant", "Adjustment_KeepHistory")), 1);

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        MainPage.QuickSearch.search(participantId);

        customerNonIndividual.update().start();

        assertThat(CustomerSummaryPage.labelMembershipInformationExpanded).
                hasValue(String.format("Membership Information for %s %s Relationship (Deprecated: Participant was removed from group)", customerNumber, customerName));
    }
}
