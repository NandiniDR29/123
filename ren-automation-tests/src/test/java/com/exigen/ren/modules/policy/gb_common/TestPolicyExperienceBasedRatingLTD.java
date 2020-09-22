/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_common;

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
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyExperienceBasedRatingLTD extends BaseTest implements RoleContext, ProfileContext, CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private TestData tdCorporateRole = roleCorporate.defaultTestData();
    private TestData tdSecurityProfile = profileCorporate.defaultTestData();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15265", "REN-15286"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCreatePreProposalWorkflowLTD() {
        adminApp().open();

        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL", "EXCLUDE " + Privilege.EXPERIENCE_BASED_RATING.get())));

        String userLoginWithoutExperienceBasedRating = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPasswordWithoutExperienceBasedRating = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        LOGGER.info("---=={Step 7-9}==---");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).isRequired().isPresent().isEnabled().hasValue("$0.00");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR)).isRequired().isPresent().isEnabled().hasValue("0.0000000");

            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass());
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("---=={REN-15286}==---");
            longTermDisabilityMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("");
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR).setValue("");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).hasWarning();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR)).hasWarning();

            premiumSummaryTab.navigate();
            premiumSummaryTab.rate();
            softly.assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), "'Experience Claim Amount' is required")).exists();
            softly.assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), "'Credibility Factor' is required")).exists();

            Tab.buttonCancel.click();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("100");
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR).setValue("2.0000000");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR)).hasWarningWithText("'Credibility Factor' allowed range is from 0.0000000 to 1.0000000");
            planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR).setValue("1.0000000");

            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });

        LOGGER.info("---=={Step 1-3}==---");
        mainApp().reopen(userLoginWithoutExperienceBasedRating, userPasswordWithoutExperienceBasedRating);
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("Create quote for product:" + GroupBenefitsMasterPolicyType.GB_DI_LTD.getName());
        initiateQuoteAndFillToTab(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), false);
        planDefinitionTab.selectDefaultPlan();

        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).isRequired().isPresent().isDisabled().hasValue(new Currency(0).toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR)).isRequired().isPresent().isDisabled().hasValue("0.0000000");

            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultLTDMasterPolicyData(),planDefinitionTab.getClass());
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}