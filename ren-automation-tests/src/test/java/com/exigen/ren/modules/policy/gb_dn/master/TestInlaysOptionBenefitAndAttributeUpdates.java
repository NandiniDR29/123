package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ENDORSEMENT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalBasicMetaData.AMALGAM_AND_COMPOSITE_RESIN_FILLINGS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMajorMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LIMITATION_FREQUENCY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LimitationFrequencyMetaData.DENTAL_BASIC;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LimitationFrequencyMetaData.DENTAL_MAJOR;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.components.Components.CASE_PROFILE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInlaysOptionBenefitAndAttributeUpdates extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String OPTION_TO_RESINS = "Option to Resins";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41102"}, component = POLICY_GROUPBENEFITS)
    public void testInlaysOptionBenefitAndAttributeUpdates_SitusStateIsNY() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData(CASE_PROFILE, "TestData_WithIntakeProfile"), groupDentalMasterPolicy.getType());

        LOGGER.info("Step 1-4");
        TestData tdPolicy = getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY").resolveLinks();
        groupDentalMasterPolicy.initiateAndFillUpToTab(tdPolicy, PlanDefinitionTab.class, false);
        planDefinitionTab.selectDefaultPlan();
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).hasValue(OPTION_TO_RESINS).isDisabled();

        LOGGER.info("Step 5");
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(LIMITED_OCCLUSAL_ADJUSTMENTS)).containsOption("Once Every 12 Months");

        LOGGER.info("Step 6");
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_BASIC).getAsset(AMALGAM_AND_COMPOSITE_RESIN_FILLINGS)).containsOption("Once Every 12 Months");

        LOGGER.info("Step 7");
        planDefinitionTab.fillTab(tdPolicy);
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS).setValue("Not Covered");
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).isAbsent();

        LOGGER.info("Step 8");
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(CROWNS).setValue("Not Covered");
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(VENEERS).setValue("Not Covered");

        Tab.buttonNext.click();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(tdPolicy, ClassificationManagementTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("Step 12");
        groupDentalMasterPolicy.dataGather().start();
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS).setValue("1 Per Year");
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).isPresent().isDisabled();

        LOGGER.info("Step 13");
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(CROWNS).setValue("1 Per Year");
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(VENEERS).setValue("1 Per Year");
        premiumSummaryTab.navigateToTab().submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("Step 16");
        groupDentalMasterPolicy.propose().perform(tdPolicy);
        groupDentalMasterPolicy.acceptContract().perform(tdPolicy);
        tdPolicy.adjust(TestData.makeKeyPath(PolicyInformationIssueActionTab.class.getSimpleName(), PolicyInformationIssueActionTabMetaData.COUNTY_CODE.getLabel()), "index=1");
        groupDentalMasterPolicy.issue().perform(tdPolicy);
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(POLICY_ACTIVE);

        LOGGER.info("Step 19, 20");
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        planDefinitionTab.navigateToTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).isPresent().isEnabled()
                .hasOptions("No Optional Benefit", "Option to Amalgams", OPTION_TO_RESINS, "Option to Amalgams on Posterior");

        LOGGER.info("Step 25");
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS).setValue("Not Covered");
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).isAbsent();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41161"}, component = POLICY_GROUPBENEFITS)
    public void testInlaysOptionBenefitAndAttributeUpdates_SitusStateIsWA() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData(CASE_PROFILE, "TestData_WithIntakeProfile"), groupDentalMasterPolicy.getType());

        LOGGER.info("Step 1-4");
        TestData tdPolicy = getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_PlanWA1").resolveLinks())
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "WA")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "50")
                .resolveLinks();
        groupDentalMasterPolicy.initiateAndFillUpToTab(tdPolicy, PlanDefinitionTab.class, true);
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).hasValue(OPTION_TO_RESINS).isDisabled();

        LOGGER.info("Step 6");
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(tdPolicy, PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("Step 7");
        groupDentalMasterPolicy.propose().perform();
        groupDentalMasterPolicy.acceptContract().perform(tdPolicy);
        groupDentalMasterPolicy.issue().perform(tdPolicy.adjust(tdSpecific().getTestData("TestData_Issue").resolveLinks()));
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(POLICY_ACTIVE);

        LOGGER.info("Step 8");
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        planDefinitionTab.navigateToTab();
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS_OPTION_BENEFIT)).isPresent().isEnabled()
                .hasValue(OPTION_TO_RESINS).hasOptions(OPTION_TO_RESINS);
    }
}
