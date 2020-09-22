package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.CHECK_EFT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestAutoGeneratePaymentCheckNumber extends ClaimGroupBenefitsACBaseTest {
    private PaymentPaymentPaymentDetailsTab paymentPaymentDetailsTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentDetailsTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38447", component = CLAIMS_GROUPBENEFITS)
    public void testAutoGeneratePaymentCheckNumber() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA")
                .mask(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())));
        createDefaultGroupAccidentCertificatePolicy();

        createDefaultGroupAccidentClaimForCertificatePolicy();
        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 1);

        LOGGER.info("TEST REN-38447: Step 1");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));
        assertThat(Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(CHECK_EFT).getValue())).isBetween(1000001, 1999999);

        LOGGER.info("TEST REN-38447: Step 2");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));
        int eft2 = Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(CHECK_EFT).getValue());
        assertThat(eft2).isBetween(1000001, 1999999);

        LOGGER.info("TEST REN-38447: Step 4");
        claim.updatePayment().start(2);
        assertThat(paymentPaymentDetailsTab.getAssetList().getAsset(CHECK_EFT_NUMBER)).hasValue(String.valueOf(eft2));

        LOGGER.info("TEST REN-38447: Step 5");
        String checkValue = "test1";
        paymentPaymentDetailsTab.getAssetList().getAsset(MANUAL_CHECK_PAYMENT).setValue("Yes");
        paymentPaymentDetailsTab.getAssetList().getAsset(CHECK).setValue(checkValue);
        claim.updatePayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2)).hasCellWithValue(CHECK_EFT, checkValue);

        LOGGER.info("TEST REN-38447: Step 6-7");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));
        assertThat(Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(CHECK_EFT).getValue())).isBetween(1000001, 1999999);

        claim.updatePayment().start(3);
        paymentPaymentDetailsTab.getAssetList().getAsset(PAYMENT_METHOD).setValueContains("EFT");
        claim.updatePayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3)).hasCellWithValue(CHECK_EFT, "");
    }
}
