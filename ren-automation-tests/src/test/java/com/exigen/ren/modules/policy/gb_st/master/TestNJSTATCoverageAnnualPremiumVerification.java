package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ENDORSEMENT;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PERCENT_OF_TAXABLE_WAGE;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.RATE_BASIS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestNJSTATCoverageAnnualPremiumVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private static final String ABSENT_FIELD = "Exclude Number Of Participants?";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-36332", "REN-34439"}, component = POLICY_GROUPBENEFITS)
    public void testNJSTATCoverageAnnualPremiumVerification() {
        LOGGER.info("General Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        initiateSTQuoteAndFillUpToTab(getDefaultSTMasterPolicyData(), PlanDefinitionTab.class, true);
        planDefinitionTab.getAssetList().getAsset(PLAN).setValue(NJ_STAT);
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME)).hasValue(CoveragesConstants.GroupStatutoryCoverages.STAT_NJ);

        updateRateBasisAndGoToClassMngmntTab(PERCENT_OF_TAXABLE_WAGE);
        classificationManagementMpTab.getAssetList().getAsset(COVERAGE_TIER).setValue("Employee");

        LOGGER.info("REN-36332 Step#1, REN-34439 Steps#1, 2 verification");
        planTierAndRatingInfoVerification();
        ClassificationManagementTab.updateExistingCoverage("Employer");
        planTierAndRatingInfoVerification();
        classificationManagementMpTab.submitTab();

        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class).fillTab(getDefaultSTMasterPolicyData());
        premiumSummaryTab.rate();

        LOGGER.info("REN-36332 Step#2 verification");
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        //verification 3.1 'expected annual premium' value by formula 'Annual Premium = (Employee Volume * Employee Rate) + (Employer Volume * Employer Rate)' is skipped due to incorrect formula provided
        //Perform verification 4.1: "Annual Premium' value on 'Premium Summary' tab and 'totalAnnualPremium' value from the response output should be the same
        Currency totalAnnualPremiumFromResponse = new Currency(responseFromLog.get("planCalcs[0].totalAnnualPremium").substring(0,11), RoundingMode.UP);
        Currency annualPremiumAmountFromUI = new Currency(PremiumSummaryTab.premiumSummaryCoveragesTable.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), NJ_STAT).getCell(ANNUAL_PREMIUM.getName()).getValue());
        assertThat(totalAnnualPremiumFromResponse).isEqualTo(annualPremiumAmountFromUI);

        LOGGER.info("REN-34439 Step#3 verification");
        planDefinitionTab.navigateToTab();
        updateRateBasisAndGoToClassMngmntTab(PER_EMPLOYEE_PER_MONTH);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ABSENT_FIELD))).isAbsent();

        LOGGER.info(" REN-34439 Step#4 verification");
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueSTMasterPolicyWithDefaultTestData();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("REN-34439 Step#5 verification");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_ST, statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        planDefinitionTab.navigateToTab();
        updateRateBasisAndGoToClassMngmntTab(PERCENT_OF_TAXABLE_WAGE);

        assertSoftly(softly -> {
            classificationManagementMpTab.getAssetList().getAsset(COVERAGE_TIER).setValue("Employee");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ABSENT_FIELD))).isAbsent();
            ClassificationManagementTab.updateExistingCoverage("Employer");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ABSENT_FIELD))).isAbsent();

            planDefinitionTab.navigateToTab();
            updateRateBasisAndGoToClassMngmntTab(PER_EMPLOYEE_PER_MONTH);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ABSENT_FIELD))).isAbsent();
        });

        LOGGER.info("REN-34439 Step#6 verification");
        premiumSummaryTab.navigateToTab();
        premiumSummaryTab.submitTab();
        PolicySummaryPage.buttonPendedEndorsement.click();
        statutoryDisabilityInsuranceMasterPolicy.issue()
                .perform(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA"));

        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(
                "Type")).hasValue(ProductConstants.TransactionHistoryType.ENDORSEMENT);
    }

    private void planTierAndRatingInfoVerification() {
        assertSoftly(softly -> {
            LOGGER.info("REN-34439 Steps#1, 2 verification");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ABSENT_FIELD))).isAbsent();

            LOGGER.info("REN-36332 Step#1 verification");
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(RATE)).isPresent().isDisabled().hasValue("1.00000000");
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS)).isPresent().isOptional().hasValue("1");
            classificationManagementMpTab.getAssetList().getAsset(VOLUME).setValue("100");
        });
    }

    private void updateRateBasisAndGoToClassMngmntTab(String rateBasis) {
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RATE_BASIS).setValue(rateBasis);
        planDefinitionTab.submitTab();

        classificationManagementMpTab.getAssetList().getAsset(ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP).click();
        classificationManagementMpTab.getAssetList().getAsset(CLASSIFICATION_GROUP_NAME).setValue("index=1");
    }
}
