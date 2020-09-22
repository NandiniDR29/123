package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaimFamilyCareBenefitPaymentSeries;
import org.testng.annotations.Test;

import java.time.format.DateTimeFormatter;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimFamilyCareBenefitPaymentSeries extends ScenarioTestClaimFamilyCareBenefitPaymentSeries {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-29702", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFamilyCareBenefitPaymentSeries() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData").resolveLinks()));

        createDefaultLongTermDisabilityCertificatePolicy();

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(BenefitsLTDInjuryPartyInformationTab.class.getSimpleName(), BenefitsLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS.getLabel()), "$300,000.00")
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), "12/31/2099")
                .resolveLinks());

        super.testClaimFamilyCareBenefitPaymentSeries();
    }
}