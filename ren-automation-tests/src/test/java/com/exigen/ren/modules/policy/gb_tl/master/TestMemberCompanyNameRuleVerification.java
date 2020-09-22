package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.MasterPolicyBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.MasterPolicyIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.MasterPolicyBindActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.MasterPolicyIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestMemberCompanyNameRuleVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32650"}, component = POLICY_GROUPBENEFITS)
    public void testMemberCompanyNameRuleVerification() {
        LOGGER.info("General Preconditions");
        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship2 = tdSpecific().getValue("RelationshipTestData2", RelationshipTabMetaData.NAME_LEGAL.getLabel());

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
        });

        LOGGER.info("Step#2 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isRequired().isPresent().hasValue(EMPTY);

        LOGGER.info("Step#4 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).containsAllOptions(EMPTY, relationship1, relationship2);

        LOGGER.info("Steps#5, 6, 7, 8 execution");//Steps 7, 8 completed for verification step
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();

        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_NO);
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#9 verification");
        MainPage.QuickSearch.search(policyNumber);
        termLifeInsuranceMasterPolicy.propose().start();
        termLifeInsuranceMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), ProposalActionTab.class, false);
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.confirm();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("'Member Company Name' is required"))).isPresent();
        Tab.buttonBack.click();

        LOGGER.info("Step#10, 13 execution");
        MainPage.QuickSearch.search(policyNumber);
        termLifeInsuranceMasterPolicy.dataGather().start();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).setValue(relationship1);
        PolicyInformationTab.buttonTopSave.click();
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.submitTab();
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());

        LOGGER.info("Step#14 verification");
        termLifeInsuranceMasterPolicy.acceptContract().start();
        AbstractContainer<?, ?> acceptContractAssetList = termLifeInsuranceMasterPolicy.acceptContract().getWorkspace().getTab(MasterPolicyBindActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(acceptContractAssetList.getAsset(MasterPolicyBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(MasterPolicyBindActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(MasterPolicyBindActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });

        LOGGER.info("Step#15 verification");
        termLifeInsuranceMasterPolicy.acceptContract().getWorkspace().fillFrom(getDefaultTLMasterPolicyData(), MasterPolicyBindActionTab.class);
        Tab.buttonNext.click();

        termLifeInsuranceMasterPolicy.issue().start();
        AbstractContainer<?, ?> issueAssetList = termLifeInsuranceMasterPolicy.issue().getWorkspace().getTab(MasterPolicyIssueActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(issueAssetList.getAsset(MasterPolicyIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(MasterPolicyIssueActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(MasterPolicyIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isPresent().isPresent().isEnabled().hasValue(MEMBER_COMPANY);
        });
    }
}
