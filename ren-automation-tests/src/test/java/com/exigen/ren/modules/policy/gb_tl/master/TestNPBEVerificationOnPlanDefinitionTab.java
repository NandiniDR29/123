package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementNPBInfoActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.ADD;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.DEP_VOL_ADD;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyTransactionHistoryTable.*;
import static com.exigen.ren.main.enums.ProductConstants.TransactionHistoryType.NPB_ENDORSEMENT;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestNPBEVerificationOnPlanDefinitionTab extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private static final AbstractContainer<?, ?> assetListPlanDefinition = termLifeInsuranceMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PlanDefinitionEndorseNPBInfoActionTab.class).getAssetList();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35802"}, component = POLICY_GROUPBENEFITS)
    public void testNPBEVerificationOnPlanDefinitionTab() {
        LOGGER.info("General Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataPolicy").resolveLinks()).resolveLinks());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Steps#1, 2, 3 execution");
        termLifeInsuranceMasterPolicy.endorseNPBInfo().start();
        termLifeInsuranceMasterPolicy.endorseNPBInfo().getWorkspace()
                .fillUpTo(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT_NPB_INFO, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(StartEndorsementNPBInfoActionTab.class.getSimpleName(), StartEndorsementNPBInfoActionTabMetaData.ENDORSEMENT_DATE.getLabel()),
                                TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY)), PlanDefinitionEndorseNPBInfoActionTab.class, false);

        LOGGER.info("Step#4 verification");
        ImmutableList.of("Coverage Included in Package", "Sponsor/Participant Funding Structure", "Rating", "Benefit Schedule",
                "Benefit Schedule - Spouse", "Benefit Schedule - Child", "Age Reduction", "Age Reduction Schedule Details", "Guaranteed Issue",
                "Options", "Schedule of Continuation Provision", "Exclusions", "Accidental Death and Dismemberment Insurance - Loss Schedule",
                "Seat Belt Benefit", "Air Bag Benefit", "Repatriation Benefit", "Common Carrier Benefit", "Child Education Benefit",
                "Child Care Benefit", "Spouse Training Benefit", "Coma Benefit", "Rehabilitation Benefit Amount", "Adaptive Home / Vehicle Benefit",
                "Bereavement Benefit", "Medical Premium Benefit", "Funeral Expense Benefit", "Hospital Inpatient Stay Benefit", "Mortgage Benefit")
                .forEach(sectionName -> assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(sectionName))).isAbsent());

        LOGGER.info("Step#5 verification");
        presentFieldsVerification();

        LOGGER.info("Steps#6, 7 verification");
        termLifeInsuranceMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PlanDefinitionEndorseNPBInfoActionTab.class).changeCoverageTo(ADD);
        presentFieldsVerification();

        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Step#8 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(PLAN_NAME)).isPresent().isEnabled().isRequired();

            LOGGER.info("Step#9 verification");
            assetListPlanDefinition.getAsset(PLAN_NAME).setValue(EMPTY);
            softly.assertThat(assetListPlanDefinition.getAsset(PLAN_NAME)).hasWarningWithText(String.format(ERROR_PATTERN, PLAN_NAME.getLabel()));

            LOGGER.info("Step#13 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY)).isPresent();

            LOGGER.info("Step#14 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(EligibilityMetadata.DOES_MIN_HOURLY_REQUIREMENT_APPLY_VS_DN)).isPresent().isDisabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(EligibilityMetadata.MINIMUM_HOURLY_REQUIREMENT)).isPresent().isDisabled();

            LOGGER.info("Step#16 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(EligibilityMetadata.ELIGIBILITY_WAITING_PERIOD_DEFINITION)).isPresent().isEnabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(EligibilityMetadata.WAITING_PERIOD_AMOUNT)).isPresent().isEnabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(EligibilityMetadata.WAITING_PERIOD_MODE)).isPresent().isEnabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(EligibilityMetadata.WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isEnabled();

            LOGGER.info("Steps#19, 21 verification");
            termLifeInsuranceMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PlanDefinitionEndorseNPBInfoActionTab.class).changeCoverageTo(DEP_VOL_ADD);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ELIGIBILITY.getLabel()))).isAbsent();

            LOGGER.info("Step#24 verification");
            assetListPlanDefinition.getAsset(PLAN_NAME).setValue("Test Plan Name");
            PlanDefinitionEndorseNPBInfoActionTab.buttonIssue.click();
            softly.assertThat(Page.dialogConfirmation.labelHeader).hasValue("Please confirm NPBE action");
            softly.assertThat(Page.dialogConfirmation.labelMessage).hasValue("Are you sure you want to issue Non-Premium Bearing Endorsement?");
        });

        LOGGER.info("Step#25 verification");
        Page.dialogConfirmation.confirm();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        //Actions added for correct Endorse NPB Info transaction verification
        PolicySummaryPage.buttonTransactionHistory.click();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(TYPE)).hasValue(NPB_ENDORSEMENT);
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(TRAN_PREMIUM)).hasValue(String.valueOf(new Currency()));
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(TRANSACTION_DATE)).hasValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(EFFECTIVE_DATE)).hasValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
        });
    }

    private void presentFieldsVerification() {
        ImmutableList.of(COVERAGE_NAME, PLAN, GRANDFATHER_PLAN, POPULATION_TYPE,
                ENROLLMENT_UNDERWRITING_OFFER, ENROLLMENT_UNDERWRITING_OFFER_OTHER_DESCRIPTION,
                CENSUS_TYPE, ENHANCED_AD_D, COVERAGE_TYPE, TOTAL_NUMBER_ELIGIBLE_LIVES, ASSUMED_PARTICIPATION)
                .forEach(asset -> assertThat(assetListPlanDefinition.getAsset(asset)).isPresent().isDisabled());
    }
}
