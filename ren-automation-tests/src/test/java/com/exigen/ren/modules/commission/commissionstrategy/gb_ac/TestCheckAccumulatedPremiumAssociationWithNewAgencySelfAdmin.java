package com.exigen.ren.modules.commission.commissionstrategy.gb_ac;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.common.controls.TripleTextBox;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.ChangeAgencyTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.ChangeAgencyTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AGENCIES;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.ADD_COMMISSION_RULE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SELECT_SALES_CHANNEL;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SelectSalesChannel.COMMISSION_CHANNEL_GROUP_NAME;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionRules.ACTIONS;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionRules.SALES_CHANNEL;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.main.enums.ActionConstants.EDIT;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.CURRENT_DUE;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SELF_ADMINISTERED;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES_ONLY;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.getCommissionTable;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.getCommissionTypeByAgency;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCheckAccumulatedPremiumAssociationWithNewAgencySelfAdmin extends CommissionStrategyBaseTest implements GBCommissionStrategyContext, CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static String SQL_GET_TRANSACTION = "select bct.id, bcp.policyNumber, bcp.commissionItemCode, bct.DTYPE, \n" +
            "bct.type, bct.amount, bcp.salesChannelType, bcp.salesChannelCd\n" +
            "from BenefitsCommissionTransaction bct \n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id \n" +
            "where bcp.policyNumber in ('%s') ORDER by bct.id;";

    private static String SQL_GET_GRID_ITEM = "select * from CommissionInvoiceGridItem cigi\n" +
            "join CommissionSplitParticipant csp on csp.id=cigi.participant_id\n" +
            "join BenefitsCommissionTransaction bct on bct.id = cigi.benefCommissionPayableTXId\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id where bcp.policyNumber in ('%s')";

    private static String SQL_GET_COMMISSION_POLICY_BALANCE = "select * from RenBenefitsCommissionPolicyBalance where policyNumber in ('%s');";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-42746"}, component = COMMISIONS_GROUPBENEFITS)
    public void testCheckAccumulatedPremiumAssociationWithNewAgencySelfAdmin() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName1 = agency.createAgency(tdAgencyDefault);
        String agencyCode1 = getAgencyCode(agencyName1);
        String agencyName2 = agency.createAgency(tdAgencyDefault);
        String agencyCode2 = getAgencyCode(agencyName2);

        createUser(agencyName1);
        createUser(agencyName2);

        String channelCommissionGroupName1 = getCommissionGroupName(agencyName1);
        String channelCommissionGroupName2 = getCommissionGroupName(agencyName2);

        TestData tieredTestData1 = tdSpecific().getTestData("Tiered_CommissionRule1")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName1).resolveLinks();

        TestData tieredTestData2 = tdSpecific().getTestData("Tiered_CommissionRule2")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName2).resolveLinks();

        createCommissionStrategy(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData1, tieredTestData2)), false);

        gbCommissionStrategy.edit().start("GB_AC - Group Accident");
        GBCommissionStrategyTab.tableCommissionRules.getRowContains(SALES_CHANNEL.getName(), channelCommissionGroupName1).getCell(ACTIONS.getName()).controls.links.get(EDIT).click();

        DialogAssetList commissionRuleAssetList = gbCommissionRuleTab.getAssetList().getAsset(GBCommissionRuleMetaData.ADD_COMMISSION_RULE);
        TripleTextBox tier2 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_2);

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier2.getControlByIndex(2).setValue("5");
        Page.dialogConfirmation.confirm();

        GBCommissionStrategyTab.tableCommissionRules.getRowContains(SALES_CHANNEL.getName(), channelCommissionGroupName2).getCell(ACTIONS.getName()).controls.links.get(EDIT).click();
        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier2.getControlByIndex(2).setValue("3");
        Page.dialogConfirmation.confirm();
        GBCommissionStrategyTab.buttonSave.click();

        LOGGER.info("Common general preconditions execution");
        TestData tdAgency1 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1);
        TestData tdAgency2 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName2);

        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()), ImmutableList.of(tdAgency1, tdAgency2)));
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupAccidentMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")));

        initiatGACQuoteAndFillUpToTab(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataWithBasicBenefitsTab")
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES_ONLY.getLabel() + "[0]",
                                PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName1).resolveLinks())
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SIC_DESCRIPTION.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", PlanDefinitionTabMetaData.SIC_DESCRIPTION.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SELF_ADMINISTERED.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[2]", PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SELF_ADMINISTERED.getLabel()), VALUE_YES).resolveLinks(), PremiumSummaryTab.class, true);

        LOGGER.info("Step#3 verification");
        assertSoftly(softly -> {
            softly.assertThat(getCommissionTypeByAgency(agencyName1, "Enhanced Accident")).isEqualTo("Cumulative Tiered");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 - 15").getCell(RATE.getName())).hasValue("10 %");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "15 - ").getCell(RATE.getName())).hasValue("5 %");

            softly.assertThat(getCommissionTypeByAgency(agencyName1, "Basic Accident")).isEqualTo("Cumulative Tiered");
            softly.assertThat(getCommissionTable(agencyName1, "Basic Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 - 15").getCell(RATE.getName())).hasValue("10 %");
            softly.assertThat(getCommissionTable(agencyName1, "Basic Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "15 - ").getCell(RATE.getName())).hasValue("5 %");
        });

        LOGGER.info("Step#4 execution");
        premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        proposeAcceptContractIssueACMasterPolicyWithDefaultTestData();

        LOGGER.info("Step#5 execution");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();

        Currency policyTotalDueFirstBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);

        Currency policyTotalDueFirstBillBasic = new Currency(BillingSummaryPage.tableBillableCoverages
                .getRow(TableConstants.BillingBillableCoveragesGB.COVERAGE_CODE.getName(), "BASIC, Employee Only (Sponsor)").getCell(TOTAL_DUE).getValue());
        Currency policyTotalDueFirstBillEnhanced = new Currency(BillingSummaryPage.tableBillableCoverages
                .getRow(TableConstants.BillingBillableCoveragesGB.COVERAGE_CODE.getName(), "ENHANCED, Employee Only (Member)").getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);
        String commissionAmountForAllBasic = policyTotalDueFirstBillBasic.multiply(0.1).toPlainString();
        String commissionAmountForAllEnhanced = policyTotalDueFirstBillEnhanced.multiply(0.1).toPlainString();
        String commissionAmountForOneBasic = commissionAmountForAllBasic.concat("000000");
        String commissionAmountForOneEnhanced = commissionAmountForAllEnhanced.concat("000000");

        List<Map<String, String>> dbResponseListTransaction1 = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListCommissionInvoice1 = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        List<Map<String, String>> dbResponseListPolicyBalance1 = dbService.getRows(String.format(SQL_GET_COMMISSION_POLICY_BALANCE, masterPolicyNumber));
        assertSoftly(softly -> {

            LOGGER.info("Step#6 verification");
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("salesChannelCd").equals(agencyCode1)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("DTYPE").equals("BenefitsCommissionPayableTX")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("commissionItemCode").equals("BASIC")).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("amount").equals(commissionAmountForAllBasic)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("amount").equals(commissionAmountForAllEnhanced)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#7 verification");
            softly.assertThat(dbResponseListCommissionInvoice1.stream().filter(x -> x.get("producerCd").equals(agencyCode1)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListCommissionInvoice1.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForOneBasic)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice1.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForOneEnhanced)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice1.stream().filter(x -> x.get("code").equals("ENHANCED")).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice1.stream().filter(x -> x.get("code").equals("BASIC")).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#8 verification");
            softly.assertThat(dbResponseListPolicyBalance1.stream().filter(x -> x.get("coverageCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyBalance1.stream().filter(x -> x.get("coverageCode").equals("BASIC")).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyBalance1.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBillBasic.toPlainString())).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyBalance1.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBillEnhanced.toPlainString())).collect(Collectors.toList())).hasSize(1);
        });

        LOGGER.info("Step#9 verification");
        billingAccount.generateFutureStatement().perform();
        Currency policiesAmountUnder15 = new Currency(15).subtract(policyTotalDueFirstBill);

        String commissionAmountPolicyOver15Enhanced = policyTotalDueFirstBillEnhanced.multiply(0.05).toPlainString().concat("000000");
        String commissionAmountPolicyUnder15 = policiesAmountUnder15.multiply(0.1).toPlainString().concat("000000");
        Currency commissionAmountReminder = new Currency(1).multiply(0.05);
        Currency commissionAmountReminderBasic = new Currency(commissionAmountPolicyUnder15).multiply(0.05);

        String amountsForAllPoliciesForSecondBillBasic = new Currency(commissionAmountPolicyUnder15).add((commissionAmountReminder)).toPlainString();
        String amountsForAllPoliciesForSecondBillEnhanced = new Currency(commissionAmountPolicyOver15Enhanced).toPlainString();

        List<Map<String, String>> dbResponseListTransaction2 = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 2,
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListCommissionInvoice2 = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        List<Map<String, String>> dbResponseListPolicyBalance2 = dbService.getRows(String.format(SQL_GET_COMMISSION_POLICY_BALANCE, masterPolicyNumber));
        assertSoftly(softly -> {

            LOGGER.info("Step#10 verification");
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("salesChannelCd").equals(agencyCode1)).collect(Collectors.toList())).hasSize(4);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("DTYPE").equals("BenefitsCommissionPayableTX")).collect(Collectors.toList())).hasSize(4);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("commissionItemCode").equals("BASIC")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("amount").equals(amountsForAllPoliciesForSecondBillBasic)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("amount").equals(amountsForAllPoliciesForSecondBillEnhanced)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#11 verification");
            softly.assertThat(dbResponseListCommissionInvoice2.stream().filter(x -> x.get("producerCd").equals(agencyCode1)).collect(Collectors.toList())).hasSize(5);
            softly.assertThat(dbResponseListCommissionInvoice2.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder15)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice2.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver15Enhanced)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice2.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountReminderBasic.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice2.stream().filter(x -> x.get("code").equals("ENHANCED")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListCommissionInvoice2.stream().filter(x -> x.get("code").equals("BASIC")).collect(Collectors.toList())).hasSize(3);

            LOGGER.info("Step#12 verification");
            softly.assertThat(dbResponseListPolicyBalance2.stream().filter(x -> x.get("coverageCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListPolicyBalance2.stream().filter(x -> x.get("coverageCode").equals("BASIC")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListPolicyBalance2.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBillBasic.toPlainString())).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListPolicyBalance2.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBillEnhanced.toPlainString())).collect(Collectors.toList())).hasSize(2);
        });

        LOGGER.info("Step#13 verification");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupAccidentMasterPolicy.changeAgency().perform(groupAccidentMasterPolicy.getDefaultTestData("ChangeAgency", "TestData_ChangeAgency")
                .adjust(TestData.makeKeyPath(ChangeAgencyTab.class.getSimpleName(), ChangeAgencyTabMetaData.TRANSFER_EFFECTIVE_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusMonths(2).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(ChangeAgencyTab.class.getSimpleName(), ChangeAgencyTabMetaData.TRANSFER_TARGET.getLabel() + "[0]", ChangeAgencyTabMetaData.TransferTargetMetaData.AGENCY_PRODUCER.getLabel()), agencyName2));

        LOGGER.info("Step#14 verification");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();

        Currency basicCurrentDue = new Currency(BillingSummaryPage.tableBillableCoverages
                .getRow(TableConstants.BillingBillableCoveragesGB.COVERAGE_CODE.getName(), "BASIC, Employee Only (Sponsor)").getCell(CURRENT_DUE).getValue(), RoundingMode.HALF_DOWN);

        String commissionAmountForAllBasicAgency2 = basicCurrentDue.multiply(0.03).toPlainString();
        String commissionAmountForAllEnhancedAgency2 = policyTotalDueFirstBillEnhanced.multiply(0.03).toPlainString();
        String commissionAmountForOneBasicAgency2 = commissionAmountForAllBasicAgency2.concat("000000");
        String commissionAmountForOneEnhancedAgency2 = commissionAmountForAllEnhancedAgency2.concat("000000");

        List<Map<String, String>> dbResponseListTransaction3 = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 4,
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListCommissionInvoice3 = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        List<Map<String, String>> dbResponseListPolicyBalance3 = dbService.getRows(String.format(SQL_GET_COMMISSION_POLICY_BALANCE, masterPolicyNumber));

        assertSoftly(softly -> {

            LOGGER.info("Step#15 verification");
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("salesChannelCd").equals(agencyCode2)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("DTYPE").equals("BenefitsCommissionPayableTX")).collect(Collectors.toList())).hasSize(6);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("commissionItemCode").equals("BASIC")).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("amount").equals(commissionAmountForAllBasicAgency2)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("amount").equals(commissionAmountForAllEnhancedAgency2)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#16 verification");
            softly.assertThat(dbResponseListCommissionInvoice3.stream().filter(x -> x.get("producerCd").equals(agencyCode2)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListCommissionInvoice3.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForOneBasicAgency2)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice3.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForOneEnhancedAgency2)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListCommissionInvoice3.stream().filter(x -> x.get("code").equals("ENHANCED")).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListCommissionInvoice3.stream().filter(x -> x.get("code").equals("BASIC")).collect(Collectors.toList())).hasSize(4);

            LOGGER.info("Step#17 verification");
            softly.assertThat(dbResponseListPolicyBalance3.stream().filter(x -> x.get("coverageCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListPolicyBalance3.stream().filter(x -> x.get("coverageCode").equals("BASIC")).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListPolicyBalance3.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBillBasic.toPlainString())).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListPolicyBalance3.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBillEnhanced.toPlainString())).collect(Collectors.toList())).hasSize(3);
        });

        //==

    }

    private String getAgencyCode(String agencyName) {
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        return AgencyVendorSearchTab.tableAgencies.getRowContains("Agency Name", agencyName).getCell("Agency Code").getValue();
    }

    private void createUser(String agencyName) {
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName);
        profileCorporate.create(tdNewProfile);
    }

    private String getCommissionGroupName(String agencyName) {
        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName));
        return channelCommissionGroup.get("Group Name");
    }
}
