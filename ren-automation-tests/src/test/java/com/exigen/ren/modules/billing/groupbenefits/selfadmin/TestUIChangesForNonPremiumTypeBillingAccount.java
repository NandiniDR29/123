package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.exigen.ren.utils.components.Components;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.BamConstants.NON_PREMIUM_BA_CREATED;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.OPEN;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.*;
import static com.exigen.ren.main.enums.TableConstants.BillingPaymentsAndTransactionsGB.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.INVOICING_CALENDAR_VALUE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PAYMENT_TAX;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentTaxMetaData.TAX_TYPE;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab.buttonAddTax;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.YTDTaxableWageMetaData.BILLING_LOCATION;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.main.pages.summary.billing.BillingSummaryPage.buttonNotesAlerts;
import static com.exigen.ren.main.pages.summary.billing.BillingSummaryPage.buttonTasks;
import static com.exigen.ren.main.pages.summary.billing.BillingSummaryPage.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUIChangesForNonPremiumTypeBillingAccount extends ClaimGroupBenefitsLTDBaseTest implements BillingAccountContext, LongTermDisabilityMasterPolicyContext, DisabilityClaimContext {
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-44816", component = BILLING_GROUPBENEFITS)
    public void testBillByLocationOneCovTwoLocationsSameBgSameBaFull() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(longTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(locationManagementTab.getMetaKey()), caseProfile.getDefaultTestData(Components.CASE_PROFILE, "LocationManagementTab")));

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true"));

        longTermDisabilityCertificatePolicy.createPolicy(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), CertificatePolicyTabMetaData.BILLING_LOCATION.getLabel()), "LOC1")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        PolicySummaryPage.linkMasterPolicy.click();
        commonSteps(customerNumber, customerName);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-44816", component = BILLING_GROUPBENEFITS)
    public void testBillByLocationOneCovTwoLocationsSameBgSameBa() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(longTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(locationManagementTab.getMetaKey()), caseProfile.getDefaultTestData(Components.CASE_PROFILE, "LocationManagementTab")));

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicySelfAdminData()
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true"));

        longTermDisabilityMasterPolicy.setupBillingGroups().perform(billingAccount.getDefaultTestData("SetupBillingGroups", "TestData_Update_With_Location"));
        commonSteps(customerNumber, customerName);
    }

    private void commonSteps(String customerNumber, String customerName) {
        LOGGER.info("---=={Step 1}==---");
        TestData td = disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        initiateClaimWithPolicyAndFillToTab(td, benefitsLTDInjuryPartyInformationTab.getClass(), true);
        assertThat(BenefitsLTDInjuryPartyInformationTab.addYTDTaxableWage).isPresent();
        BenefitsLTDInjuryPartyInformationTab.addYTDTaxableWage.click();
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION)).isPresent().valueContains("LOC1");
        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(td, benefitsLTDIncidentTab.getClass());

        disabilityClaim.claimOpen().perform();
        String claimNumber = getClaimNumber();
        assertThat(labelClaimStatus).hasValue(OPEN);

        LOGGER.info("---=={Step 2}==---");
        disabilityClaim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"), paymentAllocationTab.getClass(), true);

        paymentCalculatorTab.navigateToTab();
        buttonAddTax.click();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_TAX).getAsset(TAX_TYPE)).containsAllOptions(EMPTY, "FICA Medicare Tax", "FICA Social Security Tax");
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

        LOGGER.info("---=={Step 3}==---");
        billingAccount.navigateToBillingAccountList();
        assertThat(BillingAccountsListPage.tableBenefitAccounts).hasRows(2);

        String ba2Name = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName()).getValue();
        String ba2 = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();
        assertThat(ba2Name).isEqualTo(String.format("Non-Premium_%s", customerName));

        MainPage.QuickSearch.search(ba2);

        assertThat(elementAccountNo).hasValue(ba2);
        assertThat(elementAccountName).hasValue(ba2Name);
        assertThat(elementCustomerNo).hasValue(customerNumber);
        assertThat(elementCustomerName).hasValue(customerName);
        assertThat(buttonPaymentsBillingMaintenance).isPresent();
        assertThat(buttonCreateTask).isPresent();
        assertThat(buttonTasks).isPresent();
        assertThat(buttonNotesAlerts).isPresent();

        assertThat(BillingSummaryPage.tableBillingGeneralInformation).hasRows(1);
        assertThat(BillingSummaryPage.tableBillableCoverages).with(TableConstants.BillingBillableCoveragesGB.TYPE, "FICA Match").with(TableConstants.BillingBillableCoveragesGB.SUBTYPE, "FMT");
        BillingAccountsListPage.verifyBamActivities(String.format(NON_PREMIUM_BA_CREATED, ba2), FINISHED);

        assertThat(BillingSummaryPage.tableBillsAndStatements.getHeader().getValue()).contains(ISSUE_DATE.getName(), DUE_DATE.getName(), INVOICE.getName(), STATUS.getName(), CURRENT_DUE.getName(),
                BILLING_PERIOD.getName(), PRIOR_DUE.getName(), TOTAL_DUE.getName(), REMAINING_DUE.getName(), TableConstants.BillingBillsAndStatementsGB.ACTION.getName());

        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getHeader().getValue()).contains(TRANSACTION_DATE.getName(), EFFECTIVE_DATE.getName(), POLICY_NUMBER.getName(),
                TYPE.getName(), SUBTYPE.getName(), REASON.getName(), AMOUNT.getName(), TableConstants.BillingPaymentsAndTransactionsGB.ACTION.getName());


        LOGGER.info("---=={Step 4}==---");
        createAndVerifyFSST(claimNumber, 2, ba2);
        createAndVerifyFSST(claimNumber, 3, ba2);
    }

    private void createAndVerifyFSST(String claimNumber, int num, String ba2) {
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"), paymentAllocationTab.getClass(), true);

        paymentCalculatorTab.navigateToTab();
        buttonAddTax.click();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_TAX).getAsset(TAX_TYPE)).containsAllOptions(EMPTY, "FICA Medicare Tax", "FICA Social Security Tax");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_TAX).getAsset(TAX_TYPE).setValue("FICA Social Security Tax");

        paymentAllocationTab.navigateToTab();
        buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        mainApp().reopen(approvalUsername, approvalPassword);

        MainPage.QuickSearch.search(claimNumber);

        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), num);

        mainApp().reopen();

        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.issuePayment().perform(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_IssuePayment"), num);

        billingAccount.navigateToBillingAccountList();
        assertThat(BillingAccountsListPage.tableBenefitAccounts).hasRows(2);

        MainPage.QuickSearch.search(ba2);

        assertThat(BillingSummaryPage.tableBillableCoverages).hasRows(2);
        assertThat(BillingSummaryPage.tableBillableCoverages).with(TableConstants.BillingBillableCoveragesGB.TYPE, "FICA Match").with(TableConstants.BillingBillableCoveragesGB.SUBTYPE, "FSST");
        BillingAccountsListPage.verifyBamActivities(String.format(NON_PREMIUM_BA_CREATED, ba2), FINISHED, 1);
    }
}