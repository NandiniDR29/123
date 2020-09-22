/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_eap.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.EmployeeAssistanceProgramCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyRescind extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24715", component = POLICY_GROUPBENEFITS)
    public void testPolicyRescindCancellation() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(employeeAssistanceProgramMasterPolicy.getType());
        createDefaultEmployeeAssistanceProgramCertificatePolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String policyEffectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();

        LOGGER.info("TEST: Cancelling Policy #" + policyNumber);
        employeeAssistanceProgramCertificatePolicy.cancel().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.CANCELLATION, "TestData_Plus2Months"));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
        assertThat(NavigationPage.comboBoxListAction.isOptionPresent(ActionConstants.ProductAction.CANCELLATION)).isFalse();

        PolicySummaryPage.TransactionHistory.open();
        LocalDateTime cancelEffectiveDate = PolicySummaryPage.TransactionHistory.readEffectiveDate(1);
        String cancelReason = PolicySummaryPage.TransactionHistory.readReason(1);
        Currency transactionPremium = PolicySummaryPage.TransactionHistory.readTranPremium(1);
        PolicySummaryPage.TransactionHistory.close();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.PROCESS_CANCELLATION,
                cancelEffectiveDate.format(DateTimeUtils.MM_DD_YYYY), cancelReason, transactionPremium, policyNumber, policyEffectiveDate));

        LOGGER.info("TEST: Rescind Cancellation for Policy #" + policyNumber);
        employeeAssistanceProgramCertificatePolicy.rescindCancellation().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.RESCIND_CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        assertThat(NavigationPage.comboBoxListAction).containsOption(ActionConstants.ProductAction.CANCELLATION);

        PolicySummaryPage.TransactionHistory.open();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE)).hasValue(ProductConstants.TransactionHistoryType.REINSTATEMENT);
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON)).hasValue(getStoredValue("RescindCancellationReason"));
        transactionPremium = PolicySummaryPage.TransactionHistory.readTranPremium(1);
        PolicySummaryPage.TransactionHistory.close();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Status")).hasValue("Finished");
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.PROCESS_RESCIND_CANCELLATION,
                cancelEffectiveDate.format(DateTimeUtils.MM_DD_YYYY), transactionPremium, policyNumber, policyEffectiveDate));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24715", component = POLICY_GROUPBENEFITS)
    public void testRollBackPolicyRescindCancellation() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(employeeAssistanceProgramMasterPolicy.getType());
        createDefaultEmployeeAssistanceProgramCertificatePolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String policyEffectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();
        LOGGER.info("TEST: Rollback Rescind Cancellation for Policy #" + policyNumber);

        LOGGER.info("TEST: Mid Term Endorsement for Policy #" + policyNumber);
        employeeAssistanceProgramCertificatePolicy.endorse().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus3Months"));

        String endorseEffectiveDate = employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus3Months").getValue(StartEndorsementActionTab.class.getSimpleName(),
                StartEndorsementActionTabMetaData.ENDORSEMENT_DATE.getLabel());
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.INITIATE_ENDORSEMENT, endorseEffectiveDate, policyNumber, policyEffectiveDate));

        LOGGER.info("TEST: Cancelling Policy #" + policyNumber);
        employeeAssistanceProgramCertificatePolicy.cancel().start();
        Page.dialogConfirmation.confirm();
        employeeAssistanceProgramCertificatePolicy.cancel().getWorkspace().fill(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.CANCELLATION, "TestData_Plus2Months"));
        employeeAssistanceProgramCertificatePolicy.cancel().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
        assertThat(NavigationPage.comboBoxListAction.isOptionPresent(ActionConstants.ProductAction.CANCELLATION)).isFalse();

        PolicySummaryPage.TransactionHistory.open();
        LocalDateTime cancelEffectiveDate = PolicySummaryPage.TransactionHistory.readEffectiveDate(1);
        String cancelReason = PolicySummaryPage.TransactionHistory.readReason(1);
        Currency transactionPremium = PolicySummaryPage.TransactionHistory.readTranPremium(1);
        PolicySummaryPage.TransactionHistory.close();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.PROCESS_CANCELLATION,
                cancelEffectiveDate.format(DateTimeUtils.MM_DD_YYYY), cancelReason, transactionPremium, policyNumber, policyEffectiveDate));

        LOGGER.info("TEST: Rescind Cancellation for Policy #" + policyNumber);
        employeeAssistanceProgramCertificatePolicy.rescindCancellation().perform(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.RESCIND_CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        assertThat(NavigationPage.comboBoxListAction).containsOption(ActionConstants.ProductAction.CANCELLATION);

        PolicySummaryPage.TransactionHistory.open();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE)).hasValue(ProductConstants.TransactionHistoryType.REINSTATEMENT);
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON)).hasValue(getStoredValue("RescindCancellationReason"));
        transactionPremium = PolicySummaryPage.TransactionHistory.readTranPremium(1);
        PolicySummaryPage.TransactionHistory.close();
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Status")).hasValue("Finished");
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Description")).hasValue(String.format(BamConstants.PROCESS_RESCIND_CANCELLATION,
                cancelEffectiveDate.format(DateTimeUtils.MM_DD_YYYY), transactionPremium, policyNumber, policyEffectiveDate));
    }
}
