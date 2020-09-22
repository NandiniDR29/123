package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.FinancialPaymentPaymentDetailsActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.gb_dn.ClaimSubledgerBaseTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.ADJUDICATED;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.PaymentInterestMetaData.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimInterestCalculationInPayment extends ClaimSubledgerBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22716", component = CLAIMS_GROUPBENEFITS)
    public void testClaimInterestCalculationInPaymentREST() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        LOGGER.info("Step 1");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CHARGE.getLabel()), "100"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        NavigationPage.toSubTab(FINANCIALS);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            // TODO achykanakov: review the following and next checks after REN-44583 fixed
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_NET_AMOUNT.getName())).hasValue("$102.00");
        });

        LOGGER.info("Step 2");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "20")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_INTEREST.getLabel(),
                        INTEREST_AMOUNT.getLabel()), "30"), 1);

        LOGGER.info("Step 4");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        List<Map<String, String>> finReportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        List<String> transactions = new LinkedList<>(Arrays.asList("IN_PAY_PAY_TRX_D", "IN_PAY_PAY_TRX_C", "IN_PAY_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction", "IN_PAY_ADD_TRX_D_ClaimsPaymentCalculatorInterest", "IN_PAY_ADD_TRX_C_ClaimsPaymentCalculatorInterest"));
        List<Map<String, String>> finReportsFromTestData = getFinReportsFromTestData(transactions);
        assertReportsEqual(finReportsFromTestData, finReportsREST);

        LOGGER.info("Step 5");
        dentalClaim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 1);
        finReportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        transactions.addAll(new LinkedList<>(Arrays.asList("IN_PAY_DECL_PAY_TRX_D", "IN_PAY_DECL_PAY_TRX_C", "IN_PAY_DECL_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction", "IN_PAY_DECL_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_DECL_ADD_TRX_D_ClaimsPaymentCalculatorInterest", "IN_PAY_DECL_ADD_TRX_C_ClaimsPaymentCalculatorInterest")));
        finReportsFromTestData = getFinReportsFromTestData(transactions);
        assertReportsEqual(finReportsFromTestData, finReportsREST);

        LOGGER.info("Step 6");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("$200.00");
        buttonSubmitClaim.click();
        NavigationPage.toSubTab(FINANCIALS);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_NET_AMOUNT.getName())).hasValue("$202.00");
        });

        LOGGER.info("Step 7");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "100")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_INTEREST.getLabel(),
                        INTEREST_AMOUNT.getLabel()), "40"), 2);

        LOGGER.info("Step 9");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);
        finReportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        transactions.addAll(new LinkedList<>(Arrays.asList("IN_PAY_PAY_TRX_D_2", "IN_PAY_PAY_TRX_C_2", "IN_PAY_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction_2",
                "IN_PAY_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction_2", "IN_PAY_ADD_TRX_D_ClaimsPaymentCalculatorInterest_2", "IN_PAY_ADD_TRX_C_ClaimsPaymentCalculatorInterest_2")));
        finReportsFromTestData = getFinReportsFromTestData(transactions);
        assertReportsEqual(finReportsFromTestData, finReportsREST);

        LOGGER.info("Step 10");
        dentalClaim.stopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_StopPayment"), 2);
        finReportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        finReportsFromTestData = getFinReportsFromTestData(transactions);
        assertReportsEqual(finReportsFromTestData, finReportsREST);

        LOGGER.info("Step 11");
        dentalClaim.confirmStopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_ConfirmStopPayment"), 2);
        finReportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        transactions.addAll(new LinkedList<>(Arrays.asList("IN_PAY_STOP_PAY_TRX_D", "IN_PAY_STOP_PAY_TRX_C", "IN_PAY_STOP_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_STOP_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction", "IN_PAY_STOP_ADD_TRX_D_ClaimsPaymentCalculatorInterest", "IN_PAY_STOP_ADD_TRX_C_ClaimsPaymentCalculatorInterest")));
        finReportsFromTestData = getFinReportsFromTestData(transactions);
        assertReportsEqual(finReportsFromTestData, finReportsREST);

        LOGGER.info("Step 12");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("$300.00");
        buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);
        NavigationPage.toSubTab(FINANCIALS);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(3);
            // TODO achykanakov: review the following check after REN-44583 fixed
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(PAYMENT_NET_AMOUNT.getName())).hasValue("$216.47");
        });

        LOGGER.info("Step 13");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .mask(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), REDUCTION_AMOUNT.getLabel()))
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_INTEREST.getLabel(),
                        INTEREST_AMOUNT.getLabel()), "0"), 3);
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);
        dentalClaim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 3);
        finReportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        transactions.addAll(new LinkedList<>(Arrays.asList("IN_PAY_PAY_TRX_D_3", "IN_PAY_PAY_TRX_C_3", "IN_PAY_DECL_PAY_TRX_D_3", "IN_PAY_DECL_PAY_TRX_C_3")));
        finReportsFromTestData = getFinReportsFromTestData(transactions);
        assertReportsEqual(finReportsFromTestData, finReportsREST);

        LOGGER.info("Step 14");
        NavigationPage.toSubTab(EDIT_CLAIM);
        buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);
        NavigationPage.toSubTab(FINANCIALS);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(4);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(4).getCell(PAYMENT_NET_AMOUNT.getName())).hasValue("$216.47");
        });
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 4);
        dentalClaim.stopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_StopPayment"), 4);
        dentalClaim.confirmStopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_ConfirmStopPayment"), 4);
        // TODO achykanakov: add assert after REN-44583 fixed
    }

}
