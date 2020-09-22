package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteFundingStructure extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12493", component = POLICY_GROUPBENEFITS)
    public void testQuoteFundingStructure() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        AssetList sponsorParticipantFundingStructureAsset = planDefinitionTab.getSponsorParticipantFundingStructureAsset();
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            sponsorParticipantFundingStructureAsset.getAsset(CONTRIBUTION_TYPE).setValue("Contributory");
            softly.assertThat(sponsorParticipantFundingStructureAsset.getAsset(GROSS_UP)).isPresent().hasValue(false);

            sponsorParticipantFundingStructureAsset.getAsset(GROSS_UP).setValue(true);
            softly.assertThat(sponsorParticipantFundingStructureAsset.getAsset(PARTICIPANT_CONTRIBUTION)).hasValue("100");

            sponsorParticipantFundingStructureAsset.getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
            softly.assertThat(sponsorParticipantFundingStructureAsset.getAsset(GROSS_UP)).isAbsent();

            sponsorParticipantFundingStructureAsset.getAsset(CONTRIBUTION_TYPE).setValue("Voluntary");
            softly.assertThat(sponsorParticipantFundingStructureAsset.getAsset(GROSS_UP)).isAbsent();
        });
    }
}
