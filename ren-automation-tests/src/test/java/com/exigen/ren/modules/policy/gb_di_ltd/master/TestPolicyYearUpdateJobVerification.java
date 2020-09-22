package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.POLICY_YEAR_UPDATE_JOB;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyYearUpdateJobVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-29905", component = POLICY_GROUPBENEFITS)
    public void testPolicyYearUpdateJobVerification() {

        TextBox currentPolicyYearStartDate = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.CURRENT_POLICY_YEAR_START_DATE);
        TextBox nextPolicyYearStartDate = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE);

        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("Policy1 creation");
        masterPolicyCreation(currentDate, currentDate);
        String policyNumber1 = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Policy2 creation");
        masterPolicyCreation(currentDate, currentDate.plusMonths(1));
        String policyNumber2 = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Policy3 creation");
        masterPolicyCreation(currentDate, currentDate);
        performEndorsement(currentDate.plusMonths(1));
        performEndorsement(currentDate);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        String policyNumber3 = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Steps#1, 2, 3 verification");
        JobRunner.executeJob(POLICY_YEAR_UPDATE_JOB);

        LOGGER.info("Step#7 verification");
        mainApp().reopen();
        MainPage.QuickSearch.search(policyNumber1);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE))
                .hasValue(ProductConstants.TransactionHistoryType.ENDORSEMENT);

        LOGGER.info("Step#11 verification");
        Tab.buttonCancel.click();

        longTermDisabilityMasterPolicy.policyInquiry().start();
        assertThat(currentPolicyYearStartDate).hasValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
        assertThat(nextPolicyYearStartDate).hasValue(currentDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));

        LOGGER.info("Step#12 verification");
        MainPage.QuickSearch.search(policyNumber2);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        MainPage.QuickSearch.search(policyNumber3);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
    }

    private void masterPolicyCreation(LocalDateTime effectiveDate, LocalDateTime nextPolicyYearStartDate) {
        longTermDisabilityMasterPolicy.createPolicyViaUI(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()),
                        effectiveDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()),
                        nextPolicyYearStartDate.format(DateTimeUtils.MM_DD_YYYY)));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    private void performEndorsement(LocalDateTime endorsementDate) {
        longTermDisabilityMasterPolicy.createEndorsement(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), endorsementDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataUpdateSomething").resolveLinks())
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));
    }
}