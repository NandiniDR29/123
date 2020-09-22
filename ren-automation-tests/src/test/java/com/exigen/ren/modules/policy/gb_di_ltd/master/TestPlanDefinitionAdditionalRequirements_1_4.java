package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.ErrorPageEnum.ErrorsColumn.MESSAGE;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.GUARANTEED_ISSUE_AMOUNT_SHOULD_NOT_BE_GREATER_THAN_MAXIMUM_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.*;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.PLAN;
import static com.exigen.ren.main.enums.ValueConstants.NONE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.GuaranteedIssueMetaData.GUARANTEED_ISSUE_AMT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionAdditionalRequirements_1_4 extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private MultiAssetList planDefinitionTabAssetList;
    private String maxMonthlyBenefit;

    private static final String PER_$100_MONTHLY_COVERED_PAYROLL = "Per $100 Monthly Covered Payroll";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-25092"}, component = POLICY_GROUPBENEFITS)
    public void testAdditionalRequirementsInPlanDefinition() {
        LOGGER.info("Preconditions:" +
                "Login to Application" +
                "Create a Non-Individual Customer" +
                "Create Case for LTD (Upload the Census file and ensure the *Processing Results Tab shows the status as Success)" +
                "Initiate Quote for Case from Step 3" +
                "Select Plan- CON,NC,SGR,VOL in 'Plan Definition' tab.");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();
        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN).fill(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_AllPlans")
                .getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(0));

        LOGGER.info("Step 1. Click on Change button for the CON row from the Plan Table");
        PlanDefinitionTab.openAddedPlan(LTD_CON);

        assertSoftly(softly -> {

            softly.assertThat(planDefinitionTabAssetList.getAsset(PLAN_COMBOBOX)).hasValue(LTD_CON);

            softly.assertThat(PlanDefinitionTab.tableCoverageDefinition.getRowContains(PLAN.getName(), "CON-CON")
                    .getCell(TableConstants.CoverageDefinition.RATE_BASIS.getName())).hasValue(PER_$100_MONTHLY_COVERED_PAYROLL);

            LOGGER.info("Step 2. Navigate to the Rating section and verify the Rate Basis drop down field");
            softly.assertThat(planDefinitionTabAssetList.getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.RATE_BASIS)).hasValue(PER_$100_MONTHLY_COVERED_PAYROLL);

            LOGGER.info("Step 3. Navigate to the Guaranteed Issue section and verify Guaranteed Issue Amount field");
            maxMonthlyBenefit = planDefinitionTabAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT).getValue();
            assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasValue(maxMonthlyBenefit);

        });

        LOGGER.info("Step 4. Set the value in Guaranteed Issue Amount field to $6,100.");
        planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue("6100");
        assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasWarningWithText(GUARANTEED_ISSUE_AMOUNT_SHOULD_NOT_BE_GREATER_THAN_MAXIMUM_MONTHLY_BENEFIT_AMOUNT);

        LOGGER.info("Step 5. Set the value in Guaranteed Issue Amount field to 5,900.");
        planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue("5900");
        assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasNoWarning();
        assertThat(planDefinitionTabAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT)).hasValue(maxMonthlyBenefit);

        LOGGER.info("Step 6. Change the value in the Maximum Monthly Benefit Amount field to $5000.00 and verify the value in Guaranteed Issue Amount field.");
        planDefinitionTabAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT).setValue("5000");
        maxMonthlyBenefit = planDefinitionTabAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT).getValue();
        assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasValue(new Currency(5000).toString()).hasValue(maxMonthlyBenefit);

        LOGGER.info("Step 7. Set the value in Guaranteed Issue Amount field to 6,000.");
        planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue("6000");
        assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasWarningWithText(GUARANTEED_ISSUE_AMOUNT_SHOULD_NOT_BE_GREATER_THAN_MAXIMUM_MONTHLY_BENEFIT_AMOUNT);
        assertThat(planDefinitionTabAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT)).hasValue(maxMonthlyBenefit);

        LOGGER.info("Step 8. Enter all the mandatory fields in all the tabs,navigate to Premium Summary tab and Rate the Quote");
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_AllPlans")
                        .mask(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[0]", PLAN.getName())),
                PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertThat(ErrorPage.tableError.getRow(MESSAGE.get(), GUARANTEED_ISSUE_AMOUNT_SHOULD_NOT_BE_GREATER_THAN_MAXIMUM_MONTHLY_BENEFIT_AMOUNT)).isPresent();

        LOGGER.info("Step 9. Navigate back to Plan Definition Tab, Guarantee Issue section and change the value in Guaranteed Issue Amount field to $4,000.00.");
        Tab.buttonCancel.click();
        planDefinitionTab.navigate();
        PlanDefinitionTab.openAddedPlan(LTD_CON);
        planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue("4000");
        assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasNoWarning();

        LOGGER.info("Step 10. Navigate to Options tab and verify the drop down values in the field Mental Illness Limitation");
        assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(MENTAL_ILLNESS_LIMITATION)).hasOptions(NONE, "12 Months", "24 Months");

        LOGGER.info("Step 12. Verify the drop down values in the field Special Conditions Limitation");
        assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SPECIAL_CONDITIONS_LIMITATION)).hasOptions(NONE, "12 Months", "24 Months").hasValue(NONE);

        LOGGER.info("Step 14. Verify the drop down values in the field Substance Abuse Limitation.");
        assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SUBSTANCE_ABUSE_LIMITATION)).hasOptions(NONE, "12 Months", "24 Months");

        LOGGER.info("Step 17. Verify the Self Reported Conditions Limitation field");
        assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SELF_REPORTED_CONDITIONS_LIMITATION)).hasOptions(  NONE, "12 Months", "24 Months");

        LOGGER.info("Step 19. Verify the drop down values in the field Recovery Income Benefit");
        assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(RECOVERY_INCOME_BENEFIT)).hasOptions(NONE, "3 Months", "6 Months", "9 Months", "12 Months", "24 Months").hasValue("12 Months");

        LOGGER.info("Step 20. Set the values in the fields as mentioned below:" +
                "1. Mental Illness Limitation  to 24 Months.(for VOL plan, set the value to 12 Months, check accordingly) ." +
                "2. Special Conditions Limitation to 24 Months." +
                "3. Substance Abuse Limitation to 12 months" +
                "4. Self Reported Conditions Limitation to 12 Months" +
                "5. Recovery Income Benefit to 24 months");
        assertSoftly(softly -> {
            planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(MENTAL_ILLNESS_LIMITATION).setValue("24 Months");
            softly.assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(MENTAL_ILLNESS_LIMITATION)).hasNoWarning();
            planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SPECIAL_CONDITIONS_LIMITATION).setValue("24 Months");
            softly.assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SPECIAL_CONDITIONS_LIMITATION)).hasNoWarning();
            planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SUBSTANCE_ABUSE_LIMITATION).setValue("12 Months");
            softly.assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SUBSTANCE_ABUSE_LIMITATION)).hasNoWarning();
            planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SELF_REPORTED_CONDITIONS_LIMITATION).setValue("12 Months");
            softly.assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(SELF_REPORTED_CONDITIONS_LIMITATION)).hasNoWarning();
            planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(RECOVERY_INCOME_BENEFIT).setValue("24 Months");
            softly.assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(RECOVERY_INCOME_BENEFIT)).hasNoWarning();

            LOGGER.info("Step 22. Set the value in the field Catastrophic Disability Benefit = None and verify the Maximum Dollar Amount field.");
            planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(CATASTROPHIC_DISABILITY_BENEFIT).setValue(NONE);
            softly.assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(MAXIMUM_DOLLAR_AMOUNT)).isAbsent();
        });

        LOGGER.info("Step 26. Click on Cancel, Click Take Action -> Data Gather and navigate to Plan Definition tab, " +
                "and update Catastrophic Disability Benefit field to any value other than None for CON, NC, SGR and VOL plan");
        Tab.buttonSaveAndExit.click();
        longTermDisabilityMasterPolicy.dataGather().start();
        planDefinitionTab.navigate();
        assertSoftly(softly ->
            ImmutableList.of(LTD_CON, LTD_NC, LTD_SGR, LTD_VOL).forEach(plan -> {
                PlanDefinitionTab.openAddedPlan(plan);
                planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(CATASTROPHIC_DISABILITY_BENEFIT).setValue("10%");
                assertThat(planDefinitionTabAssetList.getAsset(OPTIONS).getAsset(MAXIMUM_DOLLAR_AMOUNT)).isPresent();
        }));

        LOGGER.info("Step 27. Enter values for all mandatory fields across all tabs, navigate to Premium Summary, click Rate, click Next");
        premiumSummaryTab.navigate();
        PremiumSummaryTab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
    }

}
