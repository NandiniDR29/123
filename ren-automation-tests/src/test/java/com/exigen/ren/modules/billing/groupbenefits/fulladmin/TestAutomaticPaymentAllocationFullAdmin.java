package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.helpers.billing.BillingHelper;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomaticPaymentAllocationFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    private static final AbstractContainer<?, ?> acceptPaymentAL = billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class).getAssetList();

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13820", component = BILLING_GROUPBENEFITS)
    public void testAutomaticPaymentAllocationFullAdmin() {
        LOGGER.info("REN-13820: TC #2");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(GroupBenefitsMasterPolicyType.GB_VS, GroupBenefitsMasterPolicyType.GB_AC);

        LOGGER.info("TEST: Create VS master policy with two coverages");

        groupVisionMasterPolicy.createPolicy(groupVisionMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_WithTwoCoverages")
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_WithTwoCoverages").resolveLinks()));
        String masterVSPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Create VS certificate policy with First plan");
        groupVisionCertificatePolicy.createPolicy(groupVisionCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        PolicySummaryPage.linkMasterPolicy.click();

        LOGGER.info("TEST: Create VS certificate policy with Second plan");
        groupVisionCertificatePolicy.createPolicy(groupVisionCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(DATA_GATHER, "PlansTab_PlanB"))
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        PolicySummaryPage.linkMasterPolicy.click();
        initModalPremiumValues();
        Currency firstCoverageModalPremiumVision = new Currency(getModalPremiumAmountFromCoverageSummary(1));
        Currency secondCoverageModalPremiumVision = new Currency(getModalPremiumAmountFromCoverageSummary(2));
        Currency modalPremiumVision = modalPremiumAmount.get();

        LOGGER.info("TEST: Create AC master policy with two coverages");
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_TwoCoveragesNonContributory")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA").resolveLinks()));
        String masterACPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Create AC certificate policy with Core plan");
        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        PolicySummaryPage.linkMasterPolicy.click();

        LOGGER.info("TEST: Create AC certificate policy with Enhanced  plan");
        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData("DataGather", "TestData")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData("DataGather", "CoveragesTab_PlanEnhanced").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        PolicySummaryPage.linkMasterPolicy.click();
        initModalPremiumValues();
        Currency firstCoverageModalPremiumAccident = new Currency(getModalPremiumAmountFromCoverageSummary(2));
        Currency modalPremiumAccident = modalPremiumAmount.get();

        LOGGER.info("TEST: Generate Future Statement(Invoice 1 and Invoice2)");
        navigateToBillingAccount(masterACPolicyNumber);
        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Step 1");
        initiateAcceptCashPaymentWithAmount(modalPremiumVision.add(modalPremiumAccident).toPlainString());
        assertThat(AcceptPaymentActionTab.tableAllocations).hasRows(2);
        assertThat(AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.AMOUNT)
                .controls.textBoxes.getFirst().getValue()).isEqualTo(modalPremiumVision.add(modalPremiumAccident).toString());
        assertThat(AcceptPaymentActionTab.tableAllocations.getRow(2).getCell(BillingConstants.BillingAllocationsTable.AMOUNT)
                .controls.textBoxes.getFirst().getValue()).isEqualTo(BillingHelper.ZERO);

        LOGGER.info("Step 2");
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(firstCoverageModalPremiumVision.toPlainString());
        AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
        assertThat(AcceptPaymentActionTab.getAllocationsBillingGroupsTableForCurrentPolicy(masterVSPolicyNumber)
                .getRow(1).getCell(AcceptPaymentActionTab.AllocationsBilling.CURRENT.getName()).controls.textBoxes.getFirst().getValue())
                .isEqualTo(firstCoverageModalPremiumVision.toString());
        AcceptPaymentActionTab.buttonOk.click();
        AcceptPaymentActionTab.buttonOk.click();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER.getName(), masterVSPolicyNumber,
                        TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT,
                        TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), firstCoverageModalPremiumVision.negate().toString()));

        LOGGER.info("Step 3");
        initiateAcceptCashPaymentWithAmount(secondCoverageModalPremiumVision.toPlainString());
        AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
        assertThat(AcceptPaymentActionTab.getAllocationsBillingGroupsTableForCurrentPolicy(masterVSPolicyNumber)
                .getRow(2).getCell(AcceptPaymentActionTab.AllocationsBilling.CURRENT.getName()).controls.textBoxes.getFirst().getValue())
                .isEqualTo(secondCoverageModalPremiumVision.toString());
        AcceptPaymentActionTab.buttonOk.click();
        AcceptPaymentActionTab.buttonOk.click();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER.getName(), masterVSPolicyNumber,
                        TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT,
                        TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), secondCoverageModalPremiumVision.negate().toString()));

        LOGGER.info("Step 4");
        initiateAcceptCashPaymentWithAmount(firstCoverageModalPremiumAccident.toPlainString());
        AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
        assertThat(AcceptPaymentActionTab.getAllocationsBillingGroupsTableForCurrentPolicy(masterACPolicyNumber)
                .getRow(1).getCell(AcceptPaymentActionTab.AllocationsBilling.CURRENT.getName()).controls.textBoxes.getFirst().getValue())
                .isEqualTo(firstCoverageModalPremiumAccident.toString());

        LOGGER.info("Step 5");
        AcceptPaymentActionTab.navButtonCancel.click();
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(firstCoverageModalPremiumAccident.add(new Currency("5")).toPlainString());
        AcceptPaymentActionTab.buttonOk.click();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER.getName(), masterACPolicyNumber,
                        TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT,
                        TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), firstCoverageModalPremiumAccident.add(new Currency("5")).negate().toString()));
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.BillingBillsAndStatementsGB.UNALLOCATED_INVOICE_PREMIUM.getName(), new Currency("5").toString()));
    }

    private void initiateAcceptCashPaymentWithAmount(String amount) {
        billingAccount.acceptPayment().start();
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.PAYMENT_METHOD).setValue("Cash");
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(amount);
    }
}