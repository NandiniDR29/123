package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab.helpTextGroupIsMemberCompany;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInformationTabAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String HELP_TEXT_GROUP_IS_MEMBER_COMPANY = "Company is member of PEO, Association, or Trust";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32046"}, component = POLICY_GROUPBENEFITS)
    public void testInformationTabAttributesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey() + "[1]", RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());

        LOGGER.info("Steps#1, 3 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
        helpTextGroupIsMemberCompany.mouseOver();
        assertThat(helpTextGroupIsMemberCompany.getAttribute("title")).isEqualTo(HELP_TEXT_GROUP_IS_MEMBER_COMPANY);//This TC is not for automation but done

        LOGGER.info("Steps#2, 3 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE).setValue("FL");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isRequired().isPresent().hasValue(EMPTY);

        LOGGER.info("Steps#4, 5 execution");
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.ASSUMED_PARTICIPATION_PCT.getLabel()), "30")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.RATING.getLabel(), PlanDefinitionTabMetaData.RatingMetaData.USE_BRO_RATES.getLabel()), VALUE_YES).resolveLinks(), PolicyInformationTab.class, PremiumSummaryTab.class, false);

        LOGGER.info("Steps#6, 7 verification");
        assertThat(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.RATE_SECTION).getAsset(PremiumSummaryTabMetaData.RateMetaData.BROCHURE_RATE_PROGRAM))
                .isPresent().isRequired().isEnabled().hasValue(EMPTY).hasOptions(ImmutableList.of("", "Reward", "Avery Hall", "BETA Health", "CBG", "Century Healthcare", "Denali",
                "FirstStarHR", "J Smith Marsh - Georgia", "Las Vegas Small Group", "Morgan White Takeover Plan", "Morgan White Total Reward", "Morgan White 2019", "Namb/Pandella Group",
                "Solstice", "StaffMetrix HR", "N/A"));

        LOGGER.info("Step#8 execution");
        premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.RATE_SECTION).getAsset(PremiumSummaryTabMetaData.RateMetaData.BROCHURE_RATE_PROGRAM).setValue("Reward");
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Steps#9, 10 verification");
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.issue().start();

        assertThat(groupDentalMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class)
                .getAssetList().getAsset(PolicyInformationIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS))
                .isPresent().isRequired().hasValue(MEMBER_COMPANY).hasOptions(GROUP_SPONSOR, MEMBER_COMPANY);
    }
}