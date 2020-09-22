package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.metadata.RefundActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.RefundActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ActionConstants.BillingAction.REFUND;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndOtherTransactionTypeGB.REFUND_APPROVED;
import static com.exigen.ren.main.enums.TableConstants.BillingPaymentsAndTransactionsGB.TYPE;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.RefundActionTabMetaData.CHECK_NUMBER;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutoGenerateRefundCheckNumber extends GroupBenefitsBillingBaseTest implements GroupAccidentMasterPolicyContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private Tab refundActionTab = billingAccount.refund().getWorkspace().getTab(RefundActionTab.class);

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38972", component = BILLING_GROUPBENEFITS)
    public void testAutoGenerateRefundCheckNumber_not_NY() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "GA"));

        LOGGER.info("TEST: Steps 1-2");
        commonSteps();

        Assertions.assertThat(Integer.parseInt(refundActionTab.getAssetList().getAsset(CHECK_NUMBER).getValue())).isBetween(7000001, 7999999);

        LOGGER.info("TEST: Step 3");
        billingAccount.refund().submit();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions).hasMatchingRows(TYPE.getName(), REFUND_APPROVED);
    }


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38972", component = BILLING_GROUPBENEFITS)
    public void testAutoGenerateRefundCheckNumber_NY() {

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "NY"));

        LOGGER.info("TEST: Steps 4-5");
        commonSteps();

        Assertions.assertThat(Integer.parseInt(refundActionTab.getAssetList().getAsset(CHECK_NUMBER).getValue())).isBetween(8000001, 8999999);

        LOGGER.info("TEST: Step 6");
        billingAccount.refund().submit();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions).hasMatchingRows(TYPE.getName(), REFUND_APPROVED);
    }

    private void commonSteps() {
        navigateToBillingAccount(PolicySummaryPage.labelPolicyNumber.getValue());

        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Suspend_Remaining"), new Currency(50).toString());
        assertThat(BillingSummaryPage.comboBoxTakeAction).containsOption(REFUND);

        billingAccount.refund().start();

        refundActionTab.fillTab(billingAccount.getDefaultTestData("Refund", "TestData_Check")
                .adjust(TestData.makeKeyPath(RefundActionTab.class.getSimpleName(), RefundActionTabMetaData.AMOUNT.getLabel()), new Currency(50).toString()));
    }
}