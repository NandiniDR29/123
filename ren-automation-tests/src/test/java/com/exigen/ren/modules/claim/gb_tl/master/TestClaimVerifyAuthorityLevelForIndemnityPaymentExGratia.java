package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;
import static com.exigen.ren.main.modules.claim.gb_tl.metadata.BenefitsAcceleratedDeathInjuryPartyInformationTabMetaData.ESTIMATED_COST_VALUE;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyAuthorityLevelForIndemnityPaymentExGratia extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20121", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyAuthorityLevelForIndemnityPaymentExGratia() {
        createProfile();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultSelfAdminTermLifeInsuranceMasterPolicy();

        LOGGER.info("TEST: Step #1");
        termLifeClaim.create(termLifeClaim.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_BenefitAcceleratedDeath")
                .adjust(TestData.makeKeyPath(benefitsAcceleratedDeathInjuryPartyInformationTab.getMetaKey(),
                        ESTIMATED_COST_VALUE.getLabel()), "200000000"));
        termLifeClaim.claimSubmit().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        LOGGER.info("TEST: Step #2");
        termLifeClaim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ExGratia")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "2500"), 1);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations).hasRows(1);

        LOGGER.info("TEST: Step #3");
        toSubTab(OVERVIEW);
        termLifeClaim.claimOpen().perform();

        termLifeClaim.addPayment().start();
        termLifeClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_ExGratiaPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), GROSS_AMOUNT.getLabel()),
                        "1")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), ALLOCATION_AMOUNT.getLabel()),
                        "1"), PaymentPaymentPaymentAllocationTab.class, true);
        buttonPostPayment.click();

        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(
                "Indemnity Payment # <empty> exceeds user's Ex Gratia authority limit $0.00."))).isPresent();
    }

    private void createProfile() {
        createUserAndRelogin(profileCorporate, profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(),
                        AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 3"));
    }
}
