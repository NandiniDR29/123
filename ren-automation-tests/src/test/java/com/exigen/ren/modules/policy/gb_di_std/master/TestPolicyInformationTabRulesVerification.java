package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.SMALL_GROUP;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyInformationTabRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private TextBox currentPolicyYearStartDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.CURRENT_POLICY_YEAR_START_DATE);
    private TextBox nextPolicyYearStartDateField = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29759", "REN-29904", "REN-19200"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyInformationTabRulesVerification() {
        LOGGER.info("General preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());

        LOGGER.info("29759 steps#1,2 and REN-29904 step#2  verification");
        LocalDateTime policyEffectiveDate = DateTimeUtils.getCurrentDateTime().withDayOfMonth(1);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentPolicyYearStartDateField).isPresent().isDisabled().isOptional().hasValue(policyEffectiveDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).isPresent().isEnabled().isRequired().hasValue(policyEffectiveDate.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-29904 step#8  verification");
            nextPolicyYearStartDateField.setValue(StringUtils.EMPTY);
            softly.assertThat(nextPolicyYearStartDateField).hasWarningWithText(String.format(ERROR_PATTERN, PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));
            LocalDateTime currentDateMinus1 = DateTimeUtils.getCurrentDateTime().minusDays(1);

            LOGGER.info("REN-29904 step#9  verification");
            policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE).clear();
            policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE).setValue(currentDateMinus1.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(currentPolicyYearStartDateField).hasValue(currentDateMinus1.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasValue(currentDateMinus1.plusYears(1).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-29904 step#10  verification");
            nextPolicyYearStartDateField.setValue(currentDateMinus1.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasWarningWithText(String.format("'%s' cannot be back dated.", PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));

            LOGGER.info("REN-29904 step#15  verification");
            nextPolicyYearStartDateField.setValue(currentDateMinus1.plusYears(1).plusDays(10).format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(nextPolicyYearStartDateField).hasWarningWithText(String.format("'%s' should be the same day as original Policy or on the last day of month if day from Policy Effective Date is not available in month'",
                    PolicyInformationTabMetaData.NEXT_POLICY_YEAR_START_DATE.getLabel()));

            LOGGER.info("REN-19200 Step 1-5");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SMALL_GROUP)).isPresent().isRequired().hasValue(VALUE_NO);
            policyInformationTab.getAssetList().getAsset(SMALL_GROUP).setValue(VALUE_YES);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SMALL_GROUP)).hasValue(VALUE_YES);
        });

        LOGGER.info("REN-29904 step#19  verification");
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY)), PolicyInformationTab.class);

        shortTermDisabilityMasterPolicy.propose().perform(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        shortTermDisabilityMasterPolicy.acceptContract().start();
        AbstractContainer<?, ?> policyInfBindActionAssetList = shortTermDisabilityMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInfBindActionAssetList.getAsset(PolicyInformationBindActionTabMetaData.NEXT_POLICY_YEAR_START_DATE)).isPresent().isDisabled();
            softly.assertThat(policyInfBindActionAssetList.getAsset(PolicyInformationBindActionTabMetaData.CURRENT_POLICY_YEAR_START_DATE)).isPresent().isDisabled();
        });
    }
}