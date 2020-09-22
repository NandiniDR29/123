package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.UpdateReinsuranceRateActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.UpdateReinsuranceRateActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonNext;
import static com.exigen.ren.main.modules.policy.common.metadata.master.UpdateReinsuranceRateActionTabMetaData.EFFECTIVE_DATE_CHANGE;
import static com.exigen.ren.main.modules.policy.common.metadata.master.UpdateReinsuranceRateActionTabMetaData.SIC_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.ReinsuranceRateTab.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab.buttonRate;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteRulesVerificationForReinsurance extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static final String REINSURER_VALUE = "MunichRe";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-19057", "REN-19096"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationForReinsurance() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), premiumSummaryTab.getClass());

        LOGGER.info("TEST: Step #3-4 (REN-19057); Step #4 (REN-19096)");
        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REINSURANCE_RATE).getAsset(MANUAL_REINSURANCE_RATE)).isOptional().isDisabled().hasValue(StringUtils.EMPTY);
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REINSURANCE_RATE).getAsset(FINAL_REINSURANCE_RATE)).isOptional().isDisabled().hasValue(StringUtils.EMPTY);
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REINSURANCE_RATE).getAsset(REINSURER)).isOptional().isDisabled().hasValue(StringUtils.EMPTY);

            premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS).setValueByIndex(1);
            premiumSummaryTab.getAssetList().getAsset(APPLY).click();
            buttonRate.click();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REINSURANCE_RATE).getAsset(MANUAL_REINSURANCE_RATE).getValue()).isNotEmpty();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REINSURANCE_RATE).getAsset(FINAL_REINSURANCE_RATE).getValue()).isNotEmpty();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REINSURANCE_RATE).getAsset(REINSURER)).hasValue(REINSURER_VALUE);
        });
        LOGGER.info("TEST: Step #7 (REN-19057); Step #10 (REN-19096)");
        buttonNext.click();
        proposeAcceptContractIssueWithDefaultTestData();

        UpdateReinsuranceRateActionTab updateReinsuranceRateActionTab =
                longTermDisabilityMasterPolicy.updateReinsuranceRate().getWorkspace().getTab(UpdateReinsuranceRateActionTab.class);
        longTermDisabilityMasterPolicy.updateReinsuranceRate().start();

        assertSoftly(softly -> {
            softly.assertThat(updateReinsuranceRateActionTab.getAssetList().getAsset(SIC_CODE)).isRequired().isDisabled();
            softly.assertThat(updateReinsuranceRateActionTab.getAssetList().getAsset(SIC_CODE).getValue()).isNotEmpty();

            softly.assertThat(updateReinsuranceRateActionTab.getAssetList().getAsset(EFFECTIVE_DATE_CHANGE)).isRequired().isEnabled()
                    .hasValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));

            softly.assertThat(updateReinsuranceRateActionTab.getAssetList()
                    .getAsset(UpdateReinsuranceRateActionTabMetaData.MANUAL_REINSURANCE_RATE)).isRequired().isDisabled();
            softly.assertThat(updateReinsuranceRateActionTab.getAssetList()
                    .getAsset(UpdateReinsuranceRateActionTabMetaData.MANUAL_REINSURANCE_RATE).getValue()).isNotEmpty();

            softly.assertThat(updateReinsuranceRateActionTab.getAssetList()
                    .getAsset(UpdateReinsuranceRateActionTabMetaData.REINSURER)).isRequired().isEnabled().hasValue(REINSURER_VALUE);

            softly.assertThat(updateReinsuranceRateActionTab.getAssetList()
                    .getAsset(UpdateReinsuranceRateActionTabMetaData.FINAL_REINSURANCE_RATE)).isRequired().isEnabled().hasValue(StringUtils.EMPTY);

            softly.assertThat(updateReinsuranceRateActionTab.getAssetList()
                    .getAsset(UpdateReinsuranceRateActionTabMetaData.COMMENTS)).isRequired().isEnabled().hasValue(StringUtils.EMPTY);
        });
    }
}
