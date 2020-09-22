package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.PLAN;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.OFFSET_PERCENT_AFTER_WIB_DURATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EmployerBenefitsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionAdditionalRequirements_5_9 extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-26269", "REN-26277"}, component = POLICY_GROUPBENEFITS)
    public void testNewlyAddedFieldsAndHiddenRules() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "IN")
                .mask(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.COUNTY_CODE.getLabel())), planDefinitionTab.getClass(), false);
        planDefinitionTab.selectDefaultPlan();

        LOGGER.info("REN-26269 Steps 1, 5, 9");
        planDefinitionTab.getBenefitScheduleAsset().getAsset(PARTIAL_DISABILITY_BENEFIT).setValue("Direct");
        assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(OFFSET_PERCENT_AFTER_WIB_DURATION)).isPresent().isRequired().isEnabled().hasValue("50%").hasOptions("50%", "60%", "70%");

        LOGGER.info("REN-26269 Steps 2, 6, 10");
        planDefinitionTab.getOptionsAsset().getAsset(REHABILITATION_INCENTIVE_BENEFIT).setValue("5%");
        assertThat(planDefinitionTab.getOptionsAsset().getAsset(REHABILITATION_INCENTIVE_BENEFIT_THRESHOLD)).isPresent().isRequired().isEnabled().hasValue("110% of Pre-Disability Earnings")
                .hasOptions("100% of Pre-Disability Earnings", "105% of Pre-Disability Earnings", "110% of Pre-Disability Earnings");

        LOGGER.info("REN-26269 Steps 3, 7, 11");
        planDefinitionTab.getOptionsAsset().getAsset(FAMILY_CARE_BENEFIT).setValue("Included");
        assertThat(planDefinitionTab.getOptionsAsset().getAsset(FAMILY_CARE_BENEFIT_MAXIMUM_ALL_FAMILY_MEMBERS)).isPresent().isRequired().isEnabled().hasValue("$2,000")
                .hasOptions("$2,000", "$3,000", "$4,000", "$5,000", "$6,000");

        LOGGER.info("REN-26269 Step 4, 8, 12");
        planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE).setValue(false);
        planDefinitionTab.getEmployerBenefitsAsset().getAsset(OWNER).setValue(true);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_MAXIMUM)).isPresent().isRequired().isEnabled().hasValue("$3,000")
                .hasOptions("$1,000", "$2,000", "$3,000", "$4,000", "$5,000");

            LOGGER.info("REN-26269 Step 13");
            softly.assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_DURATION)).hasOptions("3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "18 Months", "24 Months");
        });

        LOGGER.info("REN-26277 Step 1");
        planDefinitionTab.getBenefitScheduleAsset().getAsset(PARTIAL_DISABILITY_BENEFIT).setValue("Proportionate Loss");
        assertThat(planDefinitionTab.getBenefitScheduleAsset().getAsset(OFFSET_PERCENT_AFTER_WIB_DURATION)).isAbsent();

        LOGGER.info("REN-26277 Step 3");
        planDefinitionTab.getOptionsAsset().getAsset(REHABILITATION_INCENTIVE_BENEFIT).setValue("None");
        assertThat(planDefinitionTab.getOptionsAsset().getAsset(REHABILITATION_INCENTIVE_BENEFIT_THRESHOLD)).isAbsent();

        LOGGER.info("REN-26277 Step 7");
        planDefinitionTab.getOptionsAsset().getAsset(FAMILY_CARE_BENEFIT).setValue("Not Included");
        assertThat(planDefinitionTab.getOptionsAsset().getAsset(FAMILY_CARE_BENEFIT_MAXIMUM_ALL_FAMILY_MEMBERS)).isAbsent();

        LOGGER.info("REN-26277 Step 9");
        planDefinitionTab.getOptionsAsset().getAsset(TERMINAL_ILLNESS_BENEFIT).setValue("None");
        assertSoftly(softly -> {
            assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Pay Terminal Illness Benefit Gross"))).isAbsent();

            LOGGER.info("REN-26277 Step 11");
            assertThat(planDefinitionTab.getOptionsAsset().getAsset(TERMINAL_ILLNESS_BENEFIT_WAITING_PERIOD)).isAbsent();
        });

        LOGGER.info("REN-26277 Step 13");
        planDefinitionTab.getEmployerBenefitsAsset().getAsset(NONE).setValue(true);
        assertThat(planDefinitionTab.getEmployerBenefitsAsset().getAsset(REVENUE_PROTECTION_BENEFIT_MAXIMUM)).isAbsent();

        LOGGER.info("REN-26277 Step 15");
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                        .mask(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[0]", PLAN.getName())),
                PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
    }

}
