package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_DAYS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_BENEFIT_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyBPForToAge70 extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33868", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyBPForToAge70() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime dateOfBirth = currentDate.minusYears(68);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.minusMonths(1).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", BENEFIT_SCHEDULE.getLabel(), MAXIMUM_BENEFIT_PERIOD.getLabel()), "To Age 70")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", BENEFIT_SCHEDULE.getLabel(), ELIMINATION_PERIOD_DAYS.getLabel()), "180"));

        LOGGER.info("TEST: Steps 1-3");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), DATE_OF_BIRTH.getLabel()), dateOfBirth.format(MM_DD_YYYY)));

        disabilityClaim.claimOpen().perform();

        LOGGER.info("TEST: Step 4");
        disabilityClaim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        LOGGER.info("TEST: Step 5");
        claim.viewSingleBenefitCalculation().perform(1);

        assertThat(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod)
                .hasMatchingRows(BENEFIT_PERIOD_START_DATE.getName(), currentDate.plusDays(180).format(MM_DD_YYYY))
                .hasMatchingRows(BENEFIT_PERIOD.getName(), "To Age 70")
                .hasMatchingRows(BENEFIT_PERIOD_END_DATE.getName(), dateOfBirth.plusYears(70).minusDays(1).format(MM_DD_YYYY));
    }
}