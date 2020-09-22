package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.INCLUDED;
import static com.exigen.ren.main.enums.ValueConstants.NOT_INCLUDED;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.W2;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionW2FICA extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    private static final List<String> FICA_MATCH_VALUES = ImmutableList.of("None", "Reimbursement", "Embedded");
    private static final List<String> W2_VALUES = ImmutableList.of("Included", "Not Included");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-16456", component = POLICY_GROUPBENEFITS)

    public void testPlanDefinitionW2FICA() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        paidFamilyLeaveMasterPolicy.initiate(getDefaultPFLMasterPolicyData());
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), planDefinitionTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-16456  Step 1-15");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(FICA_MATCH)).isPresent().isRequired().isEnabled().hasValue("None").hasOptions(FICA_MATCH_VALUES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(W2)).isPresent().isRequired().isEnabled().hasValue("Not Included").hasOptions(W2_VALUES);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(FICA_MATCH).setValue("Reimbursement");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(W2)).hasValue(INCLUDED);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(FICA_MATCH).setValue("Embedded");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(W2)).hasValue(INCLUDED);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(FICA_MATCH).setValue("None");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(W2)).hasValue(NOT_INCLUDED);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(FICA_MATCH).setValue("Reimbursement");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS).getAsset(W2).setValue(NOT_INCLUDED);
        });
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultPFLMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}