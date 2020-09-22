package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.W2;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteHideAddCoverage extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private final static StaticElement LABEL_ADD_COVERAGE = new StaticElement(By.xpath("//*[@id='policyDataGatherForm:componentContextHolder']//*[text()='Add coverage' or @value='Add coverage']"));
    private final static String CHECK_COVERAGE_DEFINITION_QUERY = "SELECT * " +
            "FROM [dbo].[GroupCoverageDefinition] " +
            "WHERE coverageCd = ?";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-5739", "REN-10732", "REN-10734", "REN-10782", "REN-11685", "REN-12298", "REN-12295", "REN-12297"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteHideAddCoverage() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        MultiAssetList planDefAssetList = (MultiAssetList) planDefinitionTab.getAssetList();
        planDefinitionTab.selectDefaultPlan();

        assertSoftly(softly -> {

            // Asserts for REN-12298/#1
            softly.assertThat(planDefAssetList.getAsset(INITIAL_ENROLLMENT_UNDERWRITING_OFFER)).isRequired().hasValue(CHECKBOX_NOT_AVAILABLE);
            softly.assertThat(planDefAssetList.getAsset(ANNUAL_ENROLLMENT_UNDERWRITING_OFFER)).isRequired().hasValue("Enrollment period - EOI required");
            softly.assertThat(planDefAssetList.getAsset(UNDERWRITING_OFFER_FOR_QUALIFYING_LIFE_EVENT)).isOptional().hasValue(CHECKBOX_NOT_AVAILABLE);

            // Asserts for REN-12295/#1
            softly.assertThat(planDefAssetList.getAsset(LTD)).isPresent().hasValue(false);
            softly.assertThat(planDefAssetList.getAsset(LIFE)).isPresent().hasValue(false);
            softly.assertThat(planDefAssetList.getAsset(DENTAL)).isPresent().hasValue(false);
            softly.assertThat(planDefAssetList.getAsset(VISION)).isPresent().hasValue(false);

            // Asserts for REN-10732/#1
            softly.assertThat(planDefAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAXIMUM_BENEFIT_PERIOD_INCLUDING_EP)).isAbsent();
            softly.assertThat(planDefAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(MAXIMUM_PAYMENT_DURATION)).isPresent();

            // Assert for REN-10732/#2
            softly.assertThat(planDefAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(TEST_DEFINITION)).hasOptions("Loss of Duties and Earnings");

            // Asserts for REN-10732/#3
            softly.assertThat(planDefAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(END_OF_SALARY_CONTINUATION)).isRequired().hasValue(VALUE_NO);
            softly.assertThat(planDefAssetList.getAsset(BENEFIT_SCHEDULE).getAsset(END_OF_ACCUMULATED_SICK_LEAVE)).isRequired().hasValue(VALUE_NO);

            // Asserts for REN-10734/#1,2
            ImmutableList.of(
                    planDefAssetList.getAsset(SICK_LEAVE),
                    planDefAssetList.getAsset(PTO),
                    planDefAssetList.getAsset(TERMINATION_SEVERANCE),
                    planDefAssetList.getAsset(WORK_EARNINGS),
                    planDefAssetList.getAsset(RETIREMENT_PLAN),
                    planDefAssetList.getAsset(AUTOMOBILE_LIABILITY),
                    planDefAssetList.getAsset(THIRD_PARTY_SETTLEMENT),
                    planDefAssetList.getAsset(UNEMPLOYMENT),
                    planDefAssetList.getAsset(WORKERS_COMPENSATION),
                    planDefAssetList.getAsset(ASSOCIATION_PLANS),
                    planDefAssetList.getAsset(GOVERNMENTAL_RETIREMENT)).forEach(asset ->
                    softly.assertThat(asset).isPresent().hasValue(INCLUDED).hasOptions(INCLUDED, NOT_INCLUDED));
            softly.assertThat(planDefAssetList.getAsset(INDIVIDUAL_DISABILITY_PLAN)).isPresent().hasValue(NOT_INCLUDED).hasOptions(INCLUDED, NOT_INCLUDED);

            // Assert for REN-10782/#2
            softly.assertThat(planDefAssetList.getAsset(FICA_MATCH)).hasValue("None").hasOptions("None", "Reimbursement", "Embedded");

            // Assert for REN-5739/#1
            softly.assertThat(LABEL_ADD_COVERAGE).as("Button 'Add coverage' isn't hidden").isPresent(false);

            // Assert for REN-11685/#1
            softly.assertThat(planDefAssetList.getAsset(OPTIONS).getAsset(W2)).isRequired().hasValue(NOT_INCLUDED).hasOptions(INCLUDED, NOT_INCLUDED);
            planDefAssetList.getAsset(FICA_MATCH).setValue("Reimbursement");
            softly.assertThat(planDefAssetList.getAsset(OPTIONS).getAsset(W2)).isDisabled().hasValue(INCLUDED);

            // Assert for REN-12297/#1
            planDefAssetList.getAsset(CENSUS_TYPE).setValue("Eligible");
            List<String> values = getRangeListWithPercent(5, 100, 5);
            softly.assertThat(planDefAssetList.getAsset(REQUIRED_PARTICIPATION)).hasOptions(values);
            softly.assertThat(planDefAssetList.getAsset(ASSUMED_PARTICIPATION)).hasOptions(values);
            planDefAssetList.getAsset(CENSUS_TYPE).setValue("Enrolled");
        });

        planDefinitionTab.getAssetList().fill(tdSpecific().getTestData("PlanDefinitionTabWithThreePlans"));

        // Assert for REN-5739/#2
        assertThat(PlanDefinitionTab.tableCoverageDefinition)
                .with(TableConstants.CoverageDefinition.COVERAGE_NAME, "STD - Buy Up")
                .hasMatchingRows(0);
        PlanDefinitionTab.buttonTopSave.click();
        assertThat(DBService.get().getRows(CHECK_COVERAGE_DEFINITION_QUERY, "STD_BU")).as("The 'STD_BU' coverage exist in 'coverageCd' field of the new added row").isEmpty();
    }

    private List<String> getRangeListWithPercent(int start, int stop, int step) {
        LinkedList<String> list = new LinkedList<>();
        for (int i = start; i <= stop; i = i + step) {
            list.add(i + "%");
        }
        return list;
    }
}