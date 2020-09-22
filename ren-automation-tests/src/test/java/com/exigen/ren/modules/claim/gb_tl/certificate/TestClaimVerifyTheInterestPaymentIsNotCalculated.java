/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.claim.common.metadata.EventInformationAuthorityReportTabMetaData.CONTACT_PREFERENCE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyTheInterestPaymentIsNotCalculated extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14242", component = CLAIMS_GROUPBENEFITS)
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
        termLifeClaim.create(termLifeClaim.getDefaultTestData("DataGatherCertificate", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(eventInformationAuthorityReportTab.getMetaKey(), CONTACT_PREFERENCE.getLabel()), "Mail"));
        LOGGER.info("---=={Step 1}==---");
        claim.addBenefit().perform(tdSpecific().getTestData("TestData_Accidental_Death"));
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.OVERVIEW.get());
        claim.claimSubmit().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        LOGGER.info("---=={Step 2}==---");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.OVERVIEW.get());
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("---=={Step 3}==---");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 2);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations).hasRows(1);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations.getRow(1).getCell(ClaimConstants.ClaimAllSingleBenefitCalculationsTable.SINGLE_BENEFIT_NUMBER)).hasValue("2-1");

        LOGGER.info("---=={Step 4}==---");
        claim.addPayment().start();
        LocalDateTime currentDatePlus20 = TimeSetterUtil.getInstance().getCurrentTime().plusDays(20);
        LocalDateTime currentDatePlus10 = TimeSetterUtil.getInstance().getCurrentTime().plusDays(10);

        claim.addPayment().getWorkspace().fillUpTo(tdSpecific().getTestData("TestDataClaimFinalPayment"), PaymentCalculatorTab.class);

        assertSoftly(softly -> {

            PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
            paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAID_UP_TO_DATE).setValue(currentDatePlus20.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAYMENT_AMOUNT)).hasValue("$0.822");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(STATE)).hasValue("Hawaii");

            String firstDayOfYear = String.format("01/01/%s", currentDatePlus10.format(DateTimeFormatter.ofPattern("YYYY")));
            paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAID_UP_TO_DATE).setValue(firstDayOfYear);
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(INTEREST_PAYMENT_AMOUNT)).hasValue("$0.000");
        });
    }
}