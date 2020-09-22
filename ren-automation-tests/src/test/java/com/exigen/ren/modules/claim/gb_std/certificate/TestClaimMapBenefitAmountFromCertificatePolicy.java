package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageDeterminationTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.CoveragesTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageDeterminationTab.BenefitAmount.LIMIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageDeterminationTab.tableBenefitAmount;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData.APPROVED_PERCENT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimMapBenefitAmountFromCertificatePolicy extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33615", component = CLAIMS_GROUPBENEFITS)
    public void testClaimMapBenefitAmountFromCertificatePolicy() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        createDefaultShortTermDisabilityMasterPolicy();

        shortTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultSTDCertificatePolicyDataGatherData()
                .mask(TestData.makeKeyPath(CoveragesTab.class.getSimpleName(), APPROVED_PERCENT.getLabel()))
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST: Step 2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY), BenefitCoverageDeterminationTab.class, false);

        assertThat(tableBenefitAmount).with(LIMIT_AMOUNT, new Currency(577).toString()).hasMatchingRows(1);
    }
}