/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.policy.EmployerMasterPoliciesDetailed;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.DXPConstants.EmployerMasterPolicies.CONFIG_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.ENHANCED_10_UNITS;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpRestMasterPolicyDetailsAC extends RestBaseTest implements CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32130", component = CUSTOMER_REST)
    public void testDxpRestMasterPolicyDetailsAC() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<EmployerMasterPoliciesDetailed> response = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_ACCIDENT, customerNumberAuthorize, customerNum, policyNumber, null);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().getRevisionNumber()).isEqualTo("1");
        commonVerify(response, policyNumber);

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<EmployerMasterPoliciesDetailed> response2 = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_ACCIDENT, customerNumberAuthorize, customerNum, policyNumber,
                policyEffectiveDate.toLocalDate().toString());
        assertThat(response2.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().getRevisionNumber()).isEqualTo("1");
        commonVerify(response, policyNumber);

        LOGGER.info("---=={Step 3}==---");
        createDefaultNonIndividualCustomer();
        String customerNumC2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        ResponseContainer<EmployerMasterPoliciesDetailed> response3 = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_ACCIDENT, customerNumC2, customerNumC2, policyNumber, null);
        assertThat(response3.getResponse().getStatus()).isEqualTo(403);
        assertThat(response3.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
        assertThat(response3.getModel().getMessage()).isEqualTo("User has no access to policy or certificate");
    }

    private void commonVerify(ResponseContainer<EmployerMasterPoliciesDetailed> response, String policyNumber) {
        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getTransactionTypeCd()).isEqualTo("policy");
            softly.assertThat(response.getModel().getPolicyNumber()).isEqualTo(policyNumber);
            softly.assertThat(response.getModel().getPreconfigGroupCoverageDefinitions().get(0).getPackageName()).isEqualTo(ENHANCED_10_UNITS);
            softly.assertThat(response.getModel().getPreconfigGroupCoverageDefinitions().get(0).getGroupCoverageDefPaymentModesSelectionComponent().getSponsorPaymentMode()).isEqualTo("12");
            softly.assertThat(response.getModel().getPreconfigGroupCoverageDefinitions().get(0).getRateBasisCd()).isEqualTo("MTPPP");
            softly.assertThat(response.getModel().getPreconfigGroupCoverageDefinitions().get(0).getPackageCd()).isEqualTo("ENHANCED10");
        });
    }
}
