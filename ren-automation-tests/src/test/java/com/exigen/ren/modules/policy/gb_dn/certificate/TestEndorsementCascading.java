package com.exigen.ren.modules.policy.gb_dn.certificate;

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
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestEndorsementCascading;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionIssueActionTabMetaData.DEPENDENT_MAXIMUM_AGE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.INCLUDE_DISABLED_DEPENDENTS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEndorsementCascading extends ScenarioTestEndorsementCascading implements GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39193", component = POLICY_GROUPBENEFITS)
    public void testEndorsementCascadingTC1To4() {
        LOGGER.info("Preconditions + TC01 Step 1");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NV")
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PPO_EPO_PLAN.getLabel()), "Yes")
                        .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary"),
                PlanDefinitionTab.class, true);
        assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT)).hasValue("100").isDisabled();

        LOGGER.info("TC01 Step 2");
        planDefinitionTab.submitTab();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), classificationManagementMpTab.getClass(), GroupDentalMasterPolicyContext.premiumSummaryTab.getClass(), true);
        GroupDentalMasterPolicyContext.premiumSummaryTab.submitTab();
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Step 3");
        createDefaultGroupDentalCertificatePolicy();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Steps 4,5");
        MainPage.QuickSearch.search(masterPolicyNumber);
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        planDefinitionTab.navigate();
        planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(CONTRIBUTION_TYPE).setValue("Non-contributory");
        assertThat(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE).getAsset(PARTICIPANT_CONTRIBUTION_PCT)).hasValue("0").isDisabled();

        LOGGER.info("TC01 Step 6");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get(), Tab.doubleWaiter);
        Tab.buttonRate.click();
        Tab.buttonSaveAndExit.click();
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_BillingGroups").resolveLinks()));

        LOGGER.info("TC01 Steps 7,10");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 2);

        LOGGER.info("TC01 Step 11");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.CONTRIBUTION.getName())).hasValue("100.00 %");
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
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        GroupDentalMasterPolicyContext.premiumSummaryTab.navigate();
        Tab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TC02 Step 2");
        GroupDentalMasterPolicyContext.premiumSummaryTab.openOverrideAndRatePremiumPopUp();
        DialogRateAndPremiumOverride.overrideReason.setValue("Competitive Adjustment");
        DialogRateAndPremiumOverride.overriddenTermRateField(0).setValue("$87.00");
        Page.dialogConfirmation.buttonOk.click(Tab.doubleWaiter);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("TC02 Step 3");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT));

        LOGGER.info("TC02 Steps 4,7");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 3);

        LOGGER.info("TC02 Step 8");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName())).hasValue("87.00");

        shiftTimePlus1Day();

        LOGGER.info("TC03 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.createEndorsement(groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(groupDentalMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_DependentMaximumAge"))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC03 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 4);

        LOGGER.info("TC03 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(DEPENDENT_MAXIMUM_AGE.getLabel()))).hasValue("26");

        shiftTimePlus1Day();

        LOGGER.info("TC04 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.createEndorsement(groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_IncludeDisabledDependents"))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC04 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 5);

        LOGGER.info("TC04 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(INCLUDE_DISABLED_DEPENDENTS.getLabel()))).hasValue("Yes");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39193", component = POLICY_GROUPBENEFITS)
    public void testEndorsementCascadingTC5To9() {
        LOGGER.info("Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NV")
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PPO_EPO_PLAN.getLabel()), "Yes")
                        .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary"));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultGroupDentalCertificatePolicy();
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        String rate = tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName()).getValue();

        LOGGER.info("TC05 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.endorse().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT)
                .adjust(tdSpecific().getTestData("TestData_OverrideProcedureCode")));

        LOGGER.info("TC05 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 2);

        LOGGER.info("TC05 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName())).hasValue(rate);

        shiftTimePlus1Day();

        LOGGER.info("TC06 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.endorse().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT)
                .adjust(tdSpecific().getTestData("TestData_ProcedureCode")));

        LOGGER.info("TC06 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 3);

        LOGGER.info("TC06 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName())).hasValue(rate);

        shiftTimePlus1Day();

        LOGGER.info("TC07 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.endorse().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT)
                .adjust(tdSpecific().getTestData("TestData_OverrideOutOfNetwork")));

        LOGGER.info("TC07 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 4);

        LOGGER.info("TC07 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName())).hasValue(rate);

        shiftTimePlus1Day();

        LOGGER.info("TC08 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.endorse().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT)
                .adjust(tdSpecific().getTestData("TestData_OverrideInNetwork")));

        LOGGER.info("TC08 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 5);

        LOGGER.info("TC08 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName())).hasValue(rate);

        shiftTimePlus1Day();

        LOGGER.info("TC09 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.endorse().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(groupDentalMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT)
                .adjust(tdSpecific().getTestData("TestData_CoInsuranceEPO")));

        LOGGER.info("TC09 Steps 3,6");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 6);

        LOGGER.info("TC09 Step 7");
        Tab.buttonTopCancel.click();
        groupDentalCertificatePolicy.policyInquiry().start();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(PremiumSummaryTab.PremiumSummary.RATE.getName())).hasValue(rate);
    }

}
