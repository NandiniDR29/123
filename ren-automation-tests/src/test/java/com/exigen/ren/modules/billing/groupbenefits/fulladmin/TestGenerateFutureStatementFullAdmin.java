/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerateFutureStatementFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {
    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24778", component = BILLING_GROUPBENEFITS)
    public void testGenerateFutureStatementFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("TEST: Generate Future Statement for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        generateFutureStatementVerificationForFullAdmin();
    }
}
