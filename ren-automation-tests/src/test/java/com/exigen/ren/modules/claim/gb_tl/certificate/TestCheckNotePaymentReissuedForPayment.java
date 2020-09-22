package com.exigen.ren.modules.claim.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.cancelClickAndCloseDialog;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.REISSUE_DATE;
import static com.exigen.ren.main.pages.summary.SummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage.PaymentStatusHistory.STATUS;
import static com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage.PaymentStatusHistory.DETAILS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckNotePaymentReissuedForPayment extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40984", component = CLAIMS_GROUPBENEFITS)
    public void testCheckNotePaymentReissuedForPayment() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_TL);
        createDefaultTermLifeInsuranceClaimForCertificatePolicy();
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 1);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);
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
}
