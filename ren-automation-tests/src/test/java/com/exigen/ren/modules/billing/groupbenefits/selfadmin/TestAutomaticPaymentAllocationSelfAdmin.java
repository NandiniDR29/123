package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
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
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomaticPaymentAllocationSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext, ShortTermDisabilityMasterPolicyContext {

    private static final AbstractContainer<?, ?> acceptPaymentAL = billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class).getAssetList();

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13820", component = BILLING_GROUPBENEFITS)
    public void testAutomaticPaymentAllocationSelfAdmin() {
        LOGGER.info("REN-13820: TC #1");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(GroupBenefitsMasterPolicyType.GB_DI_STD, GroupBenefitsMasterPolicyType.GB_AC);

        LOGGER.info("TEST: Create VS policy with two coverages");
        shortTermDisabilityMasterPolicy.createPolicy(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, "TestData_TwoPlans")
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        String masterVSPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        initModalPremiumValues();
        Currency firstCoverageModalPremiumVision = new Currency(getModalPremiumAmountFromCoverageSummary(1));
        Currency secondCoverageModalPremiumVision = new Currency(getModalPremiumAmountFromCoverageSummary(2));
        Currency modalPremiumVision = modalPremiumAmount.get();

        LOGGER.info("TEST: Create AC policy with two coverages");
        TestData adjustedCorePlan = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, "ClassificationManagementTab_BASEBU")
                .adjust("Number of Participants", "30");
        TestData adjustedEnhancedPlan = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, "ClassificationManagementTab_ENHANCED10")
                .adjust("Number of Participants", "20");
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", "TestData_WithTwoCoverages")
                .adjust(ClassificationManagementTab.class.getSimpleName(), ImmutableList.of(adjustedCorePlan, adjustedEnhancedPlan))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA").resolveLinks()));
        String masterACPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        initModalPremiumValues();
        Currency firstCoverageModalPremiumAccident = new Currency(getModalPremiumAmountFromCoverageSummary(1));
        Currency modalPremiumAccident = modalPremiumAmount.get();

        LOGGER.info("TEST: Generate Future Statement(Invoice 1 and Invoice2)");
        navigateToBillingAccount(masterVSPolicyNumber);
        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();

        assertSoftly(softly -> {
            LOGGER.info("Step 1");
            initiateAcceptCashPaymentWithAmount(modalPremiumVision.add(modalPremiumAccident).toPlainString());
            softly.assertThat(AcceptPaymentActionTab.tableAllocations).hasRows(2);
            softly.assertThat(AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.AMOUNT)
                    .controls.textBoxes.getFirst().getValue()).isEqualTo(modalPremiumVision.add(modalPremiumAccident).toString());
            softly.assertThat(AcceptPaymentActionTab.tableAllocations.getRow(2).getCell(BillingConstants.BillingAllocationsTable.AMOUNT)
                    .controls.textBoxes.getFirst().getValue()).isEqualTo(BillingHelper.ZERO);

            LOGGER.info("Step 2");
            acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(firstCoverageModalPremiumVision.toPlainString());
            AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
            softly.assertThat(AcceptPaymentActionTab.getAllocationsBillingGroupsTableForCurrentPolicy(masterVSPolicyNumber)
                    .getRow(1).getCell(AcceptPaymentActionTab.AllocationsBilling.CURRENT.getName()).controls.textBoxes.getFirst().getValue())
                    .isEqualTo(firstCoverageModalPremiumVision.toString());
            AcceptPaymentActionTab.buttonOk.click();
            AcceptPaymentActionTab.buttonOk.click();
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .hasMatchingRows(1, ImmutableMap.of(
                            TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER.getName(), masterVSPolicyNumber,
                            TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT,
                            TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), firstCoverageModalPremiumVision.negate().toString()));

            LOGGER.info("Step 3");
            initiateAcceptCashPaymentWithAmount(secondCoverageModalPremiumVision.toPlainString());
            AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
            softly.assertThat(AcceptPaymentActionTab.getAllocationsBillingGroupsTableForCurrentPolicy(masterVSPolicyNumber)
                    .getRow(2).getCell(AcceptPaymentActionTab.AllocationsBilling.CURRENT.getName()).controls.textBoxes.getFirst().getValue())
                    .isEqualTo(secondCoverageModalPremiumVision.toString());
            AcceptPaymentActionTab.buttonOk.click();
            AcceptPaymentActionTab.buttonOk.click();
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .hasMatchingRows(1, ImmutableMap.of(
                            TableConstants.BillingPaymentsAndTransactionsGB.POLICY_NUMBER.getName(), masterVSPolicyNumber,
                            TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.PAYMENT,
                            TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), secondCoverageModalPremiumVision.negate().toString()));

            LOGGER.info("Step 4");
            initiateAcceptCashPaymentWithAmount(firstCoverageModalPremiumAccident.toPlainString());
            AcceptPaymentActionTab.tableAllocations.getRow(1).getCell(BillingConstants.BillingAllocationsTable.INVOICE).controls.links.getFirst().click();
            softly.assertThat(AcceptPaymentActionTab.getAllocationsBillingGroupsTableForCurrentPolicy(masterACPolicyNumber)
                    .getRow(1).getCell(AcceptPaymentActionTab.AllocationsBilling.CURRENT.getName()).controls.textBoxes.getFirst().getValue())
                    .isEqualTo(firstCoverageModalPremiumAccident.toString());

            LOGGER.info("Step 5");
            AcceptPaymentActionTab.navButtonCancel.click();
            acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(firstCoverageModalPremiumAccident.add(new Currency("5")).toPlainString());
            acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.SUSPEND_REMAINING).setValue(VALUE_YES);
            AcceptPaymentActionTab.buttonOk.click();
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .hasMatchingRows(1, ImmutableMap.of(
                            TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.ACCOUNT_SUSPENSE,
                            TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), new Currency("5").negate().toString()));
        });
    }

    private void initiateAcceptCashPaymentWithAmount(String amount) {
        billingAccount.acceptPayment().start();
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.PAYMENT_METHOD).setValue("Cash");
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(amount);
    }
}