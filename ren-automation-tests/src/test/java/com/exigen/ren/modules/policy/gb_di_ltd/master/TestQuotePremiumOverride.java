/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuotePremiumOverride extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24707", component = POLICY_GROUPBENEFITS)
    public void testQuotePremiumOverride() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_AutoSubGroup"), longTermDisabilityMasterPolicy.getType());

        LOGGER.info("TEST: Premium Override");
        longTermDisabilityMasterPolicy.initiate(longTermDisabilityMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", TestDataKey.DEFAULT_TEST_DATA_KEY));
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(longTermDisabilityMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", "TestData_UseCensusFile"), PremiumSummaryTab.class, true);

        PremiumSummaryTab.buttonRate.click();
        Currency premiumAmount = getPremium();

        PremiumSummaryTab.buttonOverrideAndRatePremium.click();
        PremiumSummaryTab.dialogPremiumOverride.controls.comboBoxes.getFirst().setValue("index=1");
        PremiumSummaryTab.dialogPremiumOverride.controls.comboBoxes.get(2).setValue("index=1");
        PremiumSummaryTab.dialogPremiumOverride.controls.textBoxes.getFirst().setValue("10");

        Currency premiumAmountNew = new Currency(PremiumSummaryTab.labelFinalAnnualPremium.getValue());
        PremiumSummaryTab.dialogPremiumOverride.confirm();

        assertThat(premiumAmountNew).isNotEqualTo(premiumAmount);
        assertThat(premiumAmountNew).isEqualTo(getPremium());
    }

    private Currency getPremium() {
        return new Currency(
                PremiumSummaryTab.labelTotalPremium.getValue());
    }
}
