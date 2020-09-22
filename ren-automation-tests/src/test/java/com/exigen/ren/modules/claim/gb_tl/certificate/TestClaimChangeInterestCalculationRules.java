/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.EventInformationAuthorityReportTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.gb_tl.tabs.BenefitDeathBeneficiaryTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
import org.testng.annotations.Test;

import javax.money.Monetary;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_tl.metadata.BenefitDeathBeneficiaryTabMetaData.REMOVE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimChangeInterestCalculationRules extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14212", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeInterestCalculationRules() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(tdSpecific().getTestData("TestDataCaseProfile"), termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(tdSpecific().getTestData("TestDataPolicyMaster")
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        termLifeInsuranceCertificatePolicy.createPolicyViaUI(tdSpecific().getTestData("TestDataPolicyCertificate")
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        termLifeClaim.create(termLifeClaim.getDefaultTestData("DataGatherCertificate", "TestData_Without_Benefits")
                .adjust(EventInformationAuthorityReportTab.class.getSimpleName(), new SimpleDataProvider()));

        LOGGER.info("---=={Step 1}==---");
        claim.addBenefit().start();
        BenefitDeathBeneficiaryTab benefitDeathBeneficiaryTab = claim.addBenefit().getWorkspace().getTab(BenefitDeathBeneficiaryTab.class);
        claim.addBenefit().getWorkspace().fillUpTo(tdSpecific().getTestData("TestData_Accidental_Death"), benefitDeathBeneficiaryTab.getClass());
        benefitDeathBeneficiaryTab.getAssetList().getAsset(REMOVE).click();
        Page.dialogConfirmation.buttonYes.click();
        claim.addBenefit().getWorkspace().fillFrom(tdSpecific().getTestData("TestData_Accidental_Death"), benefitDeathBeneficiaryTab.getClass());
        Tab.buttonSaveAndExit.click();

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.OVERVIEW.get());
        claim.claimSubmit().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        LOGGER.info("---=={Step 2}==---");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.OVERVIEW.get());
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("---=={Step 3}==---");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 1);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations).hasRows(1);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations.getRow(1).getCell(ClaimConstants.ClaimAllSingleBenefitCalculationsTable.SINGLE_BENEFIT_NUMBER)).hasValue("1-1");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdSpecific().getTestData("TestDataClaimFinalPayment"), PaymentCalculatorTab.class);
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);

        LOGGER.info("---=={Step 4}==---");
        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).hasValue("Interest Payment");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAID_UP_TO_DATE)).hasValue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(30).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(AMOUNT_TO_CALCULATE_INTEREST_ON)).hasValue("$1,000.00");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(NUMBER_OF_DAYS_FROM_DATE_OF_LOSS)).hasValue("30");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAYMENT_AMOUNT)).hasValue("$1.233");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(STATE)).hasValue("Connecticut");
        });

        LOGGER.info("---=={Step 5}==---");
        int daysOffset = 40;
        assertSoftly(softly -> {
            MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(Locale.US).set(CurrencyStyle.SYMBOL).set("pattern", "¤#,###.000").build());
            paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAID_UP_TO_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysOffset).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAYMENT_AMOUNT)).hasValue(format.format(Money.of(daysOffset * 0.015 / 365 * 1000, Monetary.getCurrency(Locale.US))));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(STATE)).hasValue("Connecticut");
        });

        assertSoftly(softly -> {
            claim.addPayment().submit();
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TRANSACTION_STATUS)).hasValue("Approved");
        });
    }
}
