package com.exigen.ren.modules.claim.scenarios.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.GroupBenefitsClaimType;
import com.exigen.ren.main.modules.claim.IClaim;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.BaseTest;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.PAYMENT_METHOD;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.SOCIAL_SECURITY_NUMBER_SSN;

public class ScenarioTestClaimDifferentPaymentStatusWithClaimReissuePayment extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, GroupAccidentMasterPolicyContext, LongTermDisabilityMasterPolicyContext {

    private static final String REISSUE_PAYMENT = "Reissue Payment";
    private static final String approvalUsername = "TestUserForClaimApproval";
    private static final String approvalPassword = "qa";

    public void testClaimDifferentPaymentStatusWithClaimReissuePayment(GroupBenefitsMasterPolicyType policyType, GroupBenefitsClaimType claimType, TestData tdPolicy, TestData tdClaimCreate, String tdCalcSingleBenefitAmount) {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(policyType);
        policyType.get().createPolicy(tdPolicy);

        IClaim claim = claimType.get();
        TestData tdClaim = testDataManager.groupBenefitsClaims.get(claimType);

        LOGGER.info("Test: Step 1");
        claimType.get().create(tdClaimCreate);
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", tdCalcSingleBenefitAmount), 1);

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check").resolveLinks());

        verifyReissuePaymentLinkAbsent(1);

        LOGGER.info("Test: Step 2.1 Assert in Pending Status");
        toSubTab(OVERVIEW);
        claim.claimUpdate().perform(tdClaim.getTestData("TestClaimUpdate", "TestData")
                .adjust(TestData.makeKeyPath(AdditionalPartiesAdditionalPartyActionTab.class.getSimpleName(), SOCIAL_SECURITY_NUMBER_SSN.getLabel()), "111-11-1111"));

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check").resolveLinks());

        verifyReissuePaymentLinkAbsent(2);

        LOGGER.info("Test: Steps 2.2 Assert in Disapproved Status");
        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);
        claim.denyPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DenyPayment"), 2);

        verifyReissuePaymentLinkAbsent(2);

        LOGGER.info("Test: Step 2.3 Assert in Voided Status");
        claim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 2);

        verifyReissuePaymentLinkAbsent(2);

        LOGGER.info("Test: Steps 3 Assert in Issued Status");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check").resolveLinks());

        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 3);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);

        ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries
                .getRow(3).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(REISSUE_PAYMENT))).isPresent();

        buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("Test: Step 4");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Electronic Funds Transfer (EFT)").resolveLinks());

        verifyReissuePaymentLinkAbsent(4);

        LOGGER.info("Test: Step 5.1 Assert in Pending Status");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Electronic Funds Transfer (EFT)").resolveLinks());

        verifyReissuePaymentLinkAbsent(5);

        LOGGER.info("Test: Step 5.2 Assert in Disapproved Status");
        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);
        claim.denyPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DenyPayment"), 5);

        verifyReissuePaymentLinkAbsent(5);

        LOGGER.info("Test: Step 5.3 Assert in Voided Status");
        claim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 5);

        verifyReissuePaymentLinkAbsent(5);

        LOGGER.info("Test: Step 5.3 Assert in Issued Status");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Electronic Funds Transfer (EFT)").resolveLinks());

        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 6);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 6);

        verifyReissuePaymentLinkAbsent(6);
    }

    private void verifyReissuePaymentLinkAbsent(int payment) {
        ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries
                .getRow(payment).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(REISSUE_PAYMENT))).isAbsent();

        buttonCancel.click();
        dialogConfirmation.confirm();
    }
}