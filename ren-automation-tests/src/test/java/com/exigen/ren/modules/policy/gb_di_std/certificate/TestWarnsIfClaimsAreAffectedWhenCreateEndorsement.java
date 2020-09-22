package com.exigen.ren.modules.policy.gb_di_std.certificate;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.EventInformationLossEventTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.IPolicy;
import com.exigen.ren.main.modules.policy.common.metadata.common.CancellationActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.CancellationActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_CANCELLED;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimContext.disabilityClaim;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimContext.eventInformationLossEventTab;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestWarnsIfClaimsAreAffectedWhenCreateEndorsement extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-30759", component = POLICY_GROUPBENEFITS)
    public void testCheckWarningsIfClaimsAffectedDuringEndorse() {
        LOGGER.info("REN-30759 TC#1 Precondition");
        ImmutableList<String> policies = preconditionSteps();
        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("REN-30759 TC#2 Scenario for Master Policy (endorsement)");
        endorsementCommonSteps(policies.get(0), "Acquisition or Merger", shortTermDisabilityMasterPolicy);

        LOGGER.info("REN-30759 TC#2 Scenario for Certificate Policy (endorsement)");
        endorsementCommonSteps(policies.get(1), "Marriage", shortTermDisabilityCertificatePolicy);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-30769", "REN-30770"}, component = POLICY_GROUPBENEFITS)
    public void testCheckWarningsIfClaimsAffectedDuringCancellationMasterCertPolicy() {
        LOGGER.info("REN-30769 REN-30770 TC#2 Precondition");
        ImmutableList<String> policies = preconditionSteps();
        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(eventInformationLossEventTab.getMetaKey(), EventInformationLossEventTabMetaData.ICD_CODE.getLabel()), "005D0ZZ"));
        disabilityClaim.claimSubmit().perform(new SimpleDataProvider());
        disabilityClaim.claimOpen().perform();
        disabilityClaim.calculateSingleBenefitAmount().perform(disabilityClaim.getSTDTestData().getTestData("CalculateASingleBenefitAmount", "TestData_STD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.INDEMNITY_RESERVE.getLabel()), "1000"), 1);
        disabilityClaim.postRecovery().perform(disabilityClaim.getSTDTestData().getTestData("ClaimPayment", "TestData_PostRecovery"));

        LOGGER.info("REN-30770 TC#2 Cancellation action Certificate Policy");
        cancelCommonSteps(policies.get(1), "Term", shortTermDisabilityCertificatePolicy);

        LOGGER.info("REN-30769 TC#2 Select Cancellation action Master Policy");
        cancelCommonSteps(policies.get(0), "Non Payment of Premium", shortTermDisabilityMasterPolicy);
    }

    private void cancelCommonSteps(String policyNumber, String endorsementReason, IPolicy policy){
        MainPage.QuickSearch.search(policyNumber);
        LOGGER.info("REN-30769 REN-30770 TC#2 STEP#1 Select Cancellation action Set Cancel Date = <today date>");
        policy.cancel().start();
        TextBox cancelDateTextBox = policy.cancel().getWorkspace().getTab(CancellationActionTab.class).getAssetList().getAsset(CancellationActionTabMetaData.CANCEL_DATE);
        cancelDateTextBox.setValue(DateTimeUtils.getCurrentDateTime().format(MM_DD_YYYY));
        CustomAssertions.assertThat(cancelDateTextBox).hasWarningWithText("Please note: Proceeding with this cancellation may impact existing claims");

        LOGGER.info("REN-30769 REN-30770 TC#2 STEP#5 Select Cancellation action Set Cancel Date = <today date - 1 month>");
        cancelDateTextBox.setValue(DateTimeUtils.getCurrentDateTime().minusMonths(1).format(MM_DD_YYYY));
        CustomAssertions.assertThat(cancelDateTextBox).hasWarningWithText("Please note: Proceeding with this cancellation may impact existing claims");

        LOGGER.info("REN-30769 REN-30770 TC#2 STEP#6 Complete Cancellation action And check Policy Status");
        policy.cancel().getWorkspace().getTab(CancellationActionTab.class).getAssetList().getAsset(CancellationActionTabMetaData.CANCELLATION_REASON).setValue(endorsementReason);
        policy.cancel().submit();
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_CANCELLED);
    }

    private void endorsementCommonSteps(String policyNumber, String endorsementReason, IPolicy policy){
        MainPage.QuickSearch.search(policyNumber);
        LOGGER.info(String.format("REN-30759 TC#2 STEP#1 Endorse Policy - %s, Set Endorsement Date = <today date>", policyNumber));
        policy.endorse().start();
        TextBox endorsementDateTextBox = policy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_DATE);
        endorsementDateTextBox.setValue(DateTimeUtils.getCurrentDateTime().format(MM_DD_YYYY));
        CustomAssertions.assertThat(endorsementDateTextBox).hasWarningWithText("Please note: Proceeding with this endorsement may impact existing claims");

        LOGGER.info(String.format("REN-30759 TC#2 STEP#5 Endorse Policy - %s, Set Endorsement Date = <today date - 1 month>", policyNumber));
        endorsementDateTextBox.setValue(DateTimeUtils.getCurrentDateTime().minusMonths(1).format(MM_DD_YYYY));
        CustomAssertions.assertThat(policy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_DATE)).hasWarningWithText("Please note: Proceeding with this endorsement may impact existing claims");

        LOGGER.info(String.format("REN-30759 TC#2 STEP#5 Complete Endorse Policy - %s", policyNumber));
        policy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_REASON).setValue(endorsementReason);
        policy.endorse().submit();
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyNumber).valueContains(policyNumber);
    }

    private ImmutableList<String> preconditionSteps(){
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(shortTermDisabilityMasterPolicy.getType())
                .adjust(makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().minusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(fileIntakeManagementTab.getMetaKey() + "[0]", FileIntakeManagementTabMetaData.EFFECTIVE_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().minusMonths(1).format(MM_DD_YYYY)));
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().minusMonths(1).format(MM_DD_YYYY)));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        shortTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultSTDCertificatePolicyDataGatherData()
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().minusMonths(1).format(MM_DD_YYYY)));
        String certPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        return ImmutableList.of(masterPolicyNumber, certPolicyNumber);
    }
}
