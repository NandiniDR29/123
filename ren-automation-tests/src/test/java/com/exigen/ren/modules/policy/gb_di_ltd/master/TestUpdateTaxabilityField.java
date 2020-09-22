package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_CON;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PREMIUM_PAID_POST_TAX;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.TAXABILITY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateTaxabilityField extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static final String BENEFITS_TAXABLE = "Benefits Taxable";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20423"}, component = POLICY_GROUPBENEFITS)
    public void testUpdateTaxabilityField() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.SIC_CODE.getLabel()), "1521"));
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(LTD_CON));
        assertSoftly(softly -> {
            LOGGER.info("Test REN-20423 Step 2 to Step 4");
            softly.assertThat(planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(TAXABILITY)).isRequired().hasOptions(ImmutableList.of(BENEFITS_TAXABLE, "Benefits Not Taxable")).hasValue(BENEFITS_TAXABLE);
            LOGGER.info("Test REN-20423 Step 5,6");
            softly.assertThat(planDefinitionTab.getSponsorParticipantFundingStructureAsset().getAsset(PREMIUM_PAID_POST_TAX)).isPresent().isRequired().hasValue(VALUE_YES);
            LOGGER.info("Test REN-20423 Step 12,13");
            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultLTDMasterPolicyData(), PlanDefinitionTab.class, premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}

