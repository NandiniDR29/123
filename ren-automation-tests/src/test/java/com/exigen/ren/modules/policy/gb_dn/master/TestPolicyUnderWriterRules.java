package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.UNDEWRITING_COMPANY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyUnderWriterRules extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-12420", component = POLICY_GROUPBENEFITS)

    public void testPolicyUnderWriterRules() {

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());

        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");

        CustomAssertions.assertThat(policyInformationTab.getAssetList().getAsset(UNDEWRITING_COMPANY)).hasValue("Renaissance Life & Health Insurance Company of New York");

        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("CA");

        CustomAssertions.assertThat(policyInformationTab.getAssetList().getAsset(UNDEWRITING_COMPANY)).hasValue("Renaissance Life & Health Insurance Company of America");

        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PremiumSummaryTab.class);

        premiumSummaryTab.submitTab();


        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());

        groupDentalMasterPolicy.acceptContract().start();
        CustomAssertions.assertThat(policyInformationTab.getAssetList().getAsset(UNDEWRITING_COMPANY)).isDisabled();

        new PolicyInformationBindActionTab().submitTab();
        groupDentalMasterPolicy.acceptContract().submit();

        groupDentalMasterPolicy.issue().start();
        CustomAssertions.assertThat(policyInformationTab.getAssetList().getAsset(UNDEWRITING_COMPANY)).isDisabled();


    }
}
