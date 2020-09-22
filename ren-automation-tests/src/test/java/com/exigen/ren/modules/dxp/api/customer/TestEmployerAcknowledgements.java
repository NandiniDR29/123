/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.customer;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEmployerAcknowledgements extends RestBaseTest implements CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39475", component = CUSTOMER_REST)
    public void testEmployerAcknowledgements() {

        mainApp().open();
        createDefaultIndividualCustomer();
        String customerNumberIC = CustomerSummaryPage.labelCustomerNumber.getValue();

        ImmutableList<String> acknowledgmentsList = ImmutableList.of("ECONSENTAGREEMENT_V1", "TERMSANDCONDITIONS_V1");
        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<List<String>> response = dxpRestService.putEmployerProfileAcknowledgments(customerNumberIC, acknowledgmentsList);
        assertThat(response.getResponse()).hasStatus(204);

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<List<String>> response2 = dxpRestService.getEmployerProfileAcknowledgments(customerNumberIC);
        assertThat(response2.getResponse()).hasStatus(200);
        assertThat(response2.getModel()).isEqualTo(acknowledgmentsList);

        LOGGER.info("---=={Step 3}==---");
        ResponseContainer<List<String>> response3 = dxpRestService.deleteEmployerProfileAcknowledgments(customerNumberIC);
        assertThat(response3.getResponse()).hasStatus(200);

        mainApp().reopen();
        MainPage.QuickSearch.search(customerNumberIC);

        LOGGER.info("---=={Step 4}==---");
        ResponseContainer<List<String>> response4 = dxpRestService.getEmployerProfileAcknowledgments(customerNumberIC);
        assertThat(response4.getResponse()).hasStatus(200);
        assertThat(response4.getModel()).isEmpty();

    }
}
