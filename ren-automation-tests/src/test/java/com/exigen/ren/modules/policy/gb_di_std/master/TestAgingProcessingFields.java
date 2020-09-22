package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAgingProcessingFields extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28208", "REN-28210"}, component = POLICY_GROUPBENEFITS)
    public void testAgingProcessingFields() {
        LOGGER.info("REN-28208, REN-28210 Preconditions:" +
                    "1.) Login to Application" +
                    "2.) Create a Non-Individual Customer" +
                    "3.) Create Case for STD product and also add the census file in File intake profile tab" +
                    "4.) Initiate Quote for Case from Step 3");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        AssetList policyInformationTabAssetList = (AssetList) policyInformationTab.getAssetList();

        assertSoftly(softly -> {

            LOGGER.info("REN-28208 Step 1. Navigate to Policy Information tab and Verify the Aging Frequency field");
            softly.assertThat(policyInformationTabAssetList.getAsset(AGING_FREQUENCY)).isPresent().isRequired().isDisabled().hasValue("On Anniversary");

            LOGGER.info("REN-28208 Step 2. Verify the Annual Anniversary Date field");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Annual Anniversary Date"))).isAbsent();

        });

        LOGGER.info("REN-28210 Preconditions:" +
                    "... set Policy effective date as 02/29/2020");
        // this value is hard-coded because only leap year February 29 should be used. Change to 2024 year in case it fails after 02/29/2020 (shouldn't fail)
        policyInformationTabAssetList.getAsset(POLICY_EFFECTIVE_DATE).setValue("02/29/2020");

        assertSoftly(softly -> {

            LOGGER.info("REN-28210 Step 1. Navigate to Policy Information tab and Verify the Anniversary Day (Month) field");
            softly.assertThat(policyInformationTabAssetList.getAsset(ANNIVERSARY_DAY_MONTH)).isPresent().isRequired().isEnabled().hasValue("February");

            LOGGER.info("REN-28210 Step 2. Verify the Anniversary Day (Day) field");
            softly.assertThat(policyInformationTabAssetList.getAsset(ANNIVERSARY_DAY_DAY)).isPresent().isRequired().isEnabled().hasValue("28");

            LOGGER.info("REN-28210 Step 7. Update the Policy Effective Date (e.g.10/29/2020)");
            policyInformationTabAssetList.getAsset(POLICY_EFFECTIVE_DATE).setValue("10/29/2020");

            softly.assertThat(policyInformationTabAssetList.getAsset(ANNIVERSARY_DAY_MONTH)).hasValue("October");
            softly.assertThat(policyInformationTabAssetList.getAsset(ANNIVERSARY_DAY_DAY)).hasValue("29");

        });

        LOGGER.info("REN-28208 Step 5, REN-28210 Step 8. Fill all mandatory fields across all tab and click on Rate and Next");
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTDMasterPolicyData(), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Aging Frequency"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Anniversary Day"))).isAbsent();
        });
    }

}
