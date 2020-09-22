package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASO;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.DATA_GATHERING;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.INITIATED;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ANNUAL_ASO_FEE;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ASO_FEE;
import static com.exigen.ren.main.enums.TableConstants.ProposalASOFeeTable.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.FEE_UPDATE_REASON;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData.ADD_CLASSIFICATION_GROUP_RELATIONSHIP;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData.ASO_FEE_BASIS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.planTierAndRatingInfoTable;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.buttonRate;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryASOFeeTable;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableASOFeeBasis;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelQuoteStatus;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestASOFeeEntity extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String ASO_ASO = "ASO-ASO";
    private static final String PER_EMPLOYEE_PER_MONTH = "Per Employee Per Month";
    private static final int totalNumberOfEligibleLivesValue = 120;
    private static final int assumedParticipation_pct = 50;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23435"}, component = POLICY_GROUPBENEFITS)
    public void testASOFeeEntity() {
        LOGGER.info("REN-23435 Preconditions and Step#1");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_YES)
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), String.valueOf(totalNumberOfEligibleLivesValue))
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[0]", PlanDefinitionTabMetaData.PLAN.getLabel()), ASO)
                        .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                        .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS.getLabel()))
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(),
                                SponsorParticipantFundingStructureMetaData.ASSUMED_PARTICIPATION_PCT.getLabel()), String.valueOf(assumedParticipation_pct))
                        .adjust(TestData.makeKeyPath(classificationManagementMpTab.getClass().getSimpleName(), PlanDefinitionTabMetaData.PLAN.getLabel()), ASO_ASO),
                ClassificationManagementTab.class, false);

        LOGGER.info("Step#2 verification");
        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        assertThat(planTierAndRatingInfoTable).isPresent();

        LOGGER.info("Step#3 verification");
        planTierAndRatingInfoTable.getRows().forEach(row -> {
            row.getCell(5).controls.links.get(CHANGE).click();
            classificationManagementMpTab.getAssetList().getAsset(PLAN_TIER_AND_RATING_INFO).getAsset(ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.NUMBER_OF_PARTICIPANTS).setValue("0");
        });

        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());
        assertSoftly(softly -> {
            softly.assertThat(labelQuoteStatus).valueContains(INITIATED);
            softly.assertThat(premiumSummaryASOFeeTable.getHeader().getValue())
                    .containsExactly(TableConstants.PremiumSummaryASOFeeTable.PARTICIPANTS.getName(), TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS.getName(),
                            UNDERWRITTEN_ASO_FEE.getName(), UNDERWRITTEN_ANNUAL_ASO_FEE.getName());
        });

        LOGGER.info("Step#4 verification");
        buttonRate.click();
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        Currency underWrittenASOFEEValue = new Currency((responseFromLog.get("asoAdminCost.asoFinalPEPM")), RoundingMode.HALF_DOWN);

        Currency underwrittenASOFee = new Currency(premiumSummaryASOFeeTable.getColumn(UNDERWRITTEN_ASO_FEE.getName()).getCell(1).getValue());
        int participantsNumber = totalNumberOfEligibleLivesValue * assumedParticipation_pct / 100;
        Currency underwrittenAnnulASOFee = underwrittenASOFee.multiply(12).multiply(participantsNumber);
        asoFeeTableColumnsVerification(String.valueOf(participantsNumber), PER_EMPLOYEE_PER_MONTH, underWrittenASOFEEValue.toString(), underwrittenAnnulASOFee.toString());

        LOGGER.info("Step#5 verification");
        NavigationPage.PolicyNavigation.leftMenu(CLASSIFICATION_MANAGEMENT.get());
        classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME).setValueByIndex(0);
        classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME).setValueByIndex(1);
        planTierAndRatingInfoTable.getRows().forEach(row -> {
            row.getCell(5).controls.links.get(CHANGE).click();
            classificationManagementMpTab.getAssetList().getAsset(PLAN_TIER_AND_RATING_INFO).getAsset(ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.NUMBER_OF_PARTICIPANTS).setValue("0");
        });

        int participantsNumberEmployeeOnly = 10;
        setNewParticipantNumberAndCheckEmptyAsoFeeTable("Employee Only", participantsNumberEmployeeOnly);

        LOGGER.info("Step#6 verification");
        assertThat(premiumSummaryTab.getAssetList().getAsset(ASO_FEE_BASIS)).hasValue(PER_EMPLOYEE_PER_MONTH);
        buttonRate.click();
        RatingLogHolder ratingLogHolder2 = new RatingLogGrabber().grabRatingLog(quoteNumber);

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder2.getResponseLog().getFormattedLogContent()));
        Map<String, String> responseFromLog2 = ratingLogHolder2.getResponseLog().getOpenLFieldsMap();
        Currency underWrittenASOFEEValue2 = new Currency((responseFromLog2.get("asoAdminCost.asoFinalPEPM")), RoundingMode.HALF_DOWN);

        Currency underwrittenASOFeeEmployeeOnly = new Currency(premiumSummaryASOFeeTable.getColumn(UNDERWRITTEN_ASO_FEE.getName()).getCell(1).getValue());
        Currency underwrittenAnnulASOFeeEmployeeOnly = underwrittenASOFeeEmployeeOnly.multiply(12).multiply(participantsNumberEmployeeOnly);
        asoFeeTableColumnsVerification(Integer.toString(participantsNumberEmployeeOnly), PER_EMPLOYEE_PER_MONTH, underWrittenASOFEEValue2.toString(), underwrittenAnnulASOFeeEmployeeOnly.toString());

        LOGGER.info("Step#7,8 verification");
        buttonSaveAndExit.click();
        assertThat(tableASOFeeBasis.getHeader().getValue())
                .containsExactly(TableConstants.PremiumSummaryASOFeeTable.PARTICIPANTS.getName(), TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS.getName(),
                        UNDERWRITTEN_ANNUAL_ASO_FEE.getName());

        LOGGER.info("Step#10 verification");
        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(CLASSIFICATION_MANAGEMENT.get());
        int participantsNumberEmployeeAndSpouse = 20;
        setNewParticipantNumberAndCheckEmptyAsoFeeTable("Employee + Spouse", participantsNumberEmployeeAndSpouse);

        LOGGER.info("Step#11 verification");
        buttonRate.click();
        RatingLogHolder ratingLogHolder3 = new RatingLogGrabber().grabRatingLog(quoteNumber);

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder3.getResponseLog().getFormattedLogContent()));
        Map<String, String> responseFromLog3 = ratingLogHolder3.getResponseLog().getOpenLFieldsMap();
        Currency underWrittenASOFEEValue3 = new Currency((responseFromLog3.get("asoAdminCost.asoFinalPEPM")), RoundingMode.HALF_DOWN);

        int totalParticipantNumber = participantsNumberEmployeeAndSpouse + participantsNumberEmployeeOnly;
        Currency underwrittenASOFeeEmployeeAndSpouse = new Currency(premiumSummaryASOFeeTable.getColumn(UNDERWRITTEN_ASO_FEE.getName()).getCell(1).getValue());
        Currency underwrittenAnnulASOFeeEmployeeAndSpouse = underwrittenASOFeeEmployeeAndSpouse.multiply(12).multiply(totalParticipantNumber);
        asoFeeTableColumnsVerification(Integer.toString(totalParticipantNumber), PER_EMPLOYEE_PER_MONTH, underWrittenASOFEEValue3.toString(), underwrittenAnnulASOFeeEmployeeAndSpouse.toString());

        LOGGER.info("Step#12 verification");
        buttonSaveAndExit.click();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupDentalMasterPolicy.propose().start();
        groupDentalMasterPolicy.propose().getWorkspace().fillUpTo(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY), ProposalActionTab.class, false);
        Currency proposedASOFee = underwrittenASOFeeEmployeeAndSpouse.subtract(1);
        groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList().getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE).setValue(String.valueOf(proposedASOFee));

        Currency proposedAnnualASOFee = proposedASOFee.multiply(12).multiply(totalParticipantNumber);
        groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList().getAsset(FEE_UPDATE_REASON).setValueByIndex(1);
        buttonCalculatePremium.click();
        assertThat(proposalASOFeeTable.getColumn(PROPOSED_ANNUAL_ASO_FEE.getName()).getCell(1)).hasValue(String.valueOf(proposedAnnualASOFee));

        LOGGER.info("Step#13 verification");
        groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), ImmutableList.of("Proposal for ASO Plan will require Underwriter approval")));
        buttonCalculatePremium.click();
        buttonGenerateProposal.click();
        dialogConfirmation.confirm();
        MainPage.QuickSearch.search(policyNumber);

        groupDentalMasterPolicy.quoteInquiry().start();
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());

        LOGGER.info("Step#14 verification");
        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryASOFeeTable.getColumn(PROPOSED_ASO_FEE.getName()).getCell(1)).hasValue(String.valueOf(proposedASOFee));
            softly.assertThat(premiumSummaryASOFeeTable.getColumn(PROPOSED_ANNUAL_ASO_FEE.getName()).getCell(1)).hasValue(String.valueOf(proposedAnnualASOFee));
        });

        LOGGER.info("Step#15 verification");
        Waiters.AJAX.go();
        Tab.buttonNext.click();
        assertSoftly(softly -> {
            softly.assertThat(tableASOFeeBasis.getColumn(PARTICIPANTS.getName()).getCell(1)).hasValue(String.valueOf(totalParticipantNumber));
            softly.assertThat(tableASOFeeBasis.getColumn(TableConstants.ProposalASOFeeTable.ASO_FEE_BASIS.getName()).getCell(1)).hasValue(PER_EMPLOYEE_PER_MONTH);
            softly.assertThat(tableASOFeeBasis.getColumn(TableConstants.ProposalASOFeeTable.UNDERWRITTEN_ANNUAL_ASO_FEE.getName()).getCell(1)).hasValue(String.valueOf(underwrittenAnnulASOFeeEmployeeAndSpouse));
            softly.assertThat(tableASOFeeBasis.getColumn(PROPOSED_ASO_FEE.getName()).getCell(1)).hasValue(String.valueOf(proposedASOFee));
            softly.assertThat(tableASOFeeBasis.getColumn(PROPOSED_ANNUAL_ASO_FEE.getName()).getCell(1)).hasValue(String.valueOf(proposedAnnualASOFee));
        });
    }

    private void asoFeeTableColumnsVerification(String participantValue, String asoFeeBasisValue, String underwrittenAsoFeeValue, String underwrittenAnnualAsoFeeValue) {
        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryASOFeeTable.getColumn(TableConstants.PremiumSummaryASOFeeTable.PARTICIPANTS.getName()).getCell(1)).hasValue(participantValue);
            softly.assertThat(premiumSummaryASOFeeTable.getColumn(TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS.getName()).getCell(1)).hasValue(asoFeeBasisValue);
            softly.assertThat(premiumSummaryASOFeeTable.getColumn(UNDERWRITTEN_ASO_FEE.getName()).getCell(1)).hasValue(underwrittenAsoFeeValue);
            softly.assertThat(premiumSummaryASOFeeTable.getColumn(UNDERWRITTEN_ANNUAL_ASO_FEE.getName()).getCell(1)).hasValue(underwrittenAnnualAsoFeeValue);
        });
    }

    private void setNewParticipantNumberAndCheckEmptyAsoFeeTable(String coverageTier, int numberOfParticipant) {
        planTierAndRatingInfoTable.getRow(ClassificationManagementTab.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), coverageTier).getCell(5).controls.links.get(CHANGE).click();
        classificationManagementMpTab.getAssetList().getAsset(PLAN_TIER_AND_RATING_INFO).getAsset(ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.NUMBER_OF_PARTICIPANTS).setValue(String.valueOf(numberOfParticipant));
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());
        assertThat(labelQuoteStatus).valueContains(DATA_GATHERING);
        asoFeeTableColumnsVerification(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
    }
}