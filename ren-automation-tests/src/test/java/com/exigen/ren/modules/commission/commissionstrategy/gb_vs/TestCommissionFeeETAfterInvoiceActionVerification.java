package com.exigen.ren.modules.commission.commissionstrategy.gb_vs;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData;
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
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionOverrideOptions.COMMISSION_TYPE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.tableCommissionOverrideOptions;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage.tableCommissionStrategies;
import static com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData.COMMISSION_STRATEGY_STATUS;
import static com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData.PRODUCT_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionFeeETAfterInvoiceActionVerification extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    private String SQL_GET_COMMISSIONABLE_PRODUCT = "select * from BenefitsCommissionableProduct where policyNumber in ('%s');";
    private String SQL_GET_GRIDTERM = "SELECT inv.invoiceAmount, primaryGrid.rate, primaryGrid.commissionAmount,  primaryGrid.code,primaryGrid.denomination, inv.invoicePeriodStartDate, * FROM CommissionInvoice inv \n" +
            "LEFT JOIN CommissionInvoiceItem mp ON mp.commissionInvoice_ID = inv.id \n" +
            "LEFT JOIN CommissionInvoiceItem cp ON cp.parent_ID = mp.id \n" +
            "LEFT JOIN CommissionInvoiceItemGridItem certificateGrids ON certificateGrids.invoiceItem_ID = cp.id \n" +
            "LEFT JOIN CommissionInvoiceGridItem primaryGrid ON primaryGrid.id = certificateGrids.gridItem_ID \n" +
            "WHERE mp.policyNumber IN ('%s') ORDER BY mp.id;";
    private String SQL_GET_EARNED_TRANSACTION = "select bcp.policyNumber, bcp.commissionItemCode, bct.* " +
            "from BenefitsCommissionTransaction bct join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id where bcp.policyNumber in ('%s') ORDER by bct.id;";
    private String SQL_GET_COMMISSION_INVOICE = "SELECT mp.policyNumber, mp.producerCd, certificateGrids.gridItem_ID, inv.invoiceAmount, primaryGrid.commissionAmount \n" +
            "FROM CommissionInvoice inv \n" +
            "LEFT JOIN CommissionInvoiceItem mp ON mp.commissionInvoice_ID = inv.id \n" +
            "LEFT JOIN CommissionInvoiceItem cp ON cp.parent_ID = mp.id \n" +
            "LEFT JOIN CommissionInvoiceItemGridItem certificateGrids ON certificateGrids.invoiceItem_ID = cp.id \n" +
            "LEFT JOIN CommissionInvoiceGridItem primaryGrid ON primaryGrid.id = certificateGrids.gridItem_ID \n" +
            "WHERE mp.policyNumber IN ('%s') ORDER BY mp.id;";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-31129", component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionFeeETAfterInvoiceActionVerification_VS() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName = agency.createAgency(tdAgencyDefault);
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName);
        profileCorporate.create(tdNewProfile);

        ImmutableMap<String, String> channelCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), CommissionGroupMetaData.AddAgencies.AGENCY_NAME.getLabel()), agencyName));
        String channelCommissionGroupName = channelCommissionGroup.get("Group Name");

        gbCommissionStrategy.search(gbCommissionStrategyDefaultTestData.getTestData("SearchData", TestDataKey.DEFAULT_TEST_DATA_KEY));
        TestData subscriberCountTestData = tdSpecific().getTestData("CommissionRuleSubscriberCount")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName).resolveLinks();

        gbCommissionStrategy.searched(gbCommissionStrategy.getDefaultTestData().getTestData("SearchData", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(gbCommissionSearchTab.getMetaKey(), COMMISSION_STRATEGY_STATUS.getLabel()), "")
                .adjust(TestData.makeKeyPath(gbCommissionSearchTab.getMetaKey(), PRODUCT_NAME.getLabel()), "Group Vision"));

        if (tableCommissionStrategies.getRowContains(CommissionPage.CommissionStrategies.EFFECTIVE_DATE.getName(), "09/01/2019")
                .getCell(CommissionPage.CommissionStrategies.STATUS.getName()).getValue().equals("Expired")) {

            tableCommissionStrategies.getRowContains(CommissionPage.CommissionStrategies.STATUS.getName(), "Active")
                    .getCell(CommissionPage.CommissionStrategies.ACTION.getName()).controls.links.getFirst().click();
            gbCommissionStrategyTab.getAssetList().getAsset(GBCommissionStrategyMetaData.EXPIRATION_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
            gbCommissionStrategyTab.submitTab();

            tableCommissionStrategies.getRowContains(CommissionPage.CommissionStrategies.EFFECTIVE_DATE.getName(), "09/01/2019")
                    .getCell(CommissionPage.CommissionStrategies.ACTION.getName()).controls.links.getFirst().click();
            gbCommissionStrategyTab.getAssetList().getAsset(GBCommissionStrategyMetaData.EXPIRATION_DATE).setValue(EMPTY);
            gbCommissionStrategyTab.submitTab();
        }

        gbCommissionStrategy.edit().perform(gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "TestDataRuleUpdateRate")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(subscriberCountTestData)), "GB_VS - Group Vision");

        gbCommissionStrategy.edit().start("GB_VS - Group Vision");
        if (tableCommissionOverrideOptions.isPresent()) {
            List<String> commissionTypeList = tableCommissionOverrideOptions.getColumn(COMMISSION_TYPE.getName()).getValue();
            if (!commissionTypeList.contains("Subscriber Count - Flat")) {
                gbCommissionStrategyTab.fillTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
            }
            gbCommissionStrategyTab.submitTab();
        }

        LOGGER.info("Common general preconditions execution");
        mainApp().reopen();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                                .adjust(TestData.makeKeyPath(AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName))));

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), groupVisionMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 execution");
        TestData tdPolicyAgency = tdSpecific().getTestData("Agency1")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName).resolveLinks();

        quoteInitiateAndFillUpToTab(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataASO")
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel()),
                                ImmutableList.of(tdPolicyAgency)).resolveLinks()), PremiumSummaryTab.class, true);
        assertThat(PremiumSummaryTab.getCommissionName(1)).isEqualTo(String.format("%s - Agency", agencyName));

        LOGGER.info("Steps#3 verification");
        Currency overrideAmount = new Currency(5);
        PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, "ASO Vision").click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValueContains("SubscriberCount");
        DialogOverrideCommissionPremiumSummary.overrideCommissionForAllCoverages((overrideAmount.toString()));
        PremiumSummaryTab.dialogOverrideCommission.confirm();

        LOGGER.info("Steps#4 verification");
        GroupVisionMasterPolicyContext.premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        TestData tdPropose = groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY);
        ProposalActionTab.performProposalForPolicyWithASO(GroupBenefitsMasterPolicyType.GB_VS, tdPropose, ImmutableList.of(
                "Proposal for ASO Plan requires Underwriter approval",
                "Proposal will require Underwriter approval as Commission Fee exceeds $3 per s..."));

        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupVisionMasterPolicy.issue().perform(tdSpecific().getTestData("TestDataIssue"));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#5 execution");
        for (int i = 1; i < 5; i++) {
            MainPage.QuickSearch.search(policyNumber);
            groupVisionCertificatePolicy.createPolicy(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PlansTabMetaData.PLAN_NAME.getLabel()), "ASO A La Carte")
                    .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PlansTabMetaData.COVERAGE_TIER.getLabel()), "Composite tier")
                    .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        }

        LOGGER.info("Step#6 execution");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());

        LOGGER.info("Step#7 verification");
        List<Map<String, String>> dbResponseListProductInfo = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_COMMISSIONABLE_PRODUCT, policyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelCd")).contains("Subproducer");
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelType")).isEqualTo("individual");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemCode")).isEqualTo("ASO_VISION");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemType")).isEqualTo("COVERAGE");
        });

        LOGGER.info("Step#8 verification");
        List<Map<String, String>> dbResponseListGridTerm = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_GRIDTERM, policyNumber)).size() > 2,
                () -> dbService.getRows(String.format(SQL_GET_GRIDTERM, policyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(dbResponseListGridTerm.stream().filter(x -> x.get("code").equals("ASO_VISION") && x.get("commissionAmount")
                .equals(overrideAmount.toPlainString().concat("000000"))).collect(Collectors.toList())).hasSize(4);

        LOGGER.info("Step#9 verification");
        List<Map<String, String>> dbResponseListPayableTransaction = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        assertThat(dbResponseListPayableTransaction.get(0).get("amount")).isEqualTo(overrideAmount.multiply(4).toPlainString());

        LOGGER.info("Step#10 verification");
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), new Currency(policyTotalDue).toString()));
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

        LOGGER.info("Step#11 verification");
        List<Map<String, String>> dbResponseListPayableTransactionForPayment = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber)).size() > 1,
                () -> dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(dbResponseListPayableTransactionForPayment.get(1).get("amount"))
                .isEqualTo(overrideAmount.multiply(4).toPlainString());

        LOGGER.info("Step#12 verification");
        List<Map<String, String>> dbResponseListGridTermFirstPayment = RetryService.run(
                predicate -> dbService.getRows(String.format(SQL_GET_COMMISSION_INVOICE, policyNumber)).size() > 2,
                () -> dbService.getRows(String.format(SQL_GET_COMMISSION_INVOICE, policyNumber)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(dbResponseListGridTermFirstPayment.stream().filter(x -> x.get("commissionAmount")
                .equals(overrideAmount.toPlainString().concat("000000")) && x.get("invoiceAmount").equals(policyTotalDue.toPlainString())).collect(Collectors.toList())).hasSize(4);
    }
}
