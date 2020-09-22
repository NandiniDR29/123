package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckFrequencyValidationForAttributeCrownsForWA extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-46561"}, component = POLICY_GROUPBENEFITS)
    public void testCheckFrequencyValidationForAttributeCrowns() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("Steps#1, 2, 3 verification");
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "WA")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "50")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Steps#5, 6 verification");
        groupDentalMasterPolicy.dataGather().start();
        planDefinitionTab.navigateToTab();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataWA5").resolveLinks()), PlanDefinitionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}
