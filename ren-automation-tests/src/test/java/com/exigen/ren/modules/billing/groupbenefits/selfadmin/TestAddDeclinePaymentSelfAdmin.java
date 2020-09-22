/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddDeclinePaymentSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24784", component = BILLING_GROUPBENEFITS)
    public void testAddDeclinePaymentSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("TEST: Generate Future Statement for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Accept Payment for Policy # " + masterPolicyNumber.get());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData(
                "AcceptPayment", "TestData_Cash_Invoice"), modalPremiumAmount.get().toString());

        acceptedPaymentVerification();

        LOGGER.info("TEST: Decline Payment for Policy # " + masterPolicyNumber.get());
        billingAccount.declinePayment().perform(billingAccount.getDefaultTestData("DeclinePayment", "TestData"), 1);

        declinePaymentVerificationSelfAdmin();
    }
}
