package com.exigen.ren.modules.commission.commissionstrategy.gb_ac;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.common.controls.TripleTextBox;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
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
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.main.enums.ActionConstants.EDIT;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SELF_ADMINISTERED;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES_ONLY;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.getCommissionTable;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.getCommissionTypeByAgency;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionEarnedProcessingVerificationForSelfAdminWithSplit extends CommissionStrategyBaseTest implements GBCommissionStrategyContext, CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static String SQL_GET_PRODUCT_INFO = "select * from BenefitsCommissionableProduct where policyNumber in ('%s'); ";
    private static String SQL_GET_TRANSACTION = "select bcp.policyNumber, bcp.commissionItemCode, bct.* from BenefitsCommissionTransaction bct\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id\n" +
            "where bcp.policyNumber in ('%s') ORDER by bct.id;";
    private static String SQL_GET_GRID_ITEM = "select * from CommissionInvoiceGridItem cigi\n" +
            "join CommissionSplitParticipant csp on csp.id=cigi.participant_id\n" +
            "join BenefitsCommissionTransaction bct on bct.id = cigi.benefCommissionPayableTXId\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id where bcp.policyNumber in ('%s')";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-28827"}, component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionEarnedProcessingVerificationForSelfAdminWithSplit() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName1 = agency.createAgency(tdAgencyDefault);
        String agencyCode1 = getAgencyCode(agencyName1);
        String agencyName2 = agency.createAgency(tdAgencyDefault);
        String agencyCode2 = getAgencyCode(agencyName2);

        createUser(agencyName1);
        createUser(agencyName2);

        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName1));
        String channelCommissionGroupName1 = channelCommissionGroup.get("Group Name");

        TestData tieredTestData = tdSpecific().getTestData("Tiered_CommissionRule")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName1).resolveLinks();

        createCommissionStrategy(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData)), false);
        gbCommissionStrategy.edit().start("GB_AC - Group Accident");
        GBCommissionStrategyTab.tableCommissionRules.findRow(COMMISSION_TYPE.getName(), "Tiered");
        GBCommissionStrategyTab.tableCommissionRules.getRowContains(COMMISSION_TYPE.getName(), "Tiered").getCell(ACTIONS.getName()).controls.links.get(EDIT).click();

        DialogAssetList commissionRuleAssetList = gbCommissionRuleTab.getAssetList().getAsset(GBCommissionRuleMetaData.ADD_COMMISSION_RULE);
        TripleTextBox tier2 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_2);
        TripleTextBox tier3 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_3);
        TripleTextBox tier4 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_4);
        TripleTextBox tier5 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_5);

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        //values were changed after agreement with test case reporter
        tier2.getControlByIndex(1).setValue("20");
        tier2.getControlByIndex(2).setValue("25");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier3.getControlByIndex(1).setValue("30");
        tier3.getControlByIndex(2).setValue("20");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier4.getControlByIndex(1).setValue("40");
        tier4.getControlByIndex(2).setValue("15");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier5.getControlByIndex(2).setValue("10");
        Page.dialogConfirmation.confirm();
        GBCommissionStrategyTab.buttonSave.click();

        LOGGER.info("Common general preconditions execution");
        TestData tdNewProfile = createUser(agencyName1);
        createUserAndRelogin(profileCorporate, tdNewProfile
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(),
                        AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 6"));

        TestData tdAgency1 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1);
        TestData tdAgency2 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName2);

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()), ImmutableList.of(tdAgency1, tdAgency2)));
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupAccidentMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")));

        initiatGACQuoteAndFillUpToTab(getDefaultACMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES_ONLY.getLabel() + "[0]", PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName1).resolveLinks()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES_ONLY.getLabel() + "[1]", PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName2).resolveLinks())
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SIC_CODE.getLabel()), "2514")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SELF_ADMINISTERED.getLabel()), VALUE_YES).resolveLinks(), PremiumSummaryTab.class, true);

        LOGGER.info("Step#3 verification");
        assertSoftly(softly -> {
            softly.assertThat(getCommissionTypeByAgency(agencyName1, "Enhanced Accident")).isEqualTo("Cumulative Tiered");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 - 10").getCell(RATE.getName())).hasValue("30 %");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "10 - 20").getCell(RATE.getName())).hasValue("25 %");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "20 - 30").getCell(RATE.getName())).hasValue("20 %");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "30 - 40").getCell(RATE.getName())).hasValue("15 %");
            softly.assertThat(getCommissionTable(agencyName1, "Enhanced Accident").getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "40 - ").getCell(RATE.getName())).hasValue("10 %");
        });

        LOGGER.info("Step#4 execution");
        premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(proposalActionTab.getMetaKey(), OVERRIDE_RULES_LIST_KEY),
                        ImmutableList.of("Proposal requires Underwriter Management approval as Total Commission exceeds...")));

        MainPage.QuickSearch.search(masterPolicyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#5 execution");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();

        Currency policyTotalDueFirstBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);
        String policyTotalDueFirstBill60 = new Currency(policyTotalDueFirstBill.multiply(0.6).multiply(0.3)).toPlainString();
        String policyTotalDueFirstBill40 = new Currency(policyTotalDueFirstBill.multiply(0.4).multiply(0.3)).toPlainString();

        List<Map<String, String>> dbResponseListProductInfo = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_PRODUCT_INFO, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        List<Map<String, String>> dbResponseListTransaction1 = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        List<Map<String, String>> dbResponseListGridItem1stBill = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        assertSoftly(softly -> {

            LOGGER.info("Step#6 verification");
            softly.assertThat(dbResponseListProductInfo.stream().filter(x -> x.get("salesChannelCd").equals(agencyCode1)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListProductInfo.stream().filter(x -> x.get("salesChannelCd").equals(agencyCode2)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListProductInfo.stream().filter(x -> x.get("salesChannelType").equals("agency")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListProductInfo.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListProductInfo.stream().filter(x -> x.get("commissionItemType").equals("COVERAGE")).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#7 verification");
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBill60)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction1.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBill40)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#8 verification");
            softly.assertThat(dbResponseListGridItem1stBill.stream().filter(x -> x.get("commissionAmount").equals(policyTotalDueFirstBill60.concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem1stBill.stream().filter(x -> x.get("commissionAmount").equals(policyTotalDueFirstBill40.concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem1stBill.stream().filter(x -> x.get("code").equals("ENHANCED")).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#9 verification");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                    .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), policyTotalDueFirstBill.toString()));
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED);

            List<Map<String, String>> dbResponseListTransactionAfterPayment1 = RetryService.run(
                    predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 2,
                    () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            List<Map<String, String>> dbResponseListGridItem1stPayment = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));

            LOGGER.info("Step#10 verification");
            softly.assertThat(dbResponseListTransactionAfterPayment1.stream().filter(x -> x.get("DTYPE").equals("BenefitsCommissionPayableTX")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransactionAfterPayment1.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(4);
            softly.assertThat(dbResponseListTransactionAfterPayment1.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBill60)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransactionAfterPayment1.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBill40)).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#11 verification");
            softly.assertThat(dbResponseListGridItem1stPayment.stream().filter(x -> x.get("commissionAmount").equals(policyTotalDueFirstBill60.concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem1stPayment.stream().filter(x -> x.get("commissionAmount").equals(policyTotalDueFirstBill40.concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem1stPayment.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBill60)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem1stPayment.stream().filter(x -> x.get("amount").equals(policyTotalDueFirstBill40)).collect(Collectors.toList())).hasSize(1);
        });

        LOGGER.info("Step#12 execution");
        billingAccount.generateFutureStatement().perform();

        Currency policiesAmountUnder10 = new Currency(new Currency(10).subtract(policyTotalDueFirstBill), RoundingMode.HALF_DOWN);
        Currency policiesAmountOver10 = new Currency(10).subtract(policyTotalDueFirstBill.multiply(2)).abs();

        Currency commissionAmountPolicyOver10For60 = new Currency(policiesAmountOver10.multiply(0.25).multiply(0.6).subtract(0.01));
        Currency commissionAmountPolicyOver10For40 = new Currency(policiesAmountOver10.multiply(0.25).multiply(0.4));
        Currency commissionAmountPolicyUnder10For60 = new Currency(policiesAmountUnder10.multiply(0.3).multiply(0.6));
        Currency commissionAmountPolicyUnder10For40 = new Currency(policiesAmountUnder10.multiply(0.3).multiply(0.4));

        String policyTotalDueSecondBill60 = new Currency(commissionAmountPolicyOver10For60.add(commissionAmountPolicyUnder10For60)).toPlainString();
        String policyTotalDueSecondBill40 = new Currency(commissionAmountPolicyUnder10For40.add(commissionAmountPolicyOver10For40)).toPlainString();

        List<Map<String, String>> dbResponseListTransaction2 = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 4,
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListGridItem2ndBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        assertSoftly(softly -> {

            LOGGER.info("Step#13 execution");
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(6);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill60)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction2.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill40)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#14 verification");
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder10For60.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder10For40.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver10For60.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver10For40.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("code").equals("ENHANCED")).collect(Collectors.toList())).hasSize(6);

            LOGGER.info("Step#15 verification");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                    .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), policyTotalDueFirstBill.toString()));
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED);

            List<Map<String, String>> dbResponseListTransactionAfterPayment2 = RetryService.run(
                    predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 6,
                    () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            List<Map<String, String>> dbResponseListGridItem2ndPayment = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));

            LOGGER.info("Step#16 verification");
            softly.assertThat(dbResponseListTransactionAfterPayment2.stream().filter(x -> x.get("DTYPE").equals("BenefitsCommissionPayableTX")).collect(Collectors.toList())).hasSize(4);
            softly.assertThat(dbResponseListTransactionAfterPayment2.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(8);
            softly.assertThat(dbResponseListTransactionAfterPayment2.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill60)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransactionAfterPayment2.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill40)).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#17 verification");
            softly.assertThat(dbResponseListGridItem2ndPayment.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder10For60.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndPayment.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder10For40.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndPayment.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver10For60.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndPayment.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver10For40.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndPayment.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill60)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListGridItem2ndPayment.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill40)).collect(Collectors.toList())).hasSize(2);
        });

        LOGGER.info("Step#18 execution");
        billingAccount.generateFutureStatement().perform();

        Currency policiesAmountUnder20 = policyTotalDueFirstBill.multiply(3).subtract(new Currency(10));
        Currency policiesAmountUnder20Reminder = policiesAmountUnder20.subtract(policiesAmountOver10);
        Currency policiesAmountUnder20ReminderF60 = policiesAmountUnder20.subtract(policiesAmountOver10).multiply(0.4).multiply(0.25);
        String policiesAmountUnder20ReminderF40 = new Currency(policiesAmountUnder20Reminder.multiply(0.25).multiply(0.6)).toPlainString();

        List<Map<String, String>> dbResponseListTransaction3 = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 8,
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListGridItem3rdBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        assertSoftly(softly -> {

            LOGGER.info("Step#19 execution");
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(10);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("amount").equals(policiesAmountUnder20ReminderF60.toPlainString())).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("amount").equals(policiesAmountUnder20ReminderF40)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#20 execution");
            softly.assertThat(dbResponseListGridItem3rdBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder10For60.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem3rdBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder10For40.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem3rdBill.stream().filter(x -> x.get("code").equals("ENHANCED")).collect(Collectors.toList())).hasSize(8);

            LOGGER.info("Step#21 verification");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                    .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), policyTotalDueFirstBill.toString()));
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED);

            List<Map<String, String>> dbResponseListTransactionAfterPayment3 = RetryService.run(
                    predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 10,
                    () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            List<Map<String, String>> dbResponseListGridItem3rdPayment = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));

            LOGGER.info("Step#22 verification");
            softly.assertThat(dbResponseListTransactionAfterPayment3.stream().filter(x -> x.get("DTYPE").equals("BenefitsCommissionPayableTX")).collect(Collectors.toList())).hasSize(6);
            softly.assertThat(dbResponseListTransactionAfterPayment3.stream().filter(x -> x.get("commissionItemCode").equals("ENHANCED")).collect(Collectors.toList())).hasSize(12);
            softly.assertThat(dbResponseListTransactionAfterPayment3.stream().filter(x -> x.get("amount").equals(policiesAmountUnder20ReminderF40)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListTransactionAfterPayment3.stream().filter(x -> x.get("amount").equals(policiesAmountUnder20ReminderF60.toPlainString())).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#23 verification");
            softly.assertThat(dbResponseListGridItem3rdPayment.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill60)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListGridItem3rdPayment.stream().filter(x -> x.get("amount").equals(policyTotalDueSecondBill40)).collect(Collectors.toList())).hasSize(2);

            softly.assertThat(dbResponseListGridItem3rdPayment.stream().filter(x -> x.get("commissionAmount").equals(policiesAmountUnder20ReminderF60.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem3rdPayment.stream().filter(x -> x.get("commissionAmount").equals(policiesAmountUnder20ReminderF40.concat("000000"))).collect(Collectors.toList())).hasSize(1);
        });
    }

    private String getAgencyCode(String agencyName) {
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        return AgencyVendorSearchTab.tableAgencies.getRowContains("Agency Name", agencyName).getCell("Agency Code").getValue();
    }

    private TestData createUser(String agencyName) {
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName);
        profileCorporate.create(tdNewProfile);
        return tdNewProfile;
    }
}
