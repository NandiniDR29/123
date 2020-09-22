/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.billing.BillingAccountsModel;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.CURRENT_DUE;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE;
import static com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage.labelBillingAccountName;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpViewPremiumBillingHistory extends RestBaseTest implements CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, BillingAccountContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36462", component = CUSTOMER_REST)
    public void testDxpViewPremiumBillingHistory() {

        mainApp().open();

        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        createDefaultGroupDentalMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        createDefaultGroupDentalCertificatePolicy();

        String accountNum = GroupBenefitsBillingBaseTest.getBillingAccountNumber(policyNum);
        billingAccount.navigateToBillingAccount();
        billingAccount.generateFutureStatement().perform();
        String accountName = labelBillingAccountName.getValue();
        String dueDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(DUE_DATE).getValue(), MM_DD_YYYY).format(YYYY_MM_DD);
        String currentDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(CURRENT_DUE).getValue()).toPlainString();

        ResponseContainer<List<BillingAccountsModel>> response = dxpRestService.getEmployerBillingAccounts(customerNumberAuthorize, customerNumNIC1);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().size()).isEqualTo(1);
        BillingAccountsModel model = response.getModel().get(0);

        assertSoftly(softly -> {
            softly.assertThat(model.getAccountNumber()).isEqualTo(accountNum);
            softly.assertThat(model.getAccountName()).isEqualTo(accountName);
            softly.assertThat(model.getDueDate()).isEqualTo(dueDate);
            softly.assertThat(model.getTotalDue().getValue()).isEqualTo(currentDue);
            softly.assertThat(model.getMasterPolicies().size()).isEqualTo(1);
            softly.assertThat(model.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(policyNum);
            softly.assertThat(model.getMasterPolicies().get(0).getEffectiveDate()).isEqualTo(policyEffectiveDate.format(YYYY_MM_DD));
        });
    }
}