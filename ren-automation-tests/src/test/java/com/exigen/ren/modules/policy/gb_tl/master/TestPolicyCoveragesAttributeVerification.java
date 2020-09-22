package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.GUARANTEED_ISSUE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.GuaranteedIssueMetaData.*;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCoveragesAttributeVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14393", "REN-14394", "REN-14459", "REN-14694", "REN-14695"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCoveragesAttributeVerification() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.selectDefaultPlan();
        LOGGER.info("REN 14393 Step 2,REN-14394 Steps 2,3,16");
        PlanDefinitionTab.changeCoverageTo(BTL);
        verifyGIAmountAttributes();
        LOGGER.info("REN 14393  Step 3,REN-14694 Steps 2,3,16");
        planDefinitionTab.addCoverage(BASIC_LIFE_PLAN, VOL_BTL);
        verifyGIAmountAttributes();
        LOGGER.info("REN 14393 Step 4,REN-14459 Steps 2,3,16");
        PlanDefinitionTab.changeCoverageTo(DEP_BTL);
        verifyGIAmountAttributes();
        LOGGER.info("REN 14393 Step 5,REN-14695 Steps 2,3,16");
        planDefinitionTab.addCoverage(BASIC_LIFE_PLAN, SP_VOL_BTL);
        verifyGIAmountAttributes();
    }

    private void verifyGIAmountAttributes() {
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(GUARANTEED_ISSUE).getAsset(CALCULATED_GI_AMOUNT)).isOptional().isPresent().isDisabled().hasValue("");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(GUARANTEED_ISSUE).getAsset(GI_AMOUNT_AT_AGE)).isOptional().isPresent().isEnabled().hasValue("")
                    .hasOptions(ImmutableList.of("", "60", "65", "70", "75", "80"));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(GUARANTEED_ISSUE).getAsset(AGE_LIMITED_GI_AMOUNT)).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("GI Multiples"))).isAbsent();
            planDefinitionTab.getAssetList().getAsset(GUARANTEED_ISSUE).getAsset(GI_AMOUNT_AT_AGE).setValue(getRandomElementFromList(ImmutableList.of("60", "65", "70", "75", "80")));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(GUARANTEED_ISSUE).getAsset(AGE_LIMITED_GI_AMOUNT)).isRequired().isPresent().isEnabled();
        });
    }
}
