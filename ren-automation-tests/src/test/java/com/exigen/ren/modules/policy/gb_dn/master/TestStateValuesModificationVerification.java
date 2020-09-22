package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.COUNTRY;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.METHOD_OF_DELIVERY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateValuesModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-39283"}, component = POLICY_GROUPBENEFITS)
    public void testStateValuesModificationVerification_DN() {
        LOGGER.info("General Preconditions");
        String stateValue = "PR";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("Step#1 verification");
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData(), PolicyInformationTab.class, false);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#2 verification");
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        GroupDentalMasterPolicyContext.premiumSummaryTab.submitTab();

        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Step#3 verification");
        groupDentalMasterPolicy.issue().start();
        groupDentalMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), IssueActionTab.class, false);

        Tab issueActionTab = groupDentalMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class);
        issueActionTab.getAssetList().getAsset(METHOD_OF_DELIVERY).setValue("Mail");

        LOGGER.info("Step#4 verification");
        issueActionTab.getAssetList().getAsset(COUNTRY).setValue("United States");
        assertThat(issueActionTab.getAssetList().getAsset(IssueActionTabMetaData.STATE_PROVINCE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#5 verification");
        groupDentalMasterPolicy.issue().getWorkspace().fillFrom(getDefaultDNMasterPolicyData()
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "IssueActionTabMail")).resolveLinks(), IssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#18 verification");
        groupDentalCertificatePolicy.initiate(getDefaultGroupDentalCertificatePolicyData());
        assertThat(certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.SITUS_STATE)).containsOption(stateValue);
    }
}
