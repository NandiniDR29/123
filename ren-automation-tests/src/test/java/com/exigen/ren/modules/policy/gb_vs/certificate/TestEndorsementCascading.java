package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.DialogRateAndPremiumOverride;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestEndorsementCascading;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.TestDataKey.EXISTENT_BILLING_ACCOUNT;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.DEPENDENT_MAXIMUM_AGE;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab.PremiumSummary.RATE;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab.PremiumSummary.CONTRIBUTION;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.INCLUDE_DISABLED_DEPENDENTS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructure.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEndorsementCascading extends ScenarioTestEndorsementCascading implements GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37446", component = POLICY_GROUPBENEFITS)
    public void testEndorsementCascading() {
        LOGGER.info("Preconditions + TC01 Step 1");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiateAndFillUpToTab(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NJ")
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()), planDefinitionTab.getClass(), true);
        assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION)).hasValue("100").isDisabled();

        LOGGER.info("TC01 Step 2");
        planDefinitionTab.submitTab();
        groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), ClassificationManagementTab.class, PremiumSummaryTab.class);
        GroupVisionMasterPolicyContext.premiumSummaryTab.submitTab();
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Step 3");
        createDefaultGroupVisionCertificatePolicy();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Steps 4,5");
        MainPage.QuickSearch.search(masterPolicyNumber);
        EndorseAction.startEndorsementForPolicy(groupVisionMasterPolicy.getType(), groupVisionMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        planDefinitionTab.navigate();
        planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
        assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION)).hasValue("0").isDisabled();

        LOGGER.info("TC01 Step 6");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get(), Tab.doubleWaiter);
        Tab.buttonRate.click();
        Tab.buttonSaveAndExit.click();
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData().adjust(tdSpecific().getTestData("TestData_BillingGroups").resolveLinks()));

        LOGGER.info("TC01 Steps 7,10");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 2);

        LOGGER.info("TC01 Step 11");
        Tab.buttonTopCancel.click();
        groupVisionCertificatePolicy.policyInquiry().start();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(CONTRIBUTION.getName())).hasValue("100.00 %");
        DBService dbService = DBService.get();
        String dbQuery = "select participantContributionPct from Coverage where RiskItem_ID in " +
                "(select id from RiskItem where POLICYDETAIL_ID in " +
                "(select policyDetail_id from PolicySummary where policyNumber = ? and txType = 'endorsement'))";
        List<Map<String, String>> dbResponse = dbService.getRows(dbQuery, certificatePolicyNumber);
        assertSoftly(softly -> {
            softly.assertThat(dbResponse).hasSize(1);
            softly.assertThat(dbResponse.get(0).get("participantContributionPct")).isEqualTo("0.00");
        });

        shiftTimePlus1Day();

        LOGGER.info("TC02 Step 1");
        MainPage.QuickSearch.search(masterPolicyNumber);
        EndorseAction.startEndorsementForPolicy(groupVisionMasterPolicy.getType(), groupVisionMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        GroupVisionMasterPolicyContext.premiumSummaryTab.navigate();
        Tab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TC02 Step 2");
        GroupVisionMasterPolicyContext.premiumSummaryTab.openOverrideAndRatePremiumPopUp();
        DialogRateAndPremiumOverride.overrideReason.setValue("Competitive Adjustment");
        DialogRateAndPremiumOverride.overriddenTermRateField(0).setValue("$84.00");
        Page.dialogConfirmation.buttonOk.click(Tab.doubleWaiter);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("TC02 Step 3");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupVisionMasterPolicy.issue().perform(groupVisionMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT));

        LOGGER.info("TC02 Steps 4,7");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 3);

        LOGGER.info("TC02 Step 8");
        Tab.buttonTopCancel.click();
        groupVisionMasterPolicy.policyInquiry().start();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(RATE.getName())).hasValue("84.00");

        shiftTimePlus1Day();

        LOGGER.info("TC03 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupVisionMasterPolicy.createEndorsement(groupVisionMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_DependentMaximumAge"))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC03 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 4);

        LOGGER.info("TC03 Step 7");
        Tab.buttonTopCancel.click();
        groupVisionCertificatePolicy.policyInquiry().start();
        GroupVisionCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(DEPENDENT_MAXIMUM_AGE.getLabel()))).hasValue("24");

        shiftTimePlus1Day();

        LOGGER.info("TC04 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupVisionMasterPolicy.createEndorsement(groupVisionMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_IncludeDisabledDependents"))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC04 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 5);

        LOGGER.info("TC04 Step 7");
        Tab.buttonTopCancel.click();
        groupVisionCertificatePolicy.policyInquiry().start();
        GroupVisionCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(INCLUDE_DISABLED_DEPENDENTS.getLabel()))).hasValue("Yes");
    }

}
