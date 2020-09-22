package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PaymentInquiryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.BILLING;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FINANCIALS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.ALLOWED;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.PAYEE_TYPE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PaymentInquiryTabMetaData.PAYMENT_DETAILS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PaymentInquiryTabMetaData.PaymentDetailsSection.EXTERNAL_PAYMENT_METHOD;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PaymentInquiryTabMetaData.PaymentDetailsSection.EXTERNAL_PAYMENT_NUM;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PaymentInquiryTabMetaData.PaymentDetailsSection.ISSUE_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimIntegrationWithPayments extends ClaimGroupBenefitsDNBaseTest {
    private DBService dbService = DBService.get();

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41338", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIntegrationWithPaymentsCheckConsolidatedPaymentsActionAndBAM() {
        LOGGER.info("TEST REN-41338: Preconditions + Steps 1-2");
        String claimNumber = commonSteps("Service Provider");

        LOGGER.info("TEST REN-41338: Step 3");
        toSubTab(FINANCIALS);
        String paymentNumber = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        dbService.executeUpdate("update ClaimsPayment set externalPaymentMethod='Zelis Check' where claimNumber=? and consolidatedPaymentNumber=?", claimNumber, paymentNumber);

        LOGGER.info("TEST REN-41338: Step 4");
        toSubTab(FINANCIALS);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertThat(ClaimPaymentsPage.buttonConsolidatedPayments).isAbsent();

        LOGGER.info("TEST REN-41338: Step 5-8");
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        ClaimPaymentsPage.buttonConsolidatedPayments.click();
        Tab.buttonCancel.click();
        assertThat(activitiesAndUserNotes).hasMatchingRows(DESCRIPTION, String.format("Consolidated Payments # %s for Claim # %s", paymentNumber, claimNumber));

        LOGGER.info("TEST REN-41338: Step 9-10");
        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", DEFAULT_TEST_DATA_KEY));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        ClaimPaymentsPage.buttonConsolidatedPayments.click();
        Tab.buttonCancel.click();
        assertThat(activitiesAndUserNotes).hasMatchingRows(DESCRIPTION, String.format("Consolidated Payments # %s for Claim # %s", paymentNumber, claimNumber));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41712", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIntegrationWithPaymentsCheckPaymentChanges() {
        LOGGER.info("TEST REN-41712: Preconditions + Steps 1-2");
        String claimNumber = commonSteps("Service Provider");

        LOGGER.info("TEST REN-41712: Step 3");
        toSubTab(FINANCIALS);
        String paymentNumber = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        String consolidatedPaymentNumber = dbService.getValue("select cp.consolidatedPaymentNumber from ClaimsSummaryInfo c, ClaimsPayment cp " +
                "where c.claimNumber = cp.claimNumber and c.currentRevisionInd = 1 and c.claimNumber=?", claimNumber).get();
        assertThat(consolidatedPaymentNumber).isEqualTo(paymentNumber);

        LOGGER.info("TEST REN-41712: Step 4");
        dentalClaim.paymentInquiry().start(1);
        AbstractContainer<?, ?> paymentAssetList = dentalClaim.paymentInquiry().getWorkspace().getTab(PaymentInquiryTab.class).getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Payment Method"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Check #"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Payment Delivery Address"))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Consolidated Payment #"))).isAbsent();
            softly.assertThat(paymentAssetList.getAsset(PAYMENT_DETAILS).getAsset(EXTERNAL_PAYMENT_METHOD)).hasValue("Pending");
            softly.assertThat(paymentAssetList.getAsset(PAYMENT_DETAILS).getAsset(EXTERNAL_PAYMENT_NUM)).hasValue("");
            softly.assertThat(paymentAssetList.getAsset(PAYMENT_DETAILS).getAsset(ISSUE_DATE)).hasValue("");
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41359", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIntegrationWithPaymentsCheckExternalPaymentMethodCondition() {
        LOGGER.info("TEST REN-41359: Preconditions + Steps 1-2");
        commonSteps("Service Provider");

        LOGGER.info("TEST REN-41359: Step 3");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertThat(ClaimPaymentsPage.buttonConsolidatedPayments).isAbsent();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41709", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIntegrationWithPaymentsCheckPayeeTypeCondition() {
        LOGGER.info("TEST REN-41709: Preconditions + Steps 9-10");
        String claimNumber = commonSteps("Primary Insured");

        LOGGER.info("TEST REN-41709: Step 11");
        toSubTab(FINANCIALS);
        String paymentNumber = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        dbService.executeUpdate("update ClaimsPayment set externalPaymentMethod='Zelis Check' where claimNumber=? and consolidatedPaymentNumber=?", claimNumber, paymentNumber);

        LOGGER.info("TEST REN-41709: Step 12");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertThat(ClaimPaymentsPage.buttonConsolidatedPayments).isAbsent();
    }

    private String commonSteps(String payeeType){
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(BILLING);
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), new Currency(policyTotalDue).toString()));
        MainPage.QuickSearch.search(certificatePolicyNumber);
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), PAYEE_TYPE.getLabel()), payeeType)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), ALLOWED));
        dentalClaim.claimSubmit().perform();
        return ClaimSummaryPage.getClaimNumber();
    }

}
