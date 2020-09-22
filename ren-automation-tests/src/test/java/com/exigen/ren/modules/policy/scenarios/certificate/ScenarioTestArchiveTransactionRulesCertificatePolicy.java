package com.exigen.ren.modules.policy.scenarios.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.Users;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants.PolicyStatus;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage.ArchivedTransactions;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.BamConstants.ARCHIVED_ENDORSEMENT_EFFECTIVE_FOR_POLICY;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.DATA_GATHERING;
import static com.exigen.ren.main.enums.ProductConstants.TransactionHistoryType.ENDORSEMENT;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.ArchivedTransactions.PREMIUM;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.ArchivedTransactions.TRANSACTION_TYPE;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.Endorsements.STATUS;

public class ScenarioTestArchiveTransactionRulesCertificatePolicy extends BaseTest implements CustomerContext, CaseProfileContext {

    public void testArchiveTransactionRulesCertificatePolicy(GroupBenefitsMasterPolicyType masterPolicyType, TestData tdMasterPolicy,
                                                             GroupBenefitsCertificatePolicyType certificatePolicyType, TestData tdCertificatePolicy) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(masterPolicyType);
        masterPolicyType.get().createPolicy(tdMasterPolicy);
        GroupBenefitsCertificatePolicy certificatePolicy = certificatePolicyType.get();
        certificatePolicy.createPolicy(tdCertificatePolicy
                .adjust(certificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String CP_number = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step 1");
        TestData tdEndorsement = certificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY);
        certificatePolicy.endorse().perform(tdEndorsement);
        PolicySummaryPage.buttonPendedEndorsement.click();
        certificatePolicy.dataGather().perform(certificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement")
                .mask("PremiumSummaryTab"));
        Tab.buttonSaveAndExit.click();

        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(1);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions.getRow(1).getCell(TRANSACTION_TYPE.getName())).hasValue("No records found.");
        });

        LOGGER.info("Step 2");
        PolicySummaryPage.buttonPendedEndorsement.click();
        assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell(STATUS.getName())).hasValue(DATA_GATHERING);

        LOGGER.info("Step 3");
        certificatePolicy.archiveTransaction().perform();

        LOGGER.info("Step 4");
        PolicySummaryPage.openActivitiesAndUserNotes();
        String firstDayOfMonth = TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).format(DateTimeUtils.MM_DD_YYYY);
        assertThat(PolicySummaryPage.activitiesAndUserNotes).hasMatchingRows(1, ImmutableMap.of(DESCRIPTION,
                String.format(ARCHIVED_ENDORSEMENT_EFFECTIVE_FOR_POLICY, firstDayOfMonth, CP_number, firstDayOfMonth)));

        LOGGER.info("Step 5");
        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(1);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions)
                    .with(TRANSACTION_TYPE, ENDORSEMENT)
                    .with(PREMIUM, ValueConstants.EMPTY)
                    .with(ArchivedTransactions.STATUS, DATA_GATHERING)
                    .with(ArchivedTransactions.EFFECTIVE_DATE, firstDayOfMonth)
                    .with(ArchivedTransactions.LAST_CHANGE_DATE, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                    .with(ArchivedTransactions.PRIMARY_BROKER, "QAG - QA Agency")
                    .with(ArchivedTransactions.LAST_PERFORMER, "ISBA ISBA")
                    .hasMatchingRows(1);
        });

        LOGGER.info("Step 7");
        PolicySummaryPage.tableArchivedTransactions.getRow(1).getCell(TRANSACTION_TYPE.getName()).controls.links.getFirst().click();
        assertThat(PolicySummaryPage.labelStatusInquiryMode).valueContains(DATA_GATHERING);

        LOGGER.info("Step 8");
        mainApp().reopen(Users.QA);
        search(CP_number);
        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(1);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions)
                    .with(TRANSACTION_TYPE, ENDORSEMENT)
                    .with(PREMIUM, ValueConstants.EMPTY)
                    .with(ArchivedTransactions.STATUS, DATA_GATHERING)
                    .with(ArchivedTransactions.EFFECTIVE_DATE, firstDayOfMonth)
                    .with(ArchivedTransactions.LAST_CHANGE_DATE, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                    .with(ArchivedTransactions.PRIMARY_BROKER, "QAG - QA Agency")
                    .with(ArchivedTransactions.LAST_PERFORMER, "ISBA ISBA")
                    .hasMatchingRows(1);
        });

        LOGGER.info("Step 9-10");
        certificatePolicy.endorse().perform(tdEndorsement);
        PolicySummaryPage.buttonPendedEndorsement.click();
        assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell(STATUS.getName())).hasValue(PolicyStatus.PREMIUM_CALCULATED);


        LOGGER.info("Step 11");
        certificatePolicy.archiveTransaction().perform();
        PolicySummaryPage.openActivitiesAndUserNotes();
        assertThat(PolicySummaryPage.activitiesAndUserNotes).hasMatchingRows(2, ImmutableMap.of(DESCRIPTION,
                String.format(ARCHIVED_ENDORSEMENT_EFFECTIVE_FOR_POLICY, firstDayOfMonth, CP_number, firstDayOfMonth)));

        LOGGER.info("Step 13");
        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(2);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions)
                    .with(TRANSACTION_TYPE, ENDORSEMENT)
                    .with(ArchivedTransactions.STATUS, PolicyStatus.PREMIUM_CALCULATED)
                    .with(ArchivedTransactions.EFFECTIVE_DATE, firstDayOfMonth)
                    .with(ArchivedTransactions.LAST_CHANGE_DATE, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                    .with(ArchivedTransactions.PRIMARY_BROKER, "QAG - QA Agency")
                    .with(ArchivedTransactions.LAST_PERFORMER, "QA QA user")
                    .hasMatchingRows(1);
        });
    }
}