/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.MAJEPOS;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.RENEWAL_NOTIFICATION_DAYS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteConfigureEligibilitySections extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14369", "REN-14391"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteConfigureRatingAndEligibility() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        assertSoftly(softly -> {
            LOGGER.info("---=={REN-14391}==---");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RENEWAL_NOTIFICATION_DAYS)).isPresent().isEnabled().isRequired().hasValue("60");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION)).isPresent().isEnabled().isRequired().hasValue(VALUE_NO);
        });

        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"),
                PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(MAJEPOS, PolicyConstants.PlanDental.BASEPOS, ALACARTE));
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());

        LOGGER.info("---=={REN-14369}==---");
        LOGGER.info("Steps: 1.1, 2.2");
        policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
        planDefinitionTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(DOES_MIN_HOURLY_REQUIREMENT_APPLY)).isPresent().isDisabled().hasValue(VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isPresent().isEnabled().isOptional();
        });

        LOGGER.info("Steps: 1.2, 2.1");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_YES);
        planDefinitionTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(DOES_MIN_HOURLY_REQUIREMENT_APPLY)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isAbsent();
        });

        LOGGER.info("---=={Step 3}==---");
        assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES))
                .isPresent().isEnabled().isOptional().hasValue(ValueConstants.EMPTY);


        LOGGER.info("---=={Step 5-6}==---");
        ImmutableList.of("First of the month coincident with or next following date of hire", "First of the month following date of hire", "None").forEach(value -> {
            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue(value);
            assertSoftly(softly -> {
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_AMOUNT)).isAbsent();
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isAbsent();

            });
        });

        ImmutableList.of("Amount and Mode Only", "First of the month coincident with or next following (amount and mode)", "First of the month following (amount and mode)").forEach(value -> {
            planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue(value);
            assertSoftly(softly -> {
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_AMOUNT)).isPresent().isEnabled().isOptional();
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isPresent().isEnabled().isOptional();
            });
        });

        LOGGER.info("---=={Step 7}==---");
        assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ALLOW_MEMBER_AND_SPOUSE_WHO_ARE_PART_OF_GROUP_ON_SEPARATE_CERTIFICATE))
                .isPresent().isEnabled().isOptional().hasValue(ValueConstants.EMPTY);
    }
}