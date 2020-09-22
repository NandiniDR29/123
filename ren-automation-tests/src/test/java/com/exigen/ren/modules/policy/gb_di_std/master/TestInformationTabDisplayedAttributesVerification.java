package com.exigen.ren.modules.policy.gb_di_std.master;

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
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
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

public class TestInformationTabDisplayedAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32212"}, component = POLICY_GROUPBENEFITS)
    public void testInformationTabDisplayedAttributesVerification() {
        LOGGER.info("General Preconditions");
        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
        });

        LOGGER.info("Step#2 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isRequired().isPresent().hasValue(EMPTY);

        LOGGER.info("Steps#5, 6, 7, 8, 9 verification");//Steps 8,9 completed for verification step 10
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), "index=1").resolveLinks(), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_NO);
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#10 verification");
        MainPage.QuickSearch.search(policyNumber);
        shortTermDisabilityMasterPolicy.propose().start();
        shortTermDisabilityMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), ProposalActionTab.class, false);
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.confirm();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("'Member Company Name' is required"))).isPresent();
        Tab.buttonBack.click();

        LOGGER.info("Step#11, 14 execution");
        MainPage.QuickSearch.search(policyNumber);
        shortTermDisabilityMasterPolicy.dataGather().start();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).setValue(relationship1);
        PolicyInformationTab.buttonTopSave.click();
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.submitTab();
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());

        LOGGER.info("Step#15 verification");
        shortTermDisabilityMasterPolicy.acceptContract().start();
        AbstractContainer<?, ?> acceptContractAssetList = shortTermDisabilityMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });

        shortTermDisabilityMasterPolicy.acceptContract().getWorkspace().fillFrom(getDefaultSTDMasterPolicyData(), PolicyInformationBindActionTab.class);
        Tab.buttonNext.click();

        shortTermDisabilityMasterPolicy.issue().start();
        AbstractContainer<?, ?> issueAssetList = shortTermDisabilityMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isPresent().isEnabled().hasValue(MEMBER_COMPANY);
        });
    }
}
