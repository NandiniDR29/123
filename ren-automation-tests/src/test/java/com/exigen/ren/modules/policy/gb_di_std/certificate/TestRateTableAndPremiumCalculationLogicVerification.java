package com.exigen.ren.modules.policy.gb_di_std.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.InsuredTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRateTableAndPremiumCalculationLogicVerification extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext {

    private static final String SPONSOR = "Sponsor";
    private static final String MEMBER = "Member";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25286", component = POLICY_GROUPBENEFITS)
    public void testRateTableAndPremiumCalculationLogicVerification() {
        LOGGER.info("REN-25286 preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(tdSpecific().getTestData("TestData_TwoPlans")
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        shortTermDisabilityMasterPolicy.policyInquiry().start();
        NavigationPage.PolicyNavigation.leftMenu(CLASSIFICATION_MANAGEMENT.get());
        String finalRateMpValue = Float.valueOf(tableClassificationSubGroupsAndRatingInfo.getRowContains(CLASSIFICATION_SUB_GROUP_NAME.getName(), "0-24")
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.RATE.getName()).getValue()).toString();
        Tab.buttonCancel.click();

        LOGGER.info("Step#1, 2 verification");
        shortTermDisabilityCertificatePolicy.initiate(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        shortTermDisabilityCertificatePolicy.getDefaultWorkspace().fillUpTo((shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(InsuredTab.class.getSimpleName(), ANNUAL_EARNINGS.getLabel()), new Currency("42119").toString()))
                .adjust(TestData.makeKeyPath(CoveragesTab.class.getSimpleName(), CoveragesTabMetaData.PLAN.getLabel()), "CON")
                .adjust(TestData.makeKeyPath(CoveragesTab.class.getSimpleName(), CoveragesTabMetaData.APPROVED_PERCENT.getLabel()), "60"), PremiumSummaryTab.class, false);
        Tab.buttonRate.click();

        assertSoftly(softly -> {
            Row sponsorRowValue = PremiumSummaryTab.tablePremiumSummary.getRowContains(PAYOR, SPONSOR);
            Row memberRowValue = PremiumSummaryTab.tablePremiumSummary.getRowContains(PAYOR, MEMBER);

            softly.assertThat(sponsorRowValue.getCell((RATE_BASIS))).hasValue(tdSpecific().getValue("PlanDefinitionTab_CON", "Rating", "Rate Basis"));
            softly.assertThat(memberRowValue.getCell((RATE_BASIS))).hasValue(tdSpecific().getValue("PlanDefinitionTab_CON", "Rating", "Rate Basis"));

            LOGGER.info("Step#3 verification");
            softly.assertThat(sponsorRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE)).hasValue(finalRateMpValue);
            softly.assertThat(memberRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE)).hasValue(finalRateMpValue);

            LOGGER.info("Step#4 verification");
            softly.assertThat(sponsorRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.PLAN)).hasValue("CON");
            softly.assertThat(memberRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.PLAN)).hasValue("CON");

            LOGGER.info("Step#5 verification");
            softly.assertThat(memberRowValue.getCell(PAYMENT_MODE)).hasValue(tdSpecific().getValue("PlanDefinitionTab_CON", "Member Payment Mode"));
            softly.assertThat(sponsorRowValue.getCell(PAYMENT_MODE)).hasValue(tdSpecific().getValue("PlanDefinitionTab_CON", "Sponsor Payment Mode"));

            LOGGER.info("Step#6 verification");
            Currency benefitAmount = new Currency(sponsorRowValue.getCell(BENEFIT_AMOUNT).getValue());
            double rateAmount = new Double(sponsorRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE).getValue());
            int insureds = Integer.parseInt(sponsorRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.INSUREDS).getValue());
            Currency totalAnnualPremium = new Currency(benefitAmount.divide(10)).multiply(rateAmount).multiply(12).divide(insureds);
            softly.assertThat(PremiumSummaryTab.labelTotalPremiumCp).hasValue(totalAnnualPremium.toString());

            LOGGER.info("Step#7 verification");
            Currency annualPremiumMember = new Currency(new Currency(totalAnnualPremium.multiply(80), RoundingMode.HALF_DOWN).divide(100), RoundingMode.HALF_DOWN);
            int paymentModeMember = Integer.parseInt((memberRowValue.getCell(PAYMENT_MODE).getValue()));
            Currency modelPremiumMember = annualPremiumMember.divide(paymentModeMember);

            softly.assertThat(memberRowValue.getCell(ANNUAL_PREMIUM)).hasValue(annualPremiumMember.toString());
            softly.assertThat(memberRowValue.getCell(MODAL_PREMIUM)).hasValue(modelPremiumMember.toString());

            LOGGER.info("Step#8 verification");
            Currency annualPremiumSponsor = new Currency(totalAnnualPremium.multiply(20), RoundingMode.HALF_DOWN).divide(100);
            int paymentModeSponsor = Integer.parseInt((sponsorRowValue.getCell(PAYMENT_MODE).getValue()));
            Currency modelPremiumSponsor = annualPremiumSponsor.divide(paymentModeSponsor);

            softly.assertThat(sponsorRowValue.getCell(ANNUAL_PREMIUM)).hasValue(annualPremiumSponsor.toString());
            softly.assertThat(sponsorRowValue.getCell(MODAL_PREMIUM)).hasValue(modelPremiumSponsor.toString());
        });
    }
}