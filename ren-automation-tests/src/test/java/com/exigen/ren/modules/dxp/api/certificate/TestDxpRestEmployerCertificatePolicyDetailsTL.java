/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.DXPConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.InsuredTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.dxp.api.certificate.scenario.TestDxpRestEmployerCertificatePolicyDetailsBase;
import org.testng.annotations.Test;

import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData.AddressInformationMetaData.ADDRESS_LINE_1;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData.AddressInformationMetaData.ZIP_POST_CODE;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData.GeneralInformationMetaData.FIRST_NAME;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData.GeneralInformationMetaData.GENDER;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.JOB_TITLE;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestEmployerCertificatePolicyDetailsTL extends TestDxpRestEmployerCertificatePolicyDetailsBase implements CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37983", component = CUSTOMER_REST)
    public void testDxpRestEmployerCertificatePolicyDetailsTL() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        customerNumNIC1.set(customerNumberAuthorize);

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();
        policyEffectiveDate.set(PolicySummaryPage.getEffectiveDate());

        termLifeInsuranceCertificatePolicy.createQuote(getDefaultCertificatePolicyDataGatherData());
        termLifeInsuranceCertificatePolicy.dataGather().start();

        insuredTab.navigate();
        firstName.set(InsuredTab.getGeneralInfoAsset().getAsset(FIRST_NAME).getValue());
        gender.set(InsuredTab.getGeneralInfoAsset().getAsset(GENDER).getValue());
        postCode.set(InsuredTab.getAddressInfoAsset().getAsset(ZIP_POST_CODE).getValue());
        addressLine1.set(InsuredTab.getAddressInfoAsset().getAsset(ADDRESS_LINE_1).getValue());
        jobTitle.set(InsuredTab.getRelationshipInfoAsset().getAsset(JOB_TITLE).getValue());

        coveragesTab.navigate();
        className.set(coveragesTab.getAssetList().getAsset(PlansTabMetaData.CLASS_NAME).getValue());

        Tab.buttonSaveAndExit.click();
        termLifeInsuranceCertificatePolicy.issue().perform(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        policyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());

        termLifeInsuranceCertificatePolicy.createEndorsement(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus1Month")
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement_Annual").resolveLinks())
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.EmployerCertificates.CONFIG_TERM_LIFE);
    }


}
