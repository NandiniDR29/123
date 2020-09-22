package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyTab;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitSTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsSTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitSTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsSTDInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitSTDInjuryPartyInformationTabMetaData.YTDTaxableWageMetaData.BILLING_LOCATION;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddLocationIndicator extends ClaimGroupBenefitsSTDBaseTest implements CaseProfileContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41216", component = CLAIMS_GROUPBENEFITS)
    public void testAddLocationIndicator() {
        final String BILLING_LOCATION_VALUE = "Location Name 1";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        caseProfile.update().perform(tdSpecific().getTestData("UpdateCaseProfile"));

        createDefaultShortTermDisabilityMasterPolicy();
        shortTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultSTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(certificatePolicyTab.getMetaKey(), BILLING_LOCATION.getLabel()), "index=1")
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST REN-41216: Step 1");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY), BenefitsSTDInjuryPartyInformationTab.class, false);
        BenefitsSTDInjuryPartyInformationTab.addYTDTaxableWage.click();
        assertThat(benefitsSTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitsSTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION))
                .hasValue(BILLING_LOCATION_VALUE).isDisabled();

        LOGGER.info("TEST REN-41216: Step 2");
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY),
                BenefitsSTDInjuryPartyInformationTab.class, AdditionalPartiesAdditionalPartyTab.class, true);
        assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.PARTY_NAME)).containsOption(BILLING_LOCATION_VALUE);

        additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.PARTY_NAME).setValue(BILLING_LOCATION_VALUE);
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-41216: Step 3");
        claim.updateBenefit().start(1);
        assertThat(claim.updateBenefit().getWorkspace().getTab(BenefitSTDInjuryPartyInformationTab.class).getAssetList().getAsset(BenefitSTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION))
                .hasValue(BILLING_LOCATION_VALUE).isDisabled();
    }
}
