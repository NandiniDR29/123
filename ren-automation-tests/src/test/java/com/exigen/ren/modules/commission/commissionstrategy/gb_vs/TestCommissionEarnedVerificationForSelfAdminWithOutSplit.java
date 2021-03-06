package com.exigen.ren.modules.commission.commissionstrategy.gb_vs;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
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
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
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
import static com.exigen.ren.main.enums.CoveragesConstants.GroupVisionCoverages.VISION;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructure.MINIMUM_NUMBER_OF_PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.getCommissionTable;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab.getCommissionTypeByAgency;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionEarnedVerificationForSelfAdminWithOutSplit extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    private static String SQL_GET_PRODUCT_INFO = "select * from BenefitsCommissionableProduct where policyNumber in ('%s'); ";
    private static String SQL_GET_TRANSACTION = "select bcp.policyNumber, bcp.commissionItemCode, bct.* from BenefitsCommissionTransaction bct\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id\n" +
            "where bcp.policyNumber in ('%s') ORDER by bct.id;";
    private static String SQL_GET_GRID_ITEM = "select * from CommissionInvoiceGridItem cigi\n" +
            "join CommissionSplitParticipant csp on csp.id=cigi.participant_id\n" +
            "join BenefitsCommissionTransaction bct on bct.id = cigi.benefCommissionPayableTXId\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id where bcp.policyNumber in ('%s')";
    private static String SQL_GET_COMMISSION_INVOICE_PAYMENT_ITEM = "select mp.policyNumber, mp.producerCd, certificateGrids.gridItem_ID, inv.invoiceAmount, primaryGrid.commissionAmount \n" +
            "FROM CommissionInvoice inv \n" +
            "LEFT JOIN CommissionInvoiceItem mp ON mp.commissionInvoice_ID = inv.id \n" +
            "LEFT JOIN CommissionInvoiceItem cp ON cp.parent_ID = mp.id \n" +
            "LEFT JOIN CommissionInvoiceItemGridItem certificateGrids ON certificateGrids.invoiceItem_ID = cp.id \n" +
            "LEFT JOIN CommissionInvoiceGridItem primaryGrid ON primaryGrid.id = certificateGrids.gridItem_ID \n" +
            "WHERE mp.policyNumber IN ('%s') ORDER BY mp.id;";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28482", component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionEarnedVerificationForFullAdminWithOutSplit() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName1 = agency.createAgency(tdAgencyDefault);
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName1));
        String agencyCode1 = AgencyVendorSearchTab.tableAgencies.getRowContains("Agency Name", agencyName1).getCell("Agency Code").getValue();
        String agencyName2 = agency.createAgency(tdAgencyDefault);

        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName1);
        profileCorporate.create(tdNewProfile);

        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName1));
        String channelCommissionGroupName1 = channelCommissionGroup.get("Group Name");

        TestData tieredTestData = tdSpecific().getTestData("Tiered_CommissionRule")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName1).resolveLinks();

        createCommissionStrategy(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData)), false);
        gbCommissionStrategy.edit().start("GB_VS - Group Vision");
        GBCommissionStrategyTab.tableCommissionRules.findRow(COMMISSION_TYPE.getName(), "Tiered");
        GBCommissionStrategyTab.tableCommissionRules.getRowContains(COMMISSION_TYPE.getName(), "Tiered")
                .getCell(ACTIONS.getName()).controls.links.get(EDIT).click();

        DialogAssetList commissionRuleAssetList = gbCommissionRuleTab.getAssetList().getAsset(GBCommissionRuleMetaData.ADD_COMMISSION_RULE);
        TripleTextBox tier2 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_2);
        TripleTextBox tier3 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_3);
        TripleTextBox tier4 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_4);
        TripleTextBox tier5 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_5);

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        //values were changed after agreement with test case reporter
        tier2.getControlByIndex(1).setValue("80");
        tier2.getControlByIndex(2).setValue("25");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier3.getControlByIndex(1).setValue("120");
        tier3.getControlByIndex(2).setValue("20");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier4.getControlByIndex(1).setValue("160");
        tier4.getControlByIndex(2).setValue("15");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier5.getControlByIndex(2).setValue("10");
        Page.dialogConfirmation.confirm();
        GBCommissionStrategyTab.buttonSave.click();

        userCreateAndReLogin(agencyName1);

        LOGGER.info("Common general preconditions execution");
        TestData tdAgency1 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1);
        TestData tdAgency2 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName2);

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()), ImmutableList.of(tdAgency1, tdAgency2)));
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupVisionMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")));

        LOGGER.info("Steps#1, 2 execution");
        quoteInitiateAndFillUpToTab(getDefaultVSMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel() + "[0]", PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName1).resolveLinks())
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), MINIMUM_NUMBER_OF_PARTICIPANTS.getLabel()), "10"), PremiumSummaryTab.class, true);

        LOGGER.info("Step#3 verification");
        assertSoftly(softly -> {
            softly.assertThat(getCommissionTypeByAgency(agencyName1, VISION)).isEqualTo("Tiered");
            softly.assertThat(getCommissionTable(agencyName1, VISION).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 - 40").getCell(RATE.getName())).hasValue("30 %");
            softly.assertThat(getCommissionTable(agencyName1, VISION).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "40 - 80").getCell(RATE.getName())).hasValue("25 %");
            softly.assertThat(getCommissionTable(agencyName1, VISION).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "80 - 120").getCell(RATE.getName())).hasValue("20 %");
            softly.assertThat(getCommissionTable(agencyName1, VISION).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "120 - 160").getCell(RATE.getName())).hasValue("15 %");
            softly.assertThat(getCommissionTable(agencyName1, VISION).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "160 - ").getCell(RATE.getName())).hasValue("10 %");
        });

        LOGGER.info("Step#4 execution");
        GroupVisionMasterPolicyContext.premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();

        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(proposalActionTab.getMetaKey(), OVERRIDE_RULES_LIST_KEY),
                        ImmutableList.of("Proposal requires Underwriter Management approval as Total Commission exceeds...")));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData().adjust(tdSpecific().getTestData("TestDataIssue")));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#4.1 execution");
        for (int i = 1; i < 5; i++) {
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupVisionCertificatePolicy.createPolicy(groupVisionCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(GroupVisionCertificatePolicyContext.certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()),
                            TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                    .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        }

        LOGGER.info("Step#5 execution");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Step#6 verification");
        Currency policyTotalDueFirstBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);
        Currency amountPerOnePolicy = policyTotalDueFirstBill.divide(4);
        String commissionAmountForAll = new Currency(amountPerOnePolicy.multiply(0.3).multiply(4)).toPlainString();
        String commissionAmountForOne = new Currency(amountPerOnePolicy.multiply(0.3)).toPlainString().concat("000000");

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

            LOGGER.info("Step#7 verification");
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelCd")).isEqualTo(agencyCode1);
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelType")).isEqualTo("agency");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemType")).isEqualTo("COVERAGE");

            LOGGER.info("Step#8 verification");
            softly.assertThat(dbResponseListTransaction1.get(0).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListTransaction1.get(0).get("amount")).isEqualTo(commissionAmountForAll);

            LOGGER.info("Step#9 verification");
            softly.assertThat(dbResponseListGridItem1stBill.get(0).get("code")).isEqualTo("VISION");
            softly.assertThat(dbResponseListGridItem1stBill.get(0).get("producerCd")).isEqualTo(agencyCode1);
            softly.assertThat(dbResponseListGridItem1stBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForOne)).collect(Collectors.toList())).hasSize(4);

            LOGGER.info("Step#10 verification");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                    .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), policyTotalDueFirstBill.toString()));
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

            List<Map<String, String>> dbResponseListAfterPayment = RetryService.run(
                    predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 1,
                    () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            List<Map<String, String>> dbResponseCommissionInvoice = dbService.getRows(String.format(SQL_GET_COMMISSION_INVOICE_PAYMENT_ITEM, masterPolicyNumber));
            String commissionAmountAfterPayment = new Currency(amountPerOnePolicy.multiply(0.3).multiply(4)).toPlainString();

            LOGGER.info("Step#11 verification");
            softly.assertThat(dbResponseListAfterPayment.get(1).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListAfterPayment.get(1).get("amount")).isEqualTo(commissionAmountAfterPayment);

            LOGGER.info("Step#12 verification");
            softly.assertThat(dbResponseCommissionInvoice.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForOne)).collect(Collectors.toList())).hasSize(4);
            softly.assertThat(dbResponseCommissionInvoice.stream().filter(x -> x.get("invoiceAmount").equals(policyTotalDueFirstBill.toPlainString())).collect(Collectors.toList())).hasSize(4);
        });

        LOGGER.info("Step#13 execution");
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDueSecondBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);

        Currency policiesAmountUnder40 = new Currency(new Currency(40).subtract(policyTotalDueFirstBill), RoundingMode.HALF_DOWN);
        Currency policiesAmountOver40 = new Currency(amountPerOnePolicy.subtract(policiesAmountUnder40).abs(), RoundingMode.HALF_DOWN);
        String amountsForAllPoliciesForSecondBill = new Currency(policiesAmountOver40.multiply(0.25).add(policiesAmountUnder40.multiply(0.3)).add(amountPerOnePolicy.multiply(0.25).multiply(3)), RoundingMode.HALF_DOWN).toPlainString();

        String commissionAmountPolicyOver40 = new Currency(policiesAmountOver40.multiply(0.25)).toPlainString().concat("000000");
        String commissionAmountOtherPolicies = new Currency(amountPerOnePolicy.multiply(0.25)).toPlainString().concat("000000");
        String commissionAmountPolicyUnder40 = new Currency(policiesAmountUnder40.multiply(0.3)).toPlainString().concat("000000");

        List<Map<String, String>> dbResponseListTransaction2 = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 2,
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListGridItem2ndBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        assertSoftly(softly -> {

            softly.assertThat(dbResponseListTransaction2.get(2).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListTransaction2.get(2).get("amount")).isEqualTo(amountsForAllPoliciesForSecondBill);

            LOGGER.info("Step#14 verification");
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountOtherPolicies)).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder40)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#15 verification");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                    .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), policyTotalDueFirstBill.toString()));
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

            List<Map<String, String>> dbResponseListAfterSecondPayment = RetryService.run(
                    predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 3,
                    () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            List<Map<String, String>> dbResponseCommissionInvoiceSecond = dbService.getRows(String.format(SQL_GET_COMMISSION_INVOICE_PAYMENT_ITEM, masterPolicyNumber));

            LOGGER.info("Step#16 verification");
            softly.assertThat(dbResponseListAfterSecondPayment.get(3).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListAfterSecondPayment.get(3).get("amount")).isEqualTo(amountsForAllPoliciesForSecondBill);

            LOGGER.info("Step#17 verification");
            softly.assertThat(dbResponseCommissionInvoiceSecond.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver40)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseCommissionInvoiceSecond.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountOtherPolicies)).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseCommissionInvoiceSecond.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyUnder40)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseCommissionInvoiceSecond.stream().filter(x -> x.get("invoiceAmount").equals(policyTotalDueFirstBill.toPlainString())).collect(Collectors.toList())).hasSize(9);
        });

        LOGGER.info("Step#18 execution");
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Step#19 verification");
        Currency policiesAmountUnder80 = new Currency(new Currency(80).subtract(policyTotalDueSecondBill.add(policyTotalDueFirstBill)), RoundingMode.HALF_DOWN);
        Currency policyAmountOver80 = new Currency(policyTotalDueSecondBill.multiply(3).subtract(new Currency(80)), RoundingMode.HALF_DOWN);
        Currency policyAmountOver80Reminder = new Currency(policyAmountOver80.subtract(amountPerOnePolicy.multiply(2)), RoundingMode.HALF_DOWN);

        String onePolicyForValueOver80 = new Currency(amountPerOnePolicy.multiply(0.2)).toPlainString();
        String amountsForAllPoliciesForThirdBill = new Currency(onePolicyForValueOver80).multiply(2).add(policiesAmountUnder80.multiply(0.25)).add(policyAmountOver80Reminder.multiply(0.2)).toPlainString();

        String commissionAmountPolicyOver80 = new Currency(policyAmountOver80Reminder.multiply(0.2)).toPlainString().concat("000000");
        String commissionAmountOtherPoliciesFor80 = new Currency(onePolicyForValueOver80).toPlainString().concat("000000");

        List<Map<String, String>> dbResponseListTransaction3 = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 4,
                () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListGridItem3rdBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        assertSoftly(softly -> {

            softly.assertThat(dbResponseListTransaction3.get(4).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListTransaction3.get(4).get("amount")).isEqualTo(amountsForAllPoliciesForThirdBill);

            LOGGER.info("Step#20 verification");
            softly.assertThat(dbResponseListGridItem3rdBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountOtherPoliciesFor80)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListGridItem3rdBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver80)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#21 verification");
            billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                    .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), policyTotalDueFirstBill.toString()));
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

            List<Map<String, String>> dbResponseListAfterThirdPayment = RetryService.run(
                    predicate -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)).size() > 5,
                    () -> dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber)),
                    StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            List<Map<String, String>> dbResponseCommissionInvoiceThird = dbService.getRows(String.format(SQL_GET_COMMISSION_INVOICE_PAYMENT_ITEM, masterPolicyNumber));

            LOGGER.info("Step#22 verification");
            softly.assertThat(dbResponseListAfterThirdPayment.get(5).get("commissionItemCode")).isEqualTo("VISION");
            softly.assertThat(dbResponseListAfterThirdPayment.get(5).get("amount")).isEqualTo(amountsForAllPoliciesForThirdBill);

            LOGGER.info("Step#23 verification");
            softly.assertThat(dbResponseCommissionInvoiceThird.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountOtherPoliciesFor80)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseCommissionInvoiceThird.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicyOver80)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseCommissionInvoiceThird.stream().filter(x -> x.get("invoiceAmount").equals(policyTotalDueFirstBill.toPlainString())).collect(Collectors.toList())).hasSize(14);
        });
    }

    private void userCreateAndReLogin(String agencyName) {
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName);

        createUserAndRelogin(profileCorporate, tdNewProfile
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(),
                        AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 6"));
    }
}
