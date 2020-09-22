package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.A_LA_CARTE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.ClassificationManagementTab.tablePlanTierAndRatingInfo;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDailyPremiumFieldVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-27527", component = POLICY_GROUPBENEFITS)
    public void testDailyPremiumFieldVerification() {

        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()));

        groupVisionMasterPolicy.policyInquiry().start();
        classificationManagementMpTab.navigateToTab();
        Currency rateValue = new Currency(tablePlanTierAndRatingInfo.getColumn(TableConstants.PlanTierAndRatingSelection.RATE.getName()).getCell(1).getValue());
        Tab.buttonCancel.click();

        groupVisionCertificatePolicy.initiate(getDefaultVSCertificatePolicyData());
        groupVisionCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultVSCertificatePolicyData(), PremiumSummaryTab.class, true);
        Tab.buttonRate.click();

        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Steps#1, 2 verification");
            assertThat(PremiumSummaryTab.tablePremiumSummary.getHeader().getValue()).contains(PolicyConstants.PolicyCoverageSummaryTable.DAILY_PREMIUM);

            LOGGER.info("Step#3 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE)).hasValue(rateValue.toPlainString());

            LOGGER.info("Step#4 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE_BASIS)).hasValue("Monthly Tiered Price Per Participant");

            LOGGER.info("Step#5 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.PAYOR)).hasValue("Member");

            LOGGER.info("Step#6 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.PAYMENT_MODE)).hasValue("12");

            LOGGER.info("Step#7 verification");
            Currency annualPremium = new Currency(rateValue.multiply(12), RoundingMode.HALF_DOWN);
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.ANNUAL_PREMIUM)).hasValue(String.valueOf(annualPremium));

            LOGGER.info("Step#8 verification");
            Currency memberModalPremium = annualPremium.divide(12);
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM)).hasValue(String.valueOf(memberModalPremium));

            LOGGER.info("Step#9 verification");
            Currency dailyPremium = new Currency(annualPremium.divide(365), RoundingMode.HALF_DOWN);
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), A_LA_CARTE)
                    .getCell(PolicyConstants.PolicyCoverageSummaryTable.DAILY_PREMIUM)).hasValue(String.valueOf(dailyPremium));
        });
    }
}
