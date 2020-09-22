/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.privilege;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTransferFundsPrivilegeFullAdmin extends GroupBenefitsBillingBaseTest implements RoleContext, PaymentsMaintenanceContext, BillingAccountContext, ProfileContext, CaseProfileContext {
    private TestData tdCorporateRole = roleCorporate.defaultTestData();
    private TestData tdSecurityProfile = profileCorporate.defaultTestData();

    private Currency suspenseAmount = new Currency(100);

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24782", component = BILLING_GROUPBENEFITS)
    public void testTransferFundsPrivilegeFullAdmin() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createCaseProfile();
        createPolicyFullAdmin();
        masterPolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());
        certificatePolicyNumber.set(PolicySummaryPage.tableCertificatePolicies.getRow(1).getCell(PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER).getValue());
        billingAccountNumber.set(getBillingAccountNumber(masterPolicyNumber.get()));

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        paymentsMaintenance.addSuspense().perform(paymentsMaintenance.getDefaultTestData("AddSuspense", "TestData_Payment Designation"), billingAccountNumber.get(), Collections.singletonList(suspenseAmount.toString()));

        navigateToBillingAccount(masterPolicyNumber.get());

        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION)).hasValue("Transfer");

        changePrivilege();

        MainPage.QuickSearch.search(billingAccountNumber.get());

        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getValue()).isNotEqualTo("Transfer");
    }

    private void changePrivilege() {
        adminApp().reopen();
        List<String> privileges = new ArrayList<>();
        privileges.add("ALL");
        privileges.add("EXCLUDE " + Privilege.PAYMENT_TRANSFER.get());

        String roleName = tdCorporateRole.getValue("DataGather", "TestData", GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), privileges));

        String userLogin = tdSecurityProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = tdSecurityProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        mainApp().reopen(userLogin, userPassword);
    }

    protected void createCaseProfile() {
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData")
                .adjust(caseProfile.getDefaultTestData("CaseProfile", "CaseProfileDetailsOtherName")).resolveLinks(), groupAccidentMasterPolicy.getType());
    }
}
