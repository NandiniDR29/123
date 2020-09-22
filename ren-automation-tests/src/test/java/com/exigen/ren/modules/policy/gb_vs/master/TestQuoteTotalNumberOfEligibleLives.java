package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteTotalNumberOfEligibleLives extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-16555", component = POLICY_GROUPBENEFITS)
    public void testQuoteTotalNumberOfEligibleLives() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        AssetList policyInfAssetList = (AssetList) policyInformationTab.getAssetList();

        assertSoftly(softly -> {
            LOGGER.info("REN-16555, TC1: Steps 1-5");
            softly.assertThat(policyInfAssetList.getAsset(POLICY_TOTAL_NUMBER_ELIGIBLE_LIVES))
                    .isPresent().isEnabled().isRequired().hasValue("");

            LOGGER.info("REN-16555, TC2: Steps 1-7");
            policyInfAssetList.getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInfAssetList.getAsset(UNDERWRITING_COMPANY))
                    .isPresent().isDisabled().hasValue("Renaissance Life & Health Insurance Company of New York");
            policyInfAssetList.getAsset(SITUS_STATE).setValue("AK");
            softly.assertThat(policyInfAssetList.getAsset(UNDERWRITING_COMPANY))
                    .hasValue("Renaissance Life & Health Insurance Company of America");
        });


    }
}
