package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
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
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NY;
import static com.exigen.ren.main.enums.PolicyConstants.OverrideReasons.MAINTAIN_IN_FORCE_RATES;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PERCENT_OF_COVERED_PAYROLL;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME;
import static com.exigen.ren.main.enums.TableConstants.RateDetailsTable.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static java.util.stream.Collectors.toList;

public class TestMaintainInForceRateVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-35745", component = POLICY_GROUPBENEFITS)
    public void testMaintainInForceRateVerification_ST_Step3() {
        LOGGER.info("General Preconditions for TC5 step#1 and step#3");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        LOGGER.info("Steps#1.1-1.5 verification");
        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataForPFL").resolveLinks()).resolveLinks(), PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();

        classificationManagementMpTab.navigateToTab();
        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), STAT_NY).getCell(1).click();
        List<TestData> listStatNY = tableClassificationSubGroupsAndRatingInfo.getContinuousValue();
        List<Currency> classificationRelationshipRatesStatNYCurrency = listStatNY.stream().map(td -> td.getValue(TableConstants.CoverageRelationships.RATE.getName())).map(Currency::new).collect(toList());

        tablePlansAndCoverages.getRowContains(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), PFL_NY).getCell(1).click();
        List<TestData> listPflNY = tableClassificationSubGroupsAndRatingInfo.getContinuousValue();
        List<String> classificationRelationshipRatesPflNY5Digits = listPflNY.stream().map(td -> td.getValue(TableConstants.CoverageRelationships.RATE.getName())).map(rateValue -> new DecimalFormat("#,##0.00000").format(new BigDecimal(rateValue)).replace(",", ".")).collect(Collectors.toList());

        premiumSummaryTab.navigateToTab();
        openViewRateDetailsByCoverageName(STAT_NY);
        List<TestData> rateColumnValuesStatNY = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getContinuousValue();
        List<Currency> rateColumnValuesCurrency = rateColumnValuesStatNY.stream().map(td -> td.getValue(RATE.getName())).map(Currency::new).collect(toList());

        assertThat(rateColumnValuesCurrency).isEqualTo(classificationRelationshipRatesStatNYCurrency);
        for (String value : RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE_BASIS.getName()).getValue()) {
            assertThat(value).isEqualTo((PER_EMPLOYEE_PER_MONTH));
        }
        RateDialogs.ViewRateDetailsDialog.close();

        openViewRateDetailsByCoverageName(PFL_NY);
        List rateColumnValuesPflNY = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(TableConstants.RateDetailsTable.RATE.getName()).getValue();
        assertThat(rateColumnValuesPflNY).isEqualTo(classificationRelationshipRatesPflNY5Digits);
        for (String value : RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(RATE_BASIS.getName()).getValue()) {
            assertThat(value).isEqualTo((PERCENT_OF_COVERED_PAYROLL));
        }
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("Step#2 execution");
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueSTMasterPolicyWithDefaultTestData();

        LOGGER.info("Steps#3.1, 3.2 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_ST, statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(buttonMaintainInForceRates).isPresent().isDisabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isDisabled();

            LOGGER.info("Steps#3.3-3.4 verification");
            premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.CREDIBILITY_FACTOR).setValue("0.25");
            premiumSummaryTab.rate();

            softly.assertThat(buttonMaintainInForceRates).isPresent().isEnabled();
            softly.assertThat(buttonOverrideAndRatePremium).isPresent().isEnabled();
            softly.assertThat(PremiumSummaryTab.viewRateDetailsButton(STAT_NY)).isPresent().isEnabled();
            softly.assertThat(PremiumSummaryTab.viewRateDetailsButton(PFL_NY)).isPresent().isEnabled();
        });

        LOGGER.info("Steps#3.5-3.6 verification");
        openViewRateDetailsByCoverageName(STAT_NY);
        List<TestData> rateInForceStatNYColumnValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getContinuousValue();
        List<Currency> rateInForceStatNYColumnValuesCurrency = rateInForceStatNYColumnValues.stream().map(td -> td.getValue(IN_FORCE_RATE.getName())).map(Currency::new).collect(toList());
        assertThat(rateInForceStatNYColumnValuesCurrency).isEqualTo(classificationRelationshipRatesStatNYCurrency);
        RateDialogs.ViewRateDetailsDialog.close();

        openViewRateDetailsByCoverageName(PFL_NY);
        List rateInForceColumnValuesPflNY = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(TableConstants.RateDetailsTable.RATE.getName()).getValue();
        assertThat(rateInForceColumnValuesPflNY).isEqualTo(classificationRelationshipRatesPflNY5Digits);

        LOGGER.info("Step#4.1 verification");
        RateDialogs.ViewRateDetailsDialog.close();
        String annualPremiumSTATNYUsualRate = premiumSummaryCoveragesTable.getRowContains(COVERAGE_NAME.getName(), STAT_NY).getCell(ANNUAL_PREMIUM.getName()).getValue();

        buttonMaintainInForceRates.click();
        String annualPremiumSTATNYForceRate = premiumSummaryCoveragesTable.getRowContains(COVERAGE_NAME.getName(), STAT_NY).getCell(ANNUAL_PREMIUM.getName()).getValue();
        assertThat(annualPremiumSTATNYUsualRate).isNotEqualTo(annualPremiumSTATNYForceRate);

        LOGGER.info("Steps#4.2-4.4 verification");
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();

        ArrayList overriddenTermRateList = getOverriddenTermRateEnabledValues();
        List<Currency> overriddenTermRateListCurrency = (List<Currency>) overriddenTermRateList.stream().map(Currency::new).collect(toList());

        assertSoftly(softly -> {
            softly.assertThat(overriddenTermRateListCurrency).isEqualTo(classificationRelationshipRatesStatNYCurrency);
            softly.assertThat(DialogRateAndPremiumOverride.overrideReason).hasValue(MAINTAIN_IN_FORCE_RATES);

            LOGGER.info("Scenario#5 Step#3 verification");
            softly.assertThat(DialogRateAndPremiumOverride.overriddenTermRateField(0)).isDisabled();
        });

        LOGGER.info("Steps#5 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();

        openViewRateDetailsByCoverageName(STAT_NY);
        List<TestData> rateColumnEndorseSTATNYValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getContinuousValue();
        List<Currency> rateColumnValuesEndorseSTATNYCurrency = rateColumnEndorseSTATNYValues.stream().map(td -> td.getValue(RATE.getName())).map(Currency::new).collect(toList());
        assertThat(rateColumnValuesEndorseSTATNYCurrency).isEqualTo(classificationRelationshipRatesStatNYCurrency);
        RateDialogs.ViewRateDetailsDialog.close();

        openViewRateDetailsByCoverageName(PFL_NY);
        List rateColumnEndorsePflNYValues = RateDialogs.ViewRateDetailsDialog.tableRateDetails.getColumn(TableConstants.RateDetailsTable.RATE.getName()).getValue();
        assertThat(rateColumnEndorsePflNYValues).isEqualTo(classificationRelationshipRatesPflNY5Digits);

        LOGGER.info("Steps#6 verification");
        RateDialogs.ViewRateDetailsDialog.close();
        premiumSummaryTab.rate();
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();

        ArrayList overriddenTermRateListNew = getOverriddenTermRateEnabledValues();
        List<Currency> overriddenTermRateListNewCurrency = (List<Currency>) overriddenTermRateListNew.stream().map(Currency::new).collect(toList());
        assertThat(overriddenTermRateListNewCurrency).isEqualTo(classificationRelationshipRatesStatNYCurrency);

        LOGGER.info("Scenario#5 Step#3 verification");
        assertThat(DialogRateAndPremiumOverride.overriddenTermRateField(0)).isDisabled();

        LOGGER.info("Steps#7 verification");
        DialogRateAndPremiumOverride.dialogPremiumOverride.reject();
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isPresent().isEnabled();
    }

    private ArrayList getOverriddenTermRateEnabledValues() {
        ArrayList<String> overriddenTermRateList = new ArrayList<String>();
        int overriddenTermRateCount = DialogRateAndPremiumOverride.rateAndPremiumOverrideTable.getRowsCount();
        for (int i = 0; i < overriddenTermRateCount; i++) {
            if (!DialogRateAndPremiumOverride.getOverriddenTermRateValueByIndex(i).equals(EMPTY)) {
                overriddenTermRateList.add(DialogRateAndPremiumOverride.getOverriddenTermRateValueByIndex(i));
            }
        }
        return overriddenTermRateList;
    }
}
