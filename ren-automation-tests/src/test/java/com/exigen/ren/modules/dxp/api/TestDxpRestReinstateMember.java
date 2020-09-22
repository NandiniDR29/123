/* Copyright © 2020 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
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
import com.exigen.ren.rest.model.RestError;
import org.testng.annotations.Test;

import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.DXPConstants.CertificateCancellationReason;
import static com.exigen.ren.main.enums.DXPConstants.CertificateCancellationReason.*;
import static com.exigen.ren.main.enums.DXPConstants.CertificateRescindCancellationReason;
import static com.exigen.ren.main.enums.DXPConstants.CertificateRescindCancellationReason.*;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpRestReinstateMember extends RestBaseTest implements CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, GroupAccidentMasterPolicyContext,
        GroupAccidentCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext,
        LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33159", component = CUSTOMER_REST)
    public void testDxpRestTerminateMemberDN() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        commonSteps(CONFIG_DENTAL, RESCIND_CANCELLATION_CONFIG_DENTAL, customerNumNIC1, customerNumberAuthorize);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33159", component = CUSTOMER_REST)
    public void testDxpRestTerminateMemberAC() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();

        commonSteps(CONFIG_ACCIDENT, RESCIND_CANCELLATION_CONFIG_ACCIDENT, customerNumNIC1, customerNumberAuthorize);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33159", component = CUSTOMER_REST)
    public void testDxpRestTerminateMemberLT() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();

        commonSteps(CONFIG_LIFE, RESCIND_CANCELLATION_CONFIG_LIFE, customerNumNIC1, customerNumberAuthorize);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33159", component = CUSTOMER_REST)
    public void testDxpRestTerminateMemberVN() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        createDefaultGroupVisionMasterPolicy();
        createDefaultGroupVisionCertificatePolicy();

        commonSteps(CONFIG_VISION, RESCIND_CANCELLATION_CONFIG_VISION, customerNumNIC1, customerNumberAuthorize);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33159", component = CUSTOMER_REST)
    public void testDxpRestTerminateMemberSTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        createDefaultShortTermDisabilityMasterPolicy();
        createDefaultShortTermDisabilityCertificatePolicy();

        commonSteps(CONFIG_STD, RESCIND_CANCELLATION_CONFIG_STD, customerNumNIC1, customerNumberAuthorize);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33159", component = CUSTOMER_REST)
    public void testDxpRestTerminateMemberLTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        createDefaultLongTermDisabilityMasterPolicy();
        createDefaultLongTermDisabilityCertificatePolicy();

        commonSteps(CONFIG_LTD, RESCIND_CANCELLATION_CONFIG_LTD, customerNumNIC1, customerNumberAuthorize);
    }

    private void commonSteps(CertificateCancellationReason productAPIConfigCancellation, CertificateRescindCancellationReason productAPIConfig, String customerNumNIC1, String customerNumNIC2) {

        String certificatePolicyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        String cancelDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(YYYY_MM_DD);
        String cancellationReason = "1";

        ResponseContainer<RestError> responseCancellation = dxpRestService.postCertificateCancellationReason(
                productAPIConfigCancellation, customerNumNIC2, certificatePolicyNum, cancelDate, cancellationReason, null);
        assertThat(responseCancellation.getResponse().getStatus()).isEqualTo(200);

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNum);
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

        LOGGER.info("---=={Step 1}==---");
        String rescindCancellationReason = "1";

        ResponseContainer<RestError> response =
                dxpRestService.postCertificateRescindCancellationReason(productAPIConfig, customerNumNIC1, certificatePolicyNum, rescindCancellationReason);
        assertThat(response.getResponse().getStatus()).isEqualTo(403);
        assertThat(response.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
        assertThat(response.getModel().getMessage()).isEqualTo("User has no access to certificate");

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<RestError> response2 =
                dxpRestService.postCertificateRescindCancellationReason(productAPIConfig, customerNumNIC2, certificatePolicyNum, rescindCancellationReason);
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);

        LOGGER.info("---=={Step 3}==---");
        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNum);
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}