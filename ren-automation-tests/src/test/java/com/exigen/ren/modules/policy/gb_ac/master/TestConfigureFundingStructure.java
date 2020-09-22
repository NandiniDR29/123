package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.ENHANCED_10_UNITS;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureFundingStructure extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18869", "REN-18925", "REN-18926", "REN-18927", "REN-18928"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureFundingStructure1() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-18869 Step 1-4");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(TAXABILITY)).isPresent().isRequired().hasValue("Benefits Not Taxable");
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Voluntary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT_EMPLOYEE_COVERAGE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT_DEPENDENT_COVERAGE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT_EMPLOYEE_COVERAGE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT_DEPENDENT_COVERAGE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Sponsor/Participant Split");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT_EMPLOYEE_COVERAGE)).isPresent().isRequired().hasValue("0");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT_DEPENDENT_COVERAGE)).isPresent().isRequired().hasValue("1");

            LOGGER.info("REN-18925 Step 1-3");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Sponsor/Participant Split");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION)).isPresent().isRequired().hasValue("0");
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Voluntary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION)).isPresent().isRequired().hasValue("100");

            LOGGER.info("REN-18926 Step 4 and Step 13");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS)).isPresent().isRequired();
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(ENHANCED_10_UNITS));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(MINIMUM_NUMBER_OF_PARTICIPANTS)).isPresent().isRequired();

            LOGGER.info("REN-18927 Step 1-4");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Voluntary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SPONSOR_PAYMENT_MODE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Sponsor/Participant Split");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SPONSOR_PAYMENT_MODE)).isPresent().isRequired().hasValue("12");
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SPONSOR_PAYMENT_MODE)).isPresent().isRequired().hasValue("12");

            LOGGER.info("REN-18928 Step 1-4");
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Sponsor/Participant Split");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(MEMBER_PAYMENT_MODE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(MEMBER_PAYMENT_MODE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Voluntary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(MEMBER_PAYMENT_MODE)).isPresent().hasValue(ImmutableList.of("12"));
        });
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        proposeAcceptContractIssueACMasterPolicyWithDefaultTestData();
        groupAccidentMasterPolicy.endorse().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21928", "REN-21929", "REN-21930"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureFundingStructure2() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA"), planDefinitionTab.getClass());
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
        PlanDefinitionTab.tableCoverageDefinition.findRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), GroupAccidentCoverages.ENHANCED_ACCIDENT)
                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        assertSoftly(softly -> {
            LOGGER.info("REN-21930 Steps 2-4");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Direct Bill"))).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SELF_ADMINISTERED)).hasValue(VALUE_NO);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Direct Bill"))).isAbsent();
            planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SELF_ADMINISTERED).setValue(VALUE_YES);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Direct Bill"))).isAbsent();

            LOGGER.info("REN-21928 Step 6");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE)).hasValue("Voluntary");

        });

        LOGGER.info("REN-21929 Step 4");
        PlanDefinitionTab.tableCoverageDefinition.findRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), GroupAccidentCoverages.BASIC_ACCIDENT)
                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE))
                    .hasOptions(ImmutableList.of("Non-contributory", "Voluntary", "Sponsor/Participant Split"));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(REQUIRED_PARTICIPATION))
                    .hasOptions(getRangeListWithPercent(5, 100, 5));

            LOGGER.info("REN-21928 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE)).hasValue("Non-contributory");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(REQUIRED_PARTICIPATION)).hasValue("25%");
        });


    }

    private List<String> getRangeListWithPercent(int start, int stop, int step) {
        LinkedList<String> list = new LinkedList<>();
        for (int i = start; i <= stop; i = i + step) {
            list.add(i + "%");
        }
        return list;
    }
}
