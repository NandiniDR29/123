package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalDeductibleMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureDentalDeductibleAttributes extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final ImmutableList<String> APPLY_DEDUCTIBLE_VALUES = ImmutableList.of("Basic", "Major");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13494", component = POLICY_GROUPBENEFITS)
    public void testConfigureDentalDeductibleAttributes() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"),
                PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
        AssetList assetListDeductible = planDefinitionTab.getAssetList().getAsset(DENTAL_DEDUCTIBLE);

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            softly.assertThat(assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE)).isPresent().isEnabled().isRequired().hasValue("No");

            LOGGER.info("---=={Step 2}==---");
            assetListDeductible.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
            ImmutableList.of(LIFETIME_DEDUCTIBLE_IN_NETWORK, LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK).forEach(control -> {
                softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isRequired().hasValue("$100")
                        .hasOptions("$0", "$25","$35","$50","$75", "$100", "$150", "$200", "$250", "$300");
            });

            LOGGER.info("---=={Step 3}==---");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);

            ImmutableList.of(APPLY_DEDUCTIBLE_IN_NETWORK, APPLY_DEDUCTIBLE_OUT_OF_NETWORK, APPLY_DEDUCTIBLE_EPO).forEach(control -> {
                softly.assertThat(assetListDeductible.getAsset(control)).isPresent().isEnabled().isOptional()
                        .hasValue(ImmutableList.of("Basic", "Major", "Prosthodontics"));
                assetListDeductible.getAsset(control).setValue(APPLY_DEDUCTIBLE_VALUES);
                softly.assertThat(assetListDeductible.getAsset(control)).hasValue(APPLY_DEDUCTIBLE_VALUES);
            });

            LOGGER.info("---=={Step 4}==---");
            softly.assertThat(assetListDeductible.getAsset(DEDUCTIBLE_CARRYOVER)).isPresent().isEnabled().isRequired().hasValue("No");
        });
    }
}