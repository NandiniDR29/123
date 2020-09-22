package com.exigen.ren.modules.policy.gb_dn.master;

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
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.OverrideReasons.MAINTAIN_IN_FORCE_RATES;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.MONTHLY_TIRED_PRICE_PER_PARTICIPANT;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.planTierAndRatingInfoTable;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMaintainInForceRateVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35745"}, component = POLICY_GROUPBENEFITS)
    public void testMaintainInForceRateVerification_DN() {
        LOGGER.info("General Preconditions");
        String planName = groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan").getValue(PlanDefinitionTabMetaData.PLAN.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("Steps#1.1-1.5 verification");
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData(), PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        classificationManagementMpTab.navigateToTab();
        List classificationSubGroupsRatesMoney = planTierAndRatingInfoTable.getColumn(TableConstants.PlanTierAndRatingSelection.RATE.getName()).getValue();
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);

        List rateColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE.getName()).getValue();
        assertThat(rateColumnValues).isEqualTo(classificationSubGroupsRatesMoney);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE_BASIS.getName()).getCell(1)).hasValue(MONTHLY_TIRED_PRICE_PER_PARTICIPANT);
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#2 execution");
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueDNMasterPolicyWithDefaultTestData();

        LOGGER.info("Steps#3.1, 3.2 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_DN, groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(buttonMaintainInForceRates).isPresent().isDisabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isDisabled();

            LOGGER.info("Steps#3.3-3.4 verification");
            premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR).setValue("0.25");
            premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.EXPERIENCE_CLAIM_AMOUNT).setValue("10");// added for amount changing
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
