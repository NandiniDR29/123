/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.DXPConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.dxp.api.certificate.scenario.TestDxpRestEmployerCertificatePolicyDetailsBase;
import org.testng.annotations.Test;

import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.GENERAL_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestEmployerCertificatePolicyDetailsAC extends TestDxpRestEmployerCertificatePolicyDetailsBase implements CaseProfileContext, GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35974", component = CUSTOMER_REST)
    public void testDxpRestEmployerCertificatePolicyDetailsAC() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        customerNumNIC1.set(customerNumberAuthorize);
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        policyEffectiveDate.set(PolicySummaryPage.getEffectiveDate());

        groupAccidentCertificatePolicy.createQuote(getDefaultACCertificatePolicyDataGatherData());
        groupAccidentCertificatePolicy.dataGather().start();
        insuredTab.navigate();
        firstName.set(insuredTab.getAssetList().getAsset(InsuredTabMetaData.FIRST_NAME).getValue());
        gender.set(insuredTab.getAssetList().getAsset(GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.GENDER).getValue());
        postCode.set(insuredTab.getAssetList().getAsset(InsuredTabMetaData.ZIP_POST_CODE).getValue());
        addressLine1.set(insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_LINE_1).getValue());
        jobTitle.set(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(InsuredTabMetaData.RelationshipInformationMetaData.JOB_TITLE).getValue());

        coveragesTab.navigate();
        className.set(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.CLASS_NAME).getValue());

        Tab.buttonSaveAndExit.click();
        groupAccidentCertificatePolicy.issue().perform(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        policyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());

        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus1Month")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.EmployerCertificates.CONFIG_ACCIDENT);
    }

}
