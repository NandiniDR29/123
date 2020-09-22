/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_ac.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.ENHANCED_10_UNITS;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.VOLUNTARY_10_UNITS;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UPLOAD_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteWithAllDataCreation extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-17485", component = POLICY_GROUPBENEFITS)
    public void testQuoteWithAllDataCreation() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", UPLOAD_FILE.getLabel(), FILE_UPLOAD.getLabel()), "$<file:REN_Rating_Census_File_All.xlsx>"),
                groupAccidentMasterPolicy.getType());

        LOGGER.info("TEST: Master Policy Creation");
        groupAccidentMasterPolicy.createPolicy(tdSpecific().getTestData("TestData_Master")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        String masterPolicyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        LOGGER.info("TEST: Certificate Policy Creation");

        groupAccidentCertificatePolicy.createPolicyViaUI(tdSpecific().getTestData("TestData_Certificate_Two_Coverages")
                .adjust(tdSpecific().getTestData("TestData_Issue_Certificate")));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        MainPage.QuickSearch.search(masterPolicyNum);

        ImmutableList.of(ENHANCED_10_UNITS, VOLUNTARY_10_UNITS).forEach(plan -> {
            groupAccidentCertificatePolicy.createPolicyViaUI(tdSpecific().getTestData("TestData_Certificate")
                    .adjust(TestData.makeKeyPath(coveragesTab.getClass().getSimpleName(), CoveragesTabMetaData.PLAN.getLabel()), plan)
                    .adjust(tdSpecific().getTestData("TestData_Issue_Certificate")));
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
            MainPage.QuickSearch.search(masterPolicyNum);
        });
    }
}