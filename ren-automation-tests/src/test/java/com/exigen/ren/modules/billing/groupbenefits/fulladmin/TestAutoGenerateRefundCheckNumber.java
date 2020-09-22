package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.metadata.RefundActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.RefundActionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ActionConstants.BillingAction.REFUND;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndOtherTransactionTypeGB.REFUND_APPROVED;
import static com.exigen.ren.main.enums.TableConstants.BillingPaymentsAndTransactionsGB.TYPE;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.RefundActionTabMetaData.CHECK_NUMBER;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutoGenerateRefundCheckNumber extends GroupBenefitsBillingBaseTest implements GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private Tab refundActionTab = billingAccount.refund().getWorkspace().getTab(RefundActionTab.class);

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38972", component = BILLING_GROUPBENEFITS)
    public void testAutoGenerateRefundCheckNumber_not_NY() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "NJ"));

        LOGGER.info("TEST: Steps 1-2");
        commonSteps();

        assertThat(Integer.parseInt(refundActionTab.getAssetList().getAsset(CHECK_NUMBER).getValue())).isBetween(7000001, 7999999);

        LOGGER.info("TEST: Step 3");
        billingAccount.refund().submit();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions).hasMatchingRows(TYPE.getName(), REFUND_APPROVED);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38972", component = BILLING_GROUPBENEFITS)
    public void testAutoGenerateRefundCheckNumber_NY() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), "index=1"));

        LOGGER.info("TEST: Steps 4-5");
        commonSteps();

        assertThat(Integer.parseInt(refundActionTab.getAssetList().getAsset(CHECK_NUMBER).getValue())).isBetween(8000001, 8999999);

        LOGGER.info("TEST: Step 6");
        billingAccount.refund().submit();
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions).hasMatchingRows(TYPE.getName(), REFUND_APPROVED);
    }

    private void commonSteps() {
        createDefaultGroupVisionCertificatePolicy();

        billingAccount.navigateToBillingAccount();

        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash_Suspend_Remaining"), new Currency(50).toString());
        assertThat(BillingSummaryPage.comboBoxTakeAction).containsOption(REFUND);

        billingAccount.refund().start();

        refundActionTab.fillTab(billingAccount.getDefaultTestData("Refund", "TestData_Check")
                .adjust(TestData.makeKeyPath(RefundActionTab.class.getSimpleName(), RefundActionTabMetaData.AMOUNT.getLabel()), new Currency(50).toString()));
    }
}