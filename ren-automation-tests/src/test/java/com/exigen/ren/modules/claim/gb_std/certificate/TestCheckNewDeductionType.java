package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.BENEFIT;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.CLAIM_GB;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCheckNewDeductionType extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37108", component = CLAIMS_GROUPBENEFITS)
    public void testCheckNewDeductionType() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_STD);

        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), BENEFIT.getLabel()), "index=1"));
        claim.claimOpen().perform();
        String customerName = ClaimSummaryPage.tableClaimSponsor.getRow(1).getCell("Company Name").getValue();
        StringBuffer insuredName = new StringBuffer(ClaimSummaryPage.tableClaimParticipant.getRow(1).getCell("Participant").getValue()).insert(12, " ");

        LOGGER.info("TEST REN-37108: Step 1");
        claim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), OtherIncomeBenefitActionTab.class);
        OtherIncomeBenefitActionTab.buttonSaveAndExit.click();
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), DateTimeFormatter.ofPattern("MM/dd/yyyy")).atStartOfDay();

        claim.recalculateSingleBenefitAmount().start(1);
        DeductionsActionTab deductionsActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(DeductionsActionTab.class);
        deductionsActionTab.navigate();
        DeductionsActionTab.buttonAdd.click();
        assertSoftly(softly -> {
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(TYPE_OF_DEDUCTION)).containsOption("Employer Reimbursement");
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(PARTY)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(BEGINNING_DATE)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(DEDUCTION_AMOUNT)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(CODE)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(PRIORITY)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(THROUGH_DATE)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(APPLY_PRE_TAX)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(CODE)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(MEMO)).isPresent();
        });

        LOGGER.info("TEST REN-37108: Step 2-5");
        deductionsActionTab.getAssetList().getAsset(TYPE_OF_DEDUCTION).setValue("Employer Reimbursement");
        assertSoftly(softly -> {
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(CODE)).hasValue("EPRE");
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(PRIORITY)).hasValue("5");
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(NON_PROVIDER_PAYMENT_TYPE)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(STATE_SERVICES_PROVIDED_IN)).isPresent();
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(PARTY)).containsAllOptions("", customerName + " - Sponsor", insuredName + " - Additional Party");
            softly.assertThat(deductionsActionTab.getAssetList().getAsset(APPLY_PRE_TAX)).hasValue("");
        });

        LOGGER.info("TEST REN-37108: Step 6");
        deductionsActionTab.removeDeduction();
        deductionsActionTab.fillTab(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), PARTY.getLabel()), "contains=Additional")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), THROUGH_DATE.getLabel()), bpStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY)));
        DeductionsActionTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-37108: Step 10");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(6).format(MM_DD_YYYY)), PaymentCalculatorTab.class);
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        checkPaymentCalculatorTab(paymentCalculatorTab, PaymentCalculatorActionTabMetaData.PAYMENT_REDUCTION, bpStartDate);
        PaymentCalculatorTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST REN-37108: Step 15");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), bpStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY)), PaymentSeriesCalculatorActionTab.class);
        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);
        paymentSeriesCalculatorActionTab.navigate();
        checkPaymentCalculatorTab(paymentSeriesCalculatorActionTab, PaymentSeriesCalculatorActionTabMetaData.PAYMENT_REDUCTION, bpStartDate);
        assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_REDUCTION).getAsset(DEDUCTION_AMOUNT)).hasValue("$100.00");
    }

    private void checkPaymentCalculatorTab(Tab actionTab, AssetDescriptor<AssetList> assetList, LocalDateTime bpStartDate) {
        assertSoftly(softly -> {
            softly.assertThat(actionTab.getAssetList().getAsset(assetList).getAsset(BEGINNING_DATE)).hasValue(bpStartDate.format(MM_DD_YYYY));
            softly.assertThat(actionTab.getAssetList().getAsset(assetList).getAsset(THROUGH_DATE)).hasValue(bpStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY));
            softly.assertThat(actionTab.getAssetList().getAsset(assetList).getAsset(APPLY_PRE_TAX)).hasValue("No");
            softly.assertThat(actionTab.getAssetList().getAsset(assetList).getAsset(PRIORITY)).hasValue("5");
            softly.assertThat(actionTab.getAssetList().getAsset(assetList).getAsset(PARTY)).valueContains("Additional");
        });
    }
}
