package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN_PLUS_VOLUNTARY;
import static com.exigen.ren.main.enums.ValueConstants.NOT_INCLUDED;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionOptionsSection extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private static final String TOTAL_DISABILITY = "Total Disability";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-17952", "REN-18931", "REN-18934", "REN-18935"}, component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionOptionsSection() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN_PLUS_VOLUNTARY));
        assertSoftly(softly -> {
            LOGGER.info("REN 17952 Step 1");
            PlanDefinitionTab.changeCoverageTo(BTL);
            LOGGER.info("REN 18931 Step 5,6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CONTINUATION)).isPresent().isRequired().hasOptions(ImmutableList.of(NOT_INCLUDED, TOTAL_DISABILITY, "Total Disability or Termination")).hasValue(TOTAL_DISABILITY);
            LOGGER.info("REN 18934 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ACCELERATED_BENEFIT_MINIMUM_AMOUNT)).hasValue(new Currency("10,000").toString());
            LOGGER.info("REN 18935 Step 5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ACCELERATED_BENEFIT_MAXIMUM_AMOUNT)).hasValue(new Currency("250,000").toString());
            LOGGER.info("REN 17952 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS)).isPresent();
            verifyOptionSectionFields();
            LOGGER.info("REN 17952 Step 4");
            PlanDefinitionTab.changeCoverageTo(VOL_BTL);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS)).isPresent();
            verifyOptionSectionFields();
            LOGGER.info("REN 17952 Step 14");
            ImmutableList.of(DEP_BTL, ADD, VOL_ADD, DEP_VOL_ADD, SP_VOL_BTL, DEP_VOL_BTL).forEach(coverage -> {
                PlanDefinitionTab.changeCoverageTo(coverage);
                verifyMissingOptionSectionFields();
            });
            LOGGER.info("REN 17952 Step 14 Add and Verify For Coverage DEP_ADD - Dependent Basic Accidental Death and Dismemberment Insurance (Spouse & Child)");
            planDefinitionTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_ADD);
            verifyMissingOptionSectionFields();
            LOGGER.info("REN 17952 Step 5,7,8 REN-18931 Step 12");
            termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            proposeAcceptContractIssueDefaultTestData();
            termLifeInsuranceMasterPolicy.endorse().perform(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
            softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
        });
    }

    private void verifyOptionSectionFields() {
        assertSoftly(softly -> {
            ImmutableList.of(CONTINUATION, PORTABILITY, ACCELERATED_BENEFIT, ACCELERATED_BENEFIT_MINIMUM_PERCENTAGE, ACCELERATED_BENEFIT_MAXIMUM_PERCENTAGE, ACCELERATED_BENEFIT_MINIMUM_AMOUNT,
                    ACCELERATED_BENEFIT_MAXIMUM_AMOUNT, WAIVER_OF_PREMIUM, WAIVER_DISABLED_PRIOR_TO_AGE, TERMINATION_AGE, PREMIUM_WAIVER_ELIMINATION_PERIOD).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(control)).isPresent()
            );
        });
    }

    private void verifyMissingOptionSectionFields() {
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(OPTIONS)).isAbsent();
            ImmutableList.of(CONTINUATION, PORTABILITY, ACCELERATED_BENEFIT, ACCELERATED_BENEFIT_MINIMUM_PERCENTAGE, ACCELERATED_BENEFIT_MAXIMUM_PERCENTAGE, ACCELERATED_BENEFIT_MINIMUM_AMOUNT,
                    ACCELERATED_BENEFIT_MAXIMUM_AMOUNT, WAIVER_OF_PREMIUM, WAIVER_DISABLED_PRIOR_TO_AGE, TERMINATION_AGE, PREMIUM_WAIVER_ELIMINATION_PERIOD).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(control)).isAbsent()
            );
        });
    }
}
