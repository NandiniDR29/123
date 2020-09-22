package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.COUNTRY;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.METHOD_OF_DELIVERY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateValuesModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-39292"}, component = POLICY_GROUPBENEFITS)
    public void testStateValuesModificationVerification_LTD() {
        LOGGER.info("General Preconditions");
        String stateValue = "PR";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("Step#1 verification");
        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), PolicyInformationTab.class, false);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#2 verification");
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultLTDMasterPolicyData(), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        LongTermDisabilityMasterPolicyContext.premiumSummaryTab.submitTab();

        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Step#3 verification");
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), IssueActionTab.class, false);

        Tab issueActionTab = longTermDisabilityMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class);
        issueActionTab.getAssetList().getAsset(METHOD_OF_DELIVERY).setValue("Mail");

        LOGGER.info("Step#4 verification");
        issueActionTab.getAssetList().getAsset(COUNTRY).setValue("United States");
        assertThat(issueActionTab.getAssetList().getAsset(IssueActionTabMetaData.STATE_PROVINCE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#5 verification");
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillFrom(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "IssueActionTabMail")).resolveLinks(), IssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#18 verification");
        longTermDisabilityCertificatePolicy.initiate(getDefaultLTDCertificatePolicyDataGatherData());
        assertThat(certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.SITUS_STATE)).containsOption(stateValue);
    }
}
