package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.EndorsementReason.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCurrentAndNextPolicyYearStartDateRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private TextBox currentPolicyYearStartDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.CURRENT_POLICY_YEAR_START_DATE);
    private TextBox nextPolicyYearStartDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE);
    private TextBox policyEffectiveDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29776"}, component = POLICY_GROUPBENEFITS)
    public void testCurrentAndNextPolicyYearStartDateRulesVerification() {
        LOGGER.info("Quote#1 creation");
        LocalDateTime policyEffectiveDate = DateTimeUtils.getCurrentDateTime().withDayOfMonth(1);
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentPolicyYearStartDateField).isPresent().isDisabled().isOptional().hasValue(policyEffectiveDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).isPresent().isEnabled().isRequired().hasValue(policyEffectiveDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        });

        policyEffectiveDateField.setValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentPolicyYearStartDateField).hasValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasValue(currentDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        });

        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Step#2 verification");
            nextPolicyYearStartDateField.setValue(currentDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField.getWarning()
                    .orElse(EMPTY)).contains(String.format("'%s' cannot be back dated", PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));

            nextPolicyYearStartDateField.setValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            LOGGER.info("Step#3 verification");
            nextPolicyYearStartDateField.setValue(currentDate.plusYears(1).plusDays(5).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasWarningWithText(String.format("'%s' should be the same day as original Policy or on the last day of month if day from Policy Effective Date is not available in month'",
                    PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));

            nextPolicyYearStartDateField.setValue(currentDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            nextPolicyYearStartDateField.setValue(currentDate.plusYears(1).plusMonths(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            policyEffectiveDateField.setValue(currentDate.minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasValue(currentDate.minusDays(2).plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
            PolicyInformationTab.buttonSaveAndExit.click();
        });

        LOGGER.info("Step#4 verification with Quote#2");
        newQuoteInitiateAndNextPolicyYearStartDateCheck(currentDate.plusYears(1).withMonth(3).withDayOfMonth(31),
                currentDate.plusYears(1).withMonth(4).withDayOfMonth(30));
        PolicyInformationTab.buttonSaveAndExit.click();

        LOGGER.info("Step#5 verification with Quote#3");
        newQuoteInitiateAndNextPolicyYearStartDateCheck(currentDate.withMonth(11).withDayOfMonth(30), currentDate.withYear(2024).withMonth(2).withDayOfMonth(29));

        policyEffectiveDateField.setValue(currentDate.plusYears(1).withMonth(4).withDayOfMonth(30).format(DateTimeUtils.MM_DD_YYYY));
        nextPolicyYearStartDateField.setValue(currentDate.plusYears(2).withMonth(2).withDayOfMonth(28).format(DateTimeUtils.MM_DD_YYYY));
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            LOGGER.info("Step#6 verification");
            LocalDateTime newSystemDate = DateTimeUtils.getCurrentDateTime();
            policyEffectiveDateField.setValue(newSystemDate.plusMonths(2).format(DateTimeUtils.MM_DD_YYYY));
            nextPolicyYearStartDateField.setValue(newSystemDate.plusMonths(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            LOGGER.info("Step#7 verification");
            nextPolicyYearStartDateField.setValue(newSystemDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            LOGGER.info("Step#8 verification");
            nextPolicyYearStartDateField.setValue(newSystemDate.plusMonths(2).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            LOGGER.info("Step#9 verification");
            nextPolicyYearStartDateField.setValue(newSystemDate.plusMonths(5).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();

            LOGGER.info("Step#10 verification");
            policyEffectiveDateField.setValue(newSystemDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            nextPolicyYearStartDateField.setValue(newSystemDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField.getWarning()
                    .orElse(EMPTY)).contains(String.format("'%s' cannot be back dated", PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));

            LOGGER.info("Step#11 verification");
            policyEffectiveDateField.setValue(newSystemDate.format(DateTimeUtils.MM_DD_YYYY));
            nextPolicyYearStartDateField.setValue(newSystemDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasNoWarning();
            PolicyInformationTab.buttonSaveAndExit.click();
        });
    }

    private void newQuoteInitiateAndNextPolicyYearStartDateCheck(LocalDateTime policyEffectiveDate, LocalDateTime nextPolicyYearStartDate) {
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        policyEffectiveDateField.setValue(policyEffectiveDate.format(DateTimeUtils.MM_DD_YYYY));
        nextPolicyYearStartDateField.setValue(nextPolicyYearStartDate.format(DateTimeUtils.MM_DD_YYYY));
        assertThat(nextPolicyYearStartDateField).hasNoWarning();
    }
}