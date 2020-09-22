package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_WEEKLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.MAX_TAXABLE_WAGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyAttributesChangesVerification extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-33935", component = POLICY_GROUPBENEFITS)
    public void testPolicyAttributesChangesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());

        initiatePFLQuoteAndFillToTab(getDefaultPFLMasterPolicyData(), PlanDefinitionTab.class, true);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME)).hasValue("PFL");

            LOGGER.info("REN-33935 TC3 Step#1 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(MAX_TAXABLE_WAGE)).hasValue(new Currency("134900").toString());

            LOGGER.info("REN-33935 TC3 Step#2 verification");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(MAXIMUM_WEEKLY_BENEFIT_AMOUNT)).hasValue(new Currency("881").toString());
        });
    }
}
