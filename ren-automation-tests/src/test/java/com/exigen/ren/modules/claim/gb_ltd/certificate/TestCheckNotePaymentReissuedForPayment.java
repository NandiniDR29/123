package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.ReissuePaymentActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.Tab.cancelClickAndCloseDialog;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.REISSUE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.BALANCE_AMOUNT;
import static com.exigen.ren.main.pages.summary.SummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage.PaymentStatusHistory.DETAILS;
import static com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage.PaymentStatusHistory.STATUS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckNotePaymentReissuedForPayment extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40984", component = CLAIMS_GROUPBENEFITS)
    public void testCheckNotePaymentReissuedForPayment() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_LTD);
        createDefaultLongTermDisabilityClaimForCertificatePolicy();
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-40984: Step 1");
        claim.reissuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ReissuePayment"), 1);
        String check = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(CHECK_EFT).getValue();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TRANSACTION_STATUS)).hasValue("Declined");
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(NOTE)).valueContains(String.format("Check #: %s", check));
        });

        LOGGER.info("TEST REN-40984: Step 2");
        ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_NUMBER.getName()).controls.links.getFirst().click();
        assertSoftly(softly -> {
            softly.assertThat(ClaimPaymentsPage.tablePaymentStatusHistory.getRow(4).getCell(STATUS.getName())).hasValue("Declined");
            softly.assertThat(ClaimPaymentsPage.tablePaymentStatusHistory.getRow(4).getCell(DETAILS.getName())).hasValue("Payment Reissued");
        });
        cancelClickAndCloseDialog();

        LOGGER.info("TEST REN-40984: Step 3");
        claim.paymentInquiry().start(1);
        assertThat(new StaticElement(REISSUE_DATE.getLocator())).isPresent();
        PaymentPaymentPaymentDetailsTab.buttonCancel.click();

        LOGGER.info("TEST REN-40984: Step 4");
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(TRANSACTION_STATUS)).hasValue("Approved");
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(NOTE)).isNotEqualTo(check);
        });

        LOGGER.info("TEST REN-40984: Step 5");
        claim.paymentInquiry().start(2);
        assertThat(new StaticElement(REISSUE_DATE.getLocator())).isAbsent();
        PaymentPaymentPaymentDetailsTab.buttonCancel.click();

        LOGGER.info("TEST REN-40984: Step 6");
        String paymentNumber = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_NUMBER.getName()).getValue();
        assertThat(activitiesAndUserNotes).hasRowsThatContain(DESCRIPTION, String.format("Change Payment # %s Status for Claim # %s from Issued to Declined", paymentNumber, claimNumber));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40993", component = CLAIMS_GROUPBENEFITS)
    public void testCheckPaymentReissuePaymentWithBalance() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_LTD);
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "250"));
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.BENEFIT_PERCENTAGE.getLabel()), "60"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-40993: Step 1");
        disabilityClaim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData_Update")
                .adjust(makeKeyPath(BenefitLTDInjuryPartyInformationTab.class.getSimpleName(), BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS.getLabel()), "200"), 1);

        toSubTab(PAYMENTS);
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_NUMBER.getName()).getValue();
        claim.checkBalance().start();
        assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1)).hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(30).toString());
        BalanceActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST REN-40993: Step 2");
        claim.reissuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ReissuePayment"), 1);
        assertThat(claim.reissuePayment().getWorkspace().getTab(ReissuePaymentActionTab.class).getAssetList().getAsset(ReissuePaymentActionTabMetaData.DETAILS))
                .hasWarningWithText("Can't reissue Payment, which was recalculated and produced balance record.");
    }
}
