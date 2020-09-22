package com.exigen.ren.modules.commission.commissionstrategy.gb_dn;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.common.controls.TripleTextBox;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
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
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.DENTAL;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.PREMIUM_RECEIVED_PER_P_YEAR;
import static com.exigen.ren.main.enums.TableConstants.AgencyCommission.RATE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.getCommissionTable;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.getCommissionTypeByAgency;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCommissionPayableProcessingVerification extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext {

    private static String SQL_GET_PRODUCT_INFO = "select * from BenefitsCommissionableProduct where policyNumber in ('%s'); ";
    private static String SQL_GET_TRANSACTION = "select bcp.policyNumber, bcp.commissionItemCode, bct.* from BenefitsCommissionTransaction bct\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id\n" +
            "where bcp.policyNumber in ('%s') ORDER by bct.id;";
    private static String SQL_GET_GRID_ITEM = "select * from CommissionInvoiceGridItem cigi\n" +
            "join CommissionSplitParticipant csp on csp.id=cigi.participant_id\n" +
            "join BenefitsCommissionTransaction bct on bct.id = cigi.benefCommissionPayableTXId\n" +
            "join BenefitsCommissionableProduct bcp on bct.commissionableProduct_id=bcp.id where bcp.policyNumber in ('%s')";

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-26923", component = COMMISIONS_GROUPBENEFITS)
    public void testCommissionPayableProcessingVerification() {
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

        TestData tieredTestData = tdSpecific().getTestData("Tiered_CommissionRule")
                .adjust(TestData.makeKeyPath(ADD_COMMISSION_RULE.getLabel(), SELECT_SALES_CHANNEL.getLabel(), COMMISSION_CHANNEL_GROUP_NAME.getLabel()), channelCommissionGroupName).resolveLinks();

        createCommissionStrategy(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(gbCommissionRuleTab.getMetaKey(), ImmutableList.of(tieredTestData)), false);

        gbCommissionStrategy.edit().start("GB_DN - Group Dental");
        GBCommissionStrategyTab.tableCommissionRules.findRow(COMMISSION_TYPE.getName(), "Tiered");
        GBCommissionStrategyTab.tableCommissionRules.getRowContains(COMMISSION_TYPE.getName(), "Tiered").getCell(ACTIONS.getName()).controls.links.get(EDIT).click();

        DialogAssetList commissionRuleAssetList = gbCommissionRuleTab.getAssetList().getAsset(GBCommissionRuleMetaData.ADD_COMMISSION_RULE);
        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();

        TripleTextBox tier2 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_2);
        TripleTextBox tier3 = commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.TIER_3);
        tier2.getControlByIndex(1).setValue("450");
        tier2.getControlByIndex(2).setValue("9");

        commissionRuleAssetList.getAsset(GBCommissionRuleMetaData.AddCommissionRule.ADD_TIER).click();
        tier3.getControlByIndex(2).setValue("5");
        Page.dialogConfirmation.confirm();
        GBCommissionStrategyTab.buttonSave.click();

        LOGGER.info("Common general preconditions execution");
        mainApp().reopen();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                                .adjust(TestData.makeKeyPath(AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName))));

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), groupDentalMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 execution");
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMasterPolicy")).resolveLinks(), PremiumSummaryTab.class, true);

        LOGGER.info("Step#3 verification");
        assertSoftly(softly -> {
            softly.assertThat(getCommissionTypeByAgency(agencyName, DENTAL)).isEqualTo("Tiered");
            softly.assertThat(getCommissionTable(agencyName, DENTAL).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "0 - 250").getCell(RATE.getName())).hasValue("15 %");
            softly.assertThat(getCommissionTable(agencyName, DENTAL).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "250 - 450").getCell(RATE.getName())).hasValue("9 %");
            softly.assertThat(getCommissionTable(agencyName, DENTAL).getRowContains(PREMIUM_RECEIVED_PER_P_YEAR.getName(), "450 - ").getCell(RATE.getName())).hasValue("5 %");
        });

        LOGGER.info("Steps#4, 4.1 execution");
        GroupDentalMasterPolicyContext.premiumSummaryTab.submitTab();
        proposeAcceptContractIssueDNMasterPolicyWithDefaultTestData();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        for (int i = 1; i < 5; i++) {
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupDentalCertificatePolicy.createPolicy(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY))
                    .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        }

        LOGGER.info("Step#5 execution");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Step#6 verification");
        Currency policyTotalDueFirstBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);
        Currency amountPerOnePolicy = policyTotalDueFirstBill.divide(4);

        String commissionAmountForAll = amountPerOnePolicy.multiply(0.15).toPlainString().concat("000000");
        String amountVerification = amountPerOnePolicy.multiply(0.15).multiply(4).toPlainString();

        List<Map<String, String>> dbResponseListProductInfo = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_PRODUCT_INFO, masterPolicyNumber)),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        List<Map<String, String>> dbResponseListGridItem1stBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        List<Map<String, String>> dbResponseListTransaction1 = dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber));
        assertSoftly(softly -> {

            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelCd")).isEqualTo(agencyCode);
            softly.assertThat(dbResponseListProductInfo.get(0).get("salesChannelType")).isEqualTo("agency");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemCode")).isEqualTo("DENTAL");
            softly.assertThat(dbResponseListProductInfo.get(0).get("commissionItemType")).isEqualTo("COVERAGE");

            LOGGER.info("Step#7 verification");
            softly.assertThat(dbResponseListGridItem1stBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountForAll)).collect(Collectors.toList())).hasSize(4);

            LOGGER.info("Step#8 verification");
            softly.assertThat(dbResponseListTransaction1.get(0).get("commissionItemCode")).isEqualTo("DENTAL");
            softly.assertThat(dbResponseListTransaction1.get(0).get("amount")).isEqualTo(amountVerification);
        });

        LOGGER.info("Step#9 verification");
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDueSecondBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_DOWN);

        LOGGER.info("Step#10 verification");
        Waiters.SLEEP(5000).go();
        List<Map<String, String>> dbResponseListGridItem2ndBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        List<Map<String, String>> dbResponseListTransaction2 = dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber));

        Currency policiesAmountUnder250 = new Currency(250).subtract(policyTotalDueFirstBill);
        Currency policyAmountReminderUnder250 = amountPerOnePolicy.subtract(policiesAmountUnder250).abs();
        Currency policyAmountReminderOver250 = amountPerOnePolicy.subtract(policyAmountReminderUnder250);
        String commissionAmountBill2 = new Currency(dbResponseListGridItem2ndBill.get(0).get("amount")).divide(4).toPlainString();

        String commissionAmountPolicy1Under250 = commissionAmountBill2.concat("000000");
        String commissionAmountPolicy2Under250 = policyAmountReminderUnder250.multiply(0.15).toPlainString().concat("000000");
        String commissionAmountOtherPolicies = amountPerOnePolicy.multiply(0.09).toPlainString().concat("000000");
        String commissionAmountPolicy2Over250 = policyAmountReminderOver250.multiply(0.09).toPlainString().concat("000000");
        assertSoftly(softly -> {

            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountOtherPolicies)).collect(Collectors.toList())).hasSize(2);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicy2Over250)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicy1Under250)).collect(Collectors.toList())).hasSize(5);
            softly.assertThat(dbResponseListGridItem2ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicy2Under250)).collect(Collectors.toList())).hasSize(1);

            LOGGER.info("Step#11 verification");
            Currency over250Commission = new Currency(policyTotalDueSecondBill.subtract(250).multiply(0.09), RoundingMode.HALF_UP);
            Currency under250Commission = new Currency(250, RoundingMode.HALF_DOWN).subtract(policyTotalDueFirstBill).multiply(0.15).add(0.01);
            Currency formulaValue250 = new Currency(over250Commission.add(under250Commission));

            softly.assertThat(dbResponseListTransaction2.get(1).get("commissionItemCode")).isEqualTo("DENTAL");
            softly.assertThat(dbResponseListTransaction2.get(1).get("amount")).isEqualTo(formulaValue250.toPlainString());
        });

        LOGGER.info("Step#12 verification");
        billingAccount.generateFutureStatement().perform();
        Currency policyTotalDueThirdBill = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue(), RoundingMode.HALF_UP);

        LOGGER.info("Step#13 verification");
        Waiters.SLEEP(5000).go();
        List<Map<String, String>> dbResponseListGridItem3ndBill = dbService.getRows(String.format(SQL_GET_GRID_ITEM, masterPolicyNumber));
        List<Map<String, String>> dbResponseListTransaction3 = dbService.getRows(String.format(SQL_GET_TRANSACTION, masterPolicyNumber));

        Currency policiesAmountUnder450 = new Currency(450, RoundingMode.HALF_UP).subtract(policyTotalDueSecondBill);
        Currency policyAmountReminderUnder450 = new Currency(amountPerOnePolicy.subtract(policiesAmountUnder450).abs(), RoundingMode.HALF_UP);
        Currency policyAmountReminderOver450 = new Currency(amountPerOnePolicy.subtract(policyAmountReminderUnder450), RoundingMode.HALF_UP);

        String commissionAmountPolicy1Under450 = amountPerOnePolicy.multiply(0.09).toPlainString().concat("000000");
        String commissionAmountPolicy2Under450 = policyAmountReminderUnder450.multiply(0.09).toPlainString().concat("000000");
        String commissionAmountPolicy1Over450 = policyAmountReminderOver450.multiply(0.05).toPlainString().concat("000000");
        String commissionAmountOtherOver = amountPerOnePolicy.multiply(0.05).toPlainString().concat("000000");
        assertSoftly(softly -> {

            softly.assertThat(dbResponseListGridItem3ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicy1Under450)).collect(Collectors.toList())).hasSize(3);
            softly.assertThat(dbResponseListGridItem3ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicy2Under450)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem3ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountPolicy1Over450)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListGridItem3ndBill.stream().filter(x -> x.get("commissionAmount").equals(commissionAmountOtherOver)).collect(Collectors.toList())).hasSize(2);

            LOGGER.info("Step#14 verification");
            Currency over450Commission = policyTotalDueThirdBill.subtract(450).multiply(0.05);
            Currency under450Commission = new Currency(450).subtract(policyTotalDueSecondBill).multiply(0.09).add(0.01);
            Currency formulaValue450 = over450Commission.add(under450Commission);

            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("amount").equals(formulaValue450.toPlainString())).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListTransaction3.stream().filter(x -> x.get("commissionItemCode").equals("DENTAL")).collect(Collectors.toList())).hasSize(3);
        });
    }
}
