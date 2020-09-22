/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.policy.policy_details_vs.PolicyViewMasterPolicyDetailsVS;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpMemberViewMasterPolicyDetailsVS extends RestBaseTest implements CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37690", component = CUSTOMER_REST)
    public void testDxpEmployerCertificatePremiumSummaryVS() {

        mainApp().open();
        createDefaultNICWithIndRelationshipDefaultRoles();

        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        createDefaultGroupVisionMasterPolicy();
        createDefaultGroupVisionCertificatePolicy();
        String policyNumberCP = PolicySummaryPage.labelPolicyNumber.getValue();
        navigateToCustomer();
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        ResponseContainer<PolicyViewMasterPolicyDetailsVS> response = dxpRestService.getMemberMasterPolicyDetailsVS(customerNumIC1, policyNumberCP);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().getPreconfigGroupCoverageDefinitions().get(0).getGroupCoverageDefinitionTiersViewOnlyComponent().getCoverageTiers()).isEqualTo("Employee Only");


        createDefaultIndividualCustomer();
        String customerNumIC2 = CustomerSummaryPage.labelCustomerNumber.getValue();

        ResponseContainer<PolicyViewMasterPolicyDetailsVS> response2 = dxpRestService.getMemberMasterPolicyDetailsVS(customerNumIC2, policyNumberCP);

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response2.getResponse().getStatus()).isEqualTo(403);
            softly.assertThat(response2.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            softly.assertThat(response2.getModel().getMessage()).isEqualTo("Requested user has no access to policy");
        });
    }
}
