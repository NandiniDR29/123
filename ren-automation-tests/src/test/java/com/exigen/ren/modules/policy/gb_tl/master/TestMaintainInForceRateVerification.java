package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
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
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.components.DialogRateAndPremiumOverride.getOverriddenTermRateValueByIndex;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.PolicyConstants.OverrideReasons.MAINTAIN_IN_FORCE_RATES;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_1000;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.EXPERIENCE_RATING;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMaintainInForceRateVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35745"}, component = POLICY_GROUPBENEFITS)
    public void testMaintainInForceRateVerification_TL() {
        LOGGER.info("General Preconditions");
        String planName = termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan").getValue(PlanDefinitionTabMetaData.PLAN.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("Steps#1.1-1.5 verification");
        initiateTLQuoteAndFillToTab(getDefaultTLMasterPolicyData(), PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        classificationManagementMpTab.navigateToTab();
        ClassificationManagementTab.coveragesTable.getRowContains(COVERAGE_NAME.getName(), BTL).getCell(1).click();
        List<TestData> list = tableCoverageRelationships.getContinuousValue();
        List<String> classificationSubGroupsRates5Digits = list.stream().map(td -> td.getValue(TableConstants.CoverageRelationships.RATE.getName())).map(rateValue -> new DecimalFormat("#,##0.00000").format(new BigDecimal(rateValue))).collect(Collectors.toList());
        List<String> classificationSubGroupsRates3Digits = list.stream().map(td -> td.getValue(TableConstants.CoverageRelationships.RATE.getName())).map(rateValue -> new DecimalFormat("#,##0.000").format(new BigDecimal(rateValue))).collect(Collectors.toList());

        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);

        List rateColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE.getName()).getValue();
        assertThat(rateColumnValues).isEqualTo(classificationSubGroupsRates5Digits);
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE_BASIS.getName()).getCell(1)).hasValue(PER_1000);
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#2 execution");
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueDefaultTestData();

        LOGGER.info("Steps#3.1, 3.2 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_TL, termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(buttonMaintainInForceRates).isPresent().isDisabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isDisabled();

            LOGGER.info("Steps#3.3-3.4 verification");
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR).setValue("0.25");
            premiumSummaryTab.rate();

            softly.assertThat(buttonMaintainInForceRates).isPresent().isEnabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isEnabled();
            softly.assertThat(PremiumSummaryTab.rateDetailsButton(planName)).isPresent().isEnabled();
        });

        LOGGER.info("Steps#3.5-3.6 verification");
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);
        List rateInForceColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(IN_FORCE_RATE.getName()).getValue();
        assertThat(rateInForceColumnValues).isEqualTo(classificationSubGroupsRates5Digits);

        LOGGER.info("Step#4.1 verification");
        RateDialogs.ViewRateDetailsDialog.close();
        String annualPremiumUsualRate = tableCoveragesName.getColumn(TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM.getName()).getCell(1).getValue();

        buttonMaintainInForceRates.click();
        String annualPremiumInForceRate = tableCoveragesName.getColumn(TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM.getName()).getCell(1).getValue();
        assertThat(annualPremiumInForceRate).isNotEqualTo(annualPremiumUsualRate);

        LOGGER.info("Steps#4.2-4.4 verification");
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();
        List overriddenTermRateList = Collections.singletonList(getOverriddenTermRateValueByIndex(2));
        assertThat(overriddenTermRateList).isEqualTo(classificationSubGroupsRates3Digits);
        assertThat(DialogRateAndPremiumOverride.overrideReason).hasValue(MAINTAIN_IN_FORCE_RATES);

        LOGGER.info("Steps#5 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();
        premiumSummaryTab.openViewRateDetailsByPlanName(planName);
        List rateColumnEndorseValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE.getName()).getValue();
        assertThat(rateColumnEndorseValues).isEqualTo(classificationSubGroupsRates5Digits);

        LOGGER.info("Steps#6 verification");
        RateDialogs.ViewRateDetailsDialog.close();
        premiumSummaryTab.rate();
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();

        List overriddenTermRateListNew = Collections.singletonList(getOverriddenTermRateValueByIndex(2));
        assertThat(overriddenTermRateListNew).isEqualTo(classificationSubGroupsRates3Digits);

        LOGGER.info("Steps#7 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isPresent().isEnabled();
    }
}
