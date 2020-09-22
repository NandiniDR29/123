package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaimPaymentCalcForPresumptiveDisabilityBenefit;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDIncidentTabMetaData.DATE_OF_HIRE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPaymentCalcForPresumptiveDisabilityBenefit extends ScenarioTestClaimPaymentCalcForPresumptiveDisabilityBenefit {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28462", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForPresumptiveDisabilityBenefitTC02() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        TestData tdClaim = disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(TestData.makeKeyPath(benefitsLTDIncidentTab.getMetaKey(), DATE_OF_HIRE.getLabel()), currentDate.minusMonths(7).format(MM_DD_YYYY));

        super.testClaimPaymentCalcForPresumptiveDisabilityBenefitTC02(false, tdClaim);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28463", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForPresumptiveDisabilityBenefitTC03() {

        super.testClaimPaymentCalcForPresumptiveDisabilityBenefitTC03(false, DATA_GATHER);
    }
}