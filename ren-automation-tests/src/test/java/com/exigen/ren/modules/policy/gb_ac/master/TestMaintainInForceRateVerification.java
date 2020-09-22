package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.DialogRateAndPremiumOverride;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.OverrideReasons.MAINTAIN_IN_FORCE_RATES;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_MONTH;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SIC_DESCRIPTION;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab.tablePlanTierAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMaintainInForceRateVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35745"}, component = POLICY_GROUPBENEFITS)
    public void testMaintainInForceRateVerification_AC() {
        LOGGER.info("General Preconditions");
        String planName = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan").getValue(PlanDefinitionTabMetaData.PLAN.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        LOGGER.info("Steps#1.1-1.5 verification");
        initiatGACQuoteAndFillUpToTab(getDefaultACMasterPolicyData(), PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        GroupAccidentMasterPolicyContext.classificationManagementMPTab.navigateToTab();
        List classificationSubGroupsRatesMoney = tablePlanTierAndRatingInfo.getColumn(TableConstants.PlanTierAndRatingSelection.RATE.getName()).getValue();
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);

        List rateColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE.getName()).getValue();
        assertThat(rateColumnValues).isEqualTo(classificationSubGroupsRatesMoney);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE_BASIS.getName()).getCell(1)).hasValue(PER_MONTH);
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#2 execution");
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueACMasterPolicyWithDefaultTestData();

        LOGGER.info("Steps#3.1, 3.2 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_AC, groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(buttonMaintainInForceRates).isPresent().isDisabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isDisabled();

            LOGGER.info("Steps#3.3-3.4 verification");
            premiumSummaryTab.rate();

            softly.assertThat(buttonMaintainInForceRates).isPresent().isEnabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isEnabled();
            softly.assertThat(PremiumSummaryTab.rateDetailsButton(planName)).isPresent().isEnabled();
        });

        LOGGER.info("Steps#3.5-3.6 verification");
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);
        List rateInForceColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(IN_FORCE_RATE.getName()).getValue();
        assertThat(rateInForceColumnValues).isEqualTo(classificationSubGroupsRatesMoney);

        LOGGER.info("Step#4.1 verification");
        RateDialogs.ViewRateDetailsDialog.close();

        enrollmentTab.navigateToTab();
        enrollmentTab.submitTab();
        planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION).setValue("index=2");//field updated for premium changing

        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.rate();
        String annualPremiumUsualRate = premiumSummaryCoveragesTable.getColumn(TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM.getName()).getCell(1).getValue();

        buttonMaintainInForceRates.click();
        String annualPremiumInForceRate = premiumSummaryCoveragesTable.getColumn(TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM.getName()).getCell(1).getValue();
        assertThat(annualPremiumInForceRate).isNotEqualTo(annualPremiumUsualRate);

        LOGGER.info("Steps#4.2-4.4 verification");
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();

        ArrayList overriddenTermRateList = DialogRateAndPremiumOverride.getOverriddenTermRateValues();
        assertThat(overriddenTermRateList).isEqualTo(classificationSubGroupsRatesMoney);
        assertThat(DialogRateAndPremiumOverride.overrideReason).hasValue(MAINTAIN_IN_FORCE_RATES);

        LOGGER.info("Steps#5 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);
        List rateColumnEndorseValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE.getName()).getValue();
        assertThat(rateColumnEndorseValues).isEqualTo(classificationSubGroupsRatesMoney);

        LOGGER.info("Steps#6 verification");
        RateDialogs.ViewRateDetailsDialog.close();
        premiumSummaryTab.rate();
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();

        ArrayList overriddenTermRateListNew = DialogRateAndPremiumOverride.getOverriddenTermRateValues();
        assertThat(overriddenTermRateListNew).isEqualTo(classificationSubGroupsRatesMoney);

        LOGGER.info("Steps#7 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isPresent().isEnabled();
    }
}
