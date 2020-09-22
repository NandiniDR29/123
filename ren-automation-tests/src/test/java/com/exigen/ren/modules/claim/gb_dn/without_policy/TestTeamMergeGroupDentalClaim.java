package com.exigen.ren.modules.claim.gb_dn.without_policy;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_WITHOUT_POLICY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FINANCIALS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NET_AMOUNT;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeGroupDentalClaim extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-21607", component = CLAIMS_GROUPBENEFITS)
    public void testTeamMergeGroupDentalClaim() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        LOGGER.info("TEST: Step #14-15");
        dentalClaim.createWithoutPolicy(dentalClaim.getDefaultTestData(DATA_GATHER_WITHOUT_POLICY, TestDataKey.DEFAULT_TEST_DATA_KEY));

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("TEST: Step #16");
        Currency benefitAmount = new Currency(tableResultsOfAdjudication.getRow(1)
                .getCell(TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.BENEFIT_AMOUNT.getName()).getValue());

        toSubTab(FINANCIALS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_NET_AMOUNT.getName())).hasValue(benefitAmount.toString());
    }
}
