package com.exigen.ren.modules.billing.api;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices_regenerate.BillingAccountsInvoicesGenerate;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingReturnInvoicesOfBillingAccountsOfEmployer extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40791", component = BILLING_REST)
    public void TestBillingReturnInvoicesOfBillingAccountsOfEmployerFull() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customer1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(GroupBenefitsMasterPolicyType.GB_AC);

        createDefaultNonIndividualCustomer();
        String customer2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(GroupBenefitsMasterPolicyType.GB_AC);

        createPolicyFullAdminOneCoverageEnhancedAccident();
        billingAccount.navigateToBillingAccountList();
        String ba1 = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(BILLING_ACCOUNT.getName()).getValue();

        createPolicySelfAdmin();
        billingAccount.navigateToBillingAccountList();
        String ba2 = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(BILLING_ACCOUNT.getName()).getValue();

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response = billingBenefitsRestService.getCustomerInvoices("test", null, null, null, null, null, null);
        assertThat(response.getResponse().getStatus()).isEqualTo(404);
        assertThat(response.getError().getErrorCode()).isEqualTo("CUSTOMER_NOT_FOUND");
        assertThat(response.getError().getMessage()).isEqualTo(String.format("Customer #%s not found", "test"));

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response2 = billingBenefitsRestService.getCustomerInvoices(customer1, null, null, null, null, null, null);
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);
        assertThat(response2.getModel()).isEmpty();

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response3 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, null, null, null, null);
        assertThat(response3.getResponse().getStatus()).isEqualTo(200);
        assertThat(response3.getModel()).isEmpty();

        LOGGER.info("---=={Step 2}==---");
        MainPage.QuickSearch.search(ba1);
        IntStream.range(0, 12).forEach($ -> billingAccount.generateFutureStatement().perform(new SimpleDataProvider()));
        String invoiceNumberFull3 = BillingSummaryPage.tableBillsAndStatements.getRow(10).getCell(INVOICE).getValue();
        String invoiceNumberFull5 = BillingSummaryPage.tableBillsAndStatements.getRow(8).getCell(INVOICE).getValue();
        String invoiceNumberFull6 = BillingSummaryPage.tableBillsAndStatements.getRow(7).getCell(INVOICE).getValue();
        String invoiceNumberFull7 = BillingSummaryPage.tableBillsAndStatements.getRow(6).getCell(INVOICE).getValue();
        String invoiceNumberFull8 = BillingSummaryPage.tableBillsAndStatements.getRow(5).getCell(INVOICE).getValue();
        String invoiceNumberFull9 = BillingSummaryPage.tableBillsAndStatements.getRow(4).getCell(INVOICE).getValue();
        String invoiceNumberFull10 = BillingSummaryPage.tableBillsAndStatements.getRow(3).getCell(INVOICE).getValue();
        String invoiceNumberFull11 = BillingSummaryPage.tableBillsAndStatements.getRow(2).getCell(INVOICE).getValue();
        String invoiceNumberFull12 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDate = LocalDate.parse(billingPeriod.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        MainPage.QuickSearch.search(ba2);
        IntStream.range(0, 12).forEach($ -> billingAccount.generateFutureStatement().perform(new SimpleDataProvider()));
        String invoiceNumberSelf1 = BillingSummaryPage.tableBillsAndStatements.getRow(12).getCell(INVOICE).getValue();
        String invoiceNumberSelf2 = BillingSummaryPage.tableBillsAndStatements.getRow(11).getCell(INVOICE).getValue();
        String invoiceNumberSelf3 = BillingSummaryPage.tableBillsAndStatements.getRow(10).getCell(INVOICE).getValue();
        String invoiceNumberSelf4 = BillingSummaryPage.tableBillsAndStatements.getRow(9).getCell(INVOICE).getValue();
        String invoiceNumberSelf5 = BillingSummaryPage.tableBillsAndStatements.getRow(8).getCell(INVOICE).getValue();
        String invoiceNumberSelf6 = BillingSummaryPage.tableBillsAndStatements.getRow(7).getCell(INVOICE).getValue();
        String invoiceNumberSelf7 = BillingSummaryPage.tableBillsAndStatements.getRow(6).getCell(INVOICE).getValue();
        String invoiceNumberSelf8 = BillingSummaryPage.tableBillsAndStatements.getRow(5).getCell(INVOICE).getValue();
        String invoiceNumberSelf9 = BillingSummaryPage.tableBillsAndStatements.getRow(4).getCell(INVOICE).getValue();
        String invoiceNumberSelf10 = BillingSummaryPage.tableBillsAndStatements.getRow(3).getCell(INVOICE).getValue();
        String invoiceNumberSelf11 = BillingSummaryPage.tableBillsAndStatements.getRow(2).getCell(INVOICE).getValue();
        String invoiceNumberSelf12 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        assertSoftly(softly -> {
            ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, null, null, null, null);
            softly.assertThat(response4.getResponse().getStatus()).isEqualTo(200);

            softly.assertThat(response4.getModel()).hasSize(10);
            response4.getModel().forEach(billingAccountsInvoice -> {
                softly.assertThat(billingAccountsInvoice.getUip()).isNotNull();
                softly.assertThat(billingAccountsInvoice.getUip().getValue()).isNotNull().isNotEmpty();
                softly.assertThat(billingAccountsInvoice.getUip().getCurrency()).isNotNull().isNotEmpty();
            });
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 = billingBenefitsRestService.getCustomerInvoices(customer2, ba1, null, null, null, null, null);
            softly.assertThat(response4.getResponse().getStatus()).isEqualTo(200);

            softly.assertThat(response4.getModel()).hasSize(10);
            response4.getModel().forEach(billingAccountsInvoice -> {
                softly.assertThat(billingAccountsInvoice.getUip()).isNotNull();
                softly.assertThat(billingAccountsInvoice.getUip().getValue()).isNotNull().isNotEmpty();
                softly.assertThat(billingAccountsInvoice.getUip().getCurrency()).isNotNull().isNotEmpty();
            });
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 = billingBenefitsRestService.getCustomerInvoices(customer2, ba2, null, null, null, null, null);
            softly.assertThat(response4.getResponse().getStatus()).isEqualTo(200);

            softly.assertThat(response4.getModel()).hasSize(10);
            response4.getModel().forEach(billingAccountsInvoice -> softly.assertThat(billingAccountsInvoice.getInvoiceAdditionalStatusCd()).isNotNull());
        });

        LOGGER.info("---=={Step 3}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, null, null, null, "30");
            softly.assertThat(response4.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response4.getModel()).hasSize(24);
        });

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, null, null, "5", "30");
        assertThat(response4.getResponse().getStatus()).isEqualTo(200);
        ImmutableList.of(invoiceNumberFull6, invoiceNumberFull7, invoiceNumberFull8, invoiceNumberFull9, invoiceNumberFull10, invoiceNumberFull11, invoiceNumberFull12, invoiceNumberSelf1, invoiceNumberSelf2, invoiceNumberSelf3, invoiceNumberSelf4, invoiceNumberSelf5,
                invoiceNumberSelf6, invoiceNumberSelf7, invoiceNumberSelf8, invoiceNumberSelf9, invoiceNumberSelf10, invoiceNumberSelf11, invoiceNumberSelf12).forEach(invoice -> response4.getModel().stream().filter(model -> model.getInvoiceNumber().equals(invoice)).findFirst()
                        .orElseThrow(() -> new IstfException(String.format("Invoice %s is not found", invoice))));

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response5 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, null, null, "4", "14");
        assertThat(response5.getResponse().getStatus()).isEqualTo(200);
        ImmutableList.of(invoiceNumberFull5, invoiceNumberFull6, invoiceNumberFull7, invoiceNumberFull8, invoiceNumberFull9, invoiceNumberFull10, invoiceNumberFull11, invoiceNumberFull12, invoiceNumberSelf1, invoiceNumberSelf2, invoiceNumberSelf3, invoiceNumberSelf4, invoiceNumberSelf5,
                invoiceNumberSelf6).forEach(invoice -> response5.getModel().stream().filter(model -> model.getInvoiceNumber().equals(invoice)).findFirst()
                    .orElseThrow(() -> new IstfException(String.format("Invoice %s is not found", invoice))));

        LOGGER.info("---=={Step 4}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response6 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, startDate, endDate, "4", "14");
        assertThat(response6.getResponse().getStatus()).isEqualTo(200);
        assertThat(response6.getModel()).isEmpty();

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response7 = billingBenefitsRestService.getCustomerInvoices(customer2, null, null, startDate, endDate, "0", "14");
        assertThat(response7.getResponse().getStatus()).isEqualTo(200);
        assertThat(response7.getModel()).hasSize(2);
        ImmutableList.of(invoiceNumberFull12, invoiceNumberSelf12).forEach(invoice -> response7.getModel().stream().filter(model -> model.getInvoiceNumber().equals(invoice)).findFirst()
                    .orElseThrow(() -> new IstfException(String.format("Invoice %s is not found", invoice)))
        );

        LOGGER.info("---=={Step 5}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response8 = billingBenefitsRestService.getCustomerInvoices(customer2, ba2, invoiceNumberSelf2, startDate, endDate, "0", "14");
        assertThat(response8.getResponse().getStatus()).isEqualTo(200);
        assertThat(response8.getModel()).isEmpty();

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response9 = billingBenefitsRestService.getCustomerInvoices(customer2, ba2, invoiceNumberSelf2, null, null, "0", "14");
        assertThat(response9.getResponse().getStatus()).isEqualTo(200);
        assertThat(response9.getModel()).hasSize(1);
        assertThat(response9.getModel().get(0).getInvoiceAdditionalStatusCd()).isNotNull();

        LOGGER.info("---=={Step 6}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response10 = billingBenefitsRestService.getCustomerInvoices(customer2, ba1, invoiceNumberFull3, null, null, "0", "14");
        assertThat(response10.getResponse().getStatus()).isEqualTo(200);
        assertThat(response10.getModel()).hasSize(1);
        assertThat(response10.getModel().get(0).getUip()).isNotNull();

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response11 = billingBenefitsRestService.getCustomerInvoices(customer2, ba2, invoiceNumberFull3, null, null, "0", "14");
        assertThat(response11.getResponse().getStatus()).isEqualTo(200);
        assertThat(response8.getModel()).isEmpty();
    }
}