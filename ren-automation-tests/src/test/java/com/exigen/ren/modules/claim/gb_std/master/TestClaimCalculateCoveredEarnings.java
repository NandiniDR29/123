package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCalculateCoveredEarnings extends ClaimGroupBenefitsSTDBaseTest {
    private static final String AMOUNT = "1000";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23726", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCalculateCoveredEarnings() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_STD);
        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));
        toSubTab(FNOL);
        policyInformationParticipantParticipantInformationTab.navigateToTab();

        ComboBox baseSalaryMode = policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(BASE_SALARY_MODE);
        TextBox baseSalaryAmount = policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(BASE_SALARY_AMOUNT);
        TextBox annualBaseSalary = policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BASE_SALARY);
        TextBox totalAnnualIncomeAmount = policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(TOTAL_ANNUAL_INCOME_AMOUNT);
        TextBox coveredEarnings = policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS);

        LOGGER.info("Step 2, 3, 4");
        assertSoftly(softly -> {
            softly.assertThat(baseSalaryMode)
                    .isPresent().isOptional().hasValue("Annual")
                    .hasOptions("", "Hrly (Assume 40hrs)", "Weekly", "Bi-Weekly", "Annual", "Monthly");
            softly.assertThat(baseSalaryAmount)
                    .isPresent().isOptional().hasValue("");
        });

        assertSoftly(softly -> {
            LOGGER.info("Step 6");
            baseSalaryAmount.setValue(AMOUNT);
            baseSalaryMode.setValue("Annual");
            softly.assertThat(annualBaseSalary).hasValue(new Currency(AMOUNT).toString());

            LOGGER.info("Step 11");
            softly.assertThat(coveredEarnings).hasValue(new Currency(totalAnnualIncomeAmount.getValue(), RoundingMode.HALF_UP).divide(52).toString());

            LOGGER.info("Step 7");
            baseSalaryMode.setValue("Monthly");
            softly.assertThat(annualBaseSalary).hasValue(new Currency(AMOUNT).multiply(12).toString());

            LOGGER.info("Step 12");
            softly.assertThat(coveredEarnings).hasValue(new Currency(totalAnnualIncomeAmount.getValue(), RoundingMode.HALF_UP).divide(52).toString());

            LOGGER.info("Step 8");
            baseSalaryMode.setValue("Bi-Weekly");
            softly.assertThat(annualBaseSalary).hasValue(new Currency(AMOUNT).multiply(26).toString());

            LOGGER.info("Step 13");
            softly.assertThat(coveredEarnings).hasValue(new Currency(totalAnnualIncomeAmount.getValue(), RoundingMode.HALF_UP).divide(52).toString());

            LOGGER.info("Step 9");
            baseSalaryMode.setValue("Weekly");
            softly.assertThat(annualBaseSalary).hasValue(new Currency(AMOUNT).multiply(52).toString());

            LOGGER.info("Step 14");
            softly.assertThat(coveredEarnings).hasValue(new Currency(totalAnnualIncomeAmount.getValue(), RoundingMode.HALF_UP).divide(52).toString());

            LOGGER.info("Step 10");
            baseSalaryMode.setValue("Hrly (Assume 40hrs)");
            softly.assertThat(annualBaseSalary).hasValue(new Currency(AMOUNT).multiply(52).multiply(40).toString());

            LOGGER.info("Step 15");
            softly.assertThat(coveredEarnings).hasValue(new Currency(totalAnnualIncomeAmount.getValue(), RoundingMode.HALF_UP).divide(52).toString());

            LOGGER.info("Step 16");
            coveredEarnings.setValue("1111");
            baseSalaryMode.setValue("Hrly (Assume 40hrs)");
            softly.assertThat(coveredEarnings).hasValue(new Currency("1111").toString());

            LOGGER.info("Step 17");
            baseSalaryAmount.setValue("2000");
            policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ANNUAL_BONUS_AMOUNT).clear();
            baseSalaryMode.setValue("Hrly (Assume 40hrs)");
            softly.assertThat(coveredEarnings).hasValue(new Currency("80000").toString());
        });
    }
}
