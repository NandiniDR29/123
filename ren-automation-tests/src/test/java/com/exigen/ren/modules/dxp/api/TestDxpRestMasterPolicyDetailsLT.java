/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.policy.EmployerMasterPoliciesDetailed;
import com.exigen.ren.rest.dxp.model.policy.PolicyPreconfigGroupCoverageDefinitions;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.DXPConstants.EmployerMasterPolicies.CONFIG_LIFE;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpRestMasterPolicyDetailsLT extends RestBaseTest implements CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-31224", component = CUSTOMER_REST)
    public void testDxpRestMasterPolicyDetailsLT() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultTermLifeInsuranceMasterPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        String planName = getDefaultTLMasterPolicyData().getTestDataList(planDefinitionTab.getMetaKey()).get(0).getValue( PlanDefinitionTabMetaData.PLAN.getLabel());
        String sponsorPaymentMode = getDefaultTLMasterPolicyData().getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(PlanDefinitionTabMetaData.SPONSOR_PAYMENT_MODE.getLabel());
        String requiredParticipationPct = getDefaultTLMasterPolicyData().getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(PlanDefinitionTabMetaData.REQUIRED_PARTICIPATION.getLabel());
        String sicCode = getDefaultTLMasterPolicyData().getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(PlanDefinitionTabMetaData.SIC_CODE.getLabel());

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<EmployerMasterPoliciesDetailed> response = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_LIFE, customerNumberAuthorize, customerNum, policyNumber, null);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().getRevisionNumber()).isEqualTo("1");
        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getTransactionTypeCd()).isEqualTo("policy");
            softly.assertThat(response.getModel().getPolicyNumber()).isEqualTo(policyNumber);
            PolicyPreconfigGroupCoverageDefinitions planDefModel = response.getModel().getPreconfigGroupCoverageDefinitions().stream()
                    .filter(plan -> !(plan.getGroupCoverageDefPaymentModesSelectionComponent().getSponsorPaymentMode() == null)).findFirst().orElseThrow(() -> new IstfException("Plan def not found"));
            softly.assertThat(planDefModel.getPackageName()).isEqualTo(planName);
            softly.assertThat(planDefModel.getGroupCoverageDefPaymentModesSelectionComponent().getSponsorPaymentMode()).isEqualTo(sponsorPaymentMode);
            softly.assertThat(planDefModel.getRequiredParticipationPct()).isEqualTo(String.valueOf(Float.parseFloat(requiredParticipationPct
                    .replaceAll("[^0-9]", "")) /100));
            softly.assertThat(planDefModel.getSicCode()).isEqualTo(sicCode);
        });

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<EmployerMasterPoliciesDetailed> response2 = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_LIFE, customerNumberAuthorize, customerNum, policyNumber,
                policyEffectiveDate.toLocalDate().toString());
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().getRevisionNumber()).isEqualTo("1");
        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getTransactionTypeCd()).isEqualTo("policy");
            softly.assertThat(response.getModel().getPolicyNumber()).isEqualTo(policyNumber);
            PolicyPreconfigGroupCoverageDefinitions planDefModel = response.getModel().getPreconfigGroupCoverageDefinitions().stream()
                    .filter(plan -> !(plan.getGroupCoverageDefPaymentModesSelectionComponent().getSponsorPaymentMode() == null)).findFirst().orElseThrow(() -> new IstfException("Plan def not found"));

            softly.assertThat(planDefModel.getPackageName()).isEqualTo(planName);
            softly.assertThat(planDefModel.getGroupCoverageDefPaymentModesSelectionComponent().getSponsorPaymentMode()).isEqualTo(sponsorPaymentMode);
            softly.assertThat(planDefModel.getRequiredParticipationPct()).isEqualTo(String.valueOf(Float.parseFloat(requiredParticipationPct
                    .replaceAll("[^0-9]", "")) /100));
            softly.assertThat(planDefModel.getSicCode()).isEqualTo(sicCode);
        });

        LOGGER.info("---=={Step 3}==---");
        createDefaultNonIndividualCustomer();
        String customerNumC2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        ResponseContainer<EmployerMasterPoliciesDetailed> response3 = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_LIFE, customerNumC2, customerNumC2, policyNumber, null);
        assertThat(response3.getResponse().getStatus()).isEqualTo(403);
        assertThat(response3.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
        assertThat(response3.getModel().getMessage()).isEqualTo("User has no access to policy or certificate");
    }
}
