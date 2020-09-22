package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASO;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASOALC;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.USE_BRO_RATES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUseBrochureRatesRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28829"}, component = POLICY_GROUPBENEFITS)
    public void testUseBrochureRatesRulesVerificationDN() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO), PlanDefinitionTab.class, true);

        LOGGER.info("Step#2 verification");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(USE_BRO_RATES)).isEnabled().hasValue(VALUE_NO);

        LOGGER.info("Step#4 verification");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_YES);

        LOGGER.info("Step#6 verification");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.fillTab(new SimpleDataProvider()
                .adjust(planDefinitionTab.getMetaKey() , new SimpleDataProvider()
                        .adjust(PlanDefinitionTabMetaData.PLAN.getLabel(), ImmutableList.of(ASOALC, ASO))).resolveLinks());
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(USE_BRO_RATES)).isDisabled().hasValue(VALUE_NO);
    }
}
