package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASOALC;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.PARTICIPANTS;
import static com.exigen.ren.main.enums.TableConstants.ProposalASOFeeTable.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureDentalASOFeeEntity extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String PER_EMPLOYEE_PER_MONTH = "Per Employee Per Month";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23334", "REN-23357"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalASOFeeEntity() {
        LOGGER.info("General Preconditions");
        String asoFeeAdjustmentValue = "50.00%";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                        .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), ASO_PLAN.getLabel()), VALUE_YES)
                        .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[0]", PlanDefinitionTabMetaData.PLAN.getLabel()), ASOALC)
                        .adjust(TestData.makeKeyPath(classificationManagementMpTab.getClass().getSimpleName(), PlanDefinitionTabMetaData.PLAN.getLabel()), "ASOALC-ASO A La Carte"),
                PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();

        Row premiumSummaryASOTableRow = PremiumSummaryTab.premiumSummaryASOFeeTable.getRowContains(ASO_FEE_BASIS.getName(), PER_EMPLOYEE_PER_MONTH);
        String premiumSummaryParticipants = premiumSummaryASOTableRow.getCell(PARTICIPANTS.getName()).getValue();
        String premiumSummaryASOFeeBasis = premiumSummaryASOTableRow.getCell(ASO_FEE_BASIS.getName()).getValue();
        String premiumSummaryUnderwrittenASOFee = premiumSummaryASOTableRow.getCell(UNDERWRITTEN_ASO_FEE.getName()).getValue();
        String premiumSummaryUnderwrittenAnnualASOFee = premiumSummaryASOTableRow.getCell(UNDERWRITTEN_ANNUAL_ASO_FEE.getName()).getValue();

        PremiumSummaryTab.buttonNext.click();
        groupDentalMasterPolicy.propose().start();
        groupDentalMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), ProposalActionTab.class, false);

        LOGGER.info("REN-23334 Step#1.1, REN-23357 Step#1 verification");
        AssetList groupDentalAssetList = (AssetList) groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList();
        Button applyButton = groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY);

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isOptional().isPresent().isEnabled().hasValue(EMPTY);
            softly.assertThat(applyButton).isPresent();

            LOGGER.info("REN-23334 Step 1.2, 1.3 verification");
            Row proposalASOTableRow = ProposalActionTab.proposalASOFeeTable.getRowContains(TableConstants.ProposalASOFeeTable.ASO_FEE_BASIS.getName(), PER_EMPLOYEE_PER_MONTH);
            softly.assertThat(proposalASOTableRow.getCell(PARTICIPANTS.getName())).hasValue(premiumSummaryParticipants);
            softly.assertThat(proposalASOTableRow.getCell(ASO_FEE_BASIS.getName())).hasValue(premiumSummaryASOFeeBasis);
            softly.assertThat(proposalASOTableRow.getCell(UNDERWRITTEN_ASO_FEE.getName())).hasValue(premiumSummaryUnderwrittenASOFee);
            softly.assertThat(proposalASOTableRow.getCell(UNDERWRITTEN_ANNUAL_ASO_FEE.getName())).hasValue(premiumSummaryUnderwrittenAnnualASOFee);
            softly.assertThat(proposalASOTableRow.getCell(PROPOSED_ASO_FEE.getName())).isEnabled().hasValue(EMPTY);
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON)).isPresent().hasValue(EMPTY).hasOptions(EMPTY, "Competitive Adjustment", "Premium Fund",
                    "Premium Fund + Competitive Adjustment", "Match Quote", "Multi-Policy Discount", "Business Decision");

            LOGGER.info("REN-23357 Step#2 verification");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue("-75.01");
            applyButton.click();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("The value must be in allowed range [-75%; 100%], in increment of 0.01%"))).isPresent();

            LOGGER.info("REN-23357 Step#6 verification");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue("-75");
            applyButton.click();
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).hasNoWarning();
        });

        LOGGER.info("REN-23357 Step#9 verification");
        groupDentalAssetList.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValue("index=1");
        groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue(asoFeeAdjustmentValue);
        applyButton.click();
        ProposalActionTab.buttonCalculatePremium.click();
        proposalActionTab.overrideRules(ImmutableList.of("Proposal for ASO Plan will require Underwriter approval"), "Life");
        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        ProposalActionTab.buttonGeneratePreProposal.click();
        Page.dialogConfirmation.confirm();

        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isEnabled().hasValue(asoFeeAdjustmentValue);

        LOGGER.info("REN-23357 Step#11 verification");
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.confirm();

        CaseProfileSummaryPage.reGenerateProposalByNumber(proposalNumber);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isDisabled().hasValue(asoFeeAdjustmentValue);
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY)).isAbsent();
        });
    }
}