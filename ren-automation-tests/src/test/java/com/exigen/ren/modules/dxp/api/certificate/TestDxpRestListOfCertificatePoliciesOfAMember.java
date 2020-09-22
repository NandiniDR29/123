/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
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
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.CertificatesModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestListOfCertificatePoliciesOfAMember extends RestBaseTest implements CaseProfileContext,
        GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext,
        GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext,
        LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15500", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberGA() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        verifyResponse();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15500", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberDN() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();
        verifyResponse();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15500", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberGV() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        createDefaultGroupVisionMasterPolicy();
        createDefaultGroupVisionCertificatePolicy();
        verifyResponse();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15500", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberTL() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();
        verifyResponse();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15500", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberLTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        createDefaultLongTermDisabilityMasterPolicy();
        createDefaultLongTermDisabilityCertificatePolicy();
        verifyResponse();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15500", component = CUSTOMER_REST)
    public void testDxpRestListOfCertificatePoliciesOfAMemberSTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        createDefaultShortTermDisabilityMasterPolicy();
        createDefaultShortTermDisabilityCertificatePolicy();
        verifyResponse();
    }

    private void verifyResponse(){

        navigateToCustomer();
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            ResponseContainer<List<CertificatesModel>> response = dxpRestService.getMemberCertificates(customerNum);
            softly.assertThat(response.getResponse()).hasStatus(200);

            CertificatesModel model = response.getModel().get(0);

            softly.assertThat(model.getPolicyNumber()).isNotNull().isNotEmpty();
            softly.assertThat(model.getTransactionEffectiveDate()).isNotNull().isNotEmpty();
            softly.assertThat(model.getCurrentRevisionInd()).isNotNull();
            softly.assertThat(model.getRevisionNumber()).isNotNull().isNotEmpty();
            softly.assertThat(model.getPolicyStatusCd()).isNotNull().isNotEmpty();
            softly.assertThat(model.getTimedPolicyStatusCd()).isNotNull().isNotEmpty();
            softly.assertThat(model.getTransactionTypeCd()).isNotNull().isNotEmpty();
            softly.assertThat(model.getEffectiveDate()).isNotNull().isNotEmpty();
            softly.assertThat(model.getExpirationDate()).isNotNull().isNotEmpty();
            softly.assertThat(model.getAnniversaryDate()).isNotNull().isNotEmpty();
            softly.assertThat(model.getRenewalDate()).isNotNull().isNotEmpty();
            softly.assertThat(model.getCancellationDate()).isNull();
            softly.assertThat(model.getProductCode()).isNotNull().isNotEmpty();
            softly.assertThat(model.getProductVersion()).isNotNull().isNotEmpty();
            softly.assertThat(model.getMasterPolicyNumber()).isNotNull().isNotEmpty();
            softly.assertThat(model.getMasterPolicyCustomerNumber()).isNotNull().isNotEmpty();
            softly.assertThat(model.getMasterPolicyCustomerName()).isNotNull().isNotEmpty();
            softly.assertThat(model.getMasterPolicyEffectiveDate()).isNotNull().isNotEmpty();
            softly.assertThat(model.getIssueState()).isNotNull().isNotEmpty();
            softly.assertThat(model.getPlanCd()).isNotNull().isNotEmpty();
        });
    }
}
