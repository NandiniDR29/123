package com.exigen.ren.modules.policy.gb_di_ltd.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.APPROVED_PERCENT_ERROR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCoverageTabFieldsVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityCertificatePolicyContext, LongTermDisabilityMasterPolicyContext {

    private static final String AMOUNT_VERIFICATION_ERROR = "Value should be greater or equal 0";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27560", "REN-27665", "REN-27667", "REN-27674"}, component = POLICY_GROUPBENEFITS)
    public void testCoverageTabFieldsVerification() {
        LOGGER.info("General preconditions");
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(longTermDisabilityMasterPolicy.getType());

        TestData certificatePolicyTestData = longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        longTermDisabilityCertificatePolicy.initiate(certificatePolicyTestData);
        longTermDisabilityCertificatePolicy.getDefaultWorkspace().fillUpTo(certificatePolicyTestData, CoveragesTab.class, false);
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValueByIndex(1);

        LOGGER.info("REN-27560 Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Current Effective %"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Pending %"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Requested %"))).isAbsent();

            LOGGER.info("REN-27674 Step#1 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(APPROVED_PERCENT)).isPresent();
            int benefitPercentageValue = Integer.parseInt(coveragesTab.getAssetList().getAsset(BENEFIT_PERCENTAGE).getValue().replace("%", ""));

            LOGGER.info("REN-27674 Step#2 verification");
            coveragesTab.getAssetList().getAsset(APPROVED_PERCENT).setValue("0");
            softly.assertThat(coveragesTab.getAssetList().getAsset(APPROVED_PERCENT)).hasNoWarning();

            LOGGER.info("REN-27674 Step#4 verification");
            coveragesTab.getAssetList().getAsset(APPROVED_PERCENT).setValue(String.valueOf((benefitPercentageValue + 1)));
            softly.assertThat(coveragesTab.getAssetList().getAsset(APPROVED_PERCENT)).hasWarningWithText(APPROVED_PERCENT_ERROR);

            coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_YES);

            LOGGER.info("REN-27665, REN-27667 Step#1 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PENDING_AMOUNT)).isPresent().isRequired().hasValue(StringUtils.EMPTY);
            assertThat(coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT)).isPresent().isRequired().hasValue(StringUtils.EMPTY);

            coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_NO);

            LOGGER.info("REN-27665, REN-27667 Step#2 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PENDING_AMOUNT)).isAbsent();
            softly.assertThat(coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT)).isAbsent();

            coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_YES);
            Tab.buttonNext.click();

            LOGGER.info("REN-27665, REN-27667 Step#7 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PENDING_AMOUNT)).hasWarningWithText(String.format(ERROR_PATTERN, PENDING_AMOUNT.getLabel()));
            softly.assertThat(coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT)).hasWarningWithText(String.format(ERROR_PATTERN, REQUESTED_AMOUNT.getLabel()));

            LOGGER.info("REN-27665 Step#13 verification");
            coveragesTab.getAssetList().getAsset(PENDING_AMOUNT).setValue("-1");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PENDING_AMOUNT)).hasWarningWithText(AMOUNT_VERIFICATION_ERROR);

            LOGGER.info("REN-27667 Step#13 verification");
            coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT).setValue("-1");
            softly.assertThat(coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT)).hasWarningWithText(AMOUNT_VERIFICATION_ERROR);

            LOGGER.info("REN-27665 Step#15 verification");
            coveragesTab.getAssetList().getAsset(PENDING_AMOUNT).setValue("0");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PENDING_AMOUNT)).hasNoWarning();

            LOGGER.info("REN-27667 Step#15 verification");
            coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT).setValue("0");
            softly.assertThat(coveragesTab.getAssetList().getAsset(REQUESTED_AMOUNT)).hasNoWarning();
        });
    }
}