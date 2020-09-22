package com.exigen.ren.modules.commission.commissionstrategy.gb_dn;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.TableConstants.CommissionsTable;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.AGENCY_PRODUCER_COMBO;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestVerifyApplyFlatRuleWithAllAndSpecificStates extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {
    private final TestData testData_DN_Quote = tdSpecific().getTestData("TestData_DN_Quote");

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-16943", component = COMMISIONS_GROUPBENEFITS)
    public void testVerifyToApplyFlatRuleForMasterPolicy() {
        LOGGER.info("REN-16943 Scenario 1");
        adminApp().open();
        createCommissionStrategy(tdSpecific().getTestData("TestData"), false);

        LOGGER.info("Step 1-2");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(testData_DN_Quote);
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(testData_DN_Quote
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "IL"), premiumSummaryTab.getClass());
        verifyCommissionRow("5.00 %");
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step 3-4");
        groupDentalMasterPolicy.dataGather().start();
        groupDentalMasterPolicy.dataGather().getWorkspace().fillUpTo(tdSpecific().getTestData("TestData_DN_Quote_Update")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "CA"), premiumSummaryTab.getClass(), true);
        verifyCommissionRow("15.00 %");
        premiumSummaryTab.submitTab();

        LOGGER.info("Step 5");
        groupDentalMasterPolicy.dataGather().start();
        groupDentalMasterPolicy.dataGather().getWorkspace().fillUpTo(tdSpecific().getTestData("TestData_DN_Quote_Update")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "HI"), premiumSummaryTab.getClass());
        verifyCommissionRow("5.00 %");
        premiumSummaryTab.submitTab();
    }

    private void verifyCommissionRow(String rate) {
        assertThat(PremiumSummaryTab.getCommissionTableForAgencySection(String.format("%s - Call Center", testData_DN_Quote.getTestData(policyInformationTab.getMetaKey()).getValue(AGENCY_PRODUCER_COMBO.getLabel())), "Dental"))
                .hasMatchingRows(1, ImmutableMap.of(CommissionsTable.COVERAGE_NAME.getName(), "Dental", CommissionsTable.RATE.getName(), rate));
    }
}
