package com.exigen.ren.modules.commission.commissionstrategy.gb_di_std;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.DefaultCommissionRateInfo.NUMBER_OF_SUBSCRIBERS;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.commissionRateInfoTable;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.defaultCommissionRateInfoTable;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupSTDCoverages.STD_CORE;
import static com.exigen.ren.main.enums.TableConstants.CommissionsTable.PREMIUM_RECEIVED_PER_POLICY_YEAR;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestVerifyCommissionTypeAndOverride extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static String AGENCY_NAME = "QA Agency - Call Center";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23958", component = COMMISIONS_GROUPBENEFITS)
    public void testVerifyCommissionTypeAndOverrideTiered() {
        LOGGER.info("REN-23958: TC4");
        commonSteps("TestData_Tiered", PREMIUM_RECEIVED_PER_POLICY_YEAR.getName(), "Tiered", DialogOverrideCommissionPremiumSummary.DefaultCommissionRateInfo.PREMIUM_RECEIVED_PER_POLICY_YEAR.getName());
    }

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-24012", component = COMMISIONS_GROUPBENEFITS)
    public void testVerifyCommissionTypeAndOverrideCumulativeTiered() {
        LOGGER.info("REN-24012: TC4");
        commonSteps("TestData_Cumulative_Tiered", PREMIUM_RECEIVED_PER_POLICY_YEAR.getName(), "Cumulative Tiered", DialogOverrideCommissionPremiumSummary.DefaultCommissionRateInfo.PREMIUM_RECEIVED_PER_POLICY_YEAR.getName());
    }

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-24036", component = COMMISIONS_GROUPBENEFITS)
    public void testVerifyCommissionTypeAndOverrideSubscriberCount() {
        LOGGER.info("REN-24036: TC4");
        commonSteps("TestData_Subscriber_Count", TableConstants.CommissionsTable.NUMBER_OF_SUBSCRIBERS.getName(), "Subscriber Count", NUMBER_OF_SUBSCRIBERS.getName());
    }

    private void commonSteps(String testDataName, String columnName, String commissionType, String columnNameForOverrideSection) {
        adminApp().open();
        createCommissionStrategy(tdSpecific().getTestData(testDataName), false);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        LOGGER.info("Step 1-2");
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue("QA Agency");
        assertThat(policyInformationTab.getAssetList().getAsset(AGENCY_TYPE)).hasValue("Call Center");

        LOGGER.info("Step 3, 4");
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTDMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()),
                                TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                        .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENCY_PRODUCER_COMBO.getLabel())),
                policyInformationTab.getClass(), premiumSummaryTab.getClass());
        assertSoftly(softly -> {
            softly.assertThat(PremiumSummaryTab.getCommissionName(1)).isEqualTo(AGENCY_NAME);
            softly.assertThat(PremiumSummaryTab.getCommissionTableForAgencySection(AGENCY_NAME, "0 - 0").getHeader().getCell(1))
                    .hasValue(columnName);
        });

        LOGGER.info("Step 5, 6");
        Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(AGENCY_NAME, STD_CORE);
        assertSoftly(softly -> {
            softly.assertThat(overrideCommissionButton).isPresent();
            softly.assertThat(PremiumSummaryTab.getCommissionTypeByAgency(AGENCY_NAME, STD_CORE)).isEqualTo(commissionType);

        });
        overrideCommissionButton.click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValueContains("Name");
        commissionRateInfoTable.getRowContains(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.COVERAGE_NAME.getName(), STD_CORE).getCell(1).click();

        defaultCommissionRateInfoTable.waitForAccessible(3000);
        assertThat(defaultCommissionRateInfoTable.getHeader().getCell(1)).hasValue(columnNameForOverrideSection);
    }
}