package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
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
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestInformationTabHiddenAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32640"}, component = POLICY_GROUPBENEFITS)
    public void testInformationTabHiddenAttributesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
        });

        groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "AK")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.RATING.getLabel(),
                        PlanDefinitionTabMetaData.RatingMetadata.USE_BROCHURE_RATES.getLabel()), VALUE_YES).resolveLinks(), PolicyInformationTab.class, PremiumSummaryTab.class, false);

        LOGGER.info("Step#4 verification");
        assertThat(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.BROCHURE_RATE_PROGRAM)).isPresent().isRequired().isEnabled().hasValue(EMPTY);
        premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.BROCHURE_RATE_PROGRAM).setValue("Reward");

        LOGGER.info("Step#7 verification");
        planDefinitionTab.navigateToTab();//Step#6 was skipped due to using 1 Plan for Dev tests
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.USE_BROCHURE_RATES).setValue(VALUE_NO);
        premiumSummaryTab.navigateToTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.USE_BROCHURE_RATES)).isAbsent();

        LOGGER.info("Step#12 verification");
        premiumSummaryTab.submitTab();
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().start();

        AbstractContainer<?, ?> acceptContractAssetList = groupVisionMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
            softly.assertThat(acceptContractAssetList.getAsset(PolicyInformationBindActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });
        premiumSummaryTab.navigateToTab();
        assertThat(groupVisionMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).getAssetList()
                .getAsset(PremiumSummaryBindActionTabMetaData.BROCHURE_RATE_PROGRAM)).isAbsent();

        LOGGER.info("Step#13 verification");
        PremiumSummaryBindActionTab.buttonNext.click();
        groupVisionMasterPolicy.issue().start();

        AbstractContainer<?, ?> issueAssetList = groupVisionMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.GROUP_IS_MEMBER_COMPANY)).isPresent().isDisabled();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
            softly.assertThat(issueAssetList.getAsset(PolicyInformationIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
        });
        premiumSummaryTab.navigateToTab();
        assertThat(groupVisionMasterPolicy.issue().getWorkspace().getTab(PremiumSummaryIssueActionTab.class).getAssetList()
                .getAsset(PremiumSummaryIssueActionTabMetaData.BROCHURE_RATE_PROGRAM)).isAbsent();
    }
}