package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CENSUS_TYPE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyPlanDefinitionCensusTypeAffectsRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12422", component = POLICY_GROUPBENEFITS)

    public void testPolicyPlanDefinitionCensusTypeAffectsRating() {


        LOGGER.info("Test REN-12422, TC02");

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("Test REN-12422, TC02, Step 1");
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        groupDentalMasterPolicy.dataGather().start();
        //navigate to plan definition tab
        Tab.buttonNext.click();
        Tab.buttonNext.click();

        LOGGER.info("Test REN-12422, TC02, Step 2");
        planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE).setValue("Eligible");
        planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE).setValue("Enrolled");

        Tab.buttonSaveAndExit.click();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);


    }
}
