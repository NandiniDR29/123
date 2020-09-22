package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.BamConstants.CONSOLIDATED_STATEMENT_BA_NUM_IS_GENERATED;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.ADD_BILLING_ACCOUNTS;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.AddBillingAccountsMetaData.BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.CONSOLIDATED_STATEMENT_ENABLED_FROM;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestBatchGenerationOfConsolidatedStatement extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-40785", component = BILLING_GROUPBENEFITS)
    public void testBatchGenerationOfConsolidatedStatement_TC1() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createSelfAdminPolicy();
        String mp1 = PolicySummaryPage.labelPolicyNumber.getValue();
        String ba1 = getBillingAccountNumber(mp1);

        createSelfAdminPolicy();
        String mp2 = PolicySummaryPage.labelPolicyNumber.getValue();
        String ba2 = getBillingAccountNumber(mp2);

        createSelfAdminPolicy();
        String mp3 = PolicySummaryPage.labelPolicyNumber.getValue();
        String ba3 = getBillingAccountNumber(mp3);

        billingAccount.navigateToBillingAccountList();

        ImmutableList<TestData> listBA = ImmutableList.of(billingAccount.getDefaultTestData("SetUpConsolidatedStatement", "BillingAccount").adjust(TestData.makeKeyPath(BILLING_ACCOUNT.getLabel()), ba2),
                billingAccount.getDefaultTestData("SetUpConsolidatedStatement", "BillingAccount").adjust(TestData.makeKeyPath(BILLING_ACCOUNT.getLabel()), ba3));

        billingAccount.setUpConsolidatedStatement().perform(billingAccount.getDefaultTestData("SetUpConsolidatedStatement", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), CONSOLIDATED_STATEMENT_ENABLED_FROM.getLabel()), String.format("contains=%s", ba1))
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), ADD_BILLING_ACCOUNTS.getLabel()), listBA)
                .resolveLinks());

        navigateToBillingAccount(mp2);
        billingAccount.generateFutureStatement().perform();

        navigateToBillingAccount(mp3);
        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Test: Step 1.1 - 1.2");
        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_CONSOLIDATED_STATEMENT_GENERATION_JOB);
        mainApp().reopen();

        LOGGER.info("Test: Step 1.3 - 1.4");
        commonVerifications(mp1, mp2, mp3, 1);

        LOGGER.info("Test: Step 1.5");
        navigateToBillingAccount(mp1);
        billingAccount.generateFutureStatement().perform();

        navigateToBillingAccount(mp2);
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Test: Step 1.6");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));  // shift time is needed in order to run the job twice per test

        JobRunner.executeJob(GeneralSchedulerPage.BILLING_CONSOLIDATED_STATEMENT_GENERATION_JOB);
        mainApp().reopen();

        LOGGER.info("Test: Step 1.7 - 1.8");
        commonVerifications(mp1, mp2, mp3, 2);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-40785", component = BILLING_GROUPBENEFITS)
    public void testBatchGenerationOfConsolidatedStatement_TC2() {
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();
        TimeSetterUtil.getInstance().nextPhase(currentTime.plusDays(2));   // shift time is needed in order to avoid the impact of running the job from the first test

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createSelfAdminPolicy();
        String mp1 = PolicySummaryPage.labelPolicyNumber.getValue();
        String ba1 = getBillingAccountNumber(mp1);

        createSelfAdminPolicy();
        String mp2 = PolicySummaryPage.labelPolicyNumber.getValue();
        String ba2 = getBillingAccountNumber(mp2);

        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestData_Issue_Quarterly")));

        String mp3 = PolicySummaryPage.labelPolicyNumber.getValue();
        String ba3 = getBillingAccountNumber(mp3);

        billingAccount.navigateToBillingAccountList();

        ImmutableList<TestData> listBA = ImmutableList.of(billingAccount.getDefaultTestData("SetUpConsolidatedStatement", "BillingAccount").adjust(TestData.makeKeyPath(BILLING_ACCOUNT.getLabel()), ba2),
                billingAccount.getDefaultTestData("SetUpConsolidatedStatement", "BillingAccount").adjust(TestData.makeKeyPath(BILLING_ACCOUNT.getLabel()), ba3));

        billingAccount.setUpConsolidatedStatement().perform(billingAccount.getDefaultTestData("SetUpConsolidatedStatement", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), CONSOLIDATED_STATEMENT_ENABLED_FROM.getLabel()), String.format("contains=%s", ba1))
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), ADD_BILLING_ACCOUNTS.getLabel()), listBA)
                .resolveLinks());

        navigateToBillingAccount(mp1);
        billingAccount.generateFutureStatement().perform();

        navigateToBillingAccount(mp2);
        billingAccount.generateFutureStatement().perform();

        navigateToBillingAccount(mp3);
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Test: Step 2.1");
        TimeSetterUtil.getInstance().nextPhase(currentTime.plusDays(3));   // shift time is needed in order to run the job twice per test
        mainApp().close();

        JobRunner.executeJob(GeneralSchedulerPage.BILLING_CONSOLIDATED_STATEMENT_GENERATION_JOB);
        mainApp().reopen();

        LOGGER.info("Test: Step 2.2 - 2.3");
        commonVerifications(mp1, mp2, mp3, 1);

        LOGGER.info("Test: Step 2.4");
        navigateToBillingAccount(mp1);
        billingAccount.discardBill().perform(new SimpleDataProvider());

        LOGGER.info("Test: Step 2.5");
        navigateToBillingAccount(mp2);
        billingAccount.generateFutureStatement().perform();

        navigateToBillingAccount(mp3);
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Test: Step 2.6");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));   // shift time is needed in order to run the job twice per

        JobRunner.executeJob(GeneralSchedulerPage.BILLING_CONSOLIDATED_STATEMENT_GENERATION_JOB);
        mainApp().reopen();

        LOGGER.info("Test: Step 2.7 - 2.8");
        commonVerifications(mp1, mp2, mp3, 2);
    }

    private void createSelfAdminPolicy() {
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
    }

    private void commonVerifications(String policy1, String policy2, String policy3, int count) {
        navigateToBillingAccount(policy1);

        BillingAccountsListPage.verifyBamActivities(String.format(CONSOLIDATED_STATEMENT_BA_NUM_IS_GENERATED, getBillingAccountNumber(policy1)), FINISHED, count);
        assertThat(Efolder.isDocumentExist("Invoices and Bills", "BILLING_GROUP_CONSOLIDATED_BILL_SA.pdf", count)).withFailMessage("Files not found in folder").isTrue();

        navigateToBillingAccount(policy2);
        assertThat(Efolder.isDocumentExist("Invoices and Bills", "BILLING_GROUP_CONSOLIDATED_BILL_SA.pdf")).withFailMessage("File found in folder").isFalse();

        navigateToBillingAccount(policy3);
        assertThat(Efolder.isDocumentExist("Invoices and Bills", "BILLING_GROUP_CONSOLIDATED_BILL_SA.pdf")).withFailMessage("File found in folder").isFalse();
    }
}