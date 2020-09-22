package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.EXPENSE_RESERVE;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.INDEMNITY_RESERVE;
import static com.exigen.ren.main.modules.claim.gb_tl.metadata.BenefitsAcceleratedDeathInjuryPartyInformationTabMetaData.ESTIMATED_COST_VALUE;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyAuthorityLevelForIndemnityExpenseReserve extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20123", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyAuthorityLevelForIndemnityExpenseReserve() {
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
        termLifeClaim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), INDEMNITY_RESERVE.getLabel()), "2501")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), EXPENSE_RESERVE.getLabel()), "2501"), 1);

        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations).hasRows(1);
    }

    private void createProfile() {
        createUserAndRelogin(profileCorporate, profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(),
                        AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 4"));
    }
}
