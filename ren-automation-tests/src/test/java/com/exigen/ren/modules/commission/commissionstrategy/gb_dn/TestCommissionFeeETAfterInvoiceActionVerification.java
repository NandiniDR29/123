package com.exigen.ren.modules.commission.commissionstrategy.gb_dn;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
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
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
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
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionFeeETAfterInvoiceActionVerification extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext {

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
    @TestInfo(testCaseId = "REN-31126", component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionFeeETAfterInvoiceActionVerification_DN() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName = agency.createAgency(tdAgencyDefault);
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        String agencyCode = AgencyVendorSearchTab.tableAgencies.getRowContains("Agency Name", agencyName).getCell("Agency Code").getValue();

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

        gbCommissionStrategy.edit().perform(gbCommissionStrategy.getDefaultTestData().getTestData("UpdateData", "TestDataRuleUpdateRate")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(subscriberCountTestData)), "GB_DN - Group Dental");

        LOGGER.info("Common general preconditions execution");
        mainApp().reopen();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                                .adjust(TestData.makeKeyPath(AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName))));

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), groupDentalMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 execution");
        TestData tdPolicyAgency = tdSpecific().getTestData("Agency1")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName).resolveLinks();

        initiateQuoteAndFillUpToTab(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataASO")
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel()),
                                ImmutableList.of(tdPolicyAgency)).resolveLinks()), PremiumSummaryTab.class, true);
        assertThat(PremiumSummaryTab.getCommissionName(1)).isEqualTo(String.format("%s - Agency", agencyName));

        LOGGER.info("Steps#3 verification");
        GroupDentalMasterPolicyContext.premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        TestData tdPropose = groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY);
        ProposalActionTab.performProposalForPolicyWithASO(GroupBenefitsMasterPolicyType.GB_DN, tdPropose, ImmutableList.of(
                "Proposal for ASO Plan will require Underwriter approval",
                "Proposal requires Underwriter approval because Major Waiting Period is less t...",
                "Proposal requires Underwriter approval because Prosthodontics Waiting Period ..."));

        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Step#4 execution");
        for (int i = 1; i < 5; i++) {
            MainPage.QuickSearch.search(policyNumber);
            groupDentalCertificatePolicy.createPolicy(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                    .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        }

        LOGGER.info("Step#5 execution");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());
        String onePolicyAmount = (policyTotalDue.subtract(40)).divide(4).toString().replace("$", "");

        LOGGER.info("Step#6 verification");
        Waiters.SLEEP(5000).go();
        List<Map<String, String>> dbResponseListProductInfo = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_COMMISSIONABLE_PRODUCT, policyNumber)),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelCd")).isEqualTo(agencyCode);
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelType")).isEqualTo("agency");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemCode")).isEqualTo("ASO_DENTAL");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemType")).isEqualTo("COVERAGE");
        });

        LOGGER.info("Step#7 verification");
        List<Map<String, String>> dbResponseListGridTerm = dbService.getRows(String.format(SQL_GET_GRIDTERM, policyNumber));
        assertThat(dbResponseListGridTerm.stream().filter(x -> x.get("code").equals("ASO_DENTAL") && x.get("commissionAmount").equals("0E-8")).collect(Collectors.toList())).hasSize(4);

        LOGGER.info("Step#8 verification");
        List<Map<String, String>> dbResponseListPayableTransaction = dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber));
        assertThat(dbResponseListPayableTransaction.get(0).get("amount")).isEqualTo("0.00");

        LOGGER.info("Step#9 verification");
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), new Currency(policyTotalDue).toString()));
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

        LOGGER.info("Step#10 verification");
        Waiters.SLEEP(5000).go();
        List<Map<String, String>> dbResponseListPayableTransactionForPayment = dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber));
        assertThat(dbResponseListPayableTransactionForPayment.get(0).get("amount")).isEqualTo("0.00");

        LOGGER.info("Step#11 verification");
        List<Map<String, String>> dbResponseListGridTermFirstPayment = dbService.getRows(String.format(SQL_GET_COMMISSION_INVOICE, policyNumber));
        assertThat(dbResponseListGridTermFirstPayment.stream().filter(x -> x.get("commissionAmount").equals("0E-8")
                && x.get("invoiceAmount").equals(policyTotalDue.toString().replace("$", ""))).collect(Collectors.toList())).hasSize(8);

        LOGGER.info("Step#12 verification");
        billingAccount.discardBill().perform(new SimpleDataProvider());
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.DISCARDED);

        LOGGER.info("Step#13 verification");
        Waiters.SLEEP(5000).go();
        List<Map<String, String>> dbResponseListPayableTransactionDiscardedPayment = dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber));
        assertThat(dbResponseListPayableTransactionDiscardedPayment.stream().filter(x -> x.get("commissionItemCode").equals("ASO_DENTAL")
                && x.get("amount").equals("0.00")).collect(Collectors.toList())).hasSize(4);

        LOGGER.info("Step#14 verification");
        List<Map<String, String>> dbResponseListGridTermDiscardedPayment = dbService.getRows(String.format(SQL_GET_GRIDTERM, policyNumber));
        assertThat(dbResponseListGridTermDiscardedPayment.stream().filter(x -> x.get("invoiceStatus").equals("discarded")
                && x.get("code").equals("ASO_DENTAL") && x.get("invoicedPremiumAmount").equals(onePolicyAmount)).collect(Collectors.toList())).hasSize(4);

        LOGGER.info("Step#15 verification");
        billingAccount.regenerateBill().perform(new SimpleDataProvider(), 1);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

        LOGGER.info("Step#16 verification");
        Waiters.SLEEP(5000).go();
        List<Map<String, String>> dbResponseListGridTermReGeneratedPayment = dbService.getRows(String.format(SQL_GET_GRIDTERM, policyNumber));
        assertThat(dbResponseListGridTermReGeneratedPayment.stream().filter(x -> x.get("code").equals("ASO_DENTAL")
                && x.get("invoiceStatus").equals("active") && x.get("commissionAmount").equals("0E-8")).collect(Collectors.toList())).hasSize(4);

        LOGGER.info("Step#17 verification");
        List<Map<String, String>> dbResponseListPayableTransactionReGeneratedPayment = dbService.getRows(String.format(SQL_GET_EARNED_TRANSACTION, policyNumber));
        assertThat(dbResponseListPayableTransactionReGeneratedPayment.stream().filter(x -> x.get("commissionItemCode").equals("ASO_DENTAL")
                && x.get("amount").equals("0.00")).collect(Collectors.toList())).hasSize(6);

        LOGGER.info("Step#18 verification");
        List<Map<String, String>> dbResponseListGridTermPrimaryGrid = dbService.getRows(String.format(SQL_GET_GRIDTERM, policyNumber));
        assertThat(dbResponseListGridTermPrimaryGrid.stream().filter(x -> x.get("invoiceStatus").equals("active")
                && x.get("invoiceAmount").equals(policyTotalDue.toString().replace("$", ""))).collect(Collectors.toList())).hasSize(8);
    }
}
