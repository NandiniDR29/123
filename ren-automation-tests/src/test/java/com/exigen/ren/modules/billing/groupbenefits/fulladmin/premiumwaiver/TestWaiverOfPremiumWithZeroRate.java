/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin.premiumwaiver;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.helpers.billing.BillingHelper.DZERO;
import static com.exigen.ren.main.enums.BillingConstants.ModalPremiumTransactionType.*;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestWaiverOfPremiumWithZeroRate extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-22483", component = BILLING_GROUPBENEFITS)
    public void testWaiverOfPremiumWithZeroRate() {
        mainApp().open();

        createCustomerNonIndividual();
        createCaseProfile();

        createPolicyFullAdminOneCoverageEnhancedAccident();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.get().plusMonths(3));

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.addPremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("PremiumWaiver", "TestData_Today"));
        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        verifyTableModalPremium(1, policyEffectiveDate.get().plusMonths(3).format(DateTimeUtils.MM_DD_YYYY), DZERO.toString(), ADD_PREMIUM_WAIVER);

        MainPage.QuickSearch.search(masterPolicyNumber.get());

        LOGGER.info("TEST: Flat Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData_Minus2Months")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData("TestPolicyEndorsementFlat", "TestData").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_ExistentBillingAccount").resolveLinks()));

        PolicySummaryPage.tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();
        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData("Endorsement", "TestData_Minus2Months")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData("TestPolicyEndorsementFlat", "TestData").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData").resolveLinks()));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.expandCoverageByName("Enhanced Accident");

        LOGGER.info("TEST: Verify new MP when Endorsement Effective Date < PW date (PW period is not completed");
        verifyModalPremiumsTableByBillableCoverage(3, 0, policyEffectiveDate.get().plusMonths(1).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), ENDORSEMENT);
        verifyModalPremiumsTableByBillableCoverage(4, 0, policyEffectiveDate.get().plusMonths(3).format(DateTimeUtils.MM_DD_YYYY), DZERO.toString(), ADD_PREMIUM_WAIVER);

        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.get().plusMonths(5));

        mainApp().open();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.removePremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("RemovePremiumWaiver", "TestData_Today"));
        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();

        verifyTableModalPremium(1, policyEffectiveDate.get().plusMonths(5).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), REMOVE_PREMIUM_WAIVER);

        MainPage.QuickSearch.search(masterPolicyNumber.get());

        LOGGER.info("TEST: Flat Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData_Minus1Months")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData("TestPolicyEndorsementFlat", "TestData").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_ExistentBillingAccount").resolveLinks()));

        PolicySummaryPage.tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();
        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData("Endorsement", "TestData_Minus1Months")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData("TestPolicyEndorsementFlat", "TestData").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData").resolveLinks()));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();

        LOGGER.info("TEST: Verify new MP if PW Date < Endorsement Effective Date < RPW date (PW period is completed");
        verifyTableModalPremium(1, policyEffectiveDate.get().plusMonths(5).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), REMOVE_PREMIUM_WAIVER);
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.get().plusMonths(8));

        mainApp().open();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.addPremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("PremiumWaiver", "TestData_Today"));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        verifyTableModalPremium(1, policyEffectiveDate.get().plusMonths(8).format(DateTimeUtils.MM_DD_YYYY), DZERO.toString(), ADD_PREMIUM_WAIVER);

        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.get().plusMonths(9));

        mainApp().open();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.removePremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("RemovePremiumWaiver", "TestData_Today"));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        verifyTableModalPremium(1, policyEffectiveDate.get().plusMonths(9).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), REMOVE_PREMIUM_WAIVER);

        MainPage.QuickSearch.search(masterPolicyNumber.get());

        LOGGER.info("TEST: Flat Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData("Endorsement", "TestData_Minus3Months")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData("TestPolicyEndorsementFlat", "TestData").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_ExistentBillingAccount").resolveLinks()));

        PolicySummaryPage.tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();

        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData("Endorsement", "TestData_Minus3Months")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData("TestPolicyEndorsementFlat", "TestData").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData").resolveLinks()));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.expandCoverageByName("Enhanced Accident");

        LOGGER.info("TEST: Verify new MP if Endorsement Effective date < PW Date < RPW Date (PW period is completed");
        verifyModalPremiumsTableByBillableCoverage(10, 0, policyEffectiveDate.get().plusMonths(6).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), ENDORSEMENT);
        verifyModalPremiumsTableByBillableCoverage(11, 0, policyEffectiveDate.get().plusMonths(8).format(DateTimeUtils.MM_DD_YYYY), DZERO.toString(), ADD_PREMIUM_WAIVER);
        verifyModalPremiumsTableByBillableCoverage(12, 0, policyEffectiveDate.get().plusMonths(9).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), REMOVE_PREMIUM_WAIVER);
    }

    protected String createCustomerNonIndividual() {
        createDefaultNonIndividualCustomer();
        return CustomerSummaryPage.labelCustomerNumber.getValue();
    }

    protected void createCaseProfile() {
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData")
                .adjust(caseProfile.getDefaultTestData("CaseProfile", "CaseProfileDetailsOtherName")).resolveLinks(), groupAccidentMasterPolicy.getType());
    }
}
