package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.CENSUS_TYPE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PremiumSummaryTabMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestValidateCensusTypeField extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21566", "REN-21574", "REN-21578"}, component = POLICY_GROUPBENEFITS)
    public void testValidateCensusTypeField() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());
        policyInformationTab.fillTab(getDefaultSTMasterPolicyData());
        policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(ValueConstants.VALUE_YES);
        planDefinitionTab.navigateToTab();

        LOGGER.info("REN-21578: TC1, Step 1");
        TextBox totalNumberOfEligibleLives = planDefinitionTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES);
        assertThat(totalNumberOfEligibleLives).isPresent().isRequired().isEnabled();
        totalNumberOfEligibleLives.setValue("-1");
        assertThat(totalNumberOfEligibleLives).hasWarningWithText("Total Number of Eligible lives must be greater than 0");

        LOGGER.info("REN-21566: Step 1-4");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(NY_STAT);
        ComboBox censusType = planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE);
        assertThat(censusType).isRequired().isEnabled()
                .hasOptions(ImmutableList.of("Enrolled", "Eligible", "None")).hasValue("Enrolled");

        LOGGER.info("REN-21566: Step 5-7");
        assertSoftly(softly ->
                ImmutableList.of("Enrolled", "Eligible", "None").forEach(value -> {
                    censusType.setValue(value);
                    softly.assertThat(censusType).hasNoWarning();
                }));

        LOGGER.info("REN-21566: Step 8");
        policyInformationTab.navigateToTab().getAssetList().getAsset(UNDER_FIFTY_LIVES).setValue(ValueConstants.VALUE_NO);

        LOGGER.info("REN-21566: Step 10");
        planDefinitionTab.navigateToTab();
        assertSoftly(softly ->
                ImmutableList.of("Enrolled", "Eligible").forEach(value -> {
                    censusType.setValue(value);
                    softly.assertThat(censusType).hasNoWarning();
                }));

        LOGGER.info("REN-21566: Step 9");
        censusType.setValue("None");
        assertThat(censusType).hasWarningWithText("None is allowed only when Under 50 lives = Yes");

        LOGGER.info("REN-21566: Step 20");
        censusType.setValue("Enrolled");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass());

        LOGGER.info("REN-21574: Step 1-2");
        TextBox credibilityFactor = premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR);
        assertThat(credibilityFactor).isPresent().isRequired().isEnabled().hasValue("0.0000000");

        LOGGER.info("REN-21574: Step 3");
        credibilityFactor.clear();
        assertThat(credibilityFactor).hasWarning();

        LOGGER.info("REN-21574: Step 4");
        premiumSummaryTab.rate();
        assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(), "'Credibility Factor' is required");
        Tab.buttonCancel.click();

        LOGGER.info("Step 7");
        credibilityFactor.setValue("1.1");
        assertThat(credibilityFactor).hasWarningWithText("Credibility Factor should be between 0 and 1");
        premiumSummaryTab.rate();
        assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(), "Credibility Factor should be between 0 and 1");
        Tab.buttonCancel.click();

        LOGGER.info("REN-21566: Step 20");
        premiumSummaryTab.fillTab(getDefaultSTMasterPolicyData()).submitTab();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(CENSUS_TYPE.getLabel()))).isAbsent();
        });

        LOGGER.info("REN-21566: Step 22");
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(getDefaultSTMasterPolicyData());
        statutoryDisabilityInsuranceMasterPolicy.issue().perform(getDefaultSTMasterPolicyData());
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}