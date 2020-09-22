package com.exigen.ren.modules.policy.gb_di_std.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.DialogRateAndPremiumOverride;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestEndorsementCascading;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData.APPROVED_PERCENT;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData.CURRENT_EFFECTIVE_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab.PremiumSummary.PAYOR;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab.PremiumSummary.CONTRIBUTION;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab.PremiumSummary.RATE;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEndorsementCascading extends ScenarioTestEndorsementCascading implements ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39275", component = POLICY_GROUPBENEFITS)
    public void testEndorsementCascadingTC1To3() {
        LOGGER.info("Preconditions + TC01 Steps 1,2");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Step 3");
        createDefaultShortTermDisabilityCertificatePolicy();
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Steps 4-6");
        MainPage.QuickSearch.search(masterPolicyNumber);
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestDataEmpty").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_ParticipantContribution").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC01 Steps 7-9");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 2);

        LOGGER.info("TC01 Steps 10");
        Tab.buttonTopCancel.click();
        shortTermDisabilityCertificatePolicy.policyInquiry().start();
        ShortTermDisabilityCertificatePolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(PAYOR.getName(), "Sponsor").getCell(CONTRIBUTION.getName())).hasValue("70.00 %");
        assertThat(tablePremiumSummary.getRow(PAYOR.getName(), "Member").getCell(CONTRIBUTION.getName())).hasValue("30.00 %");

        shiftTimePlus1Day();

        LOGGER.info("TC02 Step 1");
        MainPage.QuickSearch.search(masterPolicyNumber);
        EndorseAction.startEndorsementForPolicy(shortTermDisabilityMasterPolicy.getType(), shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        ShortTermDisabilityMasterPolicyContext.premiumSummaryTab.navigate();
        Tab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("TC02 Step 2");
        ShortTermDisabilityMasterPolicyContext.premiumSummaryTab.openOverrideAndRatePremiumPopUp();
        DialogRateAndPremiumOverride.overrideReason.setValue("Competitive Adjustment");
        DialogRateAndPremiumOverride.overriddenTermRateField(0).setValue("$89.00");
        Page.dialogConfirmation.buttonOk.click(Tab.doubleWaiter);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("TC02 Step 3");
        PolicySummaryPage.buttonPendedEndorsement.click();
        shortTermDisabilityMasterPolicy.issue().perform(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT));

        LOGGER.info("TC02 Steps 4,5");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 3);

        LOGGER.info("TC02 Step 6");
        Tab.buttonTopCancel.click();
        shortTermDisabilityMasterPolicy.policyInquiry().start();
        ShortTermDisabilityMasterPolicyContext.premiumSummaryTab.navigate();
        assertThat(tablePremiumSummary.getRow(1).getCell(RATE.getName())).hasValue("89.00");

        shiftTimePlus1Day();

        LOGGER.info("TC03 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestDataEmpty").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_EarningDefinition").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC03 Steps 3,4");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 4);

        LOGGER.info("TC03 Step 5");
        Tab.buttonTopCancel.click();
        shortTermDisabilityCertificatePolicy.policyInquiry().start();
        ShortTermDisabilityCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(EARNING_DEFINITION.getLabel()))).hasValue("Salary & Bonus (12 mo)");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39275", component = POLICY_GROUPBENEFITS)
    public void testEndorsementCascadingTC4To7() {
        LOGGER.info("Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultShortTermDisabilityCertificatePolicy();
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC04 Steps 1,2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestDataEmpty").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_MinimumWeeklyBenefit").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC04 Steps 3,4");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 2);

        LOGGER.info("TC04 Step 5");
        Tab.buttonTopCancel.click();
        shortTermDisabilityCertificatePolicy.policyInquiry().start();
        ShortTermDisabilityCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(MINIMUM_WEEKLY_BENEFIT_AMOUNT.getLabel()))).isAbsent();

        shiftTimePlus1Day();

        LOGGER.info("TC05 Steps 1-3");
        MainPage.QuickSearch.search(masterPolicyNumber);
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestDataEmpty").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_MaximumWeeklyBenefit").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 3);

        LOGGER.info("TC05 Step 4");
        Tab.buttonTopCancel.click();
        shortTermDisabilityCertificatePolicy.policyInquiry().start();
        ShortTermDisabilityCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(MAXIMUM_WEEKLY_BENEFIT_AMOUNT.getLabel()))).hasValue("$1,500.00");

        shiftTimePlus1Day();

        LOGGER.info("TC06 Steps 1-3");
        MainPage.QuickSearch.search(masterPolicyNumber);
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestDataEmpty").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_BenefitPercentage").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 4);

        LOGGER.info("TC06 Step 4");
        Tab.buttonTopCancel.click();
        shortTermDisabilityCertificatePolicy.policyInquiry().start();
        ShortTermDisabilityCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(BENEFIT_PERCENTAGE.getLabel()))).hasValue("66.67%");

        shiftTimePlus1Day();

        LOGGER.info("TC07 Step 1");
        MainPage.QuickSearch.search(masterPolicyNumber);
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks())
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_TYPE.getLabel()), "Specified Weekly Benefit Amount - Single Value")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), WEEKLY_BENEFIT_AMOUNT.getLabel()), "$1000"));
        String masterPolicyNumber2 = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC07 Step 2");
        shortTermDisabilityCertificatePolicy.initiate(shortTermDisabilityCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY));
        shortTermDisabilityCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDCertificatePolicyDataGatherData()
                .mask(TestData.makeKeyPath(CoveragesTab.class.getSimpleName(), APPROVED_PERCENT.getLabel())), coveragesTab.getClass(), true);
        assertThat(coveragesTab.getAssetList().getAsset(CURRENT_EFFECTIVE_AMOUNT)).hasValue("$1,000.00");
        coveragesTab.submitTab();
        shortTermDisabilityCertificatePolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class).fillTab(shortTermDisabilityCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY));
        ShortTermDisabilityMasterPolicyContext.premiumSummaryTab.submitTab();
        shortTermDisabilityCertificatePolicy.issue().perform(shortTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY));
        String certificatePolicyNumber2 = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC07 Step 3");
        MainPage.QuickSearch.search(masterPolicyNumber2);
        shortTermDisabilityMasterPolicy.createEndorsement(shortTermDisabilityMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestDataEmpty").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_WeeklyBenefitAmount").resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC07 Steps 4-5");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber2, certificatePolicyNumber2, 2);

        LOGGER.info("TC07 Step 6");
        Tab.buttonTopCancel.click();
        shortTermDisabilityCertificatePolicy.policyInquiry().start();
        ShortTermDisabilityCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(CURRENT_EFFECTIVE_AMOUNT.getLabel()))).hasValue("$750.00");
    }

}
