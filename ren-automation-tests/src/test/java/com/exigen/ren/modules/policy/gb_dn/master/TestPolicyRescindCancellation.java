/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.CancellationActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.CancellationActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.RESCIND_CANCELLATION;
import static com.exigen.ren.main.enums.ActionConstants.ProductAction.CANCELLATION;
import static com.exigen.ren.main.enums.ActionConstants.ProductAction.REINSTATEMENT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.CANCELLATION_PENDING;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyRescindCancellation extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24529", component = POLICY_GROUPBENEFITS)
    public void testPolicyRescindCancellation() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicySelfAdminData());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String policyEffectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();

        LOGGER.info("TEST 1: Perform Future Cancellation for Policy #" + policyNumber);
        groupDentalMasterPolicy.cancel().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, "TestData_Plus2Months"));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(CANCELLATION_PENDING);
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(CANCELLATION);
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(REINSTATEMENT);

        String cancelReason = PolicySummaryPage.TransactionHistory.getReason();
        Currency transactionPremium = PolicySummaryPage.TransactionHistory.getTranPremium();
        String cancelEffectiveDate = groupDentalMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, "TestData_Plus2Months")
                .getValue(CancellationActionTab.class.getSimpleName(), CancellationActionTabMetaData.CANCEL_DATE.getLabel());
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.PROCESS_CANCELLATION,
                cancelEffectiveDate, cancelReason, transactionPremium, policyNumber, policyEffectiveDate));

        LOGGER.info("TEST 2: Rescind Cancellation for Policy #" + policyNumber);
        groupDentalMasterPolicy.rescindCancellation().perform(groupDentalMasterPolicy.getDefaultTestData(RESCIND_CANCELLATION, DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        assertThat(NavigationPage.comboBoxListAction).containsOption(CANCELLATION);

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.PROCESS_RESCIND_CANCELLATION,
                cancelEffectiveDate, transactionPremium.negate(), policyNumber, policyEffectiveDate));
    }
}
