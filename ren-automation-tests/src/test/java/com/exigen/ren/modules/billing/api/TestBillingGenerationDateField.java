package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices_regenerate.BillingAccountsInvoicesGenerate;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingGenerationDateField extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext{


    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-41917", component = BILLING_REST)
    public void testDraftBillGenerationSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String invoiceNum1 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        String invoiceDate1 = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.ISSUE_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).format(YYYY_MM_DD);
        String ba1 = getBillingAccountNumber(masterPolicyNumber.get());

        createPolicyFullAdmin();
        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String invoiceNum2 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        String invoiceDate2 = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.ISSUE_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).format(YYYY_MM_DD);
        String ba2 = getBillingAccountNumber(masterPolicyNumber.get());

        LOGGER.info("---=={Step 1}==---");
        verifyInvoice(ba1, invoiceNum1, invoiceDate1);
        verifyInvoice(ba2, invoiceNum2, invoiceDate2);

        LOGGER.info("---=={Step 2}==---");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).plusMonths(1));
        LOGGER.info("Run job 'benefits.billingInvoiceJob'");
        JobRunner.executeJob(GeneralSchedulerPage.BENEFITS_BILLING_INVOICE_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(ba1);
        String invoiceNum3 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        String invoiceDate3 = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.ISSUE_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).format(YYYY_MM_DD);
        verifyInvoiceAfterTimeShift(ba1, invoiceNum1, invoiceNum3, invoiceDate1, invoiceDate3);

        MainPage.QuickSearch.search(ba2);
        String invoiceNum4 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        String invoiceDate4 = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.ISSUE_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).format(YYYY_MM_DD);
        verifyInvoiceAfterTimeShift(ba2, invoiceNum2, invoiceNum4, invoiceDate2, invoiceDate4);

        LOGGER.info("---=={Step 1 TC2}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response =
                billingBenefitsRestService.getCustomerInvoices(customerNum, null, null, null, null, null, null);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel()).hasSize(4);
        BillingAccountsInvoicesGenerate invoice1 = getInvoiceModel(response, invoiceNum1);
        BillingAccountsInvoicesGenerate invoice2 = getInvoiceModel(response, invoiceNum2);
        BillingAccountsInvoicesGenerate invoice3 = getInvoiceModel(response, invoiceNum3);
        BillingAccountsInvoicesGenerate invoice4 = getInvoiceModel(response, invoiceNum4);
        verifyGenerationDate(invoice1, invoiceDate1);
        verifyGenerationDate(invoice2, invoiceDate2);
        verifyGenerationDate(invoice3, invoiceDate3);
        verifyGenerationDate(invoice4, invoiceDate4);

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(5));
        mainApp().reopen();

        MainPage.QuickSearch.search(ba1);
        billingAccount.generateFutureStatement().perform();
        String invoiceNum5 =  BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        String invoiceDate5 = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.ISSUE_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).format(YYYY_MM_DD);

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response2 =
                billingBenefitsRestService.getCustomerInvoices(customerNum, ba1, null, null, null, null, null);
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);
        assertThat(response2.getModel()).hasSize(3);
        BillingAccountsInvoicesGenerate invoiceba1Model1 = getInvoiceModel(response2, invoiceNum1);
        BillingAccountsInvoicesGenerate invoiceba1Model2 = getInvoiceModel(response2, invoiceNum3);
        BillingAccountsInvoicesGenerate invoiceba1Model3 = getInvoiceModel(response2, invoiceNum5);
        verifyGenerationDate(invoiceba1Model1, invoiceDate1);
        verifyGenerationDate(invoiceba1Model2, invoiceDate3);
        verifyGenerationDate(invoiceba1Model3, invoiceDate5);

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response3 =
                billingBenefitsRestService.getCustomerInvoices(customerNum, null, invoiceNum1, null, null, null, null);
        assertThat(response3.getResponse().getStatus()).isEqualTo(200);
        assertThat(response3.getModel()).hasSize(1);
        response3.getModel().forEach(model -> assertThat(model.getGenerationDate()).contains(invoiceDate1));

        LOGGER.info("---=={Step 2 TC2}==---");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(3));
        mainApp().reopen();
        MainPage.QuickSearch.search(ba2);
        billingAccount.generateFutureStatement().perform();
        String invoiceNum3ba2 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        String invoiceDate6 = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.ISSUE_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).format(YYYY_MM_DD);

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 =
                billingBenefitsRestService.getCustomerInvoices(customerNum, ba2, null, null, null, null, null);
        assertThat(response4.getResponse().getStatus()).isEqualTo(200);
        assertThat(response4.getModel()).hasSize(3);
        BillingAccountsInvoicesGenerate invoiceba2Model1 = getInvoiceModel(response4, invoiceNum2);
        BillingAccountsInvoicesGenerate invoiceba2Model2 = getInvoiceModel(response4, invoiceNum4);
        BillingAccountsInvoicesGenerate invoiceba2Model3 = getInvoiceModel(response4, invoiceNum3ba2);
        verifyGenerationDate(invoiceba2Model1, invoiceDate2);
        verifyGenerationDate(invoiceba2Model2, invoiceDate4);
        verifyGenerationDate(invoiceba2Model3, invoiceDate6);

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response5 =
                billingBenefitsRestService.getCustomerInvoices(customerNum, null, invoiceNum3ba2, null, null, null, null);
        assertThat(response5.getResponse().getStatus()).isEqualTo(200);
        assertThat(response5.getModel()).hasSize(1);
        response5.getModel().forEach(model -> assertThat(model.getGenerationDate()).contains(invoiceDate6));
    }

    private void verifyInvoice(String ba, String invoiceNum, String invoiceDate) {
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response = billingBenefitsRestService.getAccountsAccountNumberInvoices(ba);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel()).hasSize(1);
        BillingAccountsInvoicesGenerate invoice = response.getModel().get(0);
        assertThat(invoice.getInvoiceNumber()).contains(invoiceNum);
        verifyGenerationDate(invoice, invoiceDate);
    }


    private void verifyInvoiceAfterTimeShift(String ba, String invoiceNum1, String invoiceNum2, String invoiceDate1, String invoiceDate2) {

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response = billingBenefitsRestService.getAccountsAccountNumberInvoices(ba);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel()).hasSize(2);
        BillingAccountsInvoicesGenerate invoice1 = getInvoiceModel(response, invoiceNum1);
        BillingAccountsInvoicesGenerate invoice2 = getInvoiceModel(response, invoiceNum2);
        verifyGenerationDate(invoice1, invoiceDate1);
        verifyGenerationDate(invoice2, invoiceDate2);
    }

    private BillingAccountsInvoicesGenerate getInvoiceModel(ResponseContainer<List<BillingAccountsInvoicesGenerate>> response, String invoiceNum){
        return response.getModel().stream().filter(model -> model.getInvoiceNumber().equals(invoiceNum)).findFirst().orElseThrow(() -> new IstfException(String.format("Invoice %s not found", invoiceNum)));
    }

    private void verifyGenerationDate(BillingAccountsInvoicesGenerate invoice, String invoiceDate) {
        Assertions.assertThat(invoice.getGenerationDate()).contains(invoiceDate);
    }
}