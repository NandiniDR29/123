package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_NC;
import static com.exigen.ren.main.enums.ValueConstants.CHECKBOX_NOT_AVAILABLE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.CoverageIncludedInPackageMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuotePlansAndCoverages extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static final ImmutableMap<String, String> reqParticipationValues = ImmutableMap.of(
            "Non-contributory", "100%",
            "Contributory", "75%",
            "Voluntary", "25%");

    private static final ImmutableList<String> PLANS = ImmutableList.of(LTD_NC, LTD_CON);
    private final static StaticElement ELEMENT_ADD_COVERAGE = new StaticElement(By.xpath("//*[@id='policyDataGatherForm']//*[text()='Add coverage' or @value='Add coverage']"));
    private final static String ERROR_MESSAGE = "'Plan Name' is duplicate with existing one, please enter a different Plan Name";
    private final static String TEST_NAME = "test";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-10794", component = POLICY_GROUPBENEFITS)
    public void testQuotePlansAndCoverages() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        MultiAssetList planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();

        planDefinitionTab.selectDefaultPlan();

        assertSoftly(softly -> {

            // Asserts for REN-10794/#2
            softly.assertThat(planDefinitionTab.getCoverageIncludedInPackageAsset().getAsset(INITIAL_ENROLLMENT_UNDERWRITING_OFFER)).hasValue(CHECKBOX_NOT_AVAILABLE);
            softly.assertThat(planDefinitionTab.getCoverageIncludedInPackageAsset().getAsset(ANNUAL_ENROLLMENT_UNDERWRITING_OFFER)).hasValue("Enrollment period - EOI required");

            // Asserts for REN-10794/#3
            ImmutableList.of(
                    STD,
                    LIFE,
                    DENTAL,
                    VISION).forEach(checkbox -> {
                softly.assertThat(planDefinitionTab.getCoverageIncludedInPackageAsset().getAsset(checkbox)).isOptional().hasValue(false);
                softly.assertThat(planDefinitionTab.getCoverageIncludedInPackageAsset().getAsset(checkbox).getName())
                        .as("All letters aren't CAPITAL")
                        .isEqualTo(planDefinitionTab.getCoverageIncludedInPackageAsset().getAsset(checkbox).getName().toUpperCase());
            });

            // Asserts for REN-10794/#5
            reqParticipationValues.forEach((key, value) -> {
                planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(CONTRIBUTION_TYPE).setValue(key);
                softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION)).hasValue(value);
            });
            List<String> values = getRangeListWithPercent(5, 100, 5);
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION)).hasOptions(values);

            ImmutableList.of("5%", "55%", "100%").forEach(value -> {
                planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION).setValue(value);
                softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION)).hasValue(value);
                softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION)).hasNoWarning();
            });

            // Asserts for REN-10794/#6
            planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).isRequired();
            reqParticipationValues.forEach((key, value) -> {
                planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(CONTRIBUTION_TYPE).setValue(key);
                softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).hasValue(value);
            });
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).hasOptions(values);
            planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.ASSUMED_PARTICIPATION)).isAbsent();

            // Asserts for REN-10794/#7
            softly.assertThat(ELEMENT_ADD_COVERAGE).as("Button 'Add coverage' isn't hidden").isPresent(false);
        });

        // REN-10794/#1
        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN).setValue(PLANS);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME)).isPresent().isEnabled().isRequired();

            PlanDefinitionTab.tableCoverageDefinition.getRows().forEach(row -> {
                row.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                softly.assertThat(row.getCell(TableConstants.CoverageDefinition.PLAN.getName()))
                        .hasValue(String.format(
                                "%s-%s",
                                planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_COMBOBOX).getValue(),
                                planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME).getValue()));
            });
        });

        PlanDefinitionTab.tableCoverageDefinition.getRows().forEach(row -> {
            row.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME).setValue(TEST_NAME);
        });
        assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN_NAME).getWarning().orElse("")).contains(ERROR_MESSAGE);

        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());

        assertSoftly(softly -> PLANS.forEach(plan ->
                softly.assertThat(ClassificationManagementTab.tablePlansAndCoverages)
                        .hasRowsThatContain(TableConstants.PlansAndCoverages.PLAN.getName(), String.format("%s-%s", plan, TEST_NAME))));
    }

    private List<String> getRangeListWithPercent(int start, int stop, int step) {
        LinkedList<String> list = new LinkedList<>();
        for (int i = start; i <= stop; i = i + step) {
            list.add(i + "%");
        }
        return list;
    }
}
