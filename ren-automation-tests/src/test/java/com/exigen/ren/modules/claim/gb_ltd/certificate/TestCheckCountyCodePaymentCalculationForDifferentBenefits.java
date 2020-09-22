package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimPaymentViewTableExtended.OFFSET_AMOUNT;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.ASSOCIATED_INSURABLE_RISK;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.COVERAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.OtherIncomeBenefitActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.PAYMENT_TO;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EmployerBenefitsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PARTICIPANT_CONTRIBUTION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimPaymentsPage.tableClaimPaymentView;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCheckCountyCodePaymentCalculationForDifferentBenefits extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PaymentPaymentPaymentDetailsTab paymentPaymentPaymentDetailsTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentDetailsTab.class);
    private String additionalParty = "Test Company Name";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38044", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculationForFamilyIncomeBenefit() {
        final String SURVIVOR_FAMILY_INCOME_BENEFIT = "Survivor - Family Income Benefit";
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), SURVIVOR_FAMILY_INCOME_BENEFIT_TYPE.getLabel()), "3 Months Survivor Income Benefit")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), PAY_SURVIVOR_BENEFIT_GROSS.getLabel()), "false")
                .resolveLinks());

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST REN-38044: Step 1-2-3");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(SURVIVOR_FAMILY_INCOME_BENEFIT);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(1800).toString());

        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY).
                adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), benefitsLTDIncidentTab.getClass());
        disabilityClaim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-38044: Step 5");
        claim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM)).valueContains(SURVIVOR_FAMILY_INCOME_BENEFIT);

        LOGGER.info("TEST REN-38044: Step 6");
        claim.updateBenefit().submit();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), ASSOCIATED_INSURABLE_RISK.getLabel()), "contains=" + SURVIVOR_FAMILY_INCOME_BENEFIT)
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), COVERAGE.getLabel()), "contains=LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "20000"), 1);

        LOGGER.info("TEST REN-38044: Step 7");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "20000")
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "30")
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), currentDate.plusDays(10).format((MM_DD_YYYY)))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), currentDate.plusMonths(2).plusDays(14).format((MM_DD_YYYY))), 1);

        LOGGER.info("TEST REN-38044: Step 8");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()))
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=" + additionalParty), paymentAllocationTab.getClass(), true);

        paymentAllocationTab.setSingleBenefitCalculationNumber("1-1");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Cannot be calculated due to Last LTD Benefit amount is empty."))).isPresent();

        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("100");
        claim.addPayment().submit();

        LOGGER.info("TEST REN-38044: Step 9");
        disabilityClaim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 1);
        Currency allocationAmountPayment2 = getAllocationAmount(currentDate, currentDate.plusMonths(1).minusDays(1));

        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), false);
        paymentAllocationTab.setSingleBenefitCalculationNumber("1-2");
        paymentAllocationTab.fillTab(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));
        claim.addPayment().submit();

        Currency allocationAmountPayment4 = getAllocationAmount(currentDate.plusMonths(2), currentDate.plusMonths(3).plusDays(9));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 3);
        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 4);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);

        LOGGER.info("TEST REN-38044: Step 10");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), false);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(EMPTY);
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();
        Currency offsetAmountPayment2 = new Currency(tableClaimPaymentView.getRow(1).getCell(OFFSET_AMOUNT.getName()).getValue());
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=" + additionalParty)
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), COVERAGE.getLabel()), "index=1")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel())), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(allocationAmountPayment2.subtract(offsetAmountPayment2).multiply(3).toString());

        LOGGER.info("TEST REN-38044: Step 11");
        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("1800");
        paymentAllocationTab.setSingleBenefitCalculationNumber("1-2");
        paymentAllocationTab.setSingleBenefitCalculationNumber("1-1");
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue((allocationAmountPayment2.subtract(offsetAmountPayment2)).multiply(3).toString());

        LOGGER.info("TEST REN-38044: Step 12");
        paymentPaymentPaymentDetailsTab.navigate();
        paymentPaymentPaymentDetailsTab.getAssetList().getAsset(PAYMENT_TO).setValueContains("John");
        claim.addPayment().submit();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("The claimant is not eligible for Survivor - Family Income Benefit."))).isPresent();

        LOGGER.info("TEST REN-38044: Step 13");
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        claim.clearPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ClearPayment"), 2);

        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=" + additionalParty)
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), COVERAGE.getLabel()), "index=1")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel())), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(allocationAmountPayment2.subtract(offsetAmountPayment2).multiply(3).toString());

        LOGGER.info("TEST REN-38044: Step 14");
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 4);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(4).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();
        Currency offsetAmountPayment4 = new Currency(tableClaimPaymentView.getRow(1).getCell(OFFSET_AMOUNT.getName()).getValue());
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=" + additionalParty)
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), COVERAGE.getLabel()), "index=1")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel())), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(allocationAmountPayment4.subtract(offsetAmountPayment4).multiply(3).toString());
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38051", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculationForTerminalIllnessBenefit() {
        final String TERMINAL_ILLNESS_BENEFIT_VALUE = "Terminal Illness Benefit";
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), TERMINAL_ILLNESS_BENEFIT.getLabel()), "3 Months")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), PAY_TERMINAL_ILLNESS_BENEFIT_GROSS.getLabel()), "false")
                .resolveLinks());

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST REN-38051: Step 1-2-3");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(TERMINAL_ILLNESS_BENEFIT_VALUE);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(1800).toString());

        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY).
                adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), benefitsLTDIncidentTab.getClass());
        disabilityClaim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-38051: Step 5");
        claim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM)).valueContains(TERMINAL_ILLNESS_BENEFIT_VALUE);

        LOGGER.info("TEST REN-38051: Step 6");
        claim.updateBenefit().submit();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), ASSOCIATED_INSURABLE_RISK.getLabel()), "contains=" + TERMINAL_ILLNESS_BENEFIT_VALUE)
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), COVERAGE.getLabel()), "contains=LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "20000"), 1);

        LOGGER.info("TEST REN-38051: Step 7");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "20000")
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "30")
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), currentDate.plusDays(10).format((MM_DD_YYYY)))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), currentDate.plusMonths(2).plusDays(14).format((MM_DD_YYYY))), 1);

        LOGGER.info("TEST REN-38051: Step 8");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()))
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), true);

        paymentAllocationTab.setSingleBenefitCalculationNumber("1-1");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Cannot be calculated due to Last LTD Benefit amount is empty."))).isPresent();

        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("100");
        claim.addPayment().submit();

        LOGGER.info("TEST REN-38051: Step 9");
        disabilityClaim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 1);
        Currency allocationAmountPayment2 = getAllocationAmount(currentDate, currentDate.plusMonths(1).minusDays(1));

        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), false);

        paymentAllocationTab.setSingleBenefitCalculationNumber("1-2");
        paymentAllocationTab.fillTab(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));
        claim.addPayment().submit();

        Currency allocationAmountPayment4 = getAllocationAmount(currentDate.plusMonths(2), currentDate.plusMonths(2).plusDays(10));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 3);
        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 4);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);

        LOGGER.info("TEST REN-38051: Step 10");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), false);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(EMPTY);
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);

        createPaymentWithAdditionalParty(allocationAmountPayment2.subtract(21), "John");

        LOGGER.info("TEST REN-38051: Step 11");
        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("1800");
        paymentAllocationTab.setSingleBenefitCalculationNumber("1-2");
        paymentAllocationTab.setSingleBenefitCalculationNumber("1-1");
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue((allocationAmountPayment2.subtract(21)).multiply(3).toString());

        LOGGER.info("TEST REN-38051: Step 12");
        paymentPaymentPaymentDetailsTab.navigate();
        paymentPaymentPaymentDetailsTab.getAssetList().getAsset(PAYMENT_TO).setValueContains(additionalParty);
        claim.addPayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(5);

        LOGGER.info("TEST REN-38051: Step 13");
        disabilityClaim.voidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidPayment"), 5);
        claim.clearPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ClearPayment"), 2);

        createPaymentWithAdditionalParty(allocationAmountPayment2.subtract(21), "John");

        LOGGER.info("TEST REN-38051: Step 14");
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 4);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(4).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();
        Currency offsetAmountPayment4 = new Currency(tableClaimPaymentView.getRow(1).getCell(OFFSET_AMOUNT.getName()).getValue());
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        createPaymentWithAdditionalParty(allocationAmountPayment4.subtract(offsetAmountPayment4), "John");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-REN-38134", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculationForWorkplaceModificationBenefit() {
        final String WORKPLACE_MODIFICATION_BENEFIT_VALUE = "Workplace Modification Benefit";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .mask(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.COUNTY_CODE.getLabel()))
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "GA")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), EmployerBenefitsMetaData.NONE.getLabel()), "false")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), WORKPLACE_MODIFICATION_BENEFIT_PERCENT.getLabel()), "100")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), WORKPLACE_MODIFICATION_BENEFIT_MAXIMUM.getLabel()), "2000")
                .resolveLinks());

        createDefaultLongTermDisabilityCertificatePolicy();

        LOGGER.info("TEST REN-38134: Step 1-2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(WORKPLACE_MODIFICATION_BENEFIT_VALUE);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(2000).toString());

        LOGGER.info("TEST REN-38134: Step 3");
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT)).isRequired().hasValue(EMPTY);

        LOGGER.info("TEST REN-38134: Step 4");
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT).setValue("3000");
        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY).
                adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), benefitsLTDIncidentTab.getClass());
        disabilityClaim.claimOpen().perform();

        LOGGER.info("TEST REN-38134: Step 6");
        claim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT)).isRequired().hasValue(new Currency(3000).toString());

        LOGGER.info("TEST REN-38134: Step 7");
        claim.updateBenefit().submit();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), ASSOCIATED_INSURABLE_RISK.getLabel()), "contains=" + WORKPLACE_MODIFICATION_BENEFIT_VALUE)
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), COVERAGE.getLabel()), "contains=LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "3000"), 1);

        LOGGER.info("TEST REN-38134: Step 8");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()))
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue("$2,000.00").isDisabled();

        claim.addPayment().submit();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("The claimant is not eligible for Workplace Modification Benefit."))).isPresent();

        LOGGER.info("TEST REN-38134: Step 9");
        paymentPaymentPaymentDetailsTab.navigate();
        paymentPaymentPaymentDetailsTab.getAssetList().getAsset(PAYMENT_TO).setValueContains(additionalParty);
        claim.addPayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-REN-38136", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculationForNoWorkplaceModificationBenefit() {
        final String WORKPLACE_MODIFICATION_BENEFIT_VALUE = "Workplace Modification Benefit";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .resolveLinks());

        createDefaultLongTermDisabilityCertificatePolicy();

        LOGGER.info("TEST REN-38136: Step 1-2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(WORKPLACE_MODIFICATION_BENEFIT_VALUE);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM)).doesNotContainOption(WORKPLACE_MODIFICATION_BENEFIT_VALUE);

        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT).setValue("3000");
        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY), benefitsLTDIncidentTab.getClass());
        disabilityClaim.claimOpen().perform();

        LOGGER.info("TEST REN-REN-38136: Step 3");
        claim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM)).doesNotContainOption(WORKPLACE_MODIFICATION_BENEFIT_VALUE);

        LOGGER.info("TEST REN-REN-38136: Step 4");
        claim.updateBenefit().submit();
        claim.calculateSingleBenefitAmount().start(1);
        assertThat(claim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK)).doesNotContainOption(WORKPLACE_MODIFICATION_BENEFIT_VALUE);
        claim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).fillTab(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"));
        CoveragesActionTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-REN-38136: Step 5");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentAllocationTab.getClass(), false);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK_LABEL).getValue()).doesNotContain(WORKPLACE_MODIFICATION_BENEFIT_VALUE);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38140", component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculationForPresumptiveDisabilityBenefit() {
        final String PRESUMPTIVE_DISABILITY_REMAINING_BALANCE = "Presumptive Benefit Remaining Balance";
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), PARTICIPANT_CONTRIBUTION.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), PRESUMPTIVE_DISABILITY.getLabel()), "90 days")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%")
                .resolveLinks());

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST REN-38140: Step 1-2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(PRESUMPTIVE_DISABILITY_REMAINING_BALANCE);

        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY).
                adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), benefitsLTDIncidentTab.getClass());
        disabilityClaim.claimOpen().perform();

        LOGGER.info("TEST REN-38140: Step 6");
        claim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM)).valueContains(PRESUMPTIVE_DISABILITY_REMAINING_BALANCE);

        LOGGER.info("TEST REN-38140: Step 7");
        claim.updateBenefit().submit();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), ASSOCIATED_INSURABLE_RISK.getLabel()), "contains=" + PRESUMPTIVE_DISABILITY_REMAINING_BALANCE)
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), COVERAGE.getLabel()), "contains=LTD"), 1);

        LOGGER.info("TEST REN-38140: Step 8");
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()))
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(EMPTY).isEnabled();

        LOGGER.info("TEST REN-38140: Step 9");
        paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).setValue("100");
        claim.addPayment().submit();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("The claimant is not eligible for Presumptive Benefit Remaining Balance."))).isPresent();

        LOGGER.info("TEST REN-38140: Step 10");
        paymentPaymentPaymentDetailsTab.navigate();
        paymentPaymentPaymentDetailsTab.getAssetList().getAsset(PAYMENT_TO).setValueContains(additionalParty);
        claim.addPayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
    }

    private Currency getAllocationAmount(LocalDate fromDate, LocalDate throughDate) {
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=John"), paymentAllocationTab.getClass(), false);

        paymentAllocationTab.setSingleBenefitCalculationNumber("1-2");
        paymentAllocationTab.fillTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), fromDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), throughDate.format(MM_DD_YYYY)));
        Currency allocationAmountPayment = new Currency(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());
        claim.addPayment().submit();
        return allocationAmountPayment;
    }

    private void createPaymentWithAdditionalParty(Currency amount, String party) {
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=" + party)
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()))
                .mask(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel())), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(amount.multiply(3).toString());
    }
}
