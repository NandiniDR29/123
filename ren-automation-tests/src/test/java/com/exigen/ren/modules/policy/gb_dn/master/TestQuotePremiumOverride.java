/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuotePremiumOverride extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24707", component = POLICY_GROUPBENEFITS)
    public void testQuotePremiumOverride() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("TEST: Premium Override");
        groupDentalMasterPolicy.initiate(groupDentalMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(groupDentalMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", TestDataKey.DEFAULT_TEST_DATA_KEY), PolicyInformationTab.class, PremiumSummaryTab.class);

        PremiumSummaryTab.buttonRate.click();
        Currency premiumAmount = getPremium();

        PremiumSummaryTab.buttonOverrideAndRatePremium.click();
        PremiumSummaryTab.dialogPremiumOverride.controls.textBoxes.getFirst().setValue("10");
        PremiumSummaryTab.dialogPremiumOverride.controls.comboBoxes.getFirst().setValue("index=1");
        PremiumSummaryTab.dialogPremiumOverride.controls.comboBoxes.get(2).setValue("index=1");

        Currency premiumAmountNew = new Currency(PremiumSummaryTab.labelFinalAnnualPremium.getValue());
        assertThat(premiumAmountNew).isNotEqualTo(premiumAmount);

        PremiumSummaryTab.dialogPremiumOverride.confirm();
        assertThat(premiumAmountNew).isEqualTo(getPremium());
    }

    private Currency getPremium() {
        return new Currency(PremiumSummaryTab.labelTotalPremium.getValue());
    }
}
