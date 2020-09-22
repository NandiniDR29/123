package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesWitnessTab;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageDeterminationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.*;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimEligibilityUIAndRules extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32433", component = CLAIMS_GROUPBENEFITS)
    public void testClaimEligibilityUIAndRules() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        String todayMinusMonth = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(1).format(DateTimeUtils.MM_DD_YYYY);
        shortTermDisabilityMasterPolicy.createPolicy(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_AllPlans")
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), todayMinusMonth));

        LOGGER.info("Step 1");
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        TestData testDataClaim = disabilityClaim.getSTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), todayDate)
                .adjust(tdSpecific().getTestData("TestData_Claim_MultipleCoverages").resolveLinks());
        disabilityClaim.initiate(testDataClaim);

        LOGGER.info("Steps 2, 3");
        disabilityClaim.getDefaultWorkspace().fillUpTo(testDataClaim, BenefitCoverageDeterminationTab.class, false);

        LOGGER.info("Step 13");
        AbstractContainer<?, ?> assetListCoverageDetermin = benefitCoverageDeterminationTab.getAssetList();
        assertThat(assetListCoverageDetermin.getAsset(APPROVED_THROUGH_DATE)).isOptional();
        assetListCoverageDetermin.getAsset(LIABILITY_DECISION).setValue("Yes, the loss is covered");
        assertThat(assetListCoverageDetermin.getAsset(APPROVED_THROUGH_DATE)).isRequired();
        benefitCoverageDeterminationTab.submitTab();
        assertThat(assetListCoverageDetermin.getAsset(APPROVED_THROUGH_DATE)).hasWarningWithText(String.format(ERROR_PATTERN, APPROVED_THROUGH_DATE.getLabel()));

        LOGGER.info("Step 15");
        String todayMinusMonthMinusDay = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        assetListCoverageDetermin.getAsset(APPROVED_THROUGH_DATE).setValue(todayDate);
        assetListCoverageDetermin.getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE).setValue(todayMinusMonthMinusDay);
        benefitCoverageDeterminationTab.submitTab();
        assertThat(assetListCoverageDetermin.getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE)).hasWarningWithText(EFFECTIVE_DATE_IS_PRIOR_TO_GROUP_COVERAGE_EFFECTIVE_DATE);

        LOGGER.info("Step 16");
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonSubmitClaim.click();
        assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(), EFFECTIVE_DATE_IS_PRIOR_TO_GROUP_COVERAGE_EFFECTIVE_DATE);

        LOGGER.info("Step 17");
        Tab.buttonCancel.click();
        benefitCoverageDeterminationTab.navigate();
        assetListCoverageDetermin.getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE).setValue(todayDate);
        assertThat(assetListCoverageDetermin.getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE)).hasNoWarning();
        benefitCoverageDeterminationTab.submitTab();
        assertThat(AdditionalPartiesWitnessTab.buttonAddWitness).isPresent();

        LOGGER.info("Step 18");
        benefitCoverageDeterminationTab.navigate();
        String todayPlusOneDay = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        assetListCoverageDetermin.getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE).setValue(todayPlusOneDay);
        assertThat(assetListCoverageDetermin.getAsset(INSURED_PERSON_COVERAGE_EFFECTIVE_DATE)).hasWarningWithText(EFFECTIVE_DATE_IS_AFTER_THE_DATE_OF_LOSS);
        benefitCoverageDeterminationTab.submitTab();
        assertThat(AdditionalPartiesWitnessTab.buttonAddWitness).isPresent();

        LOGGER.info("Step 19");
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);
    }

}
