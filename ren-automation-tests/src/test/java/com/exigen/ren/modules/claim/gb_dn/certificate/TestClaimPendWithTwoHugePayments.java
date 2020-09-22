package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.FinancialPaymentPaymentDetailsActionTab;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.mywork.actiontabs.CompleteTaskActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.PaymentInterestMetaData.INTEREST_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.TYPE_OF_TRANSACTION;
import static com.exigen.ren.main.modules.mywork.MyWorkContext.completeTaskActionTab;
import static com.exigen.ren.main.modules.mywork.metadata.CompleteTaskActionTabMetaData.DECISION;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.buttonTasks;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPendWithTwoHugePayments extends ClaimGroupBenefitsDNBaseTest {

    private static final String APPROVE_PAYMENT = "Approve Payment";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20811", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPendWithTwoHugePayments() {
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

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_PardisRajabi_Provider")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel()), tdSpecific()
                        .getTestDataList("AdjustTwoService", intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel())));
        dentalClaim.claimSubmit().perform();

        LOGGER.info("TEST: Step #22");
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);

        buttonTasks.click();
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), APPROVE_PAYMENT)).isAbsent();
        buttonCancel.click();

        LOGGER.info("TEST: Step #23");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("AdjustOneService"));
        Tab.buttonSaveAndExit.click();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(BillingConstants.PaymentsAndOtherTransactionStatus.PENDING);

        LOGGER.info("TEST: Step #25-26");
        buttonTasks.click();
        openTaskDetails(APPROVE_PAYMENT);
        buttonComplete.click();

        assertThat(completeTaskActionTab.getAssetList().getAsset(DECISION)).isRequired()
                .hasOptions(ImmutableList.of(StringUtils.EMPTY, "Deny Payment", APPROVE_PAYMENT));

        LOGGER.info("TEST: Step #27");
        completeTaskActionTab.getAssetList().getAsset(DECISION).setValue(APPROVE_PAYMENT);
        CompleteTaskActionTab.buttonComplete.click();

        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), APPROVE_PAYMENT)).isAbsent();
        buttonCancel.click();

        LOGGER.info("TEST: Step #28");
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED);

        LOGGER.info("TEST: Step #34");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "0.1")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_INTEREST.getLabel(),
                        INTEREST_AMOUNT.getLabel()), "0"), 1);

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED);

        LOGGER.info("TEST: Step #37");
        String paymentNumber =
                tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();

        assertSoftly(softly -> {
            ImmutableList.of(APPROVE_PAYMENT, "Disapprove Payment").forEach(value -> {
                softly.assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(value))).isAbsent();
            });
        });
        buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("TEST: Step #38");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "0")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        INTEREST_AMOUNT.getLabel()), "0"), 1);

        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format(String.format("Payment amount $2,000.01 on payment #%s exceeds $2,000.00", paymentNumber)))).isPresent();
        dialogConfirmation.confirm();

        LOGGER.info("TEST: Step #44");
        dentalClaim.approvePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_ApprovePayment"), 1);

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED);
    }
}
