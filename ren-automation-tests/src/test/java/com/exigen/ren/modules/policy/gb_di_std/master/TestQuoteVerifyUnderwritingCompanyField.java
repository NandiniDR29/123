package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteVerifyUnderwritingCompanyField extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private final ImmutableMap<String, String>  underwritingCompanyValues = ImmutableMap.of(
            "PA", "Renaissance Life & Health Insurance Company of America",
            "NY", "Renaissance Life & Health Insurance Company of New York");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-5655", component = POLICY_GROUPBENEFITS)
    public void testQuoteVerifyUnderwritingCompanyField() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());

        AssetList policyInformationTabAssetList = (AssetList) shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList();
        assertSoftly(softly -> {

            // Asserts for REN-5655/#4
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.COSMETIC_SURGERY))
                    .hasValue(true)
                    .as("Checkbox 'Cosmetic Surgery' isn't checked.")
                    .isEnabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.SUBSTANCE_ABUSE))
                    .hasValue(false)
                    .as("Checkbox 'Substance Abuse' is checked.")
                    .isEnabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.WORKERS_COMPENSATION))
                    .hasValue(true)
                    .as("Checkbox 'Workers Compensation' isn't checked.")
                    .isEnabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.IN_COURSE_OF_EMPLOYMENT))
                    .hasValue(true)
                    .as("Checkbox 'In Course of Employment' isn't checked.")
                    .isEnabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.ACTIVE_DUTY_IN_ARMED_FORCES))
                    .hasValue(true)
                    .as("Checkbox 'Active Duty In Armed Forces' isn't checked.")
                    .isDisabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.WAR))
                    .hasValue(true)
                    .as("Checkbox 'War' isn't checked.")
                    .isDisabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.SELF_INFLICTED))
                    .hasValue(true)
                    .as("Checkbox 'Self-inflicted' isn't checked.")
                    .isDisabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.RIOT))
                    .hasValue(true)
                    .as("Checkbox 'Riot' isn't checked.")
                    .isDisabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.FELONY))
                    .hasValue(true)
                    .as("Checkbox 'Felony' isn't checked.")
                    .isDisabled();
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.INTOXICATION))
                    .hasValue(false)
                    .as("Checkbox 'Intoxication' isn't checked.")
                    .isEnabled();
            Arrays.asList("WA", "VT").forEach((state) -> {
                policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.SITUS_STATE).setValue(state);
                softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.SUBSTANCE_ABUSE)).isPresent(false);
                softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.INTOXICATION)).isPresent(false);
            });

            // Asserts for REN-5655/#1
            softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.UNDEWRITING_COMPANY)).isRequired().hasOptions(underwritingCompanyValues.values().asList());
            underwritingCompanyValues.forEach((key, value) -> {
                policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.SITUS_STATE).setValue(key);
                softly.assertThat(policyInformationTabAssetList.getAsset(PolicyInformationTabMetaData.UNDEWRITING_COMPANY))
                        .hasValue(value);
            });
        });

        // REN-5655/#3
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), PlanDefinitionTab.class);
        MultiAssetList planDefinitionTabAssetList = (MultiAssetList) shortTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(PlanDefinitionTab.class).getAssetList();
        planDefinitionTab.selectDefaultPlan();
        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
        assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isRequired();

        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Eligible");
        assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue("");

        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("1");
        assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue("1");

        planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.CENSUS_TYPE).setValue("Enrolled");
        assertThat(planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue("");
    }
}
