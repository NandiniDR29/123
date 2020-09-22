package com.exigen.ren.modules.externalInterfaces.ddmi;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimConsolidatedPaymentsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.BILLING;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.pages.summary.claim.ClaimConsolidatedPaymentsPage.ListOfConsolidatedPayments.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimIntegrationWithPayments extends ClaimGroupBenefitsDNBaseTest {
    private DBService dbService = DBService.get();

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41339", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIntegrationWithPaymentsConsolidated() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks())
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()), "index=1"));
        groupDentalCertificatePolicy.createPolicy(getDefaultGroupDentalCertificatePolicyData()
                .adjust(tdSpecific().getTestData("TestData_CertificatePolicy_TwoInsured").resolveLinks()));
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(BILLING);
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), new Currency(policyTotalDue).toString()));

        LOGGER.info("Claim0 creation");
        MainPage.QuickSearch.search(certificatePolicyNumber);
        dentalClaim.create(tdSpecific().getTestData("TestData_Claim0").resolveLinks());
        String claimNumber0 = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        NavigationPage.toSubTab(FINANCIALS);
        String paymentNumber18 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();

        LOGGER.info("Claim4 creation");
        dentalClaim.create(tdSpecific().getTestData("TestData_Claim4").resolveLinks());
        String claimNumber4 = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        NavigationPage.toSubTab(FINANCIALS);
        String paymentNumber4 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        updateExternalPaymentMethod("Select", claimNumber4, paymentNumber4);
        updateConsolidatedPaymentNumber(paymentNumber18, claimNumber4, paymentNumber4);
        updateExternalPaymentNumber("222222222", claimNumber4, paymentNumber18);
        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        dentalClaim.lineOverride().perform(tdSpecific().getTestData("TestData_LineOverride"), 1);
        dentalClaim.claimSubmit().perform();
        NavigationPage.toSubTab(FINANCIALS);
        String paymentNumber5 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        updateExternalPaymentMethod("Select", claimNumber4, paymentNumber5);
        updateConsolidatedPaymentNumber(paymentNumber18.substring(1), claimNumber4, paymentNumber5);
        updateExternalPaymentNumber(paymentNumber18, claimNumber4, paymentNumber5);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);

        LOGGER.info("Claim6 creation");
        dentalClaim.create(tdSpecific().getTestData("TestData_Claim6").resolveLinks());
        String claimNumber6 = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        NavigationPage.toSubTab(FINANCIALS);
        String paymentNumber9 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        updateExternalPaymentMethod("Pending", claimNumber6, paymentNumber9);
        updateConsolidatedPaymentNumber(paymentNumber18, claimNumber6, paymentNumber9);
        updateExternalPaymentNumber(paymentNumber18, claimNumber6, paymentNumber9);

        LOGGER.info("Claim8 creation");
        dentalClaim.create(tdSpecific().getTestData("TestData_Claim8").resolveLinks());
        String claimNumber8 = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        NavigationPage.toSubTab(FINANCIALS);
        String paymentNumber11 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        updateExternalPaymentMethod("Reversal to Payer", claimNumber8, paymentNumber11);
        updateConsolidatedPaymentNumber(paymentNumber18, claimNumber8, paymentNumber11);
        updateExternalPaymentNumber("222222222", claimNumber8, paymentNumber11);
        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        NavigationPage.toSubTab(EDIT_CLAIM);
        IntakeInformationTab.removeProvider();
        intakeInformationTab.getAssetList().fill(tdSpecific().getTestData("TestData_Claim8_Adjust1"));
        buttonSubmitClaim.click();
        NavigationPage.toSubTab(FINANCIALS);
        String paymentNumber12 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        updateExternalPaymentMethod("VRA", claimNumber8, paymentNumber12);
        updateConsolidatedPaymentNumber(paymentNumber18, claimNumber8, paymentNumber12);
        updateExternalPaymentNumber("222222222", claimNumber8, paymentNumber12);
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);
        dentalClaim.clearPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ClearPayment"), 2);
        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        NavigationPage.toSubTab(EDIT_CLAIM);
        IntakeInformationTab.removeProvider();
        intakeInformationTab.getAssetList().fill(tdSpecific().getTestData("TestData_Claim8_Adjust2"));
        Tab.buttonTopSave.click();

        LOGGER.info("Steps 5,6");
        MainPage.QuickSearch.search(claimNumber0);
        Page.dialogConfirmation.confirm();
        updateExternalPaymentMethod("Zelis Check", claimNumber0, paymentNumber18);
        NavigationPage.toSubTab(FINANCIALS);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        ClaimPaymentsPage.buttonConsolidatedPayments.click();
        assertSoftly(softly -> {
            softly.assertThat(ClaimConsolidatedPaymentsPage.labelCheckNum).hasValue("");
            softly.assertThat(ClaimConsolidatedPaymentsPage.labelConsolidatedPaymentNum).hasValue(paymentNumber18);
            softly.assertThat(ClaimConsolidatedPaymentsPage.labelConsolidatedPaymentAmount).hasValue("$74.00");
            softly.assertThat(ClaimConsolidatedPaymentsPage.labelProviderTIN).hasValue("821394606");
            softly.assertThat(ClaimConsolidatedPaymentsPage.labelPracticeName).hasValue("TENDER DENTAL MANAGEMENT");
            softly.assertThat(ClaimConsolidatedPaymentsPage.labelProviderAddress).hasValue("5001 E Bonanza Rd Ste 162, Las Vegas, NV, 89110-3560, US");

            softly.assertThat(ClaimConsolidatedPaymentsPage.tableListOfConsolidatedPayments).with(PAYMENT_NUM, paymentNumber18).with(PAYMENT_STATUS, ISSUED)
                    .with(CLAIM_NUM, claimNumber0).with(NET_AMOUNT, "0.00").with(PROVIDER_NAME, "TENDER DENTAL MANAGEMENT").hasMatchingRows(1);
            softly.assertThat(ClaimConsolidatedPaymentsPage.tableListOfConsolidatedPayments).with(PAYMENT_NUM, paymentNumber4).with(PAYMENT_STATUS, VOIDED)
                    .with(CLAIM_NUM, claimNumber4).with(NET_AMOUNT, "25.00").with(PROVIDER_NAME, "DON K FLOWERS JR DMD PSC").hasMatchingRows(1);
            softly.assertThat(ClaimConsolidatedPaymentsPage.tableListOfConsolidatedPayments).with(PAYMENT_NUM, paymentNumber11).with(PAYMENT_STATUS, VOIDED)
                    .with(CLAIM_NUM, claimNumber8).with(NET_AMOUNT, "49.00").with(PROVIDER_NAME, "Jack Fredrick Backs DMD").hasMatchingRows(1);
            softly.assertThat(ClaimConsolidatedPaymentsPage.tableListOfConsolidatedPayments).with(PAYMENT_NUM, paymentNumber12).with(PAYMENT_STATUS, CLEARED)
                    .with(CLAIM_NUM, claimNumber8).with(NET_AMOUNT, "0.00").with(PROVIDER_NAME, "TENDER DENTAL MANAGEMENT").hasMatchingRows(1);
        });
    }

    private void updateExternalPaymentMethod(String externalPaymentMethod, String claimNumber, String paymentNumber) {
        dbService.executeUpdate("update ClaimsPayment set externalPaymentMethod = ? where claimNumber = ? and consolidatedPaymentNumber = ?", externalPaymentMethod, claimNumber, paymentNumber);
    }

    private void updateConsolidatedPaymentNumber(String newConsolidatedPaymentNumber, String claimNumber, String oldConsolidatedPaymentNumber) {
        dbService.executeUpdate("update ClaimsPayment set consolidatedPaymentNumber = ? where claimNumber = ? and consolidatedPaymentNumber = ?", newConsolidatedPaymentNumber, claimNumber, oldConsolidatedPaymentNumber);
    }

    private void updateExternalPaymentNumber(String externalPaymentNumber, String claimNumber, String paymentNumber) {
        dbService.executeUpdate("update ClaimsPayment set externalPaymentNumber = ? where claimNumber = ? and consolidatedPaymentNumber = ?", externalPaymentNumber, claimNumber, paymentNumber);
    }

}
