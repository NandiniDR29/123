/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.DXPConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.CertificatesModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpMemberViewPlanBenefits extends RestBaseTest implements CaseProfileContext,
        GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext,
        GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext,
        LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35106", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberSTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        createDefaultShortTermDisabilityMasterPolicy();
        createDefaultShortTermDisabilityCertificatePolicy();
        String policyNumCP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        navigateToCustomer();
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        MainPage.QuickSearch.search(policyNumCP1);
        shortTermDisabilityCertificatePolicy.createEndorsement(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus1Months")
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.MemberCertificates.CONFIG_STD, policyNumCP1, customerNumIC1, policyEffectiveDate);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35096", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberLTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        createDefaultLongTermDisabilityMasterPolicy();
        createDefaultLongTermDisabilityCertificatePolicy();
        String policyNumCP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        navigateToCustomer();
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        MainPage.QuickSearch.search(policyNumCP1);
        longTermDisabilityCertificatePolicy.createEndorsement(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus3Months")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.MemberCertificates.CONFIG_LTD, policyNumCP1, customerNumIC1, policyEffectiveDate);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36782", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberAC() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        String policyNumCP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        navigateToCustomer();
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        MainPage.QuickSearch.search(policyNumCP1);
        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus1Month")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.MemberCertificates.CONFIG_ACCIDENT, policyNumCP1, customerNumIC1, policyEffectiveDate);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36130", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberVS() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        createDefaultGroupVisionMasterPolicy();
        createDefaultGroupVisionCertificatePolicy();
        String policyNumCP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        navigateToCustomer();
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        MainPage.QuickSearch.search(policyNumCP1);
        groupVisionCertificatePolicy.createEndorsement(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus1Month")
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.MemberCertificates.CONFIG_VISION, policyNumCP1, customerNumIC1, policyEffectiveDate);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37371", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberTL() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();
        String policyNumCP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        navigateToCustomer();
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        MainPage.QuickSearch.search(policyNumCP1);
        termLifeInsuranceCertificatePolicy.createEndorsement(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestData_Plus1Month")
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));

        verifyResponse(DXPConstants.MemberCertificates.CONFIG_LIFE, policyNumCP1, customerNumIC1, policyEffectiveDate);
    }

    private void verifyResponse(DXPConstants.MemberCertificates product, String policyNumCP1, String customerNumIC1, LocalDateTime policyEffectiveDate){
        createDefaultIndividualCustomer();
        String customerNumIC2 = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            ResponseContainer<CertificatesModel> response = dxpRestService.getMemberCertificatesByProduct(product, customerNumIC1, policyNumCP1, null);
            softly.assertThat(response.getResponse()).hasStatus(200);
            CertificatesModel model = response.getModel();
            softly.assertThat(model.getPolicyNumber()).isNotNull().isNotEmpty();
            softly.assertThat(model.getTransactionTypeCd()).isEqualTo("endorsement");
            softly.assertThat(model.getRevisionNumber()).isEqualTo("2");

            LOGGER.info("---=={Step 2}==---");
            ResponseContainer<CertificatesModel> response2 = dxpRestService.getMemberCertificatesByProduct(product, customerNumIC1, policyNumCP1, policyEffectiveDate.toLocalDate().toString());
            softly.assertThat(response2.getResponse()).hasStatus(200);
            CertificatesModel model2 = response2.getModel();
            softly.assertThat(model2.getPolicyNumber()).isNotNull().isNotEmpty();
            softly.assertThat(model2.getTransactionTypeCd()).isEqualTo("policy");
            softly.assertThat(model2.getRevisionNumber()).isEqualTo("1");

            LOGGER.info("---=={Step 3}==---");
            ResponseContainer<CertificatesModel> response3 = dxpRestService.getMemberCertificatesByProduct(product, customerNumIC2, policyNumCP1, null);
            assertThat(response3.getResponse().getStatus()).isEqualTo(403);
            assertThat(response3.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            assertThat(response3.getModel().getMessage()).isEqualTo("Requested user has no access to policy");

        });
    }
}
