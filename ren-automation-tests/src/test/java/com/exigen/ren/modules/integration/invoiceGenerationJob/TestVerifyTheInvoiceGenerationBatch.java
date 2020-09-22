package com.exigen.ren.modules.integration.invoiceGenerationJob;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.INVOICE_UPLOAD_JOB;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerifyTheInvoiceGenerationBatch extends InvoiceGenerationJobBaseTest implements JobContext, CustomerContext, BillingAccountContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-19099", "REN-19092"}, component = INTEGRATION)
    public void testVerifyTheInvoiceGenerationBatch() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        billingAccount.navigateToBillingAccount();
        billingAccount.generateFutureStatement().perform();

        createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNumber2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        billingAccount.navigateToBillingAccount();
        billingAccount.generateFutureStatement().perform();

        mainApp().close();
        LOGGER.info("---=={TC1 Step 3}==---");
        JobRunner.executeJob(INVOICE_UPLOAD_JOB);

        LOGGER.info("---=={TC1 Step 4}==---");
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        String FILE_NAME = "BFC_Matrix_EIS_DEV_%s";
        String fileBFCMatrix = downloadFile(String.format(FILE_NAME, currentDate.format(YYYY_MM_DD_HH)));

        List<String> list = readFile(fileBFCMatrix);

        assertSoftly(softly -> {
            LOGGER.info("---=={TC1 Step 7}==---");
            String[] columnList = list.get(0).split(",");
            softly.assertThat(columnList[0]).isEqualTo("Grp#");
            softly.assertThat(columnList[1]).isEqualTo("Grp Name");
            softly.assertThat(columnList[2]).isEqualTo("Contact");
            softly.assertThat(columnList[3]).isEqualTo("Address Line 1");
            softly.assertThat(columnList[4]).isEqualTo("Address Line 2");
            softly.assertThat(columnList[5]).isEqualTo("City");
            softly.assertThat(columnList[6]).isEqualTo("State");
            softly.assertThat(columnList[7]).isEqualTo("ZIP");

            LOGGER.info("---=={TC2 Step 6}==---");
            String customerData1 = list.stream().filter(valueList -> valueList.split(",")[0].equals(customerNumber)).findFirst().
                    orElseThrow(() -> new IstfException(String.format("Customer %s is not found", customerNumber)));

            String customerData2 = list.stream().filter(valueList -> valueList.split(",")[0].equals(customerNumber2)).findFirst().
                    orElseThrow(() -> new IstfException(String.format("Customer %s is not found", customerNumber2)));
            softly.assertThat(customerData1).contains("Address not found");

            LOGGER.info("---=={TC2 Step 7}==---");
            softly.assertThat(customerData2).doesNotContain("Address not found");
        });
    }
}