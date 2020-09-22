package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionPremiumPaidPostTax extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20350", "REN-20413"}, component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionPremiumPaidPostTax() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        paidFamilyLeaveMasterPolicy.initiate(getDefaultPFLMasterPolicyData());
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), planDefinitionTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-20350 Step 1-3,Step 1-3,Step7,Step 1-5");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SELF_ADMINISTERED)).isDisabled();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(SELF_ADMINISTERED)).hasValue(VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(TAXABILITY)).isPresent().isRequired().hasValue("Benefits Taxable").hasOptions("Benefits Taxable", "Benefits Not Taxable");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.RATE_BASIS)).isPresent().isRequired().hasValue("Percent of Taxable Wage");

            LOGGER.info("REN-20413 Step 1-5, Step 8");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PREMIUMS_PAID_POST_TAX)).isPresent().isRequired().isEnabled().hasValue(VALUE_YES);
        });
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultPFLMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}