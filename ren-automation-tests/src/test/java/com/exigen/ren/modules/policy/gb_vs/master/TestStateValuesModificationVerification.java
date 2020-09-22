package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.COUNTRY;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.METHOD_OF_DELIVERY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateValuesModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-39291"}, component = POLICY_GROUPBENEFITS)
    public void testStateValuesModificationVerification_VS() {
        LOGGER.info("General Preconditions");
        String stateValue = "PR";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        LOGGER.info("Step#1 verification");
        quoteInitiateAndFillUpToTab(getDefaultVSMasterPolicyData(), PolicyInformationTab.class, false);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#2 verification");
        groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        GroupVisionMasterPolicyContext.premiumSummaryTab.submitTab();

        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Step#3 verification");
        groupVisionMasterPolicy.issue().start();
        groupVisionMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), IssueActionTab.class, false);

        Tab issueActionTab = groupVisionMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class);
        issueActionTab.getAssetList().getAsset(METHOD_OF_DELIVERY).setValue("Mail");

        LOGGER.info("Step#4 verification");
        issueActionTab.getAssetList().getAsset(COUNTRY).setValue("United States");
        assertThat(issueActionTab.getAssetList().getAsset(IssueActionTabMetaData.STATE_PROVINCE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#5 verification");
        groupVisionMasterPolicy.issue().getWorkspace().fillFrom(getDefaultVSMasterPolicyData()
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "IssueActionTabMail")).resolveLinks(), IssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#18 verification");
        groupVisionCertificatePolicy.initiate(getDefaultVSCertificatePolicyData());
        assertThat(certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.SITUS_STATE)).containsOption(stateValue);
    }
}
