package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData.AMOUNT_RECEIVED_WITH_APPLICATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerifyAmountReceivedWithApplicationAttributeIsOptional extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42356", component = POLICY_GROUPBENEFITS)
    public void testVerifyAmountReceivedWithApplicationAttributeIsOptional() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        LOGGER.info("TEST: Step 1");
        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData());

        LOGGER.info("TEST: Step 2");
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());

        LOGGER.info("TEST: Step 3");
        groupAccidentMasterPolicy.acceptContract().start();

        LOGGER.info("TEST: Step 4");
        PolicyInformationBindActionTab policyInformationBindActionTab = groupAccidentMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class);

        assertThat(policyInformationBindActionTab.getAssetList().getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).isPresent().hasValue(EMPTY).isOptional();

        policyInformationBindActionTab.submitTab();

        assertThat(groupAccidentMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).isTabSelected()).isTrue();

        LOGGER.info("TEST: Step 5");
        groupAccidentMasterPolicy.acceptContract().submit();
        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}