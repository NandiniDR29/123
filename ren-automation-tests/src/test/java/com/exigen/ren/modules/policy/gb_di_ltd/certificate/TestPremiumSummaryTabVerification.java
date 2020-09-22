package com.exigen.ren.modules.policy.gb_di_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPremiumSummaryTabVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityCertificatePolicyContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28172", "REN-28176"}, component = POLICY_GROUPBENEFITS)
    public void testPremiumSummaryTabVerification() {
        LOGGER.info("General preconditions");
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(longTermDisabilityMasterPolicy.getType());
        TestData certificatePolicyTestData = longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        longTermDisabilityCertificatePolicy.initiate(certificatePolicyTestData);
        longTermDisabilityCertificatePolicy.getDefaultWorkspace().fillUpTo(certificatePolicyTestData, PremiumSummaryTab.class, false);

        LOGGER.info("REN-28172 Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).doesNotContain(BENEFIT_AMOUNT);
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).contains(VOLUME, RATE_BASIS, RATE);

            LOGGER.info("REN-28176 Step#1 verification");
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).contains(ANNUAL_PREMIUM, PAYOR, PAYMENT_MODE, MODAL_PREMIUM, DAILY_PREMIUM);
        });

        LOGGER.info("REN-28172 Step#2 verification");
        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.COVERAGES.get());
        assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.BENEFIT_TYPE)).hasValue("Percentage of Monthly Salary - Single Value");
        coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_NO);
        Currency annualEarnings = new Currency(coveragesTab.getAssetList().getAsset(ANNUAL_EARNINGS).getValue());
        Currency maxMonthlyBenefitAmount = new Currency(coveragesTab.getAssetList().getAsset(MAXIMUM_MONTHLY_BENEFIT_AMOUNT).getValue());
        int benefitPercentage = Integer.parseInt(coveragesTab.getAssetList().getAsset(BENEFIT_PERCENTAGE).getValue().replace("%", ""));
        Currency maximumMonthlyCoveredPayroll = new Currency(maxMonthlyBenefitAmount.divide(benefitPercentage).multiply(100), RoundingMode.HALF_UP);
        Currency monthlyEarnings = new Currency(annualEarnings.divide(12), RoundingMode.HALF_UP);

        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get());
        Tab.buttonRate.click();
        if (maximumMonthlyCoveredPayroll.getValue().isLessThan(monthlyEarnings.getValue())) {
            assertThat(tablePremiumSummary.getColumn(VOLUME).getCell(1)).hasValue(maximumMonthlyCoveredPayroll.toString());
        } else {
            assertThat(tablePremiumSummary.getColumn(VOLUME).getCell(1)).hasValue(monthlyEarnings.toString());
        }

        LOGGER.info("REN-28172 Step#4 verification");
        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.COVERAGES.get());
        coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_YES);
        coveragesTab.getAssetList().getAsset(EOI_STATUS).setValue("Pending");
        Currency monthlyBenefitAmount = new Currency(coveragesTab.getAssetList().getAsset(MONTHLY_BENEFIT_AMOUNT).getValue());
        Currency value = new Currency((monthlyBenefitAmount.divide(benefitPercentage).multiply(100)), RoundingMode.HALF_UP);
        coveragesTab.getAssetList().getAsset(PENDING_AMOUNT).setValue("60");
        coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT).setValue("60");

        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get());
        PremiumSummaryTab.buttonRate.click();
        assertThat(tablePremiumSummary.getColumn(VOLUME).getCell(1)).hasValue(value.toString());
        LongTermDisabilityCertificatePolicyContext.premiumSummaryTab.submitTab();
    }
}