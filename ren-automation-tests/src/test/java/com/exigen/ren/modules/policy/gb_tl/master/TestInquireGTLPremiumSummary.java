package com.exigen.ren.modules.policy.gb_tl.master;

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
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.ADD;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.EXPERIENCE_RATING;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.ExperienceRatingMetaData.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInquireGTLPremiumSummary extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private TestData tdCorporateRole = roleCorporate.defaultTestData();
    private TestData tdSecurityProfile = profileCorporate.defaultTestData();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18332", "REN-18333"}, component = POLICY_GROUPBENEFITS)
    public void testInquireGTLPremiumSummaryForUserWithRatingPrivilege() {
        mainApp().open();

        LOGGER.info("REN-18332 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PremiumSummaryTab.class, true);
        LOGGER.info("REN-18332 Step 3 REN-18333 Step 5");
        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING)).isPresent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).isPresent().isEnabled().isRequired().hasValue(new Currency(0).toString());
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR)).isPresent().isEnabled().isRequired().hasValue("0.0000000");
            LOGGER.info("REN-18332 Step 3 REN-18333 Step 19");
            premiumSummaryTab.submitTab();
            termLifeInsuranceMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            PremiumSummaryTab.tableCoveragesName.getRow(COVERAGE_NAME.getName(), ADD).getCell(1).controls.links.getFirst().click();
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("1");
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR).setValue("1");
            PremiumSummaryTab.tableCoveragesName.getRow(COVERAGE_NAME.getName(), BTL).getCell(1).controls.links.getFirst().click();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).hasValue(new Currency(1).toString());
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR)).hasValue("1");
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18333"}, component = POLICY_GROUPBENEFITS)
    public void testInquireGTLPremiumSummaryForUserWithoutRatingPrivilege() {

        LOGGER.info("REN-18333 Preconditions");

        adminApp().open();

        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL", "EXCLUDE " + Privilege.EXPERIENCE_BASED_RATING.get())));

        String userLoginWithoutExperienceBasedRating = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPasswordWithoutExperienceBasedRating = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        LOGGER.info("REN-18333 Step 1");

        mainApp().reopen(userLoginWithoutExperienceBasedRating, userPasswordWithoutExperienceBasedRating);
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), PremiumSummaryTab.class);

        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT)).isPresent().isDisabled().isRequired().hasValue(new Currency(0).toString());
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR)).isPresent().isDisabled().isRequired().hasValue("0.0000000");
        });
    }
}
