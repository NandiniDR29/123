package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.QuotesSelectionActionTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.PROPOSED_ASO_FEE;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.proposalASOFeeTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureVisionASO extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private static StaticElement asoFeeAdjustmentErrorMsg = new StaticElement(By.xpath("//*[@id=\"proposalForm:proposalsQuotesSelectionTable:0:asoFeeAdjustmentGroup\"]/span"));
    private static final List<String> UPDATE_REASON_DROP_DOWN_VALUES = ImmutableList.of(StringUtils.EMPTY, "Competitive Adjustment", "Premium Fund", "Premium Fund + Competitive Adjustment", "Match Quote", "Multi-Policy Discount", "Business Decision");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23891", "REN-23893"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureVisionASO() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(tdSpecific().getTestData("TestData_WithTwoCoverages")
                        .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.ASO_PLAN.getLabel()), VALUE_YES),
                PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        groupVisionMasterPolicy.propose().start();
        groupVisionMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), ProposalActionTab.class, false);
        assertSoftly(softly -> {
            LOGGER.info("REN-23891 TC Step 1");
            AssetList groupVisionGetAssetListPath = (AssetList) groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isPresent().isEnabled().hasValue(StringUtils.EMPTY);
            Button applyButton = groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY);
            softly.assertThat(applyButton).isPresent();

            LOGGER.info("REN-23891 TC Step 2");
            ImmutableList.of("-75.01%", "-100.01%").forEach(asoFeeValue -> {
                groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue(asoFeeValue);
                applyButton.click();
                assertThat(asoFeeAdjustmentErrorMsg).hasValue("The value must be in allowed range [-75%; 100%], in increment of 0.01%");
            });

            LOGGER.info("REN-23891 TC Step 6");
            ImmutableList.of("-75%", "-100%").forEach(asoFeeValue -> {
                groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue(asoFeeValue);
                applyButton.click();
                softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).hasNoWarning();
            });

            LOGGER.info("REN-23893 Step#1 verification");
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue("50");
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY).click();
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValue("index=1");
            ProposalActionTab.buttonCalculatePremium.click();
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE).setValue("2");

            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isEnabled();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY)).isEnabled();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE)).isEnabled().hasValue(String.valueOf(new Currency("2")));

            LOGGER.info("REN-23891 TC Step 9");
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue("50");
            applyButton.click();
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValueByIndex(1);
            ProposalActionTab.buttonCalculatePremium.click();
            String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
            ProposalActionTab.buttonSaveAndExit.click();

            CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isPresent().isEnabled().hasValue("50.00%");

            LOGGER.info("REN-23891 TC Step 11");
            groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultVSMasterPolicyData().adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), ImmutableList.of(
                    "Proposal for ASO Plan requires Underwriter approval")));
            ProposalActionTab.buttonGenerateProposal.click();
            Page.dialogConfirmation.buttonYes.click();
            CaseProfileSummaryPage.reGenerateProposalByNumber(proposalNumber);
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isPresent().isDisabled().hasValue("50.00%");
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23892"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureVisionASOFeeTable() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(tdSpecific().getTestData("TestData_WithTwoCoverages")
                        .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.ASO_PLAN.getLabel()), VALUE_YES),
                PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        Row premiumSummaryASOTableRow = PremiumSummaryTab.premiumSummaryASOFeeTable.getRow(ImmutableMap.of(ASO_FEE_BASIS.getName(), PER_EMPLOYEE_PER_MONTH));
        String premiumSummaryParticipants = premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.PARTICIPANTS.getName()).getValue();
        String premiumSummaryASOFeeBasis = premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS.getName()).getValue();
        String premiumSummaryUnderwrittenASOFee = premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ASO_FEE.getName()).getValue();
        String premiumSummaryUnderwrittenAnnualASOFee = premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ANNUAL_ASO_FEE.getName()).getValue();
        PremiumSummaryTab.buttonNext.click();
        groupVisionMasterPolicy.propose().start();
        QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
        QuotesSelectionActionTab.textBoxProposalName.setValue("ProposalName2");
        Tab.buttonNext.click();

        assertSoftly(softly -> {
            LOGGER.info("REN-23892 TC Step 1.1");
            proposalASOFeeTable.isPresent();
            AssetList groupVisionGetAssetListPath = (AssetList) groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(PROPOSED_ASO_FEE)).isPresent().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23892 TC Step 1.2");
            Row proposalAsoTableRow = ProposalActionTab.proposalASOFeeTable.getRow(ImmutableMap.of(TableConstants.ProposalASOFeeTable.ASO_FEE_BASIS.getName(), PER_EMPLOYEE_PER_MONTH));
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.PARTICIPANTS.getName())).hasValue(premiumSummaryParticipants);

            LOGGER.info("REN-23892 TC Step 1.3");
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.ASO_FEE_BASIS.getName())).hasValue(premiumSummaryASOFeeBasis);

            LOGGER.info("REN-23892 TC Step 1.4");
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.UNDERWRITTEN_ASO_FEE.getName())).hasValue(premiumSummaryUnderwrittenASOFee);

            LOGGER.info("REN-23892 TC Step 1.5");
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.UNDERWRITTEN_ANNUAL_ASO_FEE.getName())).hasValue(premiumSummaryUnderwrittenAnnualASOFee);

            LOGGER.info("REN-23892 TC Step 1.6");
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23892 TC Step 1.7");
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.PROPOSED_ASO_FEE.getName())).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23892 TC step 1.8");
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.PROPOSED_ANNUAL_ASO_FEE.getName())).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23892 TC step 1.9, step 1.10 and step 1.11");
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON)).isPresent().hasValue(StringUtils.EMPTY).hasOptions(UPDATE_REASON_DROP_DOWN_VALUES);
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.RATE_ADJUSTMENT).setValue("20");
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.RATE_ADJUSTMENT_DRP_DWN).setValueByIndex(1);
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.RATE_ADJUSTMENT_APPLY).click();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.RATE_UPDATE_REASON)).hasOptions(UPDATE_REASON_DROP_DOWN_VALUES);

            LOGGER.info("REN-23892 TC Step 3");
            groupVisionGetAssetListPath.getAsset(PROPOSED_ASO_FEE).setValue("-1");
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValue("Competitive Adjustment");

            LOGGER.info("REN-23892 TC Step 3.2");
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.ASO_FEE_ADJUSTMENT.getName())).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23892 TC Step 3.3");
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.GENERATE_PRE_PROPOSAL)).isDisabled();

            LOGGER.info("REN-23892 TC Step 3.4");
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.GENERATE_PROPOSAL)).isDisabled();

            LOGGER.info("REN-23892 TC Step 6");
            groupVisionGetAssetListPath.getAsset(PROPOSED_ASO_FEE).setValue(premiumSummaryUnderwrittenASOFee);
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON)).isAbsent();
            ProposalActionTab.buttonCalculatePremium.click();
            ProposalActionTab.buttonSaveAndExit.click();

            LOGGER.info("REN-23892 TC Step 8");
            CaseProfileSummaryPage.tableProposal.getRow(QuotesSelectionActionTabMetaData.PROPOSAL_NAME.getLabel(), "ProposalName2")
                    .getCell(6).controls.links.get(ActionConstants.UPDATE).click();
            Tab.buttonNext.click();
            groupVisionGetAssetListPath.getAsset(PROPOSED_ASO_FEE).setValue("20");
            String proposalTableProposedAsoFee = proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.PROPOSED_ASO_FEE.getName()).getValue();
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.UNDERWRITTEN_ASO_FEE.getName())).doesNotHaveValue(proposalTableProposedAsoFee);
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValue("Premium Fund");
            ProposalActionTab.buttonCalculatePremium.click();
            Currency proposedASOFeeValue = new Currency(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.PROPOSED_ASO_FEE.getName()).controls.textBoxes.getFirst().getValue()).multiply(12).multiply(Integer.parseInt(premiumSummaryParticipants));
            softly.assertThat(proposalAsoTableRow.getCell(TableConstants.ProposalASOFeeTable.PROPOSED_ANNUAL_ASO_FEE.getName())).hasValue(proposedASOFeeValue.toString());

            LOGGER.info("REN-23892 TC Step 10");
            groupVisionGetAssetListPath.getAsset(PROPOSED_ASO_FEE).setValue("30");
            ProposalActionTab.buttonCalculatePremium.click();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.GENERATE_PROPOSAL)).isEnabled();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.GENERATE_PRE_PROPOSAL)).isEnabled();

            LOGGER.info("REN-23892 TC Step 13");
            groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultVSMasterPolicyData().adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), ImmutableList.of(
                    "Proposal for ASO Plan requires Underwriter approval")));
            ProposalActionTab.buttonGenerateProposal.click();
            Page.dialogConfirmation.buttonYes.click();
            CaseProfileSummaryPage.tableProposal.getRow(QuotesSelectionActionTabMetaData.PROPOSAL_NAME.getLabel(), "ProposalName2")
                    .getCell(6).controls.links.get(ActionConstants.RE_GENERATE).click();
            Tab.buttonNext.click();
            softly.assertThat(groupVisionGetAssetListPath.getAsset(PROPOSED_ASO_FEE)).isDisabled();
        });
    }
}