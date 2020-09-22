package com.exigen.ren.modules.commission.commissionstrategy.gb_ac;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.common.controls.TripleTextBox;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnrollmentTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AGENCIES;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.ADD_COMMISSION_RULE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SELECT_SALES_CHANNEL;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SelectSalesChannel.COMMISSION_CHANNEL_GROUP_NAME;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionRules.ACTIONS;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionRules.COMMISSION_TYPE;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.POLICY_YEAR_UPDATE_JOB;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.main.enums.ActionConstants.EDIT;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.CURRENT_DUE;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.getCommissionTable;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.getCommissionTypeByAgency;
import static com.exigen.ren.main.pages.summary.billing.BillingSummaryPage.tableBillableCoverages;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITH_TS;

public class TestPolicyYearForPremiumTierAccLongTermVerification extends CommissionStrategyBaseTest implements GBCommissionStrategyContext, CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final String SQL_GET_AMOUNT = "select bcp.policyNumber, bcp.commissionItemCode, bct.* from BenefitsCommissionTransaction bct join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id where bcp.policyNumber in ('%s') ORDER by bct.id;";

    @Test(groups = {COMMISSIONS, WITH_TS})
    @TestInfo(testCaseId = {"REN-33783", "REN-33785"}, component = COMMISIONS_GROUPBENEFITS)
    public void testPolicyYearForPremiumTierAccountsVerification() {
        LOGGER.info("General admin preconditions execution");
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        TestData policyTestData = tdSpecific().getTestData("TestDataMasterPolicy");

        adminApp().open();
        String agencyName = agency.createAgency(tdAgencyDefault);
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName);

        String userLogin = tdNewProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = tdNewProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());
        profileCorporate.create(tdNewProfile);

        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName));
        String channelCommissionGroupName = channelCommissionGroup.get("Group Name");

        gbCommissionStrategy.search(gbCommissionStrategyDefaultTestData.getTestData("SearchData", TestDataKey.DEFAULT_TEST_DATA_KEY));
        TestData tieredTestData = tdSpecific().getTestData("Tiered_CommissionRule")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName);

        gbCommissionStrategy.edit().perform(tdSpecific().getTestData("TestDataAdmin")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData)), "GB_AC - Group Accident");

        gbCommissionStrategy.edit().start("GB_AC - Group Accident");
        GBCommissionStrategyTab.tableCommissionRules.getRowContains(COMMISSION_TYPE.getName(), "Tiered").getCell(ACTIONS.getName()).controls.links.get(EDIT).click();
        DialogAssetList commissionRuleAssetList = gbCommissionRuleTab.getAssetList().getAsset(GBCommissionRuleMetaData.ADD_COMMISSION_RULE);
        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();

        TripleTextBox tier2 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_2);
        TripleTextBox tier3 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_3);
        tier2.getControlByIndex(1).setValue("200");
        tier2.getControlByIndex(2).setValue("9");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier3.getControlByIndex(2).setValue("5");
        Page.dialogConfirmation.confirm();
        GBCommissionStrategyTab.buttonSave.click();

        LOGGER.info("Common general preconditions execution");
        mainApp().reopen(userLogin, userPassword);
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                                .adjust(TestData.makeKeyPath(AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName))));

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), groupAccidentMasterPolicy.getType());

        LOGGER.info("REN-33783 Step#1, 2, 3, 4 verification");
        String billingNumberLongTerm = issuePolicyWithCommissionsVerification(policyTestData, agencyName, currentDate, 15);

        LOGGER.info("REN-33785 Steps#1, 2, 3, 4 verification");
        String billingNumberShortTerm = issuePolicyWithCommissionsVerification(policyTestData, agencyName, currentDate, 6);

        LOGGER.info("REN-33783 Steps#5, 7, 8, 9 verification");
        billTwoInstallmentsAndCommissionCheck(billingNumberLongTerm, currentDate);

        LOGGER.info("REN-33785 Steps#5, 7, 8, 9 verification");
        billTwoInstallmentsAndCommissionCheck(billingNumberShortTerm, currentDate);

        LOGGER.info("REN-33785 Step#10 execution");
        generateFutureStatement(4);

        LOGGER.info("REN-33785 Step#10 execution part 1");
        BillingAccountsListPage.openAccountByNumber(billingNumberLongTerm);
        generateFutureStatement(4);

        LOGGER.info("REN-33785 Steps#11, 12, 13, 15, 16, 17 execution");
        shiftDateAndCheckCommissionAfterJobExecution(currentDate, billingNumberShortTerm, 6);

        LOGGER.info("REN-33783 Step#10 execution part 2");
        BillingAccountsListPage.openAccountByNumber(billingNumberLongTerm);
        generateFutureStatement(9);

        LOGGER.info("REN-33783 Step#11, 12, 13, 15, 16, 17 execution");
        shiftDateAndCheckCommissionAfterJobExecution(currentDate, billingNumberLongTerm, 15);
    }

    private String issuePolicyWithCommissionsVerification(TestData td, String agencyName, LocalDateTime currentDate, int monthsAmount) {

        LOGGER.info("Common Step#1 execution");
        initiatGACQuoteAndFillUpToTab(getDefaultACMasterPolicyData()
                .adjust(td).resolveLinks(), PolicyInformationTab.class, true);

        assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(POLICY_EFFECTIVE_DATE)).hasValue(currentDate.format(MM_DD_YYYY));
            softly.assertThat(policyInformationTab.getAssetList().getAsset(CURRENT_POLICY_YEAR_START_DATE)).isDisabled().hasValue(currentDate.format(MM_DD_YYYY));
        });
        policyInformationTab.getAssetList().getAsset(NEXT_POLICY_YEAR_START_DATE).setValue(currentDate.plusMonths(monthsAmount).format(MM_DD_YYYY));

        LOGGER.info("Common Step#2 verification");
        policyInformationTab.submitTab();
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SIC_CODE.getLabel()), "2514")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(),
                        PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SELF_ADMINISTERED.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(GroupAccidentMasterPolicyContext.classificationManagementMPTab.getMetaKey() + "[0]", NUMBER_OF_PARTICIPANTS.getLabel()), "10"), EnrollmentTab.class, PremiumSummaryTab.class, true);

        LOGGER.info("Common Step#3 verification");
        assertSoftly(softly -> {
            softly.assertThat(getCommissionTypeByAgency(agencyName, ENHANCED_ACCIDENT)).isEqualTo("Tiered");

            softly.assertThat(getCommissionTable(agencyName, ENHANCED_ACCIDENT).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 - 100").getCell(RATE.getName())).hasValue("15 %");
            softly.assertThat(getCommissionTable(agencyName, ENHANCED_ACCIDENT).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "100 - 200").getCell(RATE.getName())).hasValue("9 %");
            softly.assertThat(getCommissionTable(agencyName, ENHANCED_ACCIDENT).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "200 - ").getCell(RATE.getName())).hasValue("5 %");
        });

        LOGGER.info("Common Step#4 verification");
        premiumSummaryTab.submitTab();
        proposeAcceptContractIssueACMasterPolicyWithDefaultTestData();

        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        return BillingAccountsListPage.getAccountNumber();
    }

    private void billTwoInstallmentsAndCommissionCheck(String billingNumber, LocalDateTime currentDate) {
        LOGGER.info("Common Step#5 verification");
        BillingAccountsListPage.openAccountByNumber(billingNumber);
        String policyNumber = tableBillableCoverages.getColumn(TableConstants.BillingBillableCoveragesGB.POLICY).getCell(1).getValue();

        generateInvoiceAndPeriodCheck(currentDate, currentDate.plusMonths(1).minusDays(1));
        Currency policyTotalDueOneBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());

        LOGGER.info("Common Step#7 verification");
        checkSQLValueAfterFirstBill(policyNumber, policyTotalDueOneBill);

        LOGGER.info("Common Step#8 verification");
        generateInvoiceAndPeriodCheck(currentDate.plusMonths(1), currentDate.plusMonths(2).minusDays(1));
        Currency policyTotalDueTwoBills = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());

        LOGGER.info("Common Step#9 verification");
        checkSQLValueAfterSecondBill(policyNumber, policyTotalDueTwoBills, policyTotalDueOneBill);
    }

    private void shiftDateAndCheckCommissionAfterJobExecution(LocalDateTime currentDate, String billingNumber, int monthsNumber) {
        LOGGER.info("Common Steps#11 execution");
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusMonths(monthsNumber).minusDays(1));
        JobRunner.executeJob(POLICY_YEAR_UPDATE_JOB);

        LOGGER.info("Common Steps#12 execution");
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusMonths(monthsNumber));

        LOGGER.info("Common Steps#13 verification");
        mainApp().reopen();
        MainPage.QuickSearch.search(billingNumber);
        LocalDateTime currentDatePhase1 = DateTimeUtils.getCurrentDateTime();
        String policyNumber = tableBillableCoverages.getColumn(TableConstants.BillingBillableCoveragesGB.POLICY).getCell(1).getValue();
        generateInvoiceAndPeriodCheck(currentDatePhase1, currentDatePhase1.plusMonths(1).minusDays(1));
        Currency policyTotalDueOneBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(CURRENT_DUE).getValue());

        LOGGER.info("Common Steps#15 verification");
        checkSQLValueAfterFirstBill(policyNumber, policyTotalDueOneBill);

        LOGGER.info("Common Steps#16 verification");
        generateInvoiceAndPeriodCheck(currentDatePhase1.plusMonths(1), currentDatePhase1.plusMonths(2).minusDays(1));

        LOGGER.info("Common Step#17 verification");
        checkSQLValueAfterSecondBill(policyNumber, policyTotalDueOneBill.multiply(2), policyTotalDueOneBill);//check this
    }

    private void generateInvoiceAndPeriodCheck(LocalDateTime time1, LocalDateTime time2) {
        billingAccount.generateFutureStatement().perform();
        String firstInvoicePeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        assertThat(firstInvoicePeriod).isEqualTo(String.format("%s - %s", time1.format(MM_DD_YYYY), time2.format(MM_DD_YYYY)));
    }

    private void checkSQLValueAfterFirstBill(String policyNumber, Currency amount) {
        List<Map<String, String>> dbResponseListStep7 = dbService.getRows(String.format(SQL_GET_AMOUNT, policyNumber));
        String amountValue = dbResponseListStep7.get(0).get("amount");
        assertThat(amountValue).isEqualTo(String.valueOf(amount.multiply(0.15)).replace("$", ""));
    }

    private void checkSQLValueAfterSecondBill(String policyNumber, Currency twoBillsValue, Currency oneBillValue) {
        List<Map<String, String>> dbResponseListStep9 = dbService.getRows(String.format(SQL_GET_AMOUNT, policyNumber));
        String amountValueNewTransaction = dbResponseListStep9.get(1).get("amount");

        Currency over100Commission = twoBillsValue.subtract(100).multiply(0.09);
        Currency under100Commission = (new Currency(100).subtract(oneBillValue)).multiply(0.15);
        Currency formulaValue = over100Commission.add(under100Commission);
        assertThat(amountValueNewTransaction).isEqualTo(String.valueOf(formulaValue).replace("$", ""));
    }

    private void generateFutureStatement(int quantityGeneratedTimes) {
        for (int a = 1; a <= quantityGeneratedTimes; a++) {
            billingAccount.generateFutureStatement().perform();
        }
    }
}
