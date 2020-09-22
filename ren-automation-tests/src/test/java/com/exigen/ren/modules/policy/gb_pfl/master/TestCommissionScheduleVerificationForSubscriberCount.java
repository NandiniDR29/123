package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionRuleTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.EnrollmentTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.agency;
import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.agencyInfoTab;
import static com.exigen.ren.admin.modules.commission.commissiongroup.CommissionGroupContext.commissionGroup;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext.gbCommissionRuleTab;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext.gbCommissionStrategy;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.ADD_COMMISSION_RULE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SELECT_SALES_CHANNEL;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SelectSalesChannel.COMMISSION_CHANNEL_GROUP_NAME;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionRuleTab.tableCommissionRules;
import static com.exigen.ren.main.enums.ActionConstants.DELETE;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.NUMBER_OF_SUBSCRIBERS;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.AssignedAgenciesMetaData.INDEPENDENT_COMMISSIONABLE_PRODUCER_ICP;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCommissionScheduleVerificationForSubscriberCount extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25788", component = POLICY_GROUPBENEFITS)
    public void testCommissionScheduleVerificationForSubscriberCount_PFL() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData()
                .getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        String channelCommissionGroupName = channelCommissionGroup.get("Group Name");

        gbCommissionStrategy.navigate();
        CommissionPage.search(gbCommissionStrategy.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        TestData tieredTestData = gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "CommissionRuleUpdateRate")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName).resolveLinks();

        gbCommissionStrategy.edit().start("GB_PFL - Paid Family Leave");
        if (tableCommissionRules.getRowsCount() > 7 ){
            tableCommissionRules.getColumn(GBCommissionRuleTab.CommissionRules.ACTIONS).getCell(8).controls.links.get(DELETE).click();
            Page.dialogConfirmation.buttonYes.click();
        }
        GBCommissionRuleTab.buttonSave.click();

        gbCommissionStrategy.edit().perform(gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "TestDataRuleUpdateRate")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData)), "GB_PFL - Paid Family Leave");

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

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(paidFamilyLeaveMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")));

        LOGGER.info("Steps#1, 2 execution");
        initiatePFLQuoteAndFillToTab(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENCY_PRODUCER_COMBO.getLabel()), agencyName1)
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENT_SUB_PRODUCER.getLabel())).resolveLinks(), PolicyInformationTab.class, true);

        LOGGER.info("Steps#3, 4 execution");
        policyInformationTab.getAssetList().getAsset(ADD_AGENCY).click();
        policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(AssignedAgenciesMetaData.AGENCY_PRODUCER).setValue(agencyName2);
        policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(INDEPENDENT_COMMISSIONABLE_PRODUCER_ICP).setValue(VALUE_YES);

        LOGGER.info("Step#5 execution");
        policyInformationTab.submitTab();
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFromTo(
                getDefaultPFLMasterPolicyData(), EnrollmentTab.class, ClassificationManagementTab.class, false);
        PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.submitTab();

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
        softly.assertThat(PremiumSummaryTab.getCommissionTypeByAgency(agencyName, "PFL")).isEqualTo("Subscriber Count");
        softly.assertThat(PremiumSummaryTab.getCommissionTable(agencyName, "PFL").getHeader().getValue()).containsExactly(NUMBER_OF_SUBSCRIBERS.getName(), RATE.getName());

        Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, "PFL");
        softly.assertThat(overrideCommissionButton).isPresent();
    }
}
