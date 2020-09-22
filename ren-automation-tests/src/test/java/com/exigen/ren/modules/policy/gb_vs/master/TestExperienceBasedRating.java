package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestExperienceBasedRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    ImmutableList<String> PREMIUM_COVERAGE_TABLE_HEADER_LIST = ImmutableList.of(EXPERIENCE_RATE.getName(), EXPERIENCE_ADJUSTMENT_FACTOR.getName(), FORMULA_RATE.getName());
    private TestData tdCorporateRole = roleCorporate.defaultTestData();
    private TestData tdSecurityProfile = profileCorporate.defaultTestData();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18178", "REN-18179"}, component = POLICY_GROUPBENEFITS)
    public void testExperienceBasedRatingForUserWithRatingPrivilege() {
        mainApp().open();

        LOGGER.info("REN-18178 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            LOGGER.info("REN-18178 Step 3 REN-18179 Step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).isPresent().isRequired().hasValue(new Currency(0).toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR)).isPresent().isEnabled().isRequired().hasValue("0.0000000");
            LOGGER.info("REN-18178 Step 5");
            premiumSummaryTab.navigate();
            softly.assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getHeader().getValue()).containsAll(PREMIUM_COVERAGE_TABLE_HEADER_LIST);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18179"}, component = POLICY_GROUPBENEFITS)
    public void testExperienceBasedRatingForUserWithoutRatingPrivilege() {
        adminApp().open();

        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL", "EXCLUDE " + Privilege.EXPERIENCE_BASED_RATING.get())));

        String userLoginWithoutExperienceBasedRating = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPasswordWithoutExperienceBasedRating = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        mainApp().reopen(userLoginWithoutExperienceBasedRating, userPasswordWithoutExperienceBasedRating);
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.selectDefaultPlan();
        assertSoftly(softly -> {
            LOGGER.info("REN-18179 Step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).isPresent().isRequired().isDisabled().hasValue(new Currency(0).toString());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR)).isPresent().isRequired().isDisabled().hasValue("0.0000000");
        });
    }
}
