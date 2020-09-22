package com.exigen.ren.modules.commission.commissionstrategy.gb_vs;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
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
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.CertificatePolicyTabMetaData;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.exigen.istf.exec.commons.util.DateUtils.getCurrentDateTime;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.tabs.ApplyManualCommissionCorrectionTab.performCommissionCorrectionForSubproducer;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.DIRECT;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.ADD_COMMISSION_RULE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SELECT_SALES_INDIVIDUAL;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData.AddCommissionRule.SelectSalesIndividual.COMMISSION_INDIVIDUAL_GROUP_NAME;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.*;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.EfolderManagmentConstants.EfolderManagmenEntityes.AGENCY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionCheckNumberGenerationVerification extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    private static final String SQL_GET_BCB = "select \n" +
            "bcp.salesChannelCd,\n" +
            "bct.DTYPE,\n" +
            "bct.amount,\n" +
            "bct.type,\n" +
            "bcb.premiumPayable, \n" +
            "bcb.premiumEarned, \n" +
            "bcb.premiumPaid \n" +
            "from BenefitsCommissionBalance bcb \n" +
            "left join BenefitsCommissionTransaction bct " +
            "on bcb.id = bct.delta_id " +
            "left join BenefitsCommissionableProduct bcp " +
            "on bct.commissionableProduct_id=bcp.id\n" +
            "where bcp.policyNumber in ('%s');";
    private static final String SQL_GET_BENEFIT_COMMISSION_TRANSACTION = "select bct.id, bcp.policyNumber, bcp.commissionItemCode, bct.DTYPE, \n" +
            "bct.type, bct.amount, bcp.salesChannelType, bcp.salesChannelCd\n" +
            "from BenefitsCommissionTransaction bct \n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id \n" +
            "where bcp.policyNumber in ('%s') ORDER by bct.id;";
    private static final String SQL_GET_BENEFITS_COLLECTED_BALANCE = "select * from BenefitsCollectedBalance cb where cb.policyNumber in ('%s')";
    private static final String SQL_UPDATE_BCT = "update BenefitsCommissionTransaction \n" +
            "SET createTime = CAST('%s' AS DATETIME),\n" +
            "effectiveDate = CAST('%s' AS DATETIME),\n" +
            "paymentStatus = 'active'\n" +
            "where DTYPE = 'BenefitsCommissionEarnedTX' \n" +
            "AND \n" +
            "id in ('%s');";
    private static final String SQL_GET_LEDGER_TRANSACTION = "SELECT distinct\n" +
            "p.id,\n" +
            "pd.DTYPE,\n" +
            "p.referenceId,\n" +
            "pd.chequeNumber,\n" +
            "LT.txType,\n" +
            "LE.productNumber,\n" +
            "LT.entityRefNo,\n" +
            "LT.entityRef_fk,\n" +
            "LT.entityType,\n" +
            "LE.entryAmt,\n" +
            "LE.entryType,\n" +
            "LE.ledgerAccountNo\n" +
            "FROM LedgerTransaction LT\n" +
            "join LedgerEntry LE on LE.LedgerTransaction_id = LT.id\n" +
            "join LedgerAccount LA on LA.accountNo=LE.ledgerAccountNo\n" +
            "join Payment p on p.id = LT.entityRef_fk\n" +
            "join PaymentDetails pd ON pd.id = p.paymentDetails_id\n" +
            "join CommissionDisbursement cd on p.id = cd.Payment_id\n" +
            "join BenefitsCommissionTransaction ct on ct.commissionDisbursement_id = cd.id\n" +
            "join BenefitsCommissionableProduct cp on cp.id=ct.commissionableProduct_id\n" +
            "WHERE LT.txType = 'COMMISSION_DISBURSED'\n" +
            "and pd.DTYPE = 'PaymentDetailsCheque'\n" +
            "and cp.policyNumber in('%s')\n" +
            "and LE.DTYPE='CommissionLedgerEntry';";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40308", component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionCheckNumberGenerationVerificationVS() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName1 = agency.createAgency(agency.defaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.CHANNEL.getLabel()), "Agency")
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.APPLICABLE_FOR_COMMISSIONS_EXTRACT.getLabel()), VALUE_NO));

        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), COMMISSIONABLE.getLabel()), "true")
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName1)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), AGENCY);
        String userLastName = tdNewProfile.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.LAST_NAME.getLabel());
        profileCorporate.create(tdNewProfile);

        ImmutableMap<String, String> individualCommissionGroup = commissionGroup.createGroup(commissionGroup.getDefaultTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Individual")
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), DIRECT.getLabel(), CommissionGroupMetaData.AddDirect.LAST_NAME.getLabel()), userLastName));
        String indCommissionGroupName = individualCommissionGroup.get("Group Name");

        TestData subsCountTestData = tdSpecific().getTestData("CommissionRuleSubscriberCount")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_INDIVIDUAL.getLabel(), COMMISSION_INDIVIDUAL_GROUP_NAME.getLabel()), indCommissionGroupName).resolveLinks();

        createCommissionStrategy(tdSpecific().getTestData("TestDataVision")
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(subsCountTestData)), false);

        LOGGER.info("Common general preconditions execution");
        mainApp().reopen();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                                .adjust(TestData.makeKeyPath(AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1))));

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupVisionMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")));

        TestData tdPolicyAgency = tdSpecific().getTestData("Agency1")
                .adjust(TestData.makeKeyPath(PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER.getLabel()), agencyName1).resolveLinks();

        quoteInitiateAndFillUpToTab(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataASO")
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel()),
                                ImmutableList.of(tdPolicyAgency)).resolveLinks()), PremiumSummaryTab.class, true);

        PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName1, "ASO Vision").click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValueContains("NameSubsCount");
        DialogOverrideCommissionPremiumSummary.overrideCommissionForAllCoverages((new Currency(7).toString()));
        PremiumSummaryTab.dialogOverrideCommission.confirm();

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

        groupVisionCertificatePolicy.createPolicy(getDefaultVSCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PlansTabMetaData.PLAN_NAME.getLabel()), "ASO A La Carte")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PlansTabMetaData.COVERAGE_TIER.getLabel()), "Composite tier")
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());

        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());

        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), new Currency(policyTotalDue).toString()));
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(TableConstants.BillingBillsAndStatementsGB.STATUS).getCell(1)).hasValue(BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);

        adminApp().reopen();
        performCommissionCorrectionForSubproducer(policyNumber, "ASO Vision", "20", "200");

        LOGGER.info("Step#1 verification");
        List<Map<String, String>> dbResponseCommTrans = dbService.getRows(String.format(SQL_GET_BENEFIT_COMMISSION_TRANSACTION, policyNumber));
        String transactionIdPayment1 = dbResponseCommTrans.get(1).get("id");

        List<Map<String, String>> dbResponseTransactions = dbService.getRows(String.format(SQL_GET_BCB, policyNumber));
        String deltaCommission = dbResponseTransactions.get(1).get("amount").concat("000000");
        String deltaPremium = dbResponseTransactions.get(1).get("premiumEarned");
        assertSoftly(softly -> {

            softly.assertThat(dbResponseTransactions.stream().filter(x -> x.get("type").equals("commission")).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseTransactions.stream().filter(x -> x.get("type").equals("correction")).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#2 verification");
            List<Map<String, String>> dbResponseCollectedBalance = dbService.getRows(String.format(SQL_GET_BENEFITS_COLLECTED_BALANCE, policyNumber));

            softly.assertThat(dbResponseCollectedBalance.stream().filter(x -> x.get("deltaPremium").equals(deltaPremium)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseCollectedBalance.stream().filter(x -> x.get("deltaPremium").equals("200.00")).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseCollectedBalance.stream().filter(x -> x.get("deltaCommission").equals(deltaCommission)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseCollectedBalance.stream().filter(x -> x.get("deltaCommission").equals("20.00000000")).collect(Collectors.toList())).hasSize(1);
        });

        LOGGER.info("Step#3 execution");
        LocalDateTime minus1day = getCurrentDateTime().minusDays(1);
        dbService.executeUpdate(String.format(SQL_UPDATE_BCT, minus1day, minus1day, transactionIdPayment1));

        LOGGER.info("Step#4 execution");
        String jobName = String.format("%s%s", GeneralSchedulerPage.BENEFITS_COMMISSION_ASYNC_DISBURSEMENT_JOB.getGroupName(), RandomStringUtils.randomNumeric(5));
        JobRunner.executeJob(new JobGroup(jobName, new Job(GeneralSchedulerPage.BENEFITS_COMMISSION_ASYNC_DISBURSEMENT_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "-t daily"))));

        LOGGER.info("Step#5 verification");
        List<Map<String, String>> dbResponseLedger = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_LEDGER_TRANSACTION, policyNumber)),
                StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        int referenceId1 = Integer.parseInt(dbResponseLedger.get(0).get("referenceId"));
        int referenceId2 = Integer.parseInt(dbResponseLedger.get(1).get("referenceId"));

        assertThat(referenceId1 <= 5999999 && referenceId1 >= 5000001).isTrue();
        assertThat(referenceId2 <= 5999999 && referenceId2 >= 5000001).isTrue();
    }
}
