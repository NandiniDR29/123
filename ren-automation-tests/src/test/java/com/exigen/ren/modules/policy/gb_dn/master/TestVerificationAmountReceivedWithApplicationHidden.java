package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData.AMOUNT_RECEIVED_WITH_APPLICATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerificationAmountReceivedWithApplicationHidden extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42357", component = POLICY_GROUPBENEFITS)
    public void testVerificationAmountReceivedWithApplicationHidden() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("TEST: Step 1");
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());

        LOGGER.info("TEST: Step 2");
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());

        LOGGER.info("TEST: Step 3");
        groupDentalMasterPolicy.acceptContract().start();

        LOGGER.info("TEST: Step 4");
        PolicyInformationBindActionTab policyInformationBindActionTab = groupDentalMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class);

        assertThat(policyInformationBindActionTab.getAssetList().getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).isAbsent();

        policyInformationBindActionTab.submitTab();

        assertThat(groupDentalMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).isTabSelected()).isTrue();

        LOGGER.info("TEST: Step 5");
        groupDentalMasterPolicy.acceptContract().submit();
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}