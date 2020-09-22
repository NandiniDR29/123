package com.exigen.ren.modules.policy.gb_common;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.PolicyConstants.PlanDental;
import com.exigen.ren.main.enums.PolicyConstants.PlanPFL;
import com.exigen.ren.main.enums.PolicyConstants.PlanVision;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_QA_QA_LOGIN;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.TableConstants.CoveragePremiumTable.*;
import static com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab.SELECT_QUOTE_BY_ROW_NUMBER_KEY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.SIC_DESCRIPTION;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.PROPOSED_RATE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.PROPOSED_RATE_UPDATE_REASON;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_TOTAL_NUMBER_ELIGIBLE_LIVES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAbilityToApplyDiscountFromMultiQuoteProposal extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, GroupAccidentMasterPolicyContext, GroupDentalMasterPolicyContext, PaidFamilyLeaveMasterPolicyContext {

    public static final String DISCOUNT_REASON_COMPETITIVE_ADJUSTMENT = "Competitive Adjustment";
    public static final String DISCOUNT_REASON_PREMIUM_FUND = "Premium Fund";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14538", component = POLICY_GROUPBENEFITS)
    public void testAbilityToApplyDiscountFromMultiQuoteProposal() {
        mainApp().open(USER_QA_QA_LOGIN, "qa");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType(), groupAccidentMasterPolicy.getType(), groupDentalMasterPolicy.getType(), paidFamilyLeaveMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "DE")
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.planDefinitionTab.getClass().getSimpleName() + "[1]", RATING.getLabel(), CREDIBILITY_FACTOR.getLabel()), "0.5")
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.planDefinitionTab.getClass().getSimpleName() + "[1]", RATING.getLabel(), SIC_DESCRIPTION.getLabel()), "index=1"));
        String dn_DE_QuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "WA")
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "52"));
        String dn_WA_QuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "WA")
                .adjust(TestData.makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getMetaKey(), POLICY_TOTAL_NUMBER_ELIGIBLE_LIVES.getLabel()), "52"));
        String vs_WA_QuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        paidFamilyLeaveMasterPolicy.createQuote(getDefaultPFLMasterPolicyData());
        String pflQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step 1");
        groupDentalMasterPolicy.propose().start();
        groupDentalMasterPolicy.propose().getWorkspace().fillUpTo(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), SELECT_QUOTE_BY_ROW_NUMBER_KEY), ImmutableList.of("1", "2", "3", "4")), ProposalActionTab.class, false);

        assertSoftly(softly ->
                ImmutableList.of(dn_DE_QuoteNumber, dn_WA_QuoteNumber, vs_WA_QuoteNumber, pflQuoteNumber).forEach(quoteNumber -> {
                    softly.assertThat(ProposalActionTab.getRateAdjustmentPercentageForQuote(quoteNumber)).hasValue(ValueConstants.EMPTY);
                    softly.assertThat(ProposalActionTab.getDiscountReasonForQuote(quoteNumber)).isAbsent();
                    softly.assertThat(ProposalActionTab.getRateAdjustmentApplyButtonForQuote(quoteNumber)).isAbsent();
                }));

        LOGGER.info("Step 2");
        checkTableCoveragePremiumForQuote(dn_DE_QuoteNumber, PlanDental.ALACARTE);
        checkTableCoveragePremiumForQuote(dn_WA_QuoteNumber, PlanDental.ALACARTE);
        checkTableCoveragePremiumForQuote(vs_WA_QuoteNumber, PlanVision.A_LA_CARTE);
        checkTableCoveragePremiumForQuote(pflQuoteNumber, PlanPFL.FLB);

        LOGGER.info("Step 5");
        assertSoftly(softly -> ImmutableList.of(dn_DE_QuoteNumber, dn_WA_QuoteNumber, vs_WA_QuoteNumber, pflQuoteNumber).forEach(quoteNumber -> {
            TextBox rateAdjustmentPercentageForQuote = ProposalActionTab.getRateAdjustmentPercentageForQuote(quoteNumber);
            rateAdjustmentPercentageForQuote.setValue("0.195");
            softly.assertThat(rateAdjustmentPercentageForQuote).hasValue("0.20%");
        }));

        LOGGER.info("Step 6-7");
        assertSoftly(softly -> ImmutableList.of(dn_DE_QuoteNumber, dn_WA_QuoteNumber, vs_WA_QuoteNumber, pflQuoteNumber).forEach(quoteNumber -> {
            ProposalActionTab.getRateAdjustmentPercentageForQuote(quoteNumber).setValue("20");
            ProposalActionTab.getDiscountReasonForQuote(quoteNumber).setValue(ValueConstants.EMPTY);
            ProposalActionTab.getRateAdjustmentApplyButtonForQuote(quoteNumber).click();
            ProposalActionTab.buttonCalculatePremium.click();
            softly.assertThat(ProposalActionTab.getDiscountReasonErrorMessageForQuote(quoteNumber)).hasValue("Reason is required.");

            ProposalActionTab.buttonCalculatePremium.click();
            softly.assertThat(ProposalActionTab.getDiscountReasonErrorMessageForQuote(quoteNumber)).hasValue("Reason is required.");
        }));


        LOGGER.info("Step 3 for dn_DE_QuoteNumber");
        setRateAdjustmentPercentageForQuoteAndApplyReason(dn_DE_QuoteNumber, "101");
        assertThat(ProposalActionTab.getErrorMessageRateForAdjustmentPercentageForQuote(dn_DE_QuoteNumber)).hasValue("Amount entered is over allowed limit");

        LOGGER.info("Step 3, 4 for pflQuoteNumber");
        assertSoftly(softly ->
                ImmutableList.of("101", "10", "-101").forEach(value -> {
                    setRateAdjustmentPercentageForQuoteAndApplyReason(pflQuoteNumber, value);
                    assertThat(ProposalActionTab.getErrorMessageRateForAdjustmentPercentageForQuote(pflQuoteNumber)).hasValue("Amount entered is over allowed limit");
                }));


        LOGGER.info("Step 3 for dn_WA_QuoteNumber, vs_WA_QuoteNumber");
        assertSoftly(softly -> ImmutableList.of(dn_WA_QuoteNumber, vs_WA_QuoteNumber).forEach(quoteNumber -> {
            setRateAdjustmentPercentageForQuoteAndApplyReason(quoteNumber, "101");
            softly.assertThat(ProposalActionTab.getErrorMessageRateForAdjustmentPercentageForQuote(quoteNumber)).isAbsent();
        }));

        LOGGER.info("Step 9");
        calculateAndCheckProposedTermRate(dn_WA_QuoteNumber, PlanDental.ALACARTE);
        calculateAndCheckProposedTermRate(vs_WA_QuoteNumber, PlanVision.A_LA_CARTE);
        assertThat(ProposalActionTab.buttonSaveAndExit).isDisabled();

        LOGGER.info("Step 10");
        overrideAndCheckProposedProposedTermRate(dn_DE_QuoteNumber, PlanDental.ALACARTE);
        overrideAndCheckProposedProposedTermRate(dn_WA_QuoteNumber, PlanDental.ALACARTE);
        overrideAndCheckProposedProposedTermRate(vs_WA_QuoteNumber, PlanVision.A_LA_CARTE);

        LOGGER.info("Step 4 for dn_WA_QuoteNumber, vs_WA_QuoteNumber");
        assertSoftly(softly -> ImmutableList.of(dn_WA_QuoteNumber, vs_WA_QuoteNumber).forEach(quoteNumber -> {
            setRateAdjustmentPercentageForQuoteAndApplyReason(quoteNumber, "-101");
            softly.assertThat(ProposalActionTab.getErrorMessageRateForAdjustmentPercentageForQuote(quoteNumber)).isAbsent();
        }));

        LOGGER.info("Step 11");
        updateAndCheckRateUpdateReason(dn_DE_QuoteNumber, PlanDental.ALACARTE);
        updateAndCheckRateUpdateReason(dn_WA_QuoteNumber, PlanDental.ALACARTE);
        updateAndCheckRateUpdateReason(vs_WA_QuoteNumber, PlanVision.A_LA_CARTE);

        LOGGER.info("Step 12");
        clearAndCheckRateAdjustmentPercent(dn_DE_QuoteNumber, PlanDental.ALACARTE);
        clearAndCheckRateAdjustmentPercent(dn_WA_QuoteNumber, PlanDental.ALACARTE);
        clearAndCheckRateAdjustmentPercent(vs_WA_QuoteNumber, PlanVision.A_LA_CARTE);

        LOGGER.info("Step 13");
        ImmutableList.of(dn_WA_QuoteNumber, vs_WA_QuoteNumber).forEach(quoteNumber ->
                setRateAdjustmentPercentageForQuoteAndApplyReason(quoteNumber, "101"));

        String dn_DE_ProposedTermRate = ProposalActionTab.getTableCoveragePremium(dn_DE_QuoteNumber, PlanDental.ALACARTE).getRow(1).getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst().getValue();

        String dn_WA_ProposedTermRate = ProposalActionTab.getTableCoveragePremium(dn_WA_QuoteNumber, PlanDental.ALACARTE).getRow(1).getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst().getValue();
        String dn_WA_RateUpdateReason = ProposalActionTab.getTableCoveragePremium(dn_WA_QuoteNumber, PlanDental.ALACARTE).getRow(1).getCell(RATE_UPDATE_REASON.getName()).controls.comboBoxes.getFirst().getValue();

        String vs_WA_ProposedTermRate = ProposalActionTab.getTableCoveragePremium(vs_WA_QuoteNumber, PlanDental.ALACARTE).getRow(1).getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst().getValue();
        String vs_WA_RateUpdateReason = ProposalActionTab.getTableCoveragePremium(vs_WA_QuoteNumber, PlanDental.ALACARTE).getRow(1).getCell(RATE_UPDATE_REASON.getName()).controls.comboBoxes.getFirst().getValue();

        String pfl_ProposedTermRate = ProposalActionTab.getTableCoveragePremium(pflQuoteNumber, PlanPFL.FLB).getRow(1).getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst().getValue();

        ProposalActionTab.buttonCalculatePremium.click();
        assertThat(ProposalActionTab.buttonSaveAndExit).isEnabled();
        ProposalActionTab.buttonSaveAndExit.click();

        LOGGER.info("Step 14");
        search(dn_DE_QuoteNumber);
        groupDentalMasterPolicy.dataGather().start();
        GroupDentalMasterPolicyContext.classificationManagementMpTab.navigateToTab();
        assertThat(GroupDentalMasterPolicyContext.classificationManagementMpTab.getAssetList().getAsset(PLAN_TIER_AND_RATING_INFO).getAsset(PlanTierAndRatingInfoMetaData.PROPOSED_RATE)).hasValue(dn_DE_ProposedTermRate);

        search(dn_WA_QuoteNumber);
        Page.dialogConfirmation.confirm();
        groupDentalMasterPolicy.dataGather().start();
        GroupDentalMasterPolicyContext.classificationManagementMpTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(GroupDentalMasterPolicyContext.classificationManagementMpTab.getAssetList().getAsset(PLAN_TIER_AND_RATING_INFO).getAsset(PlanTierAndRatingInfoMetaData.PROPOSED_RATE))
                    .hasValue(dn_WA_ProposedTermRate);
            softly.assertThat(GroupDentalMasterPolicyContext.classificationManagementMpTab.getAssetList().getAsset(PLAN_TIER_AND_RATING_INFO).getAsset(PlanTierAndRatingInfoMetaData.PROPOSED_RATE_UPDATE_REASON))
                    .hasValue(dn_WA_RateUpdateReason);
        });

        search(vs_WA_QuoteNumber);
        Page.dialogConfirmation.confirm();
        groupVisionMasterPolicy.dataGather().start();
        GroupVisionMasterPolicyContext.classificationManagementMpTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(GroupVisionMasterPolicyContext.classificationManagementMpTab.getAssetList().getAsset(PROPOSED_RATE)).hasValue(vs_WA_ProposedTermRate);
            softly.assertThat(GroupVisionMasterPolicyContext.classificationManagementMpTab.getAssetList().getAsset(PROPOSED_RATE_UPDATE_REASON)).hasValue(vs_WA_RateUpdateReason);
        });

        search(pflQuoteNumber);
        Page.dialogConfirmation.confirm();
        paidFamilyLeaveMasterPolicy.dataGather().start();
        PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.navigateToTab();
        assertThat(PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.getAssetList().getAsset(com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.ClassificationManagementTabMetaData.PROPOSED_RATE))
                .hasValue(pfl_ProposedTermRate);
    }

    private void setRateAdjustmentPercentageForQuoteAndApplyReason(String quoteNumber, String value) {
        ProposalActionTab.getRateAdjustmentPercentageForQuote(quoteNumber).setValue(value);
        ProposalActionTab.getDiscountReasonForQuote(quoteNumber).setValue(DISCOUNT_REASON_COMPETITIVE_ADJUSTMENT);
        ProposalActionTab.getRateAdjustmentApplyButtonForQuote(quoteNumber).click();
    }

    private void clearAndCheckRateAdjustmentPercent(String quoteNumber, String planName) {
        ProposalActionTab.getRateAdjustmentPercentageForQuote(quoteNumber).clear();
        Row planRow = ProposalActionTab.getTableCoveragePremium(quoteNumber, planName).getRow(1);
        assertSoftly(softly -> {
            softly.assertThat(planRow.getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst()).hasValue(planRow.getCell(UNDERWRITTEN_TERM_RATE.getName()).getValue());
            softly.assertThat(planRow.getCell(RATE_UPDATE_REASON.getName()).controls.comboBoxes.getFirst()).isAbsent();
            softly.assertThat(planRow.getCell(RATE_UPDATE_REASON.getName())).hasValue(ValueConstants.EMPTY);
        });
    }

    private void updateAndCheckRateUpdateReason(String quoteNumber, String planName) {
        Row planRow = ProposalActionTab.getTableCoveragePremium(quoteNumber, planName).getRow(1);
        ComboBox rateUpdateReason = planRow.getCell(RATE_UPDATE_REASON.getName()).controls.comboBoxes.getFirst();
        rateUpdateReason.setValue(DISCOUNT_REASON_PREMIUM_FUND);
        assertThat(rateUpdateReason).hasValue(DISCOUNT_REASON_PREMIUM_FUND);
    }

    private void overrideAndCheckProposedProposedTermRate(String quoteNumber, String planName) {
        Row planRow = ProposalActionTab.getTableCoveragePremium(quoteNumber, planName).getRow(1);
        planRow.getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst().setValue("1");
        assertThat(planRow.getCell(RATE_ADJUSTMENT_PERCENTAGE.getName())).hasValue(ValueConstants.EMPTY);
    }

    private void calculateAndCheckProposedTermRate(String quoteNumber, String planName) {
        Row planRow = ProposalActionTab.getTableCoveragePremium(quoteNumber, planName).getRow(1);
        Float rateAdjustmentPercentage = Float.valueOf(planRow.getCell(RATE_ADJUSTMENT_PERCENTAGE.getName()).getValue().replaceAll("%", ""));
        Currency underwrittenTermRate = new Currency(planRow.getCell(UNDERWRITTEN_TERM_RATE.getName()).getValue());
        assertSoftly(softly -> {
            softly.assertThat(planRow.getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst())
                    .hasValue(new Currency(underwrittenTermRate.asBigDecimal().multiply(BigDecimal.valueOf((100 + rateAdjustmentPercentage) / 100)).setScale(2, RoundingMode.DOWN)).toString());
            softly.assertThat(planRow.getCell(RATE_UPDATE_REASON.getName()).controls.comboBoxes.getFirst()).hasValue(ProposalActionTab.getDiscountReasonForQuote(quoteNumber).getValue());
        });
    }

    private void checkTableCoveragePremiumForQuote(String quoteNumber, String planName) {
        assertSoftly(softly -> {
            Row planAlacarteRow = ProposalActionTab.getTableCoveragePremium(quoteNumber, planName).getRow(1);
            softly.assertThat(planAlacarteRow).hasCellWithValue(RATE_ADJUSTMENT_PERCENTAGE.getName(), ValueConstants.EMPTY);
            softly.assertThat(planAlacarteRow).hasCellWithValue(RATE_UPDATE_REASON.getName(), ValueConstants.EMPTY);
            softly.assertThat(planAlacarteRow.getCell(PROPOSED_TERM_RATE.getName()).controls.textBoxes.getFirst()).hasValue(planAlacarteRow.getCell(UNDERWRITTEN_TERM_RATE.getName()).getValue());
            softly.assertThat(planAlacarteRow.getCell(PROPOSED_ANNUAL_PREMIUM.getName())).hasValue(planAlacarteRow.getCell(UNDERWRITTEN_ANNUAL_PREMIUM.getName()).getValue());
        });
    }

}
