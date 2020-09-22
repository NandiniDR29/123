/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.EmployerGoupsMasterPoliciesModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSmokeDxpViewEmployerProfileInformation extends RestBaseTest implements CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {DXP, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22408", component = CUSTOMER_REST)
    public void testSmokeDxpViewEmployerProfileInformation() {

        mainApp().open();
        LOGGER.info("---=={REN-22408 Step 7}==---");
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        String nonCustomerNC2 = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        ResponseContainer<List<EmployerGoupsMasterPoliciesModel>> responsePolicy = dxpRestService.getEmployerGroupMasterPolicies(customerNumberAuthorize, nonCustomerNC2);
        assertThat(responsePolicy.getResponse().getStatus()).isEqualTo(200);

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(responsePolicy.getModel()).isNotEmpty();
            softly.assertThat(responsePolicy.getModel().get(0).getPolicyNumber()).isEqualTo(policyNumber);
            softly.assertThat(responsePolicy.getModel().get(0).getEffectiveDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).format(YYYY_MM_DD));
            softly.assertThat(responsePolicy.getModel().get(0).getPolicyStatusCd()).isEqualTo("issued");
            softly.assertThat(responsePolicy.getModel().get(0).getIssueDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD));
        });
    }
}
