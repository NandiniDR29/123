package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPerpetualPolicyConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final String TWELVE = "12";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18539", "REN-19125", "REN-19401", "REN-19407", "REN-26897"}, component = POLICY_GROUPBENEFITS)
    public void testPerpetualPolicyConfiguration() {
        mainApp().open();
        LOGGER.info("Test REN-18398 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        initiatGACQuoteAndFillUpToTab(getDefaultACMasterPolicyData(), PolicyInformationTab.class, false);
        assertSoftly(softly -> {

            LOGGER.info("Test REN-19401 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(POLICY_TERM)).isAbsent();

            LOGGER.info("Test REN-19407 TC1 Step 1 REN-26897 TC2 Step 1");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).isRequired().hasValue("24");
            //get current date and set as effective date
            LocalDate effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
            String effectiveDateString = effectiveDate.format(DateTimeUtils.MM_DD_YYYY);
            policyInformationTab.getAssetList().getAsset(POLICY_EFFECTIVE_DATE).setValue(effectiveDateString);

            LOGGER.info("Test REN-19125 TC1 Step 3 TC2 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE)).isPresent().hasValue(effectiveDate.plusMonths(24).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Test REN-18539 TC1 TC2 Step 3 Step 8 REN-26897 TC2 Step 2");
            LocalDate nextRenewalEffectiveDate = LocalDate.parse(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_QUOTE_START_DATE)).isPresent().hasValue(nextRenewalEffectiveDate.minusMonths(3).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Test REN-18539 TC2 Step 4 REN-26897 TC2 Step 3");
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue(TWELVE);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasValue(TWELVE);

            LOGGER.info("Test REN-19125 TC2 Step 3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE)).hasValue(effectiveDate.plusMonths(12).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Test REN-18539 TC2 Step 6");
            nextRenewalEffectiveDate = LocalDate.parse(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(NEXT_RENEWAL_QUOTE_START_DATE)).hasValue(nextRenewalEffectiveDate.minusMonths(3).format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("REN-26897 TC2 Step 4");
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("61");
            policyInformationTab.submitTab();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasWarningWithText("Rate Guarantee must be less than or equal to 60 months");

            LOGGER.info("REN-26897 TC2 Step 5");
            policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS).setValue("60");
            policyInformationTab.submitTab();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RATE_GUARANTEE_MONTHS)).hasNoWarning();
        });
    }
}
