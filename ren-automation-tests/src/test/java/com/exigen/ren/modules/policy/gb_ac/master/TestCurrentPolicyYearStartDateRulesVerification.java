package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestCurrentPolicyYearStartDateRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private TextBox currentPolicyYearStartDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.CURRENT_POLICY_YEAR_START_DATE);
    private TextBox nextPolicyYearStartDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE);
    private TextBox policyEffectiveDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-30322", "REN-30325"}, component = POLICY_GROUPBENEFITS)
    public void testCurrentPolicyYearStartDateRulesVerificationTC1() {
        LOGGER.info("General preconditions");
        LocalDateTime policyEffectiveDate = DateTimeUtils.getCurrentDateTime().withDayOfMonth(1);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());

        LOGGER.info("REN-30322 steps#1, 2, 3");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentPolicyYearStartDateField).isPresent().isDisabled().isOptional().hasValue(policyEffectiveDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).isPresent().isEnabled().isRequired().hasValue(policyEffectiveDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-30324"}, component = POLICY_GROUPBENEFITS)
    public void testCurrentPolicyYearStartDateRulesVerificationTC2() {
        LOGGER.info("General preconditions");
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();

        initiateGroupAccidentMPWithChangedCoverageEffDate(currentDate);

        LOGGER.info("REN-30324 steps#1");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyEffectiveDateField).hasValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(currentPolicyYearStartDateField).isDisabled().hasValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-30324 step#4 verification");
            policyEffectiveDateField.setValue(currentDate.plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(currentPolicyYearStartDateField).hasValue(currentDate.plusDays(1).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-30324 step#5 verification");
            policyEffectiveDateField.setValue(currentDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(currentPolicyYearStartDateField).hasValue(currentDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-30325"}, component = POLICY_GROUPBENEFITS)
    public void testCurrentPolicyYearStartDateRulesVerificationTC3() {
        LOGGER.info("General preconditions");
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();

        initiateGroupAccidentMPWithChangedCoverageEffDate(currentDate);

        LOGGER.info("REN-30325 steps#1");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentPolicyYearStartDateField).isPresent().isDisabled().hasValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).isPresent().isEnabled().isRequired().hasValue(currentDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-30325 step#6  verification");
            nextPolicyYearStartDateField.setValue(EMPTY);
            softly.assertThat(nextPolicyYearStartDateField).hasWarningWithText(String.format(ERROR_PATTERN, PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));

            LOGGER.info("REN-30325 step#7  verification");
            policyEffectiveDateField.setValue(currentDate.plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(currentPolicyYearStartDateField).hasValue(currentDate.plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasValue(currentDate.plusDays(1).plusYears(1).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-30325 step#8  verification");
            nextPolicyYearStartDateField.setValue(currentDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField.getWarning()
                    .orElse(EMPTY)).contains(String.format("'%s' cannot be back dated", PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));
        });

        LOGGER.info("REN-30325 step#11 verification");
        setNextPolicyYearStartDateFieldVerification(currentDate.plusYears(1).minusDays(10));

        LOGGER.info("REN-30325 step#13 verification");
        setNextPolicyYearStartDateFieldVerification(currentDate.plusYears(1).plusMonths(1).withDayOfMonth(1).minusDays(3));

        LOGGER.info("REN-30325 step#14 verification");
        LocalDate policyEffectiveDate = LocalDate.parse(policyEffectiveDateField.getValue(), DateTimeUtils.MM_DD_YYYY);
        setNextPolicyYearStartDateFieldVerification(policyEffectiveDate.equals(policyEffectiveDate.withDayOfMonth(policyEffectiveDate.lengthOfMonth())) ? policyEffectiveDate.plusYears(1).plusDays(1).atTime(0, 0) : currentDate.plusYears(1).plusMonths(1).withDayOfMonth(1).minusDays(1));

        LOGGER.info("REN-30325 step#18 verification");
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY))
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.RENEWAL_FREQUENCY.getLabel())), PolicyInformationTab.class);

        groupAccidentMasterPolicy.propose().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupAccidentMasterPolicy.acceptContract().start();
        assertThat(groupAccidentMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList().getAsset(PolicyInformationBindActionTabMetaData.NEXT_POLICY_YEAR_START_DATE)).isPresent().isDisabled();

        LOGGER.info("REN-30325 step#19 verification");
        groupAccidentMasterPolicy.acceptContract().getWorkspace()
                .fill(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupAccidentMasterPolicy.acceptContract().submit();
        groupAccidentMasterPolicy.issue().start();
        assertThat(groupAccidentMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class).getAssetList().getAsset(PolicyInformationIssueActionTabMetaData.NEXT_POLICY_YEAR_START_DATE)).isPresent().isEnabled();
    }

    private void initiateGroupAccidentMPWithChangedCoverageEffDate(LocalDateTime date) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", "Coverage Effective Date"), date.format(DateTimeUtils.MM_DD_YYYY)).resolveLinks());
    }

    private void setNextPolicyYearStartDateFieldVerification(LocalDateTime date) {
        nextPolicyYearStartDateField.setValue(date.format(DateTimeUtils.MM_DD_YYYY));
        assertThat(nextPolicyYearStartDateField).hasWarningWithText(String.format("'%s' should be the same day as original Policy or on the last day of month if day from Policy Effective Date is not available in month",
                PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));
    }


}
