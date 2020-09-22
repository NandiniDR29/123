package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.COUNTRY;
import static com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData.METHOD_OF_DELIVERY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateValuesModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39295", component = POLICY_GROUPBENEFITS)
    public void testStateValuesModificationVerification_TL() {
        LOGGER.info("General Preconditions");
        String stateValue = "PR";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("Step#1 verification");
        initiateTLQuoteAndFillToTab(getDefaultTLMasterPolicyData(), PolicyInformationTab.class, false);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#2 verification");
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData(), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        TermLifeInsuranceMasterPolicyContext.premiumSummaryTab.submitTab();

        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Step#3 verification");
        termLifeInsuranceMasterPolicy.issue().start();
        termLifeInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), IssueActionTab.class, false);

        Tab issueActionTab = termLifeInsuranceMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class);
        issueActionTab.getAssetList().getAsset(METHOD_OF_DELIVERY).setValue("Mail");

        LOGGER.info("Step#4 verification");
        issueActionTab.getAssetList().getAsset(COUNTRY).setValue("United States");
        assertThat(issueActionTab.getAssetList().getAsset(IssueActionTabMetaData.STATE_PROVINCE)).doesNotContainOption(stateValue);

        LOGGER.info("Step#5 verification");
        termLifeInsuranceMasterPolicy.issue().getWorkspace().fillFrom(getDefaultTLMasterPolicyData()
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "IssueActionTabMail")).resolveLinks(), IssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#18 verification");
        termLifeInsuranceCertificatePolicy.initiate(getDefaultCertificatePolicyDataGatherData());
        assertThat(certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.SITUS_STATE)).containsOption(stateValue);
    }
}
