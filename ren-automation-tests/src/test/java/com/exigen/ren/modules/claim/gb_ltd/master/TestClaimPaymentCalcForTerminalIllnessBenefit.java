package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimPaymentViewTableExtended.OFFSET_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ESTIMATED_COST_VALUE;
import static com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage.tableClaimPaymentView;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.logging.log4j.util.Strings.EMPTY;

public class TestClaimPaymentCalcForTerminalIllnessBenefit extends ClaimGroupBenefitsLTDBaseTest {
    PaymentPaymentPaymentAllocationTab paymentAllocationTab = disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private final static String TERMINAL_ILLNESS_BENEFIT = "Terminal Illness Benefit";
    private static int TERMINAL_ILLNESS_BENEFIT_VALUE = 3;  // Terminal Illness Benefit = 3 Months

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28182", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForTerminalIllnessBenefit() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        LOGGER.info("Step 1, 2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), BenefitsLTDInjuryPartyInformationTab.class, true);

        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(TERMINAL_ILLNESS_BENEFIT);

        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(1800).toString());

        LOGGER.info("Step 3");
        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).
                adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), benefitsLTDIncidentTab.getClass());

        disabilityClaim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Step 5");
        disabilityClaim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).getValue()).matches(TERMINAL_ILLNESS_BENEFIT.concat(".*"));

        LOGGER.info("Step 6");
        Tab.buttonSaveAndExit.click();
        disabilityClaim.calculateSingleBenefitAmount().perform(tdSpecific().getTestData("TestData_OtherIncomeBenefit_F1"), 1);

        LOGGER.info("Step 7");
        disabilityClaim.calculateSingleBenefitAmount().perform(tdSpecific().getTestData("TestData_OtherIncomeBenefit_F2"), 1);

        LOGGER.info("Step 8");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment1"), PaymentPaymentPaymentAllocationTab.class, true);

        assertSoftly(softly -> {
            softly.assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(EMPTY);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Cannot be calculated due to Last LTD Benefit amount is empty."))).isPresent();
        });

        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("100");

        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("Step 9.1");
        disabilityClaim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 1);

        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment2"), PaymentPaymentPaymentAllocationTab.class, true);

        LOGGER.info("Step 9.2");
        Currency allocationAmountPayment2 = new Currency(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());

        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);

//        next actions are needed for calculation expected result for step 10.5
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();
        Currency offsetAmountPayment2 = new Currency(tableClaimPaymentView.getRow(1).getCell(OFFSET_AMOUNT.getName()).getValue());
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 9.3");
        disabilityClaim.addPayment().perform(tdSpecific().getTestData("TestData_PaymentDetails_Payment3"));

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(3);
        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 3);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        LOGGER.info("Step 9.4");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment4"), PaymentPaymentPaymentAllocationTab.class, true);

        Currency allocationAmountPayment4 = new Currency(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());

        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(4);

//        next actions are needed for calculation expected result for step 14.5
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(4).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();
        Currency offsetAmountPayment4 = new Currency(tableClaimPaymentView.getRow(1).getCell(OFFSET_AMOUNT.getName()).getValue());
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 10");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment5")
                .mask(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), RESERVE_TYPE.getLabel())).resolveLinks(), PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(EMPTY);

        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);

        Currency allocationAmountPayment5Expected = allocationAmountPayment2.subtract(offsetAmountPayment2).multiply(TERMINAL_ILLNESS_BENEFIT_VALUE);

        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment5"), PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(allocationAmountPayment5Expected).toString());

        LOGGER.info("Step 11");
        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("1800");
        disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).setAssociatedInsurableRiskByPartialValue("Smith");
        disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).setAssociatedInsurableRiskByPartialValue(TERMINAL_ILLNESS_BENEFIT);

        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(allocationAmountPayment5Expected).toString());

        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(5);

        LOGGER.info("Step 12");
        disabilityClaim.updatePayment().perform(tdSpecific().getTestData("TestData_PaymentDetails_Payment5_Update"), 5);

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(5);

        LOGGER.info("Step 13");
        disabilityClaim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 5);
        disabilityClaim.clearPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ClearPayment"), 2);

        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment5"), PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(allocationAmountPayment5Expected).toString());

        LOGGER.info("Step 14");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 4);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 4);
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_PaymentDetails_Payment5"), PaymentPaymentPaymentAllocationTab.class, true);

        allocationAmountPayment5Expected = allocationAmountPayment4.subtract(offsetAmountPayment4).multiply(TERMINAL_ILLNESS_BENEFIT_VALUE);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(allocationAmountPayment5Expected).toString());

        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(6);
    }
}