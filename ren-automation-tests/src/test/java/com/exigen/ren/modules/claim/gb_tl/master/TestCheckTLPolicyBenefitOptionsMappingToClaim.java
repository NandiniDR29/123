package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationDependentDependentCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationDependentDependentInformationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.VOLUNTARY_LIFE_PLAN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentInformationTabMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentInformationTabMetaData.RELATIONSHIP_TO_PARTICIPANT;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckTLPolicyBenefitOptionsMappingToClaim extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28225", component = CLAIMS_GROUPBENEFITS)
    public void testCheckTLPolicyBenefitOptionsMappingToClaim() {
        TestData tdCreateClaim = termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY);
        Currency tenThousand = new Currency("10000");
        Currency oneHundredThousand = new Currency("100000");
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(tdSpecific().getTestData("TestData")
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)).resolveLinks());
        termLifeClaim.initiate(tdCreateClaim);
        termLifeClaim.getDefaultWorkspace().fillUpTo(tdCreateClaim, PolicyInformationParticipantParticipantCoverageTab.class);

        LOGGER.info("TEST REN-28225: Step 1-2");
        checkLimitAmountOfCoverage(BASIC_LIFE_PLAN, ADD, new Currency("200000"));

        LOGGER.info("TEST REN-28225: Step 3-4");
        checkLimitAmountOfCoverage(VOLUNTARY_LIFE_PLAN, VOL_ADD, new Currency("500000"));

        LOGGER.info("TEST REN-28225: Step 5-6");
        checkLimitAmountOfCoverage(BASIC_LIFE_PLAN, BTL, new Currency("200000"));

        LOGGER.info("TEST REN-28225: Step 7-8");
        checkLimitAmountOfCoverage(VOLUNTARY_LIFE_PLAN, VOL_BTL, new Currency("500000"));

        LOGGER.info("TEST REN-28225: Step 9-10 for Spouse");
        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.fillTab(termLifeClaim.getDefaultTestData().getTestData("DataGatherWithoutPolicy", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(PolicyInformationDependentDependentInformationTab.class.getSimpleName(), RELATIONSHIP_TO_PARTICIPANT.getLabel()), "Spouse/Domestic Partner"));
        PolicyInformationDependentDependentInformationTab.buttonNext.click();
        checkLimitAmountOfDependedCoverage(BASIC_LIFE_PLAN, DEP_ADD, tenThousand);

        LOGGER.info("TEST REN-28225: Step 11-12 for Spouse");
        checkLimitAmountOfDependedCoverage(VOLUNTARY_LIFE_PLAN, DEP_VOL_ADD, oneHundredThousand);

        LOGGER.info("TEST REN-28225: Step 13-14 for Spouse");
        checkLimitAmountOfDependedCoverage(BASIC_LIFE_PLAN, DEP_BTL, tenThousand);

        LOGGER.info("TEST REN-28225: Step 15-16");
        checkLimitAmountOfDependedCoverage(VOLUNTARY_LIFE_PLAN, SP_VOL_BTL, oneHundredThousand);

        LOGGER.info("TEST REN-28225: Step 11-12 for Child");
        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.getAssetList().getAsset(RELATIONSHIP_TO_PARTICIPANT).setValue("Dependent Child");
        PolicyInformationDependentDependentInformationTab.buttonNext.click();
        checkLimitAmountOfDependedCoverage(VOLUNTARY_LIFE_PLAN, DEP_VOL_ADD, tenThousand);

        LOGGER.info("TEST REN-28225: Step 13-14 for Child");
        checkLimitAmountOfDependedCoverage(BASIC_LIFE_PLAN, DEP_BTL, new Currency("5000"));

        LOGGER.info("TEST REN-28225: Step 17-18");
        checkLimitAmountOfDependedCoverage(VOLUNTARY_LIFE_PLAN, DEP_VOL_BTL, tenThousand);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-29597", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCheckPercentageOfEmployeeAmount() {
        TestData tdCreateClaim = termLifeClaim.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY);
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(tdSpecific().getTestData("TestData_Multiple")
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)).resolveLinks());
        termLifeClaim.initiate(tdCreateClaim);
        termLifeClaim.getDefaultWorkspace().fillUpTo(tdCreateClaim, PolicyInformationParticipantParticipantCoverageTab.class);

        LOGGER.info("TEST REN-29597: Step 1-2");
        List<String> limitAmounts = new ArrayList<>(Arrays.asList("", "25000.00", "50000.00", "100000.00", "150000.00", "200000.00"));
        checkLimitAmountMultiplyOfCoverage(VOLUNTARY_LIFE_PLAN, VOL_ADD, limitAmounts);

        LOGGER.info("TEST REN-29597: Step 3-4");
        checkLimitAmountMultiplyOfCoverage(VOLUNTARY_LIFE_PLAN, VOL_BTL, limitAmounts);

        PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage.click();
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValueContains(BASIC_LIFE_PLAN);
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValueContains(ADD);

        PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage.click();
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValueContains(BASIC_LIFE_PLAN);
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValueContains(BTL);

        LOGGER.info("TEST REN-29597: Step 5-6 for Spouse");
        String[] spouseLimitAmounts = new String[]{"", "10000.00", "25000.00", "50000.00", "75000.00", "100000.00"};
        String[] childLimitAmounts = new String[]{"", "2500.00", "5000.00", "10000.00"};
        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.fillTab(termLifeClaim.getDefaultTestData().getTestData("DataGatherWithoutPolicy", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(PolicyInformationDependentDependentInformationTab.class.getSimpleName(), RELATIONSHIP_TO_PARTICIPANT.getLabel()), "Spouse/Domestic Partner")
                .adjust(makeKeyPath(PolicyInformationDependentDependentInformationTab.class.getSimpleName(), DATE_OF_BIRTH.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().minusYears(20).format(MM_DD_YYYY)));
        PolicyInformationDependentDependentInformationTab.buttonNext.click();
        checkLimitAmountMultiplyOfDependedCoverage(BASIC_LIFE_PLAN, DEP_ADD, childLimitAmounts);

        LOGGER.info("TEST REN-29597: Step 7-8 for Spouse");
        checkLimitAmountMultiplyOfDependedCoverage(VOLUNTARY_LIFE_PLAN, DEP_VOL_ADD, spouseLimitAmounts);

        LOGGER.info("TEST REN-29597: Step 9-10 for Spouse");
        checkLimitAmountMultiplyOfDependedCoverage(BASIC_LIFE_PLAN, DEP_BTL, childLimitAmounts);

        LOGGER.info("TEST REN-29597: Step 11-12");
        checkLimitAmountMultiplyOfDependedCoverage(VOLUNTARY_LIFE_PLAN, SP_VOL_BTL, spouseLimitAmounts);

        LOGGER.info("TEST REN-29597: Step 13-14");
        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.getAssetList().getAsset(RELATIONSHIP_TO_PARTICIPANT).setValue("Dependent Child");
        PolicyInformationDependentDependentInformationTab.buttonNext.click();
        checkLimitAmountMultiplyOfDependedCoverage(VOLUNTARY_LIFE_PLAN, DEP_VOL_BTL, childLimitAmounts);

        LOGGER.info("TEST REN-29597: Step 7-8 for Child");
        checkLimitAmountMultiplyOfDependedCoverage(VOLUNTARY_LIFE_PLAN, DEP_VOL_ADD, childLimitAmounts);

        LOGGER.info("TEST REN-29597: Step 9-10 for Child");
        checkLimitAmountMultiplyOfDependedCoverage(BASIC_LIFE_PLAN, DEP_BTL,
                "",
                new Currency(1000).toPlainString(),
                new Currency(2000).toPlainString(),
                new Currency(2500).toPlainString(),
                new Currency(5000).toPlainString());
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-31741", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCheckAcceleratedBenefit() {
        TestData tdCreateClaim = termLifeClaim.getDefaultTestData(DATA_GATHER, "TestData_BenefitAcceleratedDeath");
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SELF_ADMINISTERED.getLabel()), VALUE_YES)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MAXIMUM_PERCENTAGE.getLabel()), "75")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MINIMUM_PERCENTAGE.getLabel()), "25")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MINIMUM_AMOUNT.getLabel()), "10000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ACCELERATED_BENEFIT_MAXIMUM_AMOUNT.getLabel()), "250000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", SELF_ADMINISTERED.getLabel()), VALUE_YES)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[3]", SELF_ADMINISTERED.getLabel()), VALUE_YES)
        );
        termLifeClaim.initiate(tdCreateClaim);
        termLifeClaim.getDefaultWorkspace().fillUpTo(tdCreateClaim, PolicyInformationParticipantParticipantCoverageTab.class, false);

        LOGGER.info("TEST REN-31741: Step 1-2");
        checkLimitAmountOfCoverage(BASIC_LIFE_PLAN, BTL, new Currency("10000"));

        LOGGER.info("TEST REN-31741: Step 3-4");
        PolicyInformationParticipantParticipantCoverageTab.buttonNext.click();
        termLifeClaim.getDefaultWorkspace().fillFromTo(tdCreateClaim, PolicyInformationDependentDependentInformationTab.class, CompleteNotificationTab.class, true);
        CompleteNotificationTab.buttonOpenClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }

    private void checkLimitAmountOfCoverage(String plan, String coverage, Currency limitAmount) {
        PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage.click();

        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValueContains(plan);
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValueContains(coverage);
        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.LIMIT_AMOUNT)).hasValue(limitAmount.toString());
    }

    private void checkLimitAmountOfDependedCoverage(String plan, String coverage, Currency limitAmount) {
        PolicyInformationDependentDependentCoverageTab.buttonAddCoverage.click();

        policyInformationDependentDependentCoverageTab.getAssetList().getAsset(PolicyInformationDependentDependentCoverageTabMetaData.PLAN).setValueContains(plan);
        policyInformationDependentDependentCoverageTab.getAssetList().getAsset(PolicyInformationDependentDependentCoverageTabMetaData.COVERAGE_NAME).setValueContains(coverage);
        assertThat(policyInformationDependentDependentCoverageTab.getAssetList().getAsset(PolicyInformationDependentDependentCoverageTabMetaData.LIMIT_AMOUNT)).hasValue(limitAmount.toString());
    }

    private void checkLimitAmountMultiplyOfCoverage(String plan, String coverage, List<String> limitAmounts) {
        PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage.click();

        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.PLAN).setValueContains(plan);
        policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME).setValueContains(coverage);
        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.LIMIT_AMOUNT_COMBOBOX))
                .hasOptions(limitAmounts);
    }

    private void checkLimitAmountMultiplyOfDependedCoverage(String plan, String coverage, String... limitAmounts) {
        PolicyInformationParticipantParticipantCoverageTab.buttonAddCoverage.click();

        policyInformationDependentDependentCoverageTab.getAssetList().getAsset(PolicyInformationDependentDependentCoverageTabMetaData.PLAN).setValueContains(plan);
        policyInformationDependentDependentCoverageTab.getAssetList().getAsset(PolicyInformationDependentDependentCoverageTabMetaData.COVERAGE_NAME).setValueContains(coverage);
        assertThat(policyInformationDependentDependentCoverageTab.getAssetList().getAsset(PolicyInformationDependentDependentCoverageTabMetaData.LIMIT_AMOUNT_COMBOBOX))
                .hasOptions(limitAmounts);
    }
}