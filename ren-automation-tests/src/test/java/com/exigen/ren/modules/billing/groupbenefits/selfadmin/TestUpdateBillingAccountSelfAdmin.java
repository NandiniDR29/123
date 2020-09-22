/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;


import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateBillingAccountSelfAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24790", component = BILLING_GROUPBENEFITS)
    public void testUpdateBillingAccountSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());

        TestData updateBillingAccountTestData = billingAccount.getDefaultTestData("UpdateBillingAccount", "TestData");

        String billingAccountName = updateBillingAccountTestData.getValue(BillingAccountTabMetaData.BillingAccountGeneralOptions.class.getSimpleName(),
                BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_ACCOUNT_NAME.getLabel());

        billingAccount.updateBillingAccount().perform(updateBillingAccountTestData);

        assertThat(BillingAccountsListPage.labelBillingAccountName).hasValue(billingAccountName);
    }

}
