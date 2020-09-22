package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestEndorsementCascading;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.DEP_BTL;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleChildMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEndorsementCascadingDBL extends ScenarioTestEndorsementCascading implements TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39880", component = POLICY_GROUPBENEFITS)
    public void testEndorsementCascadingDBL() {
        LOGGER.info("Preconditions + TC01 Steps 1,2");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Step 3");
        termLifeInsuranceCertificatePolicy.createPolicyViaUI(termLifeInsuranceCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_CertificatePolicy").resolveLinks())
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String certificatePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC01 Steps 4,5");
        MainPage.QuickSearch.search(masterPolicyNumber);
        termLifeInsuranceMasterPolicy.createEndorsement(termLifeInsuranceMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_Endorsement1"))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC01 Steps 6,7");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 2);

        LOGGER.info("TC01 Steps 8");
        Tab.buttonTopCancel.click();
        termLifeInsuranceCertificatePolicy.policyInquiry().start();
        TermLifeInsuranceCertificatePolicyContext.coveragesTab.navigate();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(SIX_MONTHS_TO_MAX_AGE.getLabel()))).isAbsent();

        shiftTimePlus1Day();

        LOGGER.info("TC02 Step 1");
        MainPage.QuickSearch.search(masterPolicyNumber);
        termLifeInsuranceMasterPolicy.createEndorsement(termLifeInsuranceMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_Endorsement2"))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC02 Steps 2,3");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 3);

        LOGGER.info("TC02 Step 4");
        Tab.buttonTopCancel.click();
        termLifeInsuranceCertificatePolicy.policyInquiry().start();
        TermLifeInsuranceCertificatePolicyContext.coveragesTab.navigate();
        CoveragesTab.openAddedPlan(DEP_BTL);
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(LIVE_BIRTH_TO_FOURTEEN_DAYS.getLabel()))).hasValue("$1,000.00");

        shiftTimePlus1Day();

        LOGGER.info("TC03 Step 1");
        MainPage.QuickSearch.search(masterPolicyNumber);
        termLifeInsuranceMasterPolicy.createEndorsement(termLifeInsuranceMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_Endorsement3"))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        LOGGER.info("TC03 Steps 2,3");
        executeJobsAndAssertTransactionHistory(masterPolicyNumber, certificatePolicyNumber, 4);

        LOGGER.info("TC03 Step 4");
        Tab.buttonTopCancel.click();
        termLifeInsuranceCertificatePolicy.policyInquiry().start();
        TermLifeInsuranceCertificatePolicyContext.coveragesTab.navigate();
        CoveragesTab.openAddedPlan(DEP_BTL);
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(FIFTEEN_DAYS_TO_SIX_MONTH.getLabel()))).hasValue("$1,000.00");
    }

}
