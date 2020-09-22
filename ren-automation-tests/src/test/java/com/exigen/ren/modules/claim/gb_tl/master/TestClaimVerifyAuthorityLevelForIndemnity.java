package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.EXPENSE_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;
import static com.exigen.ren.main.modules.claim.gb_tl.metadata.BenefitsAcceleratedDeathInjuryPartyInformationTabMetaData.ESTIMATED_COST_VALUE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyAuthorityLevelForIndemnity extends ClaimGroupBenefitsTLBaseTest {

    private static final String AUTH_LEVEL_3 = "Level 3";
    private static final String AUTH_LEVEL_4 = "Level 4";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20114", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyAuthorityLevelForIndemnity() {
        User profileAuthLevel_3 = createProfile(AUTH_LEVEL_3);
        User profileAuthLevel_4 = createProfile(AUTH_LEVEL_4);

        mainApp().reopen(profileAuthLevel_4.getLogin(), profileAuthLevel_4.getPassword());
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MAXIMUM_PERCENTAGE.getLabel()), "100")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MINIMUM_AMOUNT.getLabel()), "600000")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MAXIMUM_AMOUNT.getLabel()), "1000000")
        );

        LOGGER.info("TEST: Step #1");
        termLifeClaim.create(termLifeClaim.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_BenefitAcceleratedDeath")
                .adjust(TestData.makeKeyPath(benefitsAcceleratedDeathInjuryPartyInformationTab.getMetaKey(),
                        ESTIMATED_COST_VALUE.getLabel()), "200000000"));
        termLifeClaim.claimSubmit().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST: Step #2");
        termLifeClaim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "10000")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), EXPENSE_RESERVE.getLabel()), "10000"), 1);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations).hasRows(1);

        LOGGER.info("TEST: Step #3");
        mainApp().reopen(profileAuthLevel_3.getLogin(), profileAuthLevel_3.getPassword());
        MainPage.QuickSearch.search(claimNumber);
        termLifeClaim.claimOpen().perform();

        termLifeClaim.addPayment().start();
        termLifeClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), GROSS_AMOUNT.getLabel()),
                        "500001")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), ALLOCATION_AMOUNT.getLabel()),
                        "500001"), PaymentPaymentPaymentAllocationTab.class, true);
        buttonPostPayment.click();

        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(
                    "Feature 1-1 Indemnity payments sum $500,001.00 in claim exceeds authority level limit $25,000.00."))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(
                    "Claim payments for Indemnity Injury sum $500,001.00 exceeds authority level limit $25,000.00."))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(
                    "Indemnity Payment # <empty> for 1-1 amount $500,001.00 exceeds authority level limit $25,000.00."))).isPresent();
        });
    }

    private User createProfile(String authorityLevel) {
        TestData tdSecurityProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY);

        String userLogin = tdSecurityProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = tdSecurityProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        createUserAndRelogin(profileCorporate, tdSecurityProfile
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(),
                        AuthorityLevelsMetaData.LEVEL.getLabel()), authorityLevel));

        return new User(userLogin, userPassword);
    }
}
