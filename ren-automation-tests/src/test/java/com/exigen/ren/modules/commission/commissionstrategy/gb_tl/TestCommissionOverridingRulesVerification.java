package com.exigen.ren.modules.commission.commissionstrategy.gb_tl;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AGENCIES;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.ADD_COMMISSION_RULE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.COMMISSION_RATE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SELECT_SALES_CHANNEL;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SelectSalesChannel.COMMISSION_CHANNEL_GROUP_NAME;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData.AddCommissionOverrideOptionsMetaData.ADD_COMMISSION_OVERRIDE_OPTIONS;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData.CommissionOverrideOptions.NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.commissionRateInfoTable;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.TableConstants.CommissionsTable.OVERRIDDEN;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionOverridingRulesVerification extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext {

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22119", component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionOverridingRulesVerification() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName1 = agency.createAgency(tdAgencyDefault);
        String agencyName2 = agency.createAgency(tdAgencyDefault);
        String agencyName3 = agency.createAgency(tdAgencyDefault);
        String agencyName4 = agency.createAgency(tdAgencyDefault);

        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName1)
                .adjust(TestData.makeKeyPath(authorityLevelsTab.getMetaKey(), AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 6");
        String userLogin = tdNewProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = tdNewProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());
        profileCorporate.create(tdNewProfile);

        userCreate(agencyName2);
        userCreate(agencyName3);
        userCreate(agencyName4);

        String channelCommissionGroupName1 = commissionGroupCreate(agencyName1);
        String channelCommissionGroupName2 = commissionGroupCreate(agencyName2);
        String channelCommissionGroupName3 = commissionGroupCreate(agencyName3);
        String channelCommissionGroupName4 = commissionGroupCreate(agencyName4);

        TestData flatTestData = gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "CommissionRuleUpdateFlat")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), COMMISSION_RATE.getLabel()), "20")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName1).resolveLinks();

        TestData tieredTestData = tdSpecific().getTestData("CommissionRuleTiered")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName2).resolveLinks();

        TestData cumulativeTieredTestData = tdSpecific().getTestData("CommissionRuleCumulativeTiered")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName3).resolveLinks();

        TestData subscriberCountTestData = tdSpecific().getTestData("CommissionRuleSubscriberCount")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName4).resolveLinks();

        createCommissionStrategy(tdSpecific().getTestData("TestDataCommission")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(flatTestData, tieredTestData, cumulativeTieredTestData, subscriberCountTestData)), false);

        LOGGER.info("General main preconditions execution");
        mainApp().reopen(userLogin, userPassword);
        TestData tdAgency1 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1);
        TestData tdAgency2 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName2);
        TestData tdAgency3 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName3);
        TestData tdAgency4 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName4);

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()), ImmutableList.of(tdAgency1, tdAgency2, tdAgency3, tdAgency4)));

        LocalDateTime caseEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(1);
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(termLifeInsuranceMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), caseEffectiveDate.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL"))
                .adjust(TestData.makeKeyPath(fileIntakeManagementTab.getMetaKey() + "[0]", FileIntakeManagementTabMetaData.EFFECTIVE_DATE.getLabel()), caseEffectiveDate.format(MM_DD_YYYY)));

        LOGGER.info("Step#1 verification");
        TestData tdPolicyAgency1 = tdSpecific().getTestData("Agency1")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName1).resolveLinks();
        TestData tdPolicyAgency2 = tdSpecific().getTestData("Agency2")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName2).resolveLinks();
        TestData tdPolicyAgency3 = tdSpecific().getTestData("Agency3")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName3).resolveLinks();
        TestData tdPolicyAgency4 = tdSpecific().getTestData("Agency4")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName4).resolveLinks();

        initiateTLQuoteAndFillToTab(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMaster")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel()),
                                ImmutableList.of(tdPolicyAgency1, tdPolicyAgency2, tdPolicyAgency3, tdPolicyAgency4)).resolveLinks())
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SELF_ADMINISTERED.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", PlanDefinitionTabMetaData.SELF_ADMINISTERED.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[3]", PlanDefinitionTabMetaData.SELF_ADMINISTERED.getLabel()), VALUE_NO), PremiumSummaryTab.class, true);

        assertSoftly(softly -> {
            softly.assertThat(PremiumSummaryTab.getCommissionName(1)).isEqualTo(String.format("%s - Agency", agencyName1));
            softly.assertThat(PremiumSummaryTab.getCommissionName(2)).isEqualTo(String.format("%s - Agency", agencyName2));
            softly.assertThat(PremiumSummaryTab.getCommissionName(3)).isEqualTo(String.format("%s - Agency", agencyName3));
            softly.assertThat(PremiumSummaryTab.getCommissionName(4)).isEqualTo(String.format("%s - Agency", agencyName4));

            checkCommissionSectionForAgency(agencyName1, "20 %", softly);
            checkCommissionSectionForAgency(agencyName2, "10 %", softly);
            checkCommissionSectionForAgency(agencyName3, "25 %", softly);
            checkCommissionSectionForAgency(agencyName4, new Currency(25).toString(), softly);

            LOGGER.info("Step#2 verification");
            String tieredName = tdSpecific().getValue("Add_Commission_Tiered_Override", ADD_COMMISSION_OVERRIDE_OPTIONS.getLabel(), NAME.getLabel());
            String cumulativeTieredName = tdSpecific().getValue("Add_Commission_Cumulative_Tiered_Override", ADD_COMMISSION_OVERRIDE_OPTIONS.getLabel(), NAME.getLabel());

            subscriberCountFlatOverrideVerification(agencyName1, 15, softly);
            tiersOverrideVerification(agencyName2, tieredName, "30 %", softly);
            tiersOverrideVerification(agencyName2, cumulativeTieredName, "35 %", softly);
            subscriberCountFlatOverrideVerification(agencyName4, 35, softly);
        });

        LOGGER.info("Step#3 verification");
        TermLifeInsuranceMasterPolicyContext.premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(proposalActionTab.getMetaKey(), OVERRIDE_RULES_LIST_KEY),
                        ImmutableList.of("Proposal requires Underwriter Management approval as Total Commission exceeds...")));

        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        termLifeInsuranceMasterPolicy.issue().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#4 verification");
        termLifeInsuranceCertificatePolicy.createPolicy(getDefaultCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY))
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    private void userCreate(String agencyName) {
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName);
        profileCorporate.create(tdNewProfile);
    }

    private String commissionGroupCreate(String agencyName) {
        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName));
        return channelCommissionGroup.get("Group Name");
    }

    private void checkCommissionSectionForAgency(String agencyName, String rateValue, ETCSCoreSoftAssertions softly) {
        softly.assertThat(PremiumSummaryTab.getCommissionTable(agencyName, ADD).getColumn(RATE.getName()).getCell(1)).hasValue(rateValue);
        softly.assertThat(PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, ADD)).isEnabled();
        softly.assertThat(PremiumSummaryTab.getCommissionTable(agencyName, DEP_BTL).getColumn(RATE.getName()).getCell(1)).hasValue(rateValue);
        softly.assertThat(PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, DEP_BTL)).isEnabled();
        softly.assertThat(PremiumSummaryTab.getCommissionTable(agencyName, BTL).getColumn(RATE.getName()).getCell(1)).hasValue(rateValue);
        softly.assertThat(PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, BTL)).isEnabled();
    }

    private void subscriberCountFlatOverrideVerification(String agencyName, int amount, ETCSCoreSoftAssertions softly) {
        String subscriberCountFlatName = tdSpecific().getValue("Add_Commission_Subscr_Count_Flat_Override", ADD_COMMISSION_OVERRIDE_OPTIONS.getLabel(), NAME.getLabel());

        PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, ADD).click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(subscriberCountFlatName);

        commissionRateInfoTable.getRowContains(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.COVERAGE_NAME.getName(), BTL)
                .getCell(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.RATE.getName()).controls.textBoxes.getFirst().setValue(String.valueOf(amount));

        DialogOverrideCommissionPremiumSummary.overrideCommissionForAllCoverages(String.valueOf(amount));
        PremiumSummaryTab.dialogOverrideCommision.confirm();

        PremiumSummaryTab.getCommissionTable(agencyName, ADD).getRows().forEach(row -> {
            softly.assertThat(row.getCell(RATE.getName())).hasValue(new Currency(amount).toString());
            softly.assertThat(row.getCell(OVERRIDDEN.getName())).hasValue("Yes");
        });
    }

    private void tiersOverrideVerification(String agencyName, String commissionOverrideOptionName, String rateValue, ETCSCoreSoftAssertions softly) {
        PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, ADD).click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(commissionOverrideOptionName);
        PremiumSummaryTab.dialogOverrideCommision.confirm();
        softly.assertThat(PremiumSummaryTab.getCommissionTable(agencyName, ADD).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 -").getCell(RATE.getName())).hasValue(rateValue);
    }
}
