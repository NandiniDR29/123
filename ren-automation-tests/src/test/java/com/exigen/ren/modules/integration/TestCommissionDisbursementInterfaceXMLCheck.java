package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.CommissionsInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.common.pages.MainPage;
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
import com.exigen.ren.utils.XmlValidator;
import com.google.common.collect.ImmutableMap;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.admin.modules.agencyvendor.agency.tabs.CommissionsInfoTab.tableCommissionPaymentHistory;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.BLT_COMMISSION_PAYMENT_JOB;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.*;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.AGENCY_PRODUCER_COMBO;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.modules.integration.ledgerTransferJob.LedgerTransferJobBaseTest.readFile;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCommissionDisbursementInterfaceXMLCheck extends CommissionStrategyBaseTest implements JobContext, CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext, BillingAccountContext {
    private static final String FILE_NAME ="RENAISSANCE_BOA_CHECK_COM_%s";
    private static final String FILE_PATH = "/mnt/ren/shared/commissionpaymentbottomline/outbound";
    private static final String FILE_NAME_ARCHIVE = "Commission_Payment_BLTReport_%s";
    private static final String FILE_PATH_ARCHIVE = "/mnt/ren/archive/commissionpaymentbottomlinereport/archive";

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = {"REN-42020","REN-42021", "REN-42018", "REN-45033"}, component = INTEGRATION)
    public void testCommissionDisbursementInterface() throws JSchException, SftpException, IOException {


        adminApp().open();
        //Create Agency1 with Channel = Agency
        String agencyName = agency.createAgency(tdAgencyDefault
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

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENCY_PRODUCER_COMBO.getLabel()), agencyName));
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

        LOGGER.info("---=={REN-42018 Step 1-2}==---");
        adminApp().close();
        JobRunner.executeJob(BLT_COMMISSION_PAYMENT_JOB);

        LOGGER.info("---=={REN-42018 Step 3}==---");
        adminApp().reopen();
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        String agencyCode = AgencyVendorSearchTab.tableAgencies.getRowContains("Agency Name", agencyName).getCell("Agency Code").getValue();
        agencyVendorSearchTab.openFirst();
        commissionTab.navigateToTab();
        assertThat(tableCommissionPaymentHistory.getRow(1)).hasCellWithValue(CommissionsInfoTab.CommissionPaymentHistory.STATUS.getName(), "issued");

        String refId = tableCommissionPaymentHistory.getRow(1).getCell(CommissionsInfoTab.CommissionPaymentHistory.REFERENCE_ID.getName()).getValue();

        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();
        String refNum = currentTime.format(YYYYMMDD);
        String fileName = SFTPConnection.getClient().getFilesList(new File(FILE_PATH)).stream().filter(f -> f.contains(String.format(FILE_NAME, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME, refNum))));

        String destinationDir = PropertyProvider.getProperty(TestProperties.BROWSER_DOWNLOAD_FILES_LOCATION);
        SFTPConnection.getClient().downloadFile(new File(FILE_PATH, fileName), new File(destinationDir));

        LOGGER.info("---=={REN-42020 Step 5}==---");
        XmlValidator xmlValidator = new XmlValidator(new File(destinationDir, fileName));

        xmlValidator.checkDocument("//FileCreateDate", currentTime.format(YYYY_MM_DD));
        xmlValidator.checkDocument("//FileSource", "RLHI");
        xmlValidator.checkDocumentContains("//FileTraceNumber", "COM00");
        xmlValidator.checkDocument("//FileVersion", "XMLv1.1 CSv6.7.2");

        xmlValidator.checkDocument("//ApplicationName", "COMREN");
        xmlValidator.checkDocument("//BatchDescription",  String.format("REN Commission Check Payments %s", currentTime.format(MMDDYYYY)));
        xmlValidator.checkDocument("//BatchRemitType", "");
        xmlValidator.checkDocument("//EffectiveDate", currentTime.format(YYYY_MM_DD));
        xmlValidator.checkDocument("//BatchStatus", "AP");
        xmlValidator.checkDocument("//UserDefinedShortString1", "America");
        xmlValidator.checkDocumentContains("//Transactions//CheckNumber", "5000");
        xmlValidator.checkDocument("//TranAmount", "1.43");
        xmlValidator.checkDocument("//AmountText", "");
        xmlValidator.checkDocument("//PayeeName1", agencyName);
        xmlValidator.checkDocument("//AddressLine1", "Address Line 1");
        xmlValidator.checkDocument("//AddressLine2", "");
        xmlValidator.checkDocument("//AddressLine3", "");
        xmlValidator.checkDocument("//City", "Walnut Creek");
        xmlValidator.checkDocument("//State", "CA");
        xmlValidator.checkDocument("//PostalCode", "94596");
        xmlValidator.checkDocument("//TranDate",  currentTime.format(YYYY_MM_DD));

        LOGGER.info("---=={Test REN-45033}==---");
        xmlValidator.checkDocument("//LongString1", "Commission Payment");
        xmlValidator.checkDocument("//UserDefinedShortString3", refId);
        xmlValidator.checkDocument("//Note", "Note: refer to commission statement for details");
        xmlValidator.checkDocument("//UserDefinedShortString2", agencyCode);

        LOGGER.info("---=={Test REN-42021}==---");
        String fileNameArchive = SFTPConnection.getClient().getFilesList(new File(FILE_PATH_ARCHIVE)).stream().filter(f -> f.contains(String.format(FILE_NAME_ARCHIVE, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME_ARCHIVE, refNum))));

        SFTPConnection.getClient().downloadFile(new File(FILE_PATH_ARCHIVE, fileNameArchive), new File(destinationDir));

        List<String> list = readFile(StringUtils.join(Arrays.asList(destinationDir, fileNameArchive), "/"));

        assertThat(list.get(0)).isEqualTo(String.format("Report Date,%s" , currentTime.format(MM_DD_YYYY)));
        assertThat(list.get(1)).isEqualTo(String.format("File Name,%s", fileName));
        assertThat(list.get(2)).isEqualTo("Outbound Directory,file:/mnt/ren/shared/commissionpaymentbottomline/outbound");
        assertThat(list.get(3)).isEqualTo("Archive Directory,file:/mnt/ren/archive/commissionpaymentbottomline/archive");
        assertThat(list.get(4)).isEqualTo(String.format("File Process Date & Time,%s", fileNameArchive.substring(29,43)));
        assertThat(list.get(5)).isEqualTo("Total Transaction Count,1");
        assertThat(list.get(6)).isEqualTo("Total Transaction Amount,1.43");
    }
}