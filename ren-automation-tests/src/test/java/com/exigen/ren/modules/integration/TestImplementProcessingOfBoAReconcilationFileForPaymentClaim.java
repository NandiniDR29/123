package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Random;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndAdjustmentsStatusGB.CLEARED;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.ISSUED;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestImplementProcessingOfBoAReconcilationFileForPaymentClaim extends ClaimGroupBenefitsACBaseTest implements JobContext {

    private static final String FILE_NAME = "RENAISSANCE_DAILY_PAID_%s.txt";
    private static final String FILE_PATH = "/mnt/ren/shared/checkfile/inbound/";
    private static final String FILE_PATH_ARCHIVE = "/mnt/ren/archive/checkfile/archive/";


    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-43619", component = INTEGRATION)
    public void testImplementProcessingOfBoAReconcilationFileForPaymentClaim() throws JSchException, SftpException {

        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_AC);
        createDefaultGroupAccidentClaimForCertificatePolicy();
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        String amount = "20.33";

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_AccidentalDeath"), 1);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT.getLabel()), amount)
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()), amount)
                .resolveLinks());
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        String checkNumber = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.CHECK_EFT).getValue();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries)
                .with(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.TRANSACTION_STATUS, ISSUED).hasMatchingRows(1);

        LOGGER.info("---=={Step 4}==---");
        String fileText = "111111111115H                      05222020                                                                                                                                                                                                                                                                                 \n" +
                "111111111115R000%s00000000203305222020          CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANCO MAINTENANCE CORP CLEANA       \n" +
                "111111111115T000%s000000002033  ";

        String newFileName = prepareAndUploadFile(String.format(fileText, checkNumber, checkNumber));
        JobRunner.executeJob(GeneralSchedulerPage.CHECK_BOA_RECONCILIATION_FILE_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PAYMENTS.get());
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries)
                .with(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.TRANSACTION_STATUS, CLEARED).hasMatchingRows(1);

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