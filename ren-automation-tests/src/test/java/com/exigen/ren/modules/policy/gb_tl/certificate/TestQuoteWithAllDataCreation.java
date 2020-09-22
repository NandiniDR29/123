/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.*;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UPLOAD_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteWithAllDataCreation extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-17485", component = POLICY_GROUPBENEFITS)
    public void testQuoteWithAllDataCreation() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", UPLOAD_FILE.getLabel(), FILE_UPLOAD.getLabel()), "$<file:REN_Rating_Census_File_All.xlsx>"),
                termLifeInsuranceMasterPolicy.getType());
        LOGGER.info("TEST: Master Policy Creation");
        termLifeInsuranceMasterPolicy.createPolicy(tdSpecific().getTestData("TestData_Master")
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        String masterPolicyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        LOGGER.info("TEST: Certificate Policy Creation");

        TestData tdADD = tdSpecific().getTestData("CoveragesTabAddCoverageADD");
        TestData tdDEP_BTL = tdSpecific().getTestData("CoveragesTabAddCoverageDEP_BTL");
        TestData tdVOL_BTL = tdSpecific().getTestData("CoveragesTabAddCoverageVOL_BTL");
        TestData tdDEP_VOL_BTL = tdSpecific().getTestData("CoveragesTabAddCoverageDEP_VOL_BTL");
        TestData tdVOL_ADD = tdSpecific().getTestData("CoveragesTabAddCoverageVOL_ADD");
        TestData tdDEP_VOL_ADD = tdSpecific().getTestData("CoveragesTabAddCoverageDEP_VOL_ADD");
        TestData tdSP_VOL_BTL = tdSpecific().getTestData("CoveragesTabAddCoverageSP_VOL_BTL");

        TestData tdBasicLife = tdSpecific().getTestData("CoveragesTab").adjust(CoveragesTabMetaData.PLAN.getLabel(), BASIC_LIFE_PLAN);
        ImmutableList<TestData> tdBasicLifeCoverageList = ImmutableList.of(tdBasicLife, tdADD, tdDEP_BTL);

        TestData tdBasicLifeVoluntary = tdSpecific().getTestData("CoveragesTab").adjust(CoveragesTabMetaData.PLAN.getLabel(), BASIC_LIFE_PLAN_PLUS_VOLUNTARY)
                .adjust(CoveragesTabMetaData.COVERAGE_NAME.getLabel(), BTL);
        ImmutableList<TestData> tdBasicLifeVoluntaryCoverageList = ImmutableList.of(tdBasicLifeVoluntary, tdADD, tdDEP_BTL, tdVOL_BTL, tdDEP_VOL_BTL, tdVOL_ADD, tdSP_VOL_BTL, tdDEP_VOL_ADD);

        TestData tdVoluntaryLife = tdSpecific().getTestData("CoveragesTabAddPlanVOL_BTL");
        ImmutableList<TestData> tdVoluntaryLifeCoverageList = ImmutableList.of(tdVoluntaryLife, tdDEP_VOL_BTL, tdVOL_ADD,  tdSP_VOL_BTL, tdDEP_VOL_ADD);

        TestData tdVolunteerFireFighters = tdSpecific().getTestData("CoveragesTab").adjust(CoveragesTabMetaData.PLAN.getLabel(), VOLUNTEER_FIRE_FIGHTERS)
                .adjust(CoveragesTabMetaData.COVERAGE_NAME.getLabel(), BTL);
        ImmutableList<TestData> tdVolunteerFireFightersCoverageList = ImmutableList.of(tdVolunteerFireFighters, tdADD);

        ImmutableList.of(tdBasicLifeCoverageList, tdBasicLifeVoluntaryCoverageList, tdVoluntaryLifeCoverageList, tdVolunteerFireFightersCoverageList).forEach(coveragePlanList -> {
            termLifeInsuranceCertificatePolicy.createPolicyViaUI(tdSpecific().getTestData("TestData_Certificate")
                    .adjust(TestData.makeKeyPath(coveragesTab.getClass().getSimpleName()), coveragePlanList)
                    .adjust(tdSpecific().getTestData("TestData_Issue_Certificate")));
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
            MainPage.QuickSearch.search(masterPolicyNum);
        });
    }
}