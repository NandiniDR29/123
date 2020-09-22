package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.MasterPolicyBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.MasterPolicyIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.MasterPolicyBindActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.MasterPolicyIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PROPOSED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInformationTabHiddenAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32647"}, component = POLICY_GROUPBENEFITS)
    public void testInformationTabHiddenAttributesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
        });

        LOGGER.info("Steps#2, 3, 4 execution");
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultTLMasterPolicyData(), PolicyInformationTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Step#7 execution");
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PROPOSED);

        LOGGER.info("Step#8 verification");
        termLifeInsuranceMasterPolicy.acceptContract().start();
        AbstractContainer<?, ?> acceptContractAssetList = termLifeInsuranceMasterPolicy.acceptContract().getWorkspace().getTab(MasterPolicyBindActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(acceptContractAssetList.getAsset(MasterPolicyBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(MasterPolicyBindActionTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
            softly.assertThat(acceptContractAssetList.getAsset(MasterPolicyBindActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });

        LOGGER.info("Step#9 verification");
        termLifeInsuranceMasterPolicy.acceptContract().getWorkspace().fillFrom(getDefaultTLMasterPolicyData(), MasterPolicyBindActionTab.class);
        Tab.buttonNext.click();

        termLifeInsuranceMasterPolicy.issue().start();
        AbstractContainer<?, ?> issueAssetList = termLifeInsuranceMasterPolicy.issue().getWorkspace().getTab(MasterPolicyIssueActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(issueAssetList.getAsset(MasterPolicyIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(MasterPolicyIssueActionTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
            softly.assertThat(issueAssetList.getAsset(MasterPolicyIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });
    }
}
