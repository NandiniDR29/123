package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigurePlansAndCoverages extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String LABEL_SECTION_PATTERN = "//*[normalize-space(text())='%s']";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-11433", "REN-13326"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteConfigurePlansAndCoverages() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();

        assertSoftly(softly -> {
            // Asserts for REN-13326/01-1
            softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format("Service Categories"))).isPresent();
            softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format("Non-Standard- Benefit Levels"))).isAbsent();

            // Asserts for REN-13326/02-2
            ImmutableList.of("Endodontic", "Periodontics", "Surgical Extraction", "Oral Surgery", "Non-Surgical Extraction").forEach(fieldName ->
                    softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format(fieldName))).isAbsent());
        });

        RadioGroup ppoEpoPlanAsset = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN);
        assertThat(ppoEpoPlanAsset).isPresent().hasValue(VALUE_NO).isRequired();

        // Asserts for REN-11433
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(ByT.xpath(LABEL_SECTION_PATTERN).format("TMJ"))).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList()
                    .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
                    .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.EXCLUSIONS)
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COSMETIC_SERVICES))
                    .hasValue(true).isEnabled();
            softly.assertThat(planDefinitionTab.getAssetList()
                    .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
                    .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.EXCLUSIONS)
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.MISSING_TOOTH))
                    .hasValue(false).isEnabled();
        });
    }
}