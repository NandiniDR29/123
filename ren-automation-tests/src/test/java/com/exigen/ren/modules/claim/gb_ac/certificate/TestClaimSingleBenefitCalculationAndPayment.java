package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimAllSingleBenefitCalculationsTable.SINGLE_BENEFIT_NUMBER;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab.CoverageLimits.LIMIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab.CoverageLimits.LIMIT_LEVEL;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimSingleBenefitCalculationAndPayment extends ClaimGroupBenefitsACBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30237", component = CLAIMS_GROUPBENEFITS)
    public void testClaimSingleBenefitCalculationAndPayment() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoCoveragesNonContributory")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        createDefaultGroupAccidentCertificatePolicy();
        accHealthClaim.create(tdSpecific().getTestData("TestData_Claim"));
        claim.claimOpen().perform();

        LOGGER.info("Step 13 (Repeat Step 1)");
        claim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).fillTab(tdSpecific().getTestData("TestData_CalculateSingleBenefitAmount"));
        assertSoftly(softly -> {
            softly.assertThat(CoveragesActionTab.tableCoverageLimits.getRow(1)).hasCellWithValue(LIMIT_LEVEL.getName(), "Per Occurrence");
            softly.assertThat(CoveragesActionTab.tableCoverageLimits.getRow(1)).hasCellWithValue(LIMIT_AMOUNT.getName(), "$300.00");
        });

        LOGGER.info("Step 13 (Repeat Step 2)");
        Tab.buttonSaveAndExit.click();
        assertThat(ClaimAdjudicationSingleBenefitCalculationPage.tableSingleBenefitCalculation.getRow(1).getCell(SINGLE_BENEFIT_NUMBER)).hasValue("1-1");

        LOGGER.info("Step 13 (Repeat Step 3)");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(tdSpecific().getTestData("TestData_Payment")), PaymentPaymentPaymentAllocationTab.class, true);
        assertSoftly(softly -> {
            softly.assertThat(claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue("$100.00");
            softly.assertThat(PaymentPaymentPaymentAllocationTab.tableCoverageLimits.getRow(1)).hasCellWithValue(LIMIT_LEVEL.getName(), "Per Occurrence");
            softly.assertThat(PaymentPaymentPaymentAllocationTab.tableCoverageLimits.getRow(1)).hasCellWithValue(LIMIT_AMOUNT.getName(), "$300.00");
        });
    }

}
