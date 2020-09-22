package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentDetailsActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.CHECK_EFT;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_SCHEDULE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableScheduledPaymentsOfSeries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestAutoGeneratePaymentCheckNumber extends ClaimGroupBenefitsSTBaseTest {
    private PaymentPaymentPaymentDetailsTab paymentPaymentDetailsTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentDetailsTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = {"REN-38463", "REN-38466"}, component = CLAIMS_GROUPBENEFITS)
    public void testAutoGeneratePaymentCheckNumber() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA")
                .mask(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())));

        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(10).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData_EFTPaymentMethod"), 1);
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-38463: Step 1");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check"));
        assertThat(Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(CHECK_EFT).getValue())).isBetween(2000001, 2999999);

        LOGGER.info("TEST REN-38463: Step 2");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check"));
        int eft2 = Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(CHECK_EFT).getValue());
        assertThat(eft2).isBetween(2000001, 2999999);

        LOGGER.info("TEST REN-38463: Step 4");
        claim.updatePayment().start(2);
        assertThat(paymentPaymentDetailsTab.getAssetList().getAsset(CHECK_EFT_NUMBER)).hasValue(String.valueOf(eft2));

        LOGGER.info("TEST REN-38463: Step 5");
        String checkValue = "test1";
        paymentPaymentDetailsTab.getAssetList().getAsset(MANUAL_CHECK_PAYMENT).setValue("Yes");
        paymentPaymentDetailsTab.getAssetList().getAsset(CHECK).setValue(checkValue);
        claim.updatePayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2)).hasCellWithValue(CHECK_EFT, checkValue);

        LOGGER.info("TEST REN-38463: Step 6-7");
        setPaymentMethodEFT();

        LOGGER.info("TEST REN-38466: Step 1-3");
        claim.createPaymentSeries().perform(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(makeKeyPath(PaymentSeriesPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check")
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), bpStartDate.format((MM_DD_YYYY)))
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), bpStartDate.plusWeeks(2).plusDays(1).format((MM_DD_YYYY))));

        ClaimSummaryPage.tableSummaryOfClaimPaymentSeries.getRow(1).getCell(1).controls.links.getFirst().click();
        String firstPaymentScheduleDate = tableScheduledPaymentsOfSeries.getRow(1).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue();
        LocalDateTime nextPhaseDate = LocalDateTime.parse(firstPaymentScheduleDate, MM_DD_YYYY_H_MM_A);

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate, true);
        mainApp().reopen();
        search(claimNumber);

        claim.updatePaymentSeries().start(1);
        claim.updatePaymentSeries().submit();
        toSubTab(PAYMENTS);
        assertThat(Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(4).getCell(CHECK_EFT).getValue())).isBetween(2000001, 2999999);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38464", component = CLAIMS_GROUPBENEFITS)
    public void testAutoGeneratePaymentCheckNumberNY() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);
        createDefaultStatutoryDisabilityInsuranceClaimForMasterPolicy();
        claim.claimOpen().perform();

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData_EFTPaymentMethod"), 1);
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);

        LOGGER.info("TEST REN-38464: Step 1");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check"));
        assertThat(Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(CHECK_EFT).getValue())).isBetween(4000001, 4999999);

        LOGGER.info("TEST REN-38464: Step 2");
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check"));
        int eft2 = Integer.parseInt(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(CHECK_EFT).getValue());
        assertThat(eft2).isBetween(4000001, 4999999);

        LOGGER.info("TEST REN-38464: Step 4");
        claim.updatePayment().start(2);
        assertThat(paymentPaymentDetailsTab.getAssetList().getAsset(CHECK_EFT_NUMBER)).hasValue(String.valueOf(eft2));

        LOGGER.info("TEST REN-38464: Step 5");
        String checkValue = "test2";
        paymentPaymentDetailsTab.getAssetList().getAsset(MANUAL_CHECK_PAYMENT).setValue("Yes");
        paymentPaymentDetailsTab.getAssetList().getAsset(CHECK).setValue(checkValue);
        claim.updatePayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2)).hasCellWithValue(CHECK_EFT, checkValue);

        LOGGER.info("TEST REN-38464: Step 6-7");
        setPaymentMethodEFT();
    }

    private void setPaymentMethodEFT() {
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check"));
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(CHECK_EFT).getValue()).isNotEmpty();

        claim.updatePayment().start(3);
        paymentPaymentDetailsTab.getAssetList().getAsset(PAYMENT_METHOD).setValueContains("EFT");
        claim.updatePayment().submit();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3)).hasCellWithValue(CHECK_EFT, "");
    }
}
