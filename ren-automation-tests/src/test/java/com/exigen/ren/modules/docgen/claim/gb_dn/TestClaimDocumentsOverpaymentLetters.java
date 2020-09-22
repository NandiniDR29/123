package com.exigen.ren.modules.docgen.claim.gb_dn;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.DBHelper.EventName;
import com.exigen.ren.utils.XmlValidator;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FINANCIALS;
import static com.exigen.ren.common.module.efolder.Efolder.collapseFolder;
import static com.exigen.ren.common.module.efolder.Efolder.expandFolder;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.RECOVERY_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SEARCH_PROVIDER;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.DBHelper.EventName.SEND_OVERPAYMENT_LETTER_POST_NOTIFICATION;
import static com.exigen.ren.utils.DBHelper.EventName.SEND_OVERPAYMENT_LETTER_PRE_NOTIFICATION;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimDocumentsOverpaymentLetters extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, DentalClaimContext {

    private static final String FILE_NAME_TEMPLATE_POSTNOTICE = "Postnotice-Overpayment-%s-%s-\\d{2}-\\d{2}-\\d{2}-\\d{2,3}.pdf";
    private static final String FILE_NAME_TEMPLATE_PRENOTICE = "Prenotice-Overpayment-%s-%s-\\d{2}-\\d{2}-\\d{2}-\\d{2,3}.pdf";
    private static final int CREDIT_AMOUNT_2 = 200;

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-29902", component = CLAIMS_GROUPBENEFITS)
    public void testClaimDocumentsOverpaymentLettersNy() {
        LOGGER.info("REN-29902 Precondition");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NY")
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.COUNTY_CODE.getLabel()), "index=1"));
        createDefaultGroupDentalCertificatePolicy();

        List<TestData> tdServices = new ArrayList<>(Arrays.asList(
                dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .getTestData(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel())
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.DOS.getLabel(), "$<today>")
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE.getLabel(), "D1120"),
                dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .getTestData(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel())
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.DOS.getLabel(), "$<today>")
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE.getLabel(), "D1110"),
                dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .getTestData(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel())
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.DOS.getLabel(), "$<today>")
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE.getLabel(), "D1208")));

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SEARCH_PROVIDER.getLabel()), tdSpecific().getTestData("TestData_Provider"))
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel()), tdServices));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime currentDatePlus106Days = currentDate.plusDays(106);
        int creditAmount1 = 100;
        commonSteps(creditAmount1);
        JobRunner.executeJob(GeneralSchedulerPage.OVERPAYMENT_PRENOTIFICATION_LETTER_GENERATION_JOB);
        String pathClaim = "Outbound Correspondence/Overpayment Letters";
        checkAsyncTasksCompletedAndPrenoticeLetterGenerated(pathClaim, claimNumber);

        TimeSetterUtil.getInstance().nextPhase(currentDatePlus106Days);
        JobRunner.executeJob(GeneralSchedulerPage.OVERPAYMENT_POSTNOTIFICATION_LETTER_GENERATION_JOB);
        String totalBalance =  String.format("%s.00", Integer.toString(creditAmount1 + CREDIT_AMOUNT_2));

        LOGGER.info("REN-29902 STEP#1");
        mainApp().open();
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        // get data from Claim > Financials
        String practiceName = ClaimPaymentsPage.tablePaymentDetails.getRow(1).getCell(ClaimPaymentsPage.PaymentDetails.PAYEE.getName()).getValue();
        String addressLine1 = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.ADDRESS_LINE_1.getName()).getValue();
        String city = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.CITY.getName()).getValue();
        String state = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.STATE_PROVINCE.getName()).getValue();
        String zip = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.ZIP_POSTAL_CODE.getName()).getValue();

        // navigate to Adjudication tab and check files template
        NavigationPage.toSubTab(ADJUDICATION.get());
        Page.dialogConfirmation.confirm();
        checkFilesNameTemplate(pathClaim, practiceName, currentDatePlus106Days, currentDate);

        LOGGER.info("REN-29902 STEP#2");
        NavigationPage.toMainTab(CUSTOMER.get());
        String patientName = CustomerSummaryPage.labelCustomerName.getValue();
        String pathCustomer = "Dental Claims/Overpayment Letters";
        checkFilesNameTemplate(pathCustomer, practiceName, currentDatePlus106Days, currentDate);

        LOGGER.info("REN-29902 STEP#3");
        String dosValue = currentDatePlus106Days.withHour(0).withMinute(0).withSecond(0).minusDays(106).format(YYYY_MM_DD_HH_MM_SS_Z);
        String issueDate = currentDate.format(MM_DD_YYYY);
        ImmutableMap<String, String> expectedMap = new ImmutableMap.Builder<String, String>()
                .put("practiceName", practiceName)
                .put("addressLine1", addressLine1)
                .put("city", city)
                .put("state", state)
                .put("zip", zip)
                .put("claimNumber", claimNumber)
                .put("patientName", patientName)
                .put("dosValue1", dosValue)
                .put("dosValue2", dosValue)
                .put("dosValue3", dosValue)
                .put("issueDate", issueDate)
                .put("totalBalance", totalBalance)
                .put("underwritingCompany", "RLHINY")
                .build();

        LOGGER.info("REN-29902 STEP#3");
        verificationXmlNy(claimNumber, SEND_OVERPAYMENT_LETTER_POST_NOTIFICATION, expectedMap, currentDatePlus106Days.format(YYYY_MM_DD));

        LOGGER.info("REN-29902 STEP#4");
        verificationXmlNy(claimNumber, SEND_OVERPAYMENT_LETTER_PRE_NOTIFICATION, expectedMap, currentDate.format(YYYY_MM_DD));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-29906", component = CLAIMS_GROUPBENEFITS)
    public void testClaimDocumentsOverpaymentLettersNj() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NJ"));
        createDefaultGroupDentalCertificatePolicy();

        List<TestData> tdServices = new ArrayList<>(Arrays.asList(
                dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .getTestData(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel())
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.DOS.getLabel(), "$<today>")
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE.getLabel(), "D1120"),
                dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .getTestData(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel())
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.DOS.getLabel(), "$<today-1d>")
                        .adjust(IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE.getLabel(), "D0160")));

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SEARCH_PROVIDER.getLabel()), tdSpecific().getTestData("TestData_Provider"))
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel()), tdServices));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime currentDatePlus106Days = currentDate.plusDays(106);
        int creditAmount1 = 200;
        commonSteps(creditAmount1);
        JobRunner.executeJob(GeneralSchedulerPage.OVERPAYMENT_PRENOTIFICATION_LETTER_GENERATION_JOB);
        String pathClaim = "Outbound Correspondence/Overpayment Letters";
        checkAsyncTasksCompletedAndPrenoticeLetterGenerated(pathClaim, claimNumber);

        TimeSetterUtil.getInstance().nextPhase(currentDatePlus106Days);
        JobRunner.executeJob(GeneralSchedulerPage.OVERPAYMENT_POSTNOTIFICATION_LETTER_GENERATION_JOB);
        String totalBalance =  String.format("%s.00", Integer.toString(creditAmount1 + CREDIT_AMOUNT_2));

        mainApp().open();
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        // get data from Claim > Financials
        String practiceName = ClaimPaymentsPage.tablePaymentDetails.getRow(1).getCell(ClaimPaymentsPage.PaymentDetails.PAYEE.getName()).getValue();
        String addressLine1 = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.ADDRESS_LINE_1.getName()).getValue();
        String city = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.CITY.getName()).getValue();
        String state = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.STATE_PROVINCE.getName()).getValue();
        String zip = ClaimPaymentsPage.tablePaymentDeliveryAddress.getRow(1).getCell(ClaimPaymentsPage.PaymentDeliveryAddress.ZIP_POSTAL_CODE.getName()).getValue();
        NavigationPage.toMainTab(CUSTOMER.get());
        Page.dialogConfirmation.confirm();
        //// get data from Customer
        String patientName = CustomerSummaryPage.labelCustomerName.getValue();
        String dosValue1 = currentDate.withHour(0).withMinute(0).withSecond(0).format(YYYY_MM_DD_HH_MM_SS_Z);
        String dosValue2 = currentDate.withHour(0).withMinute(0).withSecond(0).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z);
        //LocalDateTime issueDate = DateTimeUtils.getCurrentDateTime().minusDays(106);
        ImmutableMap<String, String> expectedMap = new ImmutableMap.Builder<String, String>()
                .put("practiceName", practiceName)
                .put("addressLine1", addressLine1)
                .put("city", city)
                .put("state", state)
                .put("zip", zip)
                .put("claimNumber", claimNumber)
                .put("patientName", patientName)
                .put("dosValue1", dosValue1)
                .put("dosValue2", dosValue2)
                .put("issueDate", currentDate.format(MM_DD_YYYY))
                .put("totalBalance", totalBalance)
                .put("underwritingCompany", "RLHICA")
                .build();

        LOGGER.info("REN-29906 STEP#3");
        verificationXmlNj(claimNumber, SEND_OVERPAYMENT_LETTER_POST_NOTIFICATION, expectedMap, currentDatePlus106Days.format(YYYY_MM_DD));

        LOGGER.info("REN-29906 STEP#4");
        verificationXmlNj(claimNumber, SEND_OVERPAYMENT_LETTER_PRE_NOTIFICATION, expectedMap, currentDate.format(YYYY_MM_DD));
    }

    private void commonSteps(int creditAmount1) {
        dentalClaim.claimSubmit().perform();
        dentalClaim.issuePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        dentalClaim.postRecovery().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_AMOUNT.getLabel()), "100"));
        dentalClaim.postRecovery().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_AMOUNT.getLabel()), "200")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERED_FROM.getLabel()), "Primary Insured"));
        String recoveryNumber1 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(RECOVERY_AMOUNT.getName(), "$100.00")
                .getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        String recoveryNumber2 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(RECOVERY_AMOUNT.getName(), "$200.00")
                .getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        DBService.get().executeUpdate("UPDATE ClaimsBalancingEntity SET creditAmount = ?, debitAmount=0 where targetPaymentNumber = ?", Integer.toString(creditAmount1), recoveryNumber1);
        DBService.get().executeUpdate("UPDATE ClaimsBalancingEntity SET creditAmount = ?, debitAmount=0 where targetPaymentNumber = ?", Integer.toString(CREDIT_AMOUNT_2), recoveryNumber2);
    }

    private void verificationXmlNy(String claimNumber, EventName eventName, ImmutableMap<String, String> map, String systemDate) {
        XmlValidator xmlValidator = DBHelper.getDocument(claimNumber, DBHelper.EntityType.CLAIM, eventName);
        xmlValidator.checkDocument(PAYEE, map.get("practiceName"));
        xmlValidator.checkDocument(ADDRESS_LINE_1, map.get("addressLine1"));
        xmlValidator.checkDocument(CITY, map.get("city"));
        xmlValidator.checkDocument(STATE, map.get("state"));
        xmlValidator.checkDocument(ZIP, map.get("zip"));
        CustomAssertions.assertThat(xmlValidator.getNodeValue(SYSTEM_DATE))
                .matches(String.format("%sT\\d{2}:\\d{2}:\\S{2,6}Z", systemDate));
        xmlValidator.checkDocument(CLAIM_NUMB, map.get("claimNumber"));
        xmlValidator.checkDocument(PATIENT_NAME, map.get("patientName"));
        xmlValidator.checkDocument(String.format(DOS_DATE.get(), 1), map.get("dosValue1"));
        xmlValidator.checkDocument(String.format(DOS_DATE.get(), 2), map.get("dosValue2"));
        xmlValidator.checkDocument(String.format(DOS_DATE.get(), 3), map.get("dosValue3"));
        CustomAssertions.assertThat(xmlValidator.getNodeValue(CHECK_NUM)).isNotEmpty();
        xmlValidator.checkDocument(ISSUE_DATE, map.get("issueDate"));
        xmlValidator.checkDocument(TOTAL_BALANCE, map.get("totalBalance"));
        xmlValidator.checkDocument(UNDERWRITING_COMPANY, map.get("underwritingCompany"));
    }

    private void verificationXmlNj(String claimNumber, EventName eventName, ImmutableMap<String, String> map, String systemDate) {
        XmlValidator xmlValidator = DBHelper.getDocument(claimNumber, DBHelper.EntityType.CLAIM, eventName);
        xmlValidator.checkDocument(PAYEE, map.get("practiceName"));
        xmlValidator.checkDocument(ADDRESS_LINE_1, map.get("addressLine1"));
        xmlValidator.checkDocument(CITY, map.get("city"));
        xmlValidator.checkDocument(STATE, map.get("state"));
        xmlValidator.checkDocument(ZIP, map.get("zip"));
        CustomAssertions.assertThat(xmlValidator.getNodeValue(SYSTEM_DATE))
                .matches(String.format("%sT\\d{2}:\\d{2}:\\S{2,6}Z", systemDate));
        xmlValidator.checkDocument(CLAIM_NUMB, map.get("claimNumber"));
        xmlValidator.checkDocument(PATIENT_NAME, map.get("patientName"));
        xmlValidator.checkDocument(String.format(DOS_DATE.get(), 1), map.get("dosValue1"));
        xmlValidator.checkDocument(String.format(DOS_DATE.get(), 2), map.get("dosValue2"));
        CustomAssertions.assertThat(xmlValidator.getNodeValue(CHECK_NUM)).isNotEmpty();
        xmlValidator.checkDocument(ISSUE_DATE, map.get("issueDate"));
        xmlValidator.checkDocument(TOTAL_BALANCE, map.get("totalBalance"));
        xmlValidator.checkDocument(UNDERWRITING_COMPANY, map.get("underwritingCompany"));
    }

    private void checkFilesNameTemplate(String path, String practiceName, LocalDateTime postNoticeDate, LocalDateTime preNoticeDate) {
        assertSoftly(softly -> {
            softly.assertThat(Efolder.getFileName(path, "Postnotice-Overpayment"))
                    .matches(String.format(FILE_NAME_TEMPLATE_POSTNOTICE, practiceName, postNoticeDate.format(YYYY_MM_DD)));
            collapseFolder(path);
            softly.assertThat(Efolder.getFileName(path, "Prenotice-Overpayment"))
                    .matches(String.format(FILE_NAME_TEMPLATE_PRENOTICE, practiceName, preNoticeDate.format(YYYY_MM_DD)));
        });
    }

    private void checkAsyncTasksCompletedAndPrenoticeLetterGenerated(String pathClaim, String claimNumber) {
        mainApp().open();
        MainPage.QuickSearch.search(claimNumber);

        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(pathClaim, "Prenotice-Overpayment", ".pdf"),
                () -> {
                    expandFolder(pathClaim);
                    return null;
                },
                StopStrategies.stopAfterAttempt(15),
                WaitStrategies.fixedWait(15, TimeUnit.SECONDS));
    }
}
