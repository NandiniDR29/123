package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.BillingBenefitsRestService;
import com.exigen.ren.rest.billing.BillingRestService;
import com.exigen.ren.rest.billing.model.*;
import com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments.BillingAccountsBenefitsPayments;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices.BillingAccountsInvoices;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices.BillingAccountsInvoicesCoveragesParticipants;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices_regenerate.BillingAccountsInvoicesGenerate;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices_regenerate.BillingAccountsInvoicesRegenerate;
import com.exigen.ren.rest.billing.model.billing_payment.BillingAccountPayment;
import com.exigen.ren.utils.components.Components;
import com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.INVOICING_CALENDAR_VALUE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PAYMENT_TAX;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentTaxMetaData.TAX_TYPE;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab.buttonAddTax;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage.labelBillingAccountName;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.getClaimNumber;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestExtendExistingRESTServicesToNonPremiumTypeBillingAccounts extends ClaimGroupBenefitsLTDBaseTest implements LongTermDisabilityMasterPolicyContext, DisabilityClaimContext {

    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private BillingBenefitsRestService billingBenefitsRestService = new BillingBenefitsRestService();
    private BillingRestService billingRestService = new BillingRestService();

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-47569", component = BILLING_REST)
    public void testExtendExistingRESTServicesToNonPremiumTypeBillingAccounts() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(longTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(locationManagementTab.getMetaKey()), caseProfile.getDefaultTestData(Components.CASE_PROFILE, "LocationManagementTab")));

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicySelfAdminData()
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true"));
        String masterPolicy = PolicySummaryPage.labelPolicyNumber.getValue();

        longTermDisabilityMasterPolicy.setupBillingGroups().perform(billingAccount.getDefaultTestData("SetupBillingGroups", "TestData_Update_With_Location"));
        billingAccount.navigateToBillingAccount();

        MainPage.QuickSearch.search(masterPolicy);

        TestData td = disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), YTD_TAXABLE_WAGE.getLabel()),
                        DataProviderFactory.dataOf(BenefitsLTDInjuryPartyInformationTabMetaData.YTDTaxableWageMetaData.YEAR.getLabel(), "2020"));
        disabilityClaim.create(td);
        disabilityClaim.claimOpen().perform();
        String claimNumber = getClaimNumber();
        disabilityClaim.calculateSingleBenefitAmount().perform(disabilityClaim.getLTDTestData().getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);


        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"), paymentAllocationTab.getClass(), true);

        paymentCalculatorTab.navigateToTab();
        buttonAddTax.click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_TAX).getAsset(TAX_TYPE).setValue("FICA Medicare Tax");

        paymentAllocationTab.navigateToTab();
        buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        mainApp().reopen(approvalUsername, approvalPassword);

        MainPage.QuickSearch.search(claimNumber);

        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);

        mainApp().reopen();

        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.issuePayment().perform(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        billingAccount.navigateToBillingAccountList();
        String ba2 = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        MainPage.QuickSearch.search(ba2);
        String accountName = labelBillingAccountName.getValue();
        mainApp().close();

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<List<BillingAccountModel>> response = billingRestService.getCustomersAccounts(customerNum);
        assertThat(response.getResponse()).hasStatus(200);
        List<BillingAccountModel> modelCustomersAccountList = response.getModel();
        assertThat(modelCustomersAccountList).hasSize(2);
        BillingAccountModel modelCustomersAccount = response.getModel().stream().filter(i -> i.getAccountNumber().equals(ba2)).findFirst().orElseThrow(() -> new IstfException(String.format("Account num %s is not found", accountName)));
        assertThat(modelCustomersAccount.getMasterPolicies()).hasSize(1);
        assertSoftly(softly -> {
            softly.assertThat(modelCustomersAccount.getAccountName()).isEqualTo(accountName);
            softly.assertThat(modelCustomersAccount.getAccountNumber()).isEqualTo(ba2);
            softly.assertThat(modelCustomersAccount.getBlobCd()).isEqualTo("G");
            softly.assertThat(modelCustomersAccount.getBillType()).isEqualTo(null);
            softly.assertThat(modelCustomersAccount.getInvoicingCalendar().getFrequency()).isEqualTo("Monthly");
            softly.assertThat(modelCustomersAccount.getInvoicingCalendar().getDueDay()).isEqualTo("15");
            softly.assertThat(modelCustomersAccount.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy);
        });

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<List<BillingAccountsActionsModel>> response2 = billingRestService.getAccountBillingReturnActions(ba2);
        assertThat(response2.getResponse()).hasStatus(200);
        assertThat(response2.getModel().stream().map(BillingAccountsActionsModel::getActionCd).collect(Collectors.toList())).isEqualTo(
                ImmutableList.of("inquiryNonPremiumBillingAccount", "updateNonPremiumBillingAccount", "generateDraftBill", "billableAmountAdjustment", "generateStatement"));

        LOGGER.info("---=={Step 3}==---");
        ResponseContainer<List<BillingBenefitsAccountsModel>> response3 =
                billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customerNum), null, null, null, null, null, null);
        assertThat(response3.getResponse()).hasStatus(200);
        List<BillingBenefitsAccountsModel> modelAccountsBillingAccountList = response3.getModel();
        assertThat(modelAccountsBillingAccountList).hasSize(2);
        BillingBenefitsAccountsModel modelAccountsBillingAccount = modelAccountsBillingAccountList.stream()
                .filter(i -> i.getAccountNumber().equals(ba2)).findFirst().orElseThrow(() -> new IstfException(String.format("Account num %s is not found", accountName)));

        assertSoftly(softly -> {
            softly.assertThat(modelAccountsBillingAccount.getCustomerNumber()).isEqualTo(customerNum);
            softly.assertThat(modelAccountsBillingAccount.getAccountNumber()).isEqualTo(ba2);
            softly.assertThat(modelAccountsBillingAccount.getAccountName()).isEqualTo(accountName);
            softly.assertThat(modelAccountsBillingAccount.getGroupType()).isEqualTo("NON_PREMIUM");
            softly.assertThat(modelAccountsBillingAccount.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy);
        });

        LOGGER.info("---=={Step 4}==---");
        ResponseContainer<BillingAccountsInvoicesGenerate> responseGenerate = billingBenefitsRestService.postAccountsInvoicesGenerate(ba2);
        assertThat(responseGenerate.getResponse().getStatus()).isEqualTo(200);
        String invoiceNumber = responseGenerate.getModel().getInvoiceNumber();
        assertThat(invoiceNumber).isNotNull().isNotEmpty();

        LOGGER.info("---=={Step 5}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response4 = billingBenefitsRestService.getAccountsAccountNumberInvoices(ba2);
        assertThat(response4.getResponse().getStatus()).isEqualTo(200);
        assertThat(response4.getModel()).hasSize(1);
        assertThat(response4.getModel().get(0).getInvoiceNumber()).isEqualTo(invoiceNumber);

        LOGGER.info("---=={Step 6}==---");
        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response5 =
                billingBenefitsRestService.getCustomerInvoices(customerNum, ba2, null, null, null, null, null);
        assertThat(response5.getResponse().getStatus()).isEqualTo(200);
        assertThat(response5.getModel()).hasSize(1);
        assertThat(response5.getModel().get(0).getInvoiceNumber()).isEqualTo(invoiceNumber);

        LOGGER.info("---=={Step 7}==---");
        mainApp().reopen();
        MainPage.QuickSearch.search(ba2);
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());
        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(ImmutableMap.of(TableConstants.BillingBillsAndStatementsGB.INVOICE.getName(), invoiceNumber))
                .getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDate = LocalDate.parse(billingPeriod.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);
        TestData tdCash = billingAccount.defaultAPITestData().getTestData("RestAccountsBenefitsPayments", "TestData_Cash")
                .adjust(TestData.makeKeyPath("amount", "value"), policyTotalDue.toPlainString());

        ResponseContainer<BillingAccountsBenefitsPayments> response6 = billingBenefitsRestService.postAccountsBenefitsPayments("REST1", invoiceNumber, tdCash);
        assertThat(response6.getResponse()).hasStatus(200);

        LOGGER.info("---=={Step 8}==---");
        ResponseContainer<List<BillingAccountPayment>> response7 = billingBenefitsRestService.getAccountsAccountNumberPayments(ba2, null, null, null, null, null, null);
        assertThat(response7.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            List<BillingAccountPayment> responseModel = response7.getModel();
            softly.assertThat(responseModel.size()).isEqualTo(1);
            responseModel.forEach(billingAccountPayment -> {
                softly.assertThat(billingAccountPayment.getTransactionType()).isEqualTo("PAYMENT");
                softly.assertThat(billingAccountPayment.getBalanceAmount()).isEqualTo(policyTotalDue.toPlainString());
                softly.assertThat(billingAccountPayment.getPaymentAmount()).isEqualTo(policyTotalDue.toPlainString());
                softly.assertThat(billingAccountPayment.getPaymentDetails().getPaymentMethod()).isEqualTo("cash");
            });
        });

        LOGGER.info("---=={Step 9}==---");
        billingAccount.discardBill().perform(new SimpleDataProvider());

        LOGGER.info("---=={Step 10}==---");
        ResponseContainer<BillingAccountsInvoicesRegenerate> response8 = billingBenefitsRestService.postAccountsInvoicesRegenerate(ba2, invoiceNumber);
        assertThat(response8.getResponse().getStatus()).isEqualTo(200);
        BillingAccountsInvoicesRegenerate invoiceRegenerate = response8.getModel();
        assertThat(invoiceRegenerate.getDocument()).isNotNull();
        assertThat(invoiceRegenerate.getInvoiceNumber()).isNotNull();

        LOGGER.info("---=={Step 11}==---");
        ResponseContainer<BillingGenerateDraftBillModel> response9 = billingBenefitsRestService.postGenerateDraftBill(ba2);
        assertThat(response9.getResponse()).hasStatus(200);
        assertThat(response9.getModel().getDocgenTicket()).isNotNull();

        LOGGER.info("---=={Step 12}==---");
        ResponseContainer<List<BillingAccountsInvoicesCoveragesParticipants>> response10 = billingBenefitsRestService.getAccountsInvoicesCoveragesParticipants(ba2, invoiceNumber, invoiceNumber);
        assertThat(response10.getResponse().getStatus()).isEqualTo(404);
        assertThat(response10.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response10.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", ba2));

        ResponseContainer<BillingAccountsInvoices> response11 = billingBenefitsRestService.getAccountsInvoices(ba2, invoiceNumber);
        assertThat(response11.getResponse().getStatus()).isEqualTo(404);
        assertThat(response11.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response11.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", ba2));

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response12 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba2, startDate, endDate);
        assertThat(response12.getResponse().getStatus()).isEqualTo(404);
        assertThat(response12.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response12.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", ba2));

        ResponseContainer<List<BillingInvoiceCoveragePeriodModel>> response13 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriods(ba2);
        assertThat(response13.getResponse().getStatus()).isEqualTo(404);
        assertThat(response13.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response13.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", ba2));

        BillingInvoiceCoveragePeriodDatePUTModel model = new BillingInvoiceCoveragePeriodDatePUTModel();
        model.setTransactionReason("test");
        BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems premiumItemsModel = new BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems();
        premiumItemsModel.setItemId("3");
        premiumItemsModel.setPeriodAmount("2");
        premiumItemsModel.setNumberOfInsureds("0");
        premiumItemsModel.setBenefitsAmountOrVolume("0");
        model.setPremiumItems(Collections.singletonList(premiumItemsModel));
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response14 = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(model, ba2, startDate, endDate);
        assertThat(response14.getResponse().getStatus()).isEqualTo(404);
        assertThat(response14.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response14.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", ba2));

        ResponseContainer<List<BillingAccountsInvoicesGenerate>> response15 = billingRestService.getAccountsAccountNumber(ba2);
        assertThat(response15.getResponse().getStatus()).isEqualTo(404);
        assertThat(response15.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response15.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", ba2));
    }
}