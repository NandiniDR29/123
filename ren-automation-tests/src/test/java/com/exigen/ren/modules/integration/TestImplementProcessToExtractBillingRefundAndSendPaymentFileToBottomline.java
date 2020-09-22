package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.google.common.collect.ImmutableMap;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.*;
import static com.exigen.ren.main.enums.ActionConstants.BillingAction.REFUND;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestImplementProcessToExtractBillingRefundAndSendPaymentFileToBottomline extends GroupBenefitsBillingBaseTest  {

    private static final String FILE_NAME ="RENAISSANCE_BOA_CHECK_RFD_%s";
    private static final String FILE_PATH = "/mnt/ren/shared/billingrefundbottomline/outbound";
    private static final String FILE_PATH_ARCHIVE = "/mnt/ren/archive/billingrefundbottomline/archive";
    private static final String FILE_NAME_ARCHIVE = "BillingRefund_BLTReport_%s";
    private static final String FILE_PATH_REPORT_ARCHIVE = "/mnt/ren/archive/billingrefundbottomlinereport/archive";

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41310", component = INTEGRATION)
    public void testImplementProcessToExtractBillingRefundAndSendPaymentFileToBottomline() throws SftpException, JSchException{

        commonPreconditions();

        LOGGER.info("TEST: Accept Payment for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "Payment_Methods_Credit_EFT"), modalPremiumAmount.get().add(new Currency(100)).toString());
        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED);

        LOGGER.info("---=={Step 1-2}==---");
        JobRunner.executeJob(BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB);

        LOGGER.info("---=={Step 3}==---");
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();
        String refNum = currentTime.format(YYYYMMDD);
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH)).stream().filter(f -> f.contains(String.format(FILE_NAME, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME, refNum))));

        LOGGER.info("---=={Step 4}==---");
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH_ARCHIVE)).stream().filter(f -> f.contains(String.format(FILE_NAME, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME, refNum))));

        LOGGER.info("---=={Step 5}==---");
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.ISSUED);

        LOGGER.info("---=={Step 7}==---");
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH_REPORT_ARCHIVE)).stream().filter(f -> f.contains(String.format(FILE_NAME_ARCHIVE, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME_ARCHIVE, refNum))));
    }


    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41312", component = INTEGRATION)
    public void testImplementProcessToExtractBillingRefundAndSendPaymentFileToBottomLineTC1() throws SftpException, JSchException {

        commonPreconditions();
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();
        LOGGER.info("TEST: Accept Payment for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "Payment_Methods_Credit_EFT"), modalPremiumAmount.get().add(new Currency(100)).toString());
        String timeX = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY_H_MM);
        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED);
        billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.ISSUE);
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.ISSUED);

        String timeY = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY_H_MM);
        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());

        LOGGER.info("---=={Step 1-3}==---");
        Job job = new Job(GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB_FROM_DATE_TO_DATE.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("fromDate=%s&toDate=%s",timeX, timeY)));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB_FROM_DATE_TO_DATE.getGroupName(), job));
        commonSteps(currentTime);
    }

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41312", component = INTEGRATION)
    public void testImplementProcessToExtractBillingRefundAndSendPaymentFileToBottomLineTC2() throws SftpException, JSchException  {

        commonPreconditions();
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();

        LOGGER.info("TEST: Accept Payment for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "Payment_Methods_Credit_EFT"), modalPremiumAmount.get().add(new Currency(100)).toString());

        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED);
        billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.ISSUE);
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.ISSUED);

        String paymentNum1 = BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.TYPE.getName()).getValue().split("\\D+")[1];

        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());
        String paymentNum2 = BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.TYPE.getName()).getValue().split("\\D+")[1];

        LOGGER.info("---=={Step 1-3}==---");
        Job job = new Job(GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB_PAYMENT.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("paymentNo=%s, %s",paymentNum2, paymentNum1)));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB_PAYMENT.getGroupName(), job));
        commonSteps(currentTime);
    }


    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41312", component = INTEGRATION)
    public void testImplementProcessToExtractBillingRefundAndSendPaymentFileToBottomLineTC3() throws SftpException, JSchException  {

        commonPreconditions();
        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();

        LOGGER.info("TEST: Accept Payment for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "Payment_Methods_Credit_EFT"), modalPremiumAmount.get().add(new Currency(100)).toString());
        String timeX = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY_H_MM);
        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED);
        billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.ISSUE);
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.ISSUED);

        String paymentNum1 = BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.TYPE.getName()).getValue().split("\\D+")[1];
        String timeY = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY_H_MM);
        billingAccount.refund().perform(billingAccount.getDefaultTestData(REFUND, "TestData_Check"), new Currency(100).toString());
        String paymentNum2 = BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.TYPE.getName()).getValue().split("\\D+")[1];

        LOGGER.info("---=={Step 1-3}==---");
        Job job = new Job(GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB_FROM_DATE_TO_DATE_AND_PAYMENT.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("fromDate=%s&toDate=%s&paymentNo=%s, %s",timeX, timeY, paymentNum2, paymentNum1)));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.BLT_BILLING_REFUND_PAYMENT_TRANSFER_JOB_FROM_DATE_TO_DATE_AND_PAYMENT.getGroupName(), job));
        commonSteps(currentTime);
    }
    private void commonPreconditions(){
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();

    }

    private void commonSteps(LocalDateTime currentTime) throws JSchException, SftpException {
        LOGGER.info("---=={Step 4}==---");

        String refNum = currentTime.format(YYYYMMDD);
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH)).stream().filter(f -> f.contains(String.format(FILE_NAME, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME, refNum))));

        LOGGER.info("---=={Step 5}==---");
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH_ARCHIVE)).stream().filter(f -> f.contains(String.format(FILE_NAME, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME, refNum))));

        LOGGER.info("---=={Step 6}==---");
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());

        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(2).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.ISSUED);

        LOGGER.info("---=={Step 7}==---");
        assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS.getName())).hasValue(
                BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED);

        LOGGER.info("---=={Step 9}==---");
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH_REPORT_ARCHIVE)).stream().filter(f -> f.contains(String.format(FILE_NAME_ARCHIVE, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME_ARCHIVE, refNum))));
    }

}
