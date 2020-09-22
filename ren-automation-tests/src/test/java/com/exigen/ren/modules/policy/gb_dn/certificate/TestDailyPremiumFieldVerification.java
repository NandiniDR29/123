package com.exigen.ren.modules.policy.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.util.Map;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.MONTHLY_TIRED_PRICE_PER_PARTICIPANT;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PremiumSummaryTab.PremiumSummary.*;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PremiumSummaryTab.PremiumSummary.PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.RATE_BASIS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDailyPremiumFieldVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalCertificatePolicyContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26393", component = POLICY_GROUPBENEFITS)
    public void testDailyPremiumFieldVerification() {
        LOGGER.info("General preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()), "12")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), RATE_BASIS.getLabel()), MONTHLY_TIRED_PRICE_PER_PARTICIPANT).resolveLinks());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(policyNumber);

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        Currency rateValue = new Currency((responseFromLog.get("adminCosts[8].perEmployeeAmt")), RoundingMode.HALF_DOWN);

        groupDentalCertificatePolicy.initiate(getDefaultGroupDentalCertificatePolicyData());
        groupDentalCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultGroupDentalCertificatePolicyData()
                .adjust(tdSpecific().getTestData("CoveragesTab").resolveLinks()).resolveLinks(), PremiumSummaryTab.class, true);
        PremiumSummaryTab.buttonRate.click();
        assertSoftly(softly -> {

            LOGGER.info("Step#1 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getHeader().getValue()).contains(DAILY_PREMIUM.getName());

            LOGGER.info("Step#3 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(RATE.getName())).hasValue(String.valueOf(rateValue).replace("$", ""));

            LOGGER.info("Step#4 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(PremiumSummaryTab.PremiumSummary.RATE_BASIS.getName())).hasValue(MONTHLY_TIRED_PRICE_PER_PARTICIPANT);

            LOGGER.info("Step#5 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(PAYOR.getName())).hasValue("Sponsor");

            LOGGER.info("Step#6 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(PAYMENT_MODE.getName())).hasValue("12");

            LOGGER.info("Step#7 verification");
            Currency annualPremium = rateValue.multiply(12);
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(ANNUAL_PREMIUM.getName())).hasValue(String.valueOf(annualPremium));

            LOGGER.info("Step#8 verification");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(MODAL_PREMIUM.getName())).hasValue(String.valueOf(rateValue));

            LOGGER.info("Step#2, 9 verification");
            Currency dailyPremium = new Currency(annualPremium.divide(365), RoundingMode.HALF_DOWN);
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getRowContains(PLAN.getName(), ALACARTE).getCell(DAILY_PREMIUM.getName())).hasValue(String.valueOf(dailyPremium));
        });
    }
}
