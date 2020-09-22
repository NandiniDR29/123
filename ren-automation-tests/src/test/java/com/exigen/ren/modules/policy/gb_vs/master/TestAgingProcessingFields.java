package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAgingProcessingFields extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27692", "REN-27694"}, component = POLICY_GROUPBENEFITS)
    public void testAgingProcessingFields() {
        LOGGER.info("REN-27692 REN-27694 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup"), groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        AssetList policyInformationTabAssetList = (AssetList) policyInformationTab.getAssetList();

        assertSoftly(softly -> {

            LOGGER.info("REN-27692 Step 1");
            softly.assertThat(policyInformationTabAssetList.getAsset(AGING_FREQUENCY)).isPresent().isRequired().isDisabled().hasValue("On Anniversary");

            LOGGER.info("REN-27692 Step 2");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Annual Anniversary Date"))).isAbsent();

        });

        LOGGER.info("REN-27694 Preconditions: ... set Policy effective date as 02/29/2020");
        policyInformationTabAssetList.getAsset(POLICY_EFFECTIVE_DATE).setValue("02/29/2020");

        assertSoftly(softly -> {

            LOGGER.info("REN-27694 Step 1");
            softly.assertThat(policyInformationTabAssetList.getAsset(ANNIVERSARY_DAY_MONTH)).isPresent().isRequired().isDisabled().hasValue("January");

            LOGGER.info("REN-27694 Step 2");
            softly.assertThat(policyInformationTabAssetList.getAsset(ANNIVERSARY_DAY_DAY)).isPresent().isRequired().isDisabled().hasValue("1");

        });

        LOGGER.info("REN-27692 Step 5 REN-27694 Step 11");
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), POLICY_EFFECTIVE_DATE.getLabel()), todayDate), PolicyInformationTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Aging Frequency"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Anniversary Day"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Anniversary Day (Month)"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Anniversary Day (Day)"))).isAbsent();
        });
    }

}
