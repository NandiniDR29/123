package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class TestMultiQuoteProposalVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23361", "REN-23891"}, component = POLICY_GROUPBENEFITS)
    public void testMultiQuoteProposalVerification() {
        LOGGER.info("General Preconditions");
        String asoFeeAdjustmentValue = "50.00%";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataASO"));

        groupDentalMasterPolicy.propose().start();
        groupDentalMasterPolicy.propose().getWorkspace().fillUpTo(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY), ProposalActionTab.class, false);
        AssetList groupDentalAssetList = (AssetList) groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("REN-23891 Step#1 verification");
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isPresent().isEnabled().hasValue(EMPTY);
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY)).isPresent();

            LOGGER.info("REN-23891 Step#2 verification");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue("-75.01");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY).click();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("The value must be in allowed range [-75%; 100%], in increment of 0.01%"))).isPresent();

            LOGGER.info("REN-23891 Step#6 verification");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue("-75");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY).click();
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).hasNoWarning();

            LOGGER.info("REN-23361 Step#1 verification");
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue(asoFeeAdjustmentValue);
            groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY).click();
            groupDentalAssetList.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValue("index=1");
            ProposalActionTab.buttonCalculatePremium.click();
            groupDentalAssetList.getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE).setValue("2");
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isEnabled();
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY)).isEnabled();
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE)).isEnabled().hasValue(String.valueOf(new Currency("2")));
        });

        LOGGER.info("REN-23891 Step#9 verification");
        groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT).setValue(asoFeeAdjustmentValue);
        groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY).click();
        ProposalActionTab.buttonCalculatePremium.click();
        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        ProposalActionTab.buttonSaveAndExit.click();

        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isEnabled().hasValue(asoFeeAdjustmentValue);

        LOGGER.info("REN-23891 Step#11 verification");
        groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class)
                .overrideRules(ImmutableList.of("Proposal for ASO Plan will require Underwriter approval",
                        "Proposal requires Underwriter approval because Major Waiting Period is less t...",
                        "Proposal requires Underwriter approval because Prosthodontics Waiting Period ..."), "Life");
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.confirm();

        CaseProfileSummaryPage.reGenerateProposalByNumber(proposalNumber);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT)).isDisabled().hasValue(asoFeeAdjustmentValue);
            softly.assertThat(groupDentalAssetList.getAsset(ProposalTabMetaData.ASO_FEE_ADJUSTMENT_APPLY)).isAbsent();
        });
    }
}
