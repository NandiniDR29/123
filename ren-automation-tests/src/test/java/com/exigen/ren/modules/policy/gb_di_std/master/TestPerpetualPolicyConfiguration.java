package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPerpetualPolicyConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18863", "REN-18864", "REN-18866", "REN-18873", "REN-26758"}, component = POLICY_GROUPBENEFITS)
    public void testPerpetualPolicyConfiguration() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData(), PolicyInformationTab.class, false);
        assertSoftly(softly -> {

            LOGGER.info("Test REN-17872 Step 3 Step 4 REN-26758 TC1 Step 1");
            LocalDate effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
            String effectiveDateString = effectiveDate.format(DateTimeUtils.MM_DD_YYYY);
            policyInformationTab.getAssetList().getAsset(POLICY_EFFECTIVE_DATE).setValue(effectiveDateString);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RENEWAL_FREQUENCY)).isAbsent();

            LOGGER.info("Test REN-18866 TC1 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(POLICY_TERM)).isAbsent();

            LOGGER.info("Test REN-18873 TC1 Step 1");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasValue("24").isRequired();

            LOGGER.info("Test REN-18863 TC1 Step 4 TC2 Step 2 REN-26758 TC2 Step 1");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE)).isPresent().hasValue(effectiveDate.plusMonths(24).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Test REN-18864 TC1 Step 3 Step 8 REN-26758 TC2 Step 2");
            LocalDate nextRenewalEffectiveDate = LocalDate.parse(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_QUOTE_START_DATE)).isPresent().hasValue(nextRenewalEffectiveDate.minusMonths(3).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Test REN-18863 TC2 Step 2 REN-26758 TC2 Step 3");
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("12");

            LOGGER.info("Test REN-18863 TC2 Step 3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE)).hasValue(effectiveDate.plusMonths(12).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Test REN-18864 TC2 Step 6");
            nextRenewalEffectiveDate = LocalDate.parse(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_QUOTE_START_DATE)).isPresent().hasValue(nextRenewalEffectiveDate.minusMonths(3).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-26758 TC2 Step 4");
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("61");
            policyInformationTab.submitTab();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasWarningWithText("Rate Guarantee must be less than or equal to 60 months");

            LOGGER.info("REN-26758 TC2 Step 5");
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("60");
            policyInformationTab.submitTab();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasNoWarning();
        });
    }
}
