package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.DialogRateAndPremiumOverride;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanPFL.FLB;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.*;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab.buttonMaintainInForceRates;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab.buttonOverrideAndRatePremium;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMaintainInForceRateVerification extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35745"}, component = POLICY_GROUPBENEFITS)
    public void testMaintainInForceRateVerification_PFL() {
        LOGGER.info("General Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());

        LOGGER.info("Steps#1.1-1.5 verification");
        initiatePFLQuoteAndFillToTab(getDefaultPFLMasterPolicyData(), PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.navigateToTab();

        List<TestData> list = tableCoverageRelationships.getContinuousValue();
        List<String> coverageRelationshipsRates5Digits = list.stream().map(td -> td.getValue(TableConstants.CoverageRelationships.RATE.getName())).map(rateValue -> new DecimalFormat("#,##0.00000").format(new BigDecimal(rateValue)).replace(",", ".")).collect(Collectors.toList());

        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.openViewRateDetailsByPlanName(FLB);

        List rateColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(TableConstants.RateDetailsTable.RATE.getName()).getValue();
        assertThat(rateColumnValues).isEqualTo(coverageRelationshipsRates5Digits);
        for (String value : RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE_BASIS.getName()).getValue()) {
            assertThat(value).isEqualTo((PolicyConstants.RateBasisValues.PERCENT_OF_TAXABLE_WAGE));
        }
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#2 execution");
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssuePFLMasterPolicyWithDefaultTestData();

        LOGGER.info("Steps#3.1, 3.2 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_PFL, paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(buttonMaintainInForceRates).isAbsent();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isDisabled();

            LOGGER.info("Steps#3.3-3.4 verification");
            premiumSummaryTab.rate();

            softly.assertThat(buttonMaintainInForceRates).isAbsent();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isEnabled();
            softly.assertThat(PremiumSummaryTab.rateDetailsButton(FLB)).isPresent().isEnabled();
        });

        LOGGER.info("Steps#3.5-3.6 verification");
        premiumSummaryTab.openViewRateDetailsByPlanName(FLB);
        List rateInForceColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(IN_FORCE_RATE.getName()).getValue();
        assertThat(rateInForceColumnValues).isEqualTo(coverageRelationshipsRates5Digits);

        LOGGER.info("Step#4.1 verification");
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Steps#4.2-4.4 verification");
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();
        assertThat(DialogRateAndPremiumOverride.overriddenTermRateField(0)).isDisabled();

        LOGGER.info("Step#5 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();
        premiumSummaryTab.openViewRateDetailsByPlanName(FLB);
        List rateColumnEndorseValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE.getName()).getValue();
        assertThat(rateColumnEndorseValues).isEqualTo(coverageRelationshipsRates5Digits);

        LOGGER.info("Step#6 verification");
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#7 verification");
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isPresent().isEnabled();
    }
}