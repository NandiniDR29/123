package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.FinancialPaymentPaymentDetailsActionTab;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.gb_dn.ClaimSubledgerBaseTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.PaymentInterestMetaData.INTEREST_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.getClaimNumber;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimSubledgerSplitAccountNumbersByBLOB extends ClaimSubledgerBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20738", component = CLAIMS_GROUPBENEFITS)
    public void testClaimSubledgerSplitAccountNumbersByBLOB() {
        mainApp().open();
        TestData individualCustomerTestData = getDefaultCustomerIndividualTestData();
        String firstName = "Rest" + individualCustomerTestData.getValue(generalTab.getMetaKey(), GeneralTabMetaData.FIRST_NAME.getLabel());
        customerIndividual.create(individualCustomerTestData);
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        groupDentalCertificatePolicy.createPolicyViaUI(groupDentalCertificatePolicy.defaultTestData().getTestData(TestDataKey.DATA_GATHER, "TestDataWithoutNewCustomer")
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), SEARCH_CUSTOMER.getLabel(),
                        InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), firstName));

        LOGGER.info("TEST: Step #1");
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithPayment"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment"), 1);

        String claimNumber = getClaimNumber();
        String paymentNumber1 =
                tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();

        List<Map<String, String>> listFinanceReport =
                parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));

        assertThat(listFinanceReport).hasSize(0);

        LOGGER.info("TEST: Step #2");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST: validate table #1");
        listFinanceReport = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));

        assertThat(listFinanceReport).hasSize(6);
        validatePayment("IN_PAY_PAY_TRX_D",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2027",
                "Indemnity payment",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_PAY_TRX_C",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2003",
                "Indemnity payment",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2027",
                "Indemnity payment offset",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2027",
                "Indemnity payment offset",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_ADD_TRX_D_ClaimsPaymentCalculatorInterest",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2018",
                "Indemnity payment interest",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_ADD_TRX_C_ClaimsPaymentCalculatorInterest",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2003",
                "Indemnity payment interest",
                paymentNumber1,
                listFinanceReport);

        LOGGER.info("TEST: Step #3");
        dentalClaim.stopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_StopPayment"), 1);

        LOGGER.info("TEST: validate table #1");
        listFinanceReport = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));

        assertThat(listFinanceReport).hasSize(6);
        validatePayment("IN_PAY_PAY_TRX_D",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2027",
                "Indemnity payment",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_PAY_TRX_C",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2003",
                "Indemnity payment",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2027",
                "Indemnity payment offset",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2027",
                "Indemnity payment offset",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_ADD_TRX_D_ClaimsPaymentCalculatorInterest",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2018",
                "Indemnity payment interest",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_ADD_TRX_C_ClaimsPaymentCalculatorInterest",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2003",
                "Indemnity payment interest",
                paymentNumber1,
                listFinanceReport);

        LOGGER.info("TEST: Step #4");
        dentalClaim.confirmStopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_ConfirmStopPayment"), 1);

        LOGGER.info("TEST: validate table #2");
        listFinanceReport = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));

        assertThat(listFinanceReport).hasSize(12);
        validatePayment("IN_PAY_STOP_PAY_TRX_D",
                "IN_PAY_STOP",
                "indemnity payment stopped",
                "DEBIT",
                "2003",
                "Indemnity payment stopped",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_STOP_PAY_TRX_C",
                "IN_PAY_STOP",
                "indemnity payment stopped",
                "CREDIT",
                "2027",
                "Indemnity payment stopped",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_STOP_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_STOP",
                "indemnity payment stopped",
                "DEBIT",
                "2027",
                "Indemnity payment stopped offset",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_STOP_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_STOP",
                "indemnity payment stopped",
                "CREDIT",
                "2027",
                "Indemnity payment stopped offset",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_STOP_ADD_TRX_D_ClaimsPaymentCalculatorInterest",
                "IN_PAY_STOP",
                "indemnity payment stopped",
                "DEBIT",
                "2003",
                "Indemnity payment stopped interest",
                paymentNumber1,
                listFinanceReport);
        validatePayment("IN_PAY_STOP_ADD_TRX_C_ClaimsPaymentCalculatorInterest",
                "IN_PAY_STOP",
                "indemnity payment stopped",
                "CREDIT",
                "2018",
                "Indemnity payment stopped interest",
                paymentNumber1,
                listFinanceReport);

        LOGGER.info("TEST: Step #5");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        dentalClaim.claimSubmit().perform();
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "40")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_INTEREST.getLabel(),
                        INTEREST_AMOUNT.getLabel()), "45"), 2);

        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);
        dentalClaim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 2);
        String paymentNumber2 =
                tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();

        LOGGER.info("TEST: validate table #1");
        listFinanceReport = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));

        assertThat(listFinanceReport).hasSize(24);
        validatePayment("IN_PAY_PAY_TRX_D",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2027",
                "Indemnity payment",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_PAY_TRX_C",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2003",
                "Indemnity payment",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2027",
                "Indemnity payment offset",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2027",
                "Indemnity payment offset",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_ADD_TRX_D_ClaimsPaymentCalculatorInterest",
                "IN_PAY",
                "indemnity payment",
                "DEBIT",
                "2018",
                "Indemnity payment interest",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_ADD_TRX_C_ClaimsPaymentCalculatorInterest",
                "IN_PAY",
                "indemnity payment",
                "CREDIT",
                "2003",
                "Indemnity payment interest",
                paymentNumber2,
                listFinanceReport);

        LOGGER.info("TEST: validate table #3");
        validatePayment("IN_PAY_DECL_PAY_TRX_D",
                "IN_PAY_DECL",
                "indemnity payment declined",
                "DEBIT",
                "2003",
                "Indemnity payment declined",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_DECL_PAY_TRX_C",
                "IN_PAY_DECL",
                "indemnity payment declined",
                "CREDIT",
                "2027",
                "Indemnity payment declined",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_DECL_OFF_TRX_D_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_DECL",
                "indemnity payment declined",
                "DEBIT",
                "2027",
                "Indemnity payment declined offset",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_DECL_OFF_TRX_C_ClaimsDentalPaymentCalculatorReduction",
                "IN_PAY_DECL",
                "indemnity payment declined",
                "CREDIT",
                "2027",
                "Indemnity payment declined offset",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_DECL_ADD_TRX_D_ClaimsPaymentCalculatorInterest",
                "IN_PAY_DECL",
                "indemnity payment declined",
                "DEBIT",
                "2003",
                "Indemnity payment declined interest",
                paymentNumber2,
                listFinanceReport);
        validatePayment("IN_PAY_DECL_ADD_TRX_C_ClaimsPaymentCalculatorInterest",
                "IN_PAY_DECL",
                "indemnity payment declined",
                "CREDIT",
                "2018",
                "Indemnity payment declined interest",
                paymentNumber2,
                listFinanceReport);
    }

}
