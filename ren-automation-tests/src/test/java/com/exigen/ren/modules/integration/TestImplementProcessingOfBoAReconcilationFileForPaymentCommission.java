package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.CommissionsInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.google.common.collect.ImmutableMap;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.admin.modules.agencyvendor.agency.tabs.CommissionsInfoTab.tableCommissionPaymentHistory;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.BLT_COMMISSION_PAYMENT_JOB;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.*;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.AGENCY_PRODUCER_COMBO;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestImplementProcessingOfBoAReconcilationFileForPaymentCommission extends CommissionStrategyBaseTest implements JobContext, CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext, BillingAccountContext {

    private static final String FILE_NAME = "RENAISSANCE_DAILY_PAID_%s.txt";
    private static final String FILE_PATH = "/mnt/ren/shared/checkfile/inbound/";
    private static final String FILE_PATH_ARCHIVE = "/mnt/ren/archive/checkfile/archive/";


    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-43566 ", component = INTEGRATION)
    public void testImplementProcessingOfBoAReconcilationFileForPaymentCommission() throws JSchException, SftpException {

        adminApp().open();
        //Create Agency1 with Channel = Agency
        String agencyName = agency.createAgency(agency.defaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.COMMISSION_GROUPS.getLabel()), ImmutableList.of("Producers - Standard Broker Schedule")));
        createChannelCommissionGroup(agencyName);

        TestData tdSecurityProfile = profileCorporate.defaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), COMMISSIONABLE.getLabel()), "true")
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()),
                        tdAgencyDefault.getTestData(agencyInfoTab.getMetaKey()).getValue(AgencyInfoMetaData.CHANNEL.getLabel()));

        createUserAndRelogin(profileCorporate, tdSecurityProfile
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(), AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 6")
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(), AuthorityLevelsMetaData.TYPE.getLabel()), "Underwriting"));

        TestData tdAgency = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName);

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()), ImmutableList.of(tdAgency)));

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupAccidentMasterPolicy.getType())
                .mask(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel())));

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData().adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENCY_PRODUCER_COMBO.getLabel()), agencyName));
        String mp1 = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultGroupAccidentCertificatePolicy();
        String ba1 = getBillingAccountNumber(mp1);

        MainPage.QuickSearch.search(ba1);
        billingAccount.generateFutureStatement().perform();

        String paymentAmount = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.CURRENT_DUE).getValue();
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Payment_Over_Amount"),
                new Currency(paymentAmount).add(new Currency("1")).toString());

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

        Job job = new Job(GeneralSchedulerPage.BENEFITS_COMMISSION_ASYNC_DISBURSEMENT_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "-t daily"));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.BENEFITS_COMMISSION_ASYNC_DISBURSEMENT_JOB.getGroupName(), job));

        adminApp().open();
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        agencyVendorSearchTab.openFirst();
        commissionTab.navigateToTab();
        assertThat(tableCommissionPaymentHistory.getRow(1)).hasCellWithValue(CommissionsInfoTab.CommissionPaymentHistory.STATUS.getName(), "pending");

        LOGGER.info("---=={Step 1}==---");
        adminApp().close();
        JobRunner.executeJob(BLT_COMMISSION_PAYMENT_JOB);

        LOGGER.info("---=={Step 5}==---");
        adminApp().reopen();
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        agencyVendorSearchTab.openFirst();
        commissionTab.navigateToTab();
        assertThat(tableCommissionPaymentHistory.getRow(1)).hasCellWithValue(CommissionsInfoTab.CommissionPaymentHistory.STATUS.getName(), "issued");
        String refId = tableCommissionPaymentHistory.getRow(1).getCell(CommissionsInfoTab.CommissionPaymentHistory.REFERENCE_ID.getName()).getValue();

        Map<String, String> dbRow = DBService.get().getRow(String.format("select * from LedgerEntry le\n" +
                "join LedgerTransaction lc on lc.id = le.LedgerTransaction_id where lc.entityRefNo='%s'", refId));

        LOGGER.info("---=={Step 1}==---");
        String fileText = "111111111115H                      05222020                                                                                                                                                                                                                                                                                 \n" +
                "111111111115R000%s00000000014305222020          CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANA       \n" +
                "111111111115T000%s000000000143  ";

        String newFileName = prepareAndUploadFile(String.format(fileText, dbRow.get("description"), dbRow.get("description")));
        JobRunner.executeJob(GeneralSchedulerPage.CHECK_BOA_RECONCILIATION_FILE_JOB);

        adminApp().reopen();
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        agencyVendorSearchTab.openFirst();
        commissionTab.navigateToTab();
        assertThat(tableCommissionPaymentHistory.getRow(1)).hasCellWithValue(CommissionsInfoTab.CommissionPaymentHistory.STATUS.getName(), "cleared");

        SFTPConnection.getClient().getFilesList(new File(FILE_PATH_ARCHIVE)).stream().filter(f -> f.contains(newFileName)).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", newFileName)));
    }

    private String prepareAndUploadFile(String fileText) throws SftpException, JSchException {
        //Random timestamp to avoid similar filenames
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime().plusSeconds(new Random().nextInt(59));
        String currentDateString = currentDate.format(DateTimeUtils.TIME_STAMP);
        //No seconds inside file needed
        String dateForText = currentDateString.substring(0, currentDateString.length() - 2);
        File newFileName = new File(String.format(FILE_NAME, currentDateString));
        FileHelper.addTextToFile(newFileName, String.format(fileText, dateForText, null));
        SFTPConnection.getClient().uploadFile(newFileName, new File(FILE_PATH));
        return newFileName.getName();
    }
}