package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionRuleTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CommissionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.EnrollmentTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.agency;
import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.agencyInfoTab;
import static com.exigen.ren.admin.modules.commission.commissiongroup.CommissionGroupContext.commissionGroup;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext.*;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.ADD_COMMISSION_RULE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SELECT_SALES_CHANNEL;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SelectSalesChannel.COMMISSION_CHANNEL_GROUP_NAME;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionRuleTab.tableCommissionRules;
import static com.exigen.ren.main.enums.ActionConstants.DELETE;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCommissionScheduleVerificationForCumulativeTiered extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25787", component = POLICY_GROUPBENEFITS)
    public void testCommissionScheduleVerificationForCumulativeTiered_STD() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData()
                .getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        String channelCommissionGroupName = channelCommissionGroup.get("Group Name");

        gbCommissionStrategy.navigate();
        gbCommissionSearchTab.getAssetList().getAsset(CommissionSearchTabMetaData.COMMISSION_STRATEGY_STATUS).setValue(CommissionConstants.CommissionStatus.ACTIVE);
        CommissionPage.buttonSearch.click();

        CommissionPage.search(gbCommissionStrategy.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));

        TestData tieredTestData = gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "CommissionRuleUpdate")
                .adjust(TestData.makeKeyPath(GBCommissionRuleMetaData.COMMISSION_TYPE.getLabel()), "Cumulative Tiered").resolveLinks()
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName).resolveLinks();

        gbCommissionStrategy.edit().start("GB_DI_STD - Short Term Disability");
        if (tableCommissionRules.getRowsCount() > 7 ){
            tableCommissionRules.getColumn(GBCommissionRuleTab.CommissionRules.ACTIONS).getCell(8).controls.links.get(DELETE).click();
            Page.dialogConfirmation.buttonYes.click();
        }
        GBCommissionRuleTab.buttonSave.click();

        gbCommissionStrategy.edit().perform(gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "TestDataRuleUpdate")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData)), "GB_DI_STD - Short Term Disability");

        String agencyName1 = agency.createAgency(agency.defaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.COMMISSION_GROUPS.getLabel()), ImmutableList.of(channelCommissionGroupName))
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.AGENCY_TYPE.getLabel()), "Call Center"));

        String agencyName2 = agency.createAgency(agency.defaultTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Corporate")
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.COMMISSION_GROUPS.getLabel()), ImmutableList.of(channelCommissionGroupName))
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.AGENCY_TYPE.getLabel()), "Branch"));

        LOGGER.info("General main preconditions execution");
        mainApp().reopen();
        TestData tdAgency1 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1);
        TestData tdAgency2 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName2);

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()), ImmutableList.of(tdAgency1, tdAgency2)));

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), shortTermDisabilityMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 execution");
        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENCY_PRODUCER_COMBO.getLabel()), agencyName1)
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENT_SUB_PRODUCER.getLabel())).resolveLinks(), PolicyInformationTab.class, true);

        LOGGER.info("Steps#3, 4 execution");
        policyInformationTab.getAssetList().getAsset(ADD_AGENCY).click();
        policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(AssignedAgenciesMetaData.AGENCY_PRODUCER).setValue(agencyName2);
        policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(INDEPENDENT_COMMISSIONABLE_PRODUCER_ICP).setValue(VALUE_YES);

        LOGGER.info("Step#5 execution");
        policyInformationTab.submitTab();
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultSTDMasterPolicyData(), EnrollmentTab.class, PremiumSummaryTab.class, true);

        LOGGER.info("Step#6 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(PremiumSummaryTab.getCommissionName(1)).isEqualTo(String.format("%s - Call Center", agencyName1));
            softly.assertThat(PremiumSummaryTab.getCommissionName(2)).isEqualTo(String.format("%s - Branch", agencyName2));

            LOGGER.info("Steps#7, 8 verification");
            commissionsVerifications(agencyName1, softly);

            LOGGER.info("Steps#10, 11 verification");
            commissionsVerifications(agencyName2, softly);
        });
    }

    private void commissionsVerifications(String agencyName, ETCSCoreSoftAssertions softly) {
        softly.assertThat(PremiumSummaryTab.getCommissionTypeByAgency(agencyName, "STD Core")).isEqualTo("Cumulative Tiered");
        softly.assertThat(PremiumSummaryTab.getCommissionTable(agencyName, "STD Core").getHeader().getValue()).containsExactly(PREMIUM_RECEIVED_PER_P_YEAR.getName(), RATE.getName());

        Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, "STD Core");
        softly.assertThat(overrideCommissionButton).isPresent();
    }
}
