package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.dblookups.DBLookupsContext;
import com.exigen.ren.admin.modules.general.dblookups.pages.DBLookupsPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaim401KBenefit;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.LONG_TERM_DISABILITY;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.BENEFIT;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.SOCIAL_SECURITY_NUMBER_SSN;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.CLAIM_GB;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;
import static com.exigen.ren.utils.groups.Groups.WITH_TS;

public class TestClaim401KBenefit extends ScenarioTestClaim401KBenefit implements DBLookupsContext {
    private DeductionsActionTab deductionsActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(DeductionsActionTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33762", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUIChangeFor401KContribution() {
        TestData tdPolicy = getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "10%").resolveLinks()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_MONTHLY_MAXIMUM_AMOUNT.getLabel()), "1000").resolveLinks();
        preconditions(tdPolicy);
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"));
        claim.claimOpen().perform();
        testClaimUIChangeFor401KContributionCommonSteps();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-33813", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCalculatedChangeFor401KContribution() {
        TestData tdPolicy = getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "10%").resolveLinks()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_MONTHLY_MAXIMUM_AMOUNT.getLabel()), "1000").resolveLinks();
        preconditions(tdPolicy);
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusYears(100).format(DateTimeUtils.MM_DD_YYYY)));
        disabilityClaim.claimOpen().perform();
        testClaimCalculatedChangeFor401KContributionCommonSteps();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27662", component = CLAIMS_GROUPBENEFITS)
    public void testCheckLookup() {
        adminApp().open();
        String lookupName = "PrecClaimsTypeOfDeduction";
        dbLookups.initiate(lookupName);
        DBLookupsPage.tableSearchResults.getRow(1).getCell(DBLookupsPage.DBLookupsSearchResults.NAME.getName()).controls.links.get(lookupName).click();

        assertSoftly(softly -> {
            softly.assertThat(DBLookupsPage.tableLookupValues.getRow(1)).hasCellWithValue(DBLookupsPage.DBLookupsValues.CODE.getName(), "401K");
            softly.assertThat(DBLookupsPage.tableLookupValues.getRow(1)).hasCellWithValue(DBLookupsPage.DBLookupsValues.VALUE.getName(), _401K_CONTRIBUTION_DURING_DISABILITY_BENEFIT);
            softly.assertThat(DBLookupsPage.tableLookupValues.getRow(1)).hasCellWithValue(DBLookupsPage.DBLookupsValues.LOB_CD.getName(), "DISABILITY");
            softly.assertThat(DBLookupsPage.tableLookupValues.getRow(1)).hasCellWithValue(DBLookupsPage.DBLookupsValues.ORDER_NO.getName(), "30");
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28080", component = CLAIMS_GROUPBENEFITS)
    public void testPaymentCalculationWithFor401KContribution() {
        TestData tdPolicy = getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "N/A").resolveLinks();
        preconditions(tdPolicy);

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_Without_Benefits_Without_AdditionalParties"));
        claim.claimOpen().perform();

        LOGGER.info("TEST REN-28080: Step 1.4");
        claim.addBenefit().start(LONG_TERM_DISABILITY);
        BenefitLTDInjuryPartyInformationTab benefitLTDInjuryPartyInformationTab = claim.addBenefit().getWorkspace().getTab(BenefitLTDInjuryPartyInformationTab.class);
        assertThat(benefitLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).getValue()).containsOnlyOnce("");

        disabilityClaim.addBenefit().getWorkspace().fill(tdClaim.getTestData("NewBenefit", "TestData_LTD_OtherValues"));
        ServicesServiceRequestTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-28080: Step 2");
        checkDeductionsActionTab();
        assertThat(deductionsActionTab.getAssetList().getAsset(REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT)).isAbsent();

        DeductionsActionTab.buttonRemove.click();
        Page.dialogConfirmation.buttonYes.click();
        DeductionsActionTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-28080: Step 1.5");
        claim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fill(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"));
        assertThat(claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class).getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).isAbsent();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28080", component = CLAIMS_GROUPBENEFITS)
    public void testPaymentCalculationWithoutFor401KContribution() {
        TestData tdPolicy = getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "10%").resolveLinks();
        preconditions(tdPolicy);

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit"));
        claim.claimOpen().perform();

        LOGGER.info("TEST REN-28080: Step 3, 5, 6");
        checkDeductionsActionTab();
        deductionsActionTab.getAssetList().getAsset(TYPE_OF_DEDUCTION).setValue(_401K_CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        assertSoftly(softly -> {
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(PARTY).getAllValues()).hasSize(2);
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(PRIORITY)).hasValue("3");
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(MEMO)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(CODE)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(APPLY_PRE_TAX)).isPresent();
        });
        DeductionsActionTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("TEST REN-28080: Step 4");
        toSubTab(OVERVIEW);
        AdditionalPartiesAdditionalPartyActionTab additionalPartyActionTab = claim.claimUpdate().getWorkspace().getTab(AdditionalPartiesAdditionalPartyActionTab.class);
        claim.claimUpdate().perform(tdClaim.getTestData("TestClaimUpdate", "TestData")
                .adjust(makeKeyPath(additionalPartyActionTab.getMetaKey(), BENEFIT.getLabel()), "index=1")
                .adjust(makeKeyPath(additionalPartyActionTab.getMetaKey(), SOCIAL_SECURITY_NUMBER_SSN.getLabel()), "111-11-1111"));
        checkDeductionsActionTab();
        deductionsActionTab.getAssetList().getAsset(TYPE_OF_DEDUCTION).setValue(_401K_CONTRIBUTION_DURING_DISABILITY_BENEFIT);
        assertThat(deductionsActionTab.getAssetList().getAsset(PARTY).getAllValues()).hasSize(3);
    }

    private void preconditions(TestData tdPolicy) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(tdPolicy);
    }

    private void checkDeductionsActionTab() {
        claim.calculateSingleBenefitAmount().start(1);
        disabilityClaim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), OtherIncomeBenefitActionTab.class);
        deductionsActionTab.navigate();
        DeductionsActionTab.buttonAdd.click();
    }
}
