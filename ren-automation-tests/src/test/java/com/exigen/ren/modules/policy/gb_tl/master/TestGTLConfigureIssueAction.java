package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.BenefitType.SPECIFIED_AMOUNT_RANGE;
import static com.exigen.ren.main.enums.PolicyConstants.BenefitType.SPECIFIED_AMOUNT_SINGLE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN_PLUS_VOLUNTARY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab.tableCoverageDefinition;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGTLConfigureIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-22133"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureIssueActionEIN() {
        LOGGER.info("REN-22133 TC1 Preconditions");

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EIN.getLabel()), StringUtils.EMPTY));
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createQuote(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());

        LOGGER.info("REN-22133 TC1 Step 3");

        NavigationPage.setActionAndGo("Issue");
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("EIN is mandatory for the Group Sponsor customer record. Please complete EIN before continuing with Master Quote issue.");
        Page.dialogConfirmation.reject();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-22570", "REN-22571", "REN-22613", "REN-22428"}, component = POLICY_GROUPBENEFITS)
    public void testValidateFieldsUnderEmployee() {

        LOGGER.info("REN-22570 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {

            LOGGER.info("REN-22428 TC1 Step 1 and TC1 Step 3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).isPresent().isOptional().isEnabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-22428 TC1 Step 4");
            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("003 - Allegany County");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).hasNoWarning();

            LOGGER.info("REN-22428 TC1 Step 6 set County Code to blank");
            termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData()
                    .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), StringUtils.EMPTY), PolicyInformationTab.class, PlanDefinitionTab.class);
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN));

            LOGGER.info("REN-22570 Step 1");
            PlanDefinitionTab.changeCoverageTo(ADD);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY)).isPresent().isOptional().hasValue(StringUtils.EMPTY);

            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY).setValue(VALUE_YES);
            ImmutableList.of(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD_AMOUNT, WAITING_PERIOD_MODE, WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).isPresent().isOptional().hasValue(StringUtils.EMPTY));

            LOGGER.info("REN-22570 Step 5,6 In Range Without Warning");
            ImmutableList.of("10", "10.05", "11.05", "40").forEach(value -> {
                planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue(value);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasNoWarning();
            });

            LOGGER.info("REN-22570 Step 5,6 Out Of Range");
            ImmutableList.of("9", "11.01", "41").forEach(value -> {
                planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue(value);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).hasWarning();
            });

            LOGGER.info("REN-22570 Step 10,11,12 In Range");
            ImmutableList.of("1", "365").forEach(value -> {
                planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_AMOUNT).setValue(value);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_AMOUNT)).hasNoWarning();
            });

            LOGGER.info("REN-22570 Step 10,11,12 Out Of Range");
            ImmutableList.of("0", "366").forEach(value -> {
                planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_AMOUNT).setValue(value);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_AMOUNT)).hasWarning();
            });

            LOGGER.info("REN-22571 Preconditions");
            PlanDefinitionTab.changeCoverageTo(DEP_BTL);

            LOGGER.info("REN-22571 Step 1");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE)).isPresent().isRequired().hasValue("26");

            LOGGER.info("REN-22571 Step 9 Preconditions");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASIC_LIFE_PLAN_PLUS_VOLUNTARY));

            LOGGER.info("REN-22571 Step 9 Preconditions Remove Coverages not needed");

            ImmutableList.of(DEP_VOL_BTL, DEP_BTL).forEach(PlanDefinitionTab::removeCoverage);
            PlanDefinitionTab.changeCoverageTo(DEP_VOL_ADD);
            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE).setValue("18");
            termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
            premiumSummaryTab.submitTab();

            LOGGER.info("REN-22428 TC1 Step 6 checking Status");
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-22428 TC1 Step 9");
            termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
            termLifeInsuranceMasterPolicy.acceptContract().start();
            softly.assertThat(termLifeInsuranceMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isPresent().isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-22428 TC1 Step 11");
            termLifeInsuranceMasterPolicy.acceptContract().getWorkspace().fill(getDefaultTLMasterPolicyData());
            PremiumSummaryBindActionTab.buttonNext.click();
            termLifeInsuranceMasterPolicy.issue().start();
            softly.assertThat(termLifeInsuranceMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-22571 Step 9");

            NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
            PlanDefinitionTab.changeCoverageTo(DEP_VOL_ADD);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE)).isPresent().isRequired().hasValue("18");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(INCLUDE_DOMESTIC_PARTNER)).isPresent().isRequired().hasValue(VALUE_YES);

            LOGGER.info("REN-22571 Step 12,14 In Range");

            ImmutableList.of("18", "19", "26").forEach(value -> {
                planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE).setValue(value);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE)).hasNoWarning();
            });

            LOGGER.info("REN-22571 Step 12,14 Out Of Range");

            ImmutableList.of("17", "18.5", "27").forEach(value -> {
                planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE).setValue(value);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(CHILD_STUDENT_TERMINATION_AGE)).hasWarning();
            });

            LOGGER.info("REN-22613 Step 3 Verifying table column labels");

            softly.assertThat(tableCoverageDefinition.getHeader().getValue().subList(1, 6)).hasSameElementsAs(Stream.of(TableConstants.CoverageDefinition.values())
                    .map(TableConstants.CoverageDefinition::getName).collect(Collectors.toList()));

            LOGGER.info("REN-22613 Step 4 Verifying Coverage Names");

            ImmutableList.of(ADD, BTL, VOL_ADD, DEP_VOL_ADD, VOL_BTL).forEach(coverage -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage)).isPresent();
            });

            LOGGER.info("REN-22613 Step 5, Step 7, and Step 9");

            ImmutableList.of(ADD, BTL, VOL_ADD, DEP_VOL_ADD, VOL_BTL).forEach(coverage -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.CoverageDefinition.PLAN.getName())).hasValue("BLV-Basic Life + Voluntary");
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.CoverageDefinition.RATE_BASIS.getName())).hasValue("Per $1,000");

                PlanDefinitionTab.tableCoverageDefinition.getRows().forEach(row -> {
                    softly.assertThat(row.getCell(7).controls.links.get(ActionConstants.CHANGE).isPresent());
                });
            });

            LOGGER.info("REN-22613 Step 6 and Step 8 For Contribution Type Voluntary");

            ImmutableList.of(VOL_ADD, DEP_VOL_ADD, VOL_BTL).forEach(coverage -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.CoverageDefinition.CONTRIBUTION_TYPE.getName())).hasValue("Voluntary");
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.CoverageDefinition.BENEFIT_TYPE.getName())).hasValue(SPECIFIED_AMOUNT_RANGE);
            });

            LOGGER.info("REN-22613 Step 6 and Step 8 For Contribution Type Non-contributory");

            ImmutableList.of(ADD, BTL).forEach(coverage -> {
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.CoverageDefinition.CONTRIBUTION_TYPE.getName())).hasValue("Non-contributory");
                softly.assertThat(tableCoverageDefinition.getRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), coverage).getCell(TableConstants.CoverageDefinition.BENEFIT_TYPE.getName())).hasValue(SPECIFIED_AMOUNT_SINGLE);
            });

            LOGGER.info("REN-22613 Step 10");

            PlanDefinitionTab.changeCoverageTo(VOL_ADD);

            LOGGER.info("REN-22613 Step 12 and Step 13");

            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME)).isPresent().isDisabled().hasValue("Employee Voluntary Accidental Death and Dismemberment Insurance");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_COMBOBOX)).isPresent().isDisabled().hasValue("Basic Life + Voluntary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isPresent().isDisabled().hasValue("Basic Life + Voluntary");

            LOGGER.info("REN-22613 Step 14");

            ImmutableList.of(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY, ELIGIBILITY_WAITING_PERIOD_DEFINITION, WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).isPresent().isRequired().hasValue(StringUtils.EMPTY));

            LOGGER.info("REN-22613 Step 15");

            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY).setValue(VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).isPresent().isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-22613 Step 16");

            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue("Amount and mode only");
            ImmutableList.of(WAITING_PERIOD_MODE, WAITING_PERIOD_AMOUNT).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(control)).isPresent().hasValue(StringUtils.EMPTY));
        });
    }
}
