package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.DEP_BTL;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.*;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCalculationLogicVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28308", component = POLICY_GROUPBENEFITS)
    public void testCalculationLogicVerification() {
        LOGGER.info("REN-28308 preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicyViaUI(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataCoverages").resolveLinks()));

        termLifeInsuranceMasterPolicy.policyInquiry().start();
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT.get());
        ClassificationManagementTab.coveragesTable.getRow(COVERAGE_NAME.getName(), BTL).getCell(COVERAGE_NAME.getName()).click();
        String employeeBasicLifeRateValue = Float.valueOf(tableCoverageRelationships.getColumn(TableConstants.CoverageRelationships.RATE.getName())
                .getCell(1).getValue()).toString();
        ClassificationManagementTab.buttonCancel.click();

        LOGGER.info("Steps#1,2 execution");
        termLifeInsuranceCertificatePolicy.initiate(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestDataCertificatePolicy").resolveLinks()), PremiumSummaryTab.class);

        LOGGER.info("Step#3 verification");
        PremiumSummaryTab.buttonRate.click();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatusCp).valueContains(PREMIUM_CALCULATED);

            LOGGER.info("Step#4 verification");
            Row employeeBasicLifeInsuranceRowValue = tablePremiumSummary.getRowContains(PolicyConstants.PolicyCoverageSummaryTable.COVERAGE_NAME, BTL);
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(RATE)).hasValue(employeeBasicLifeRateValue);

            LOGGER.info("Step#5 verification");
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(RATE_BASIS)).hasValue(tdSpecific().getValue("PlanDefinitionBasicLifeInsurance", "Rating", "Rate Basis"));

            LOGGER.info("Step#6 verification");
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(PAYOR)).hasValue("Sponsor");

            LOGGER.info("Step#7 verification");
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(PAYMENT_MODE)).hasValue(tdSpecific().getValue("PlanDefinitionBasicLifeInsurance", "Sponsor Payment Mode"));

            LOGGER.info("Step#8 verification");
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(INSUREDS)).hasValue("1");

            LOGGER.info("Step#9 verification");
            Currency sponsorBenefitAmount = new Currency(employeeBasicLifeInsuranceRowValue.getCell(BENEFIT_AMOUNT).getValue());
            double sponsorRateAmount = new Double(employeeBasicLifeInsuranceRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE).getValue());
            Currency sponsorAnnualPremium = new Currency(((sponsorBenefitAmount.divide(1000)).multiply(sponsorRateAmount)).multiply(12), RoundingMode.HALF_DOWN);
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(ANNUAL_PREMIUM)).hasValue(String.valueOf(sponsorAnnualPremium));

            LOGGER.info("Step#10 verification");
            int sponsorPaymentMode = Integer.parseInt(employeeBasicLifeInsuranceRowValue.getCell(PAYMENT_MODE).getValue());
            Currency sponsorModalPremium = sponsorAnnualPremium.divide(sponsorPaymentMode);
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(MODAL_PREMIUM)).hasValue(String.valueOf(sponsorModalPremium));

            LOGGER.info("Step#11 verification");
            Currency sponsorDailyPremium = new Currency(sponsorAnnualPremium.divide(365), RoundingMode.HALF_DOWN);
            softly.assertThat(employeeBasicLifeInsuranceRowValue.getCell(DAILY_PREMIUM)).hasValue(String.valueOf(sponsorDailyPremium));

            LOGGER.info("Step#13 verification");
            Row dependentBasicLifeInsuranceRowValue = tablePremiumSummary.getRowContains(PolicyConstants.PolicyCoverageSummaryTable.COVERAGE_NAME, DEP_BTL);
            softly.assertThat(dependentBasicLifeInsuranceRowValue.getCell(RATE_BASIS)).hasValue(tdSpecific().getValue("PlanDefinitionDependentBasicLife", "Rating", "Rate Basis"));

            LOGGER.info("Step#14 verification");
            softly.assertThat(dependentBasicLifeInsuranceRowValue.getCell(PAYOR)).hasValue("Member");

            LOGGER.info("Step#17 verification");
            double memberRateAmount = new Double(dependentBasicLifeInsuranceRowValue.getCell(PolicyConstants.PolicyCoverageSummaryTable.RATE).getValue());
            Currency memberAnnualPremium = new Currency(memberRateAmount, RoundingMode.HALF_DOWN).multiply(12);
            softly.assertThat(dependentBasicLifeInsuranceRowValue.getCell(ANNUAL_PREMIUM)).hasValue(String.valueOf(memberAnnualPremium));
        });
    }
}