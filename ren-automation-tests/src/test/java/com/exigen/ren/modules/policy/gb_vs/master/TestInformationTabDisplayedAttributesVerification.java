package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.EnrollmentTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestInformationTabDisplayedAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32641"}, component = POLICY_GROUPBENEFITS)
    public void testInformationTabDisplayedAttributesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey() + "[1]", RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
        });

        LOGGER.info("Step#2 verification");
        groupVisionMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).fillTab(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "AK")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .resolveLinks());
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isRequired().isPresent().hasValue(EMPTY);
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).setValue("index=1");
        policyInformationTab.submitTab();

        LOGGER.info("Step#5 verification");
        groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.RATING.getLabel(),
                        PlanDefinitionTabMetaData.RatingMetadata.USE_BROCHURE_RATES.getLabel()), VALUE_YES).resolveLinks(), EnrollmentTab.class, PremiumSummaryTab.class, false);

        LOGGER.info("Step#6 verification");
        assertThat(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.BROCHURE_RATE_PROGRAM)).isPresent().isRequired().isEnabled().hasValue(EMPTY);

        LOGGER.info("Step#9, 10 verification");
        premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.BROCHURE_RATE_PROGRAM).setValue("Reward");
        premiumSummaryTab.rate();
        premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.BROCHURE_RATE_PROGRAM).setValue("Avery Hall");
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("Step#19 verification");
        premiumSummaryTab.submitTab();
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().start();

        AbstractContainer<?, ?> acceptContractAssetList = groupVisionMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });
        premiumSummaryTab.navigateToTab();
        assertThat(groupVisionMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).getAssetList()
                .getAsset(PremiumSummaryBindActionTabMetaData.BROCHURE_RATE_PROGRAM)).isPresent();

        LOGGER.info("Step#21 verification");
        PremiumSummaryBindActionTab.buttonNext.click();
        groupVisionMasterPolicy.issue().start();

        AbstractContainer<?, ?> issueAssetList = groupVisionMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.MEMBER_COMPANY_NAME)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isPresent().isEnabled().hasValue(MEMBER_COMPANY);
        });
        premiumSummaryTab.navigateToTab();

        assertThat(groupVisionMasterPolicy.issue().getWorkspace().getTab(PremiumSummaryIssueActionTab.class).getAssetList()
                .getAsset(PremiumSummaryIssueActionTabMetaData.BROCHURE_RATE_PROGRAM)).isPresent();
    }
}
