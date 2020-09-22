package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.GUARANTEED_ISSUE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.GuaranteedIssueMetaData.GUARANTEED_ISSUE_AMT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionGuaranteedIssue extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private MultiAssetList planDefinitionTabAssetList;
    private ETCSCoreSoftAssertions softly;
    private static final String GUARANTEED_ISSUE_MORE_THAN_BENEFIT_ERROR_MESSAGE = "Guaranteed Issue Amount should not be greater than Maximum Monthly Benefit Amount";
    private static final String GUARANTEED_ISSUE_AMT_MINMAX_ERROR_MESSAGE = "Guaranteed Issue Amount' should be within [$1,000.00, $25,000.00], and the increment is $100.00.";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13240", component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionGuaranteedIssue() {
        preconditions();

        assertSoftly(softly -> {
            this.softly = softly;
            LOGGER.info("---=={TC REN-13240 Step 1.1}==---");
            softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE)).isPresent();

            LOGGER.info("---=={TC REN-13240 Step 1.2}==---");
            softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).isRequired().isPresent().isEnabled();

            LOGGER.info("---=={TC REN-13240 Step 4}==---");
            verifyGuaranteedIssueDefaultValue();

            LOGGER.info("---=={TC REN-13240 Step 5}==---");
            verifyMinMaxIncrementErrorMessage();

            LOGGER.info("---=={TC REN-13240 Step 6}==---");
            verifyHideMinMaxIncrementErrorMessage();

            LOGGER.info("---=={TC REN-13240 Step 7}==---");
            verifyGuaranteedIssueMoreBenefitErrorMessage();
        });
    }

    private void preconditions() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();
        planDefinitionTab.selectDefaultPlan();
    }

    private void verifyGuaranteedIssueDefaultValue() {
        String maxMonthlyBenefitAmount = planDefinitionTabAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT).getValue();
        softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasValue(maxMonthlyBenefitAmount);
    }

    private void verifyMinMaxIncrementErrorMessage() {
        planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue("25100");
        softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT))
                .hasWarningWithText(GUARANTEED_ISSUE_AMT_MINMAX_ERROR_MESSAGE + "; " + GUARANTEED_ISSUE_MORE_THAN_BENEFIT_ERROR_MESSAGE);

        ImmutableList.of("90", "1001").forEach(amount -> {
            planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue(amount);
            softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasWarningWithText(GUARANTEED_ISSUE_AMT_MINMAX_ERROR_MESSAGE);
        });
    }

    private void verifyHideMinMaxIncrementErrorMessage() {
        ImmutableList.of("1000", "6000", "1100").forEach(amount -> {
            planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue(amount);
            softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT)).hasNoWarning();
        });
    }

    private void verifyGuaranteedIssueMoreBenefitErrorMessage() {
        planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT).setValue("6100");
        softly.assertThat(planDefinitionTabAssetList.getAsset(GUARANTEED_ISSUE).getAsset(GUARANTEED_ISSUE_AMT))
                .hasWarningWithText(GUARANTEED_ISSUE_MORE_THAN_BENEFIT_ERROR_MESSAGE);
    }
}
