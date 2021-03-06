package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.FEE_UPDATE_REASON;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.PROPOSED_ASO_FEE;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction.startEndorsementForPolicy;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.asoFeeOverrideReasonByIndex;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.RatingReportView.proposedASOFeeByIndex;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryASOFeeTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestOverrideASOFeeFunctionalityVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41142"}, component = POLICY_GROUPBENEFITS)
    public void testOverrideASOFeeFunctionalityVerification_VS() {
        LOGGER.info("General Preconditions");
        Currency underwrittenASOFee = createQuoteWithUnderwrittenASOFee();
        Currency proposalASOFee = underwrittenASOFee.add(new Currency(1));
        issuePolicyWithProposedAsoFee(proposalASOFee, false);

        LOGGER.info("Step#1.1 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_DN, groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();
        int rowIndex = premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getIndex();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(proposedASOFeeByIndex(rowIndex)).isAbsent();
            softly.assertThat(asoFeeOverrideReasonByIndex(rowIndex)).isAbsent();
            softly.assertThat(premiumSummaryASOFeeTable.getHeader().getValue()).doesNotContain(PROPOSED_ANNUAL_ASO_FEE.getName());

            LOGGER.info("Steps#1.3, 2 verification");
            premiumSummaryTab.rate();
            softly.assertThat(proposedASOFeeByIndex(rowIndex)).isPresent().isEnabled().hasValue(proposalASOFee.toString());

            LOGGER.info("Steps#1.3, 3 verification");
            Currency proposedAnnualAsoFee = proposalASOFee.multiply(12).multiply(32);
            softly.assertThat(premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getCell(PROPOSED_ANNUAL_ASO_FEE.getName())).isPresent().hasValue(proposedAnnualAsoFee.toString());

            LOGGER.info("Steps#1.3, 4 verification");
            softly.assertThat(asoFeeOverrideReasonByIndex(rowIndex)).isPresent().isEnabled().hasValue("Maintain In Force ASO Fee")
                    .containsAllOptions(EMPTY, "Competitive Adjustment", "Premium Fund", "Premium Fund + Competitive Adjustment", "Match Quote", "Multi Policy Discount", "Business Decision", "Maintain In Force ASO Fee");

            LOGGER.info("Step#5 verification");
            proposedASOFeeByIndex(rowIndex).setValue(underwrittenASOFee.subtract(new Currency(1)).toString());
            softly.assertThat(asoFeeOverrideReasonByIndex(rowIndex)).isEnabled().hasValue(EMPTY)
                    .containsAllOptions(EMPTY, "Competitive Adjustment", "Premium Fund", "Premium Fund + Competitive Adjustment", "Match Quote", "Multi Policy Discount", "Business Decision");

            proposedASOFeeByIndex(rowIndex).setValue(EMPTY);
            PremiumSummaryTab.buttonTopSave.click();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("'Proposed ASO Fee' is Required."))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("'Fee Update Reason' is required"))).isPresent();

            LOGGER.info("REN-41159 TC1 Steps#5.5, 5.6, 6 verification");
            Currency newValueAsoFee = proposalASOFee.add(5);
            proposedASOFeeByIndex(rowIndex).setValue(newValueAsoFee.toString());
            checkAsoFeeAttributesWithOverride(newValueAsoFee, softly);

            LOGGER.info("REN-41159 TC1 Step#8 verification");
            clickOnPendedEndorseAndNavigateToPremium();
            proposedASOFeeByIndex(rowIndex).setValue(underwrittenASOFee.toString());
            checkAsoFeeAttributesWithOutOverride(underwrittenASOFee, softly);

            LOGGER.info("REN-41159 TC1 Step#11 verification");
            clickOnPendedEndorseAndNavigateToPremium();
            proposedASOFeeByIndex(rowIndex).setValue(proposalASOFee.toString());

            Currency proposedAnnualAsoFeeEndorseInitial = proposalASOFee.multiply(12).multiply(32);
            softly.assertThat(asoFeeOverrideReasonByIndex(rowIndex)).isPresent().isEnabled().hasValue("Maintain In Force ASO Fee");
            softly.assertThat(premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getCell(PROPOSED_ANNUAL_ASO_FEE.getName())).isPresent().hasValue(proposedAnnualAsoFeeEndorseInitial.toString());
            asoFeeOverrideReasonByIndex(rowIndex).setValue("Premium Fund");

            PremiumSummaryTab.buttonSaveAndExit.click();
            softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41159"}, component = POLICY_GROUPBENEFITS)
    public void testOverrideASOFeeRuleVerification_VS() {
        Currency underwrittenASOFee = createQuoteWithUnderwrittenASOFee();
        issuePolicyWithProposedAsoFee(underwrittenASOFee, true);

        LOGGER.info("REN-41159 TC2 steps#1, 2 verification");
        startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_VS, groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.rate();

        assertSoftly(softly -> {
            int rowIndex = checkAsoFeeAttributesWithOutOverride(underwrittenASOFee, softly);

            LOGGER.info("REN-41159 TC2 step#5 verification");
            clickOnPendedEndorseAndNavigateToPremium();
            checkAsoFeeAttributesWithOverride(underwrittenASOFee.add(1), softly);

            LOGGER.info("REN-41159 TC2 step#6 verification");
            clickOnPendedEndorseAndNavigateToPremium();
            proposedASOFeeByIndex(rowIndex).setValue(underwrittenASOFee.toString());
            checkAsoFeeAttributesWithOutOverride(underwrittenASOFee, softly);
        });
    }

    private Currency createQuoteWithUnderwrittenASOFee() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        quoteInitiateAndFillUpToTab(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataASO")
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()), PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        Currency underwrittenASOFee = new Currency(premiumSummaryASOFeeTable.getRowContains(ASO_FEE_BASIS.getName(), PER_EMPLOYEE_PER_MONTH)
                .getCell(TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ASO_FEE.getName()).getValue());
        PremiumSummaryTab.buttonSaveAndExit.click();
        return underwrittenASOFee;
    }

    private void issuePolicyWithProposedAsoFee(Currency proposalASOFee, boolean equalValues) {
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupVisionMasterPolicy.propose().start();
        groupVisionMasterPolicy.propose().getWorkspace().fillUpTo(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY), ProposalActionTab.class, false);
        proposalActionTab.getAssetList().getAsset(PROPOSED_ASO_FEE).setValue(proposalASOFee.toString());
        if (!equalValues) {
            proposalActionTab.getAssetList().getAsset(FEE_UPDATE_REASON).setValue("index=1");
        }

        groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), ImmutableList.of(
                        "Proposal for ASO Plan requires Underwriter approval")));
        proposalActionTab.submitTab();

        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), "PlanKey"), "ASO A La Carte-ASO A La Carte").resolveLinks());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    private int checkAsoFeeAttributesWithOutOverride(Currency proposalASOFee, ETCSCoreSoftAssertions softly) {
        int rowIndex = premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getIndex();
        Currency proposedAnnualAsoFeeEndorseInitial = proposalASOFee.multiply(12).multiply(32);

        softly.assertThat(proposedASOFeeByIndex(rowIndex)).isPresent().isEnabled().hasValue(proposalASOFee.toString());
        softly.assertThat(premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getCell(PROPOSED_ANNUAL_ASO_FEE.getName())).isPresent()
                .hasValue(proposedAnnualAsoFeeEndorseInitial.toString());
        softly.assertThat(asoFeeOverrideReasonByIndex(rowIndex)).isAbsent();

        PremiumSummaryTab.buttonSaveAndExit.click();
        softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
        return rowIndex;
    }

    private void checkAsoFeeAttributesWithOverride(Currency proposalASOFee, ETCSCoreSoftAssertions softly) {
        int rowIndex = PremiumSummaryTab.premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getIndex();
        proposedASOFeeByIndex(rowIndex).setValue(proposalASOFee.toString());

        softly.assertThat(asoFeeOverrideReasonByIndex(rowIndex)).isPresent().isEnabled().hasValue(EMPTY).doesNotContainOption("Maintain In Force ASO Fee");
        Currency proposedAnnualAsoFeeEndorseInitial = proposalASOFee.multiply(12).multiply(32);
        softly.assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable.getRowContains(PARTICIPANTS.getName(), "32").getCell(PROPOSED_ANNUAL_ASO_FEE.getName())).isPresent()
                .hasValue(proposedAnnualAsoFeeEndorseInitial.toString());

        asoFeeOverrideReasonByIndex(rowIndex).setValueByIndex(1);
        PremiumSummaryTab.buttonSaveAndExit.click();
        softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
    }

    private void clickOnPendedEndorseAndNavigateToPremium() {
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupVisionMasterPolicy.dataGather().start();
        premiumSummaryTab.navigateToTab();
    }
}
