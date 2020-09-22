/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.BillingGenerateBillModel;
import com.exigen.ren.rest.dxp.model.DXPBillingAccountsDocumentGenerationStatus;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpRestCreateFinalBill extends RestBaseTest implements CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, GroupAccidentMasterPolicyContext,
        GroupAccidentCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext,
        LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext,
        StatutoryDisabilityInsuranceMasterPolicyContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillDN() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        createDefaultGroupDentalMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultGroupDentalCertificatePolicy();
        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillAC() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultGroupAccidentCertificatePolicy();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillLT() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultTermLifeInsuranceMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultTermLifeInsuranceCertificatePolicy();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillVS() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        createDefaultGroupVisionMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultGroupVisionCertificatePolicy();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillSTD() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        createDefaultShortTermDisabilityMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultShortTermDisabilityCertificatePolicy();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillLTD() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        createDefaultLongTermDisabilityMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultLongTermDisabilityCertificatePolicy();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillST() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        createDefaultStatutoryDisabilityInsuranceMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34000", component = CUSTOMER_REST)
    public void testDxpRestCreateFinalBillPFL() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());

        createDefaultPaidFamilyLeaveMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        commonSteps(customerNumberAuthorize, policyNum);
    }

    private void commonSteps(String customerNumNIC1, String policyNum) {

        String accountNum = GroupBenefitsBillingBaseTest.getBillingAccountNumber(policyNum);
        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<BillingGenerateBillModel> response =
                dxpRestService.postEmployerBillingGenerate(customerNumNIC1, accountNum);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getDocgenTicket()).isNotNull().isNotEmpty();
            softly.assertThat(response.getModel().getInvoiceNumber()).isNotNull().isNotEmpty();
        });
        String docgenTicket = response.getModel().getDocgenTicket();

        LOGGER.info("---=={Step 2}==---");
        waitEmployerBillingGenerationStatusSuccess(customerNumNIC1, docgenTicket);
        ResponseContainer<DXPBillingAccountsDocumentGenerationStatus> response2 =
                dxpRestService.getEmployerBillingGenerationStatus(customerNumNIC1, docgenTicket);
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(response2.getModel().getDocumentId()).isNotNull().isNotEmpty();
            softly.assertThat(response2.getModel().getDocumentName()).isNotNull().isNotEmpty();
        });
    }

    private void waitEmployerBillingGenerationStatusSuccess(String customerNum, String docgenTicket) {
        try {
            RetryService.run(predicate -> dxpRestService.getEmployerBillingGenerationStatus(customerNum, docgenTicket).getModel().getStatusCd().equals("SUCCESS"),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Status not SUCCESS", e);
        }
    }
}