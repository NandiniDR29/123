/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin.premiumwaiver;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.helpers.billing.BillingHelper.DZERO;
import static com.exigen.ren.helpers.billing.BillingHelper.ZERO;
import static com.exigen.ren.main.enums.BillingConstants.ModalPremiumTransactionType.*;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestWaiverOfPremiumWithZeroRateSixthScenario extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-22483", component = BILLING_GROUPBENEFITS)
    public void testWaiverOfPremiumWithZeroRateSixthScenario() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData")
                .adjust(caseProfile.getDefaultTestData("CaseProfile", "CaseProfileDetailsOtherName")).resolveLinks(), groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_OneCoverageNonContributory")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "ClassificationManagementTabEnhancedAccidentOtherRate").resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_OnTime").resolveLinks()));
        LOGGER.info("TEST: Master Quote is created #" + PolicySummaryPage.labelPolicyNumber.getValue());

        masterPolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        createDefaultGroupAccidentCertificatePolicy();
        certificatePolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());
        PolicySummaryPage.linkMasterPolicy.click();
        initModalPremiumValues();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(5));

        mainApp().open();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.addPremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("PremiumWaiver", "TestData_Today"));
        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();

        verifyTableModalPremium(1, policyEffectiveDate.plusMonths(5).format(DateTimeUtils.MM_DD_YYYY), DZERO.toString(), ADD_PREMIUM_WAIVER);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(6));

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.cancel().perform(groupAccidentCertificatePolicy.getDefaultTestData("Cancellation", "TestData"));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();

        verifyTableModalPremium(1, policyEffectiveDate.plusMonths(6).format(DateTimeUtils.MM_DD_YYYY), DZERO.toString(), CANCELLATION_TERM);

        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.reinstate().perform(groupAccidentCertificatePolicy.getDefaultTestData("Reinstatement", "TestData"));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();

        LOGGER.info("TEST: Verify new MP if PW Date < Cancellation/Reinstatement Date (PW period is not completed)");
        verifyTableModalPremium(1, policyEffectiveDate.plusMonths(6).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), REINSTATEMENT_OTHER);

        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.cancel().perform(groupAccidentCertificatePolicy.getDefaultTestData("Cancellation", "TestData_Plus3Months"));

        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.expandCoverageByName("Enhanced Accident");

        verifyModalPremiumsTableByBillableCoverage(5, 0,
                policyEffectiveDate.plusMonths(9).format(DateTimeUtils.MM_DD_YYYY), ZERO,
                CANCELLATION_TERM);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(7));

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.addPremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("PremiumWaiver", "TestData_Today"));
        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.expandCoverageByName("Enhanced Accident");

        LOGGER.info("TEST: Verify new MP if PW Date < Pended Cancellation Effective Date (PW period is not completed)");
        verifyModalPremiumsTableByBillableCoverage(6, 0, policyEffectiveDate.plusMonths(7).format(DateTimeUtils.MM_DD_YYYY), ZERO, ADD_PREMIUM_WAIVER);
        verifyModalPremiumsTableByBillableCoverage(7, 0, policyEffectiveDate.plusMonths(9).format(DateTimeUtils.MM_DD_YYYY), ZERO, CANCELLATION_TERM);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(8));

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        groupAccidentCertificatePolicy.removePremiumWaiver().perform(groupAccidentCertificatePolicy.getDefaultTestData("RemovePremiumWaiver", "TestData_Today"));
        PolicySummaryPage.linkMasterPolicy.click();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.expandCoverageByName("Enhanced Accident");

        verifyModalPremiumsTableByBillableCoverage(8, 0, policyEffectiveDate.plusMonths(8).format(DateTimeUtils.MM_DD_YYYY), modalPremiumAmount.get().toString(), REMOVE_PREMIUM_WAIVER);
        verifyModalPremiumsTableByBillableCoverage(9, 0, policyEffectiveDate.plusMonths(9).format(DateTimeUtils.MM_DD_YYYY), ZERO, CANCELLATION_TERM);
    }
}
