package com.exigen.ren.modules.commission.commissionstrategy.gb_tl;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionOverrideOptions.COMMISSION_TYPE;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionOverrideOptions.NAME;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.commissionRateInfoTable;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.TableConstants.CommissionsTable.OVERRIDDEN;
import static com.exigen.ren.main.enums.TableConstants.CommissionsTable.RATE;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestVerifyFlatOverrideForEachCommissionableProducer extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext {

    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-17104", "REN-17088"}, component = COMMISIONS_GROUPBENEFITS)
    public void testVerifyFlatOverrideForEachCommissionableProducer() {
        adminApp().open();

        LOGGER.info("Create Agencies 1-3");
        String agency_1_Name = agency.createAgency(tdAgencyDefault);
        String agency_3_Name = agency.createAgency(tdAgencyDefault);
        String agency_4_Name = agency.createAgency(tdAgencyDefault);

        LOGGER.info("Create Three Commission Groups");
        ImmutableMap<String, String> firstChannelCommissionGroup = createChannelCommissionGroup(agency_1_Name);
        ImmutableMap<String, String> thirdChannelCommissionGroup = createChannelCommissionGroup(agency_3_Name);
        ImmutableMap<String, String> fourthChannelCommissionGroup = createChannelCommissionGroup(agency_4_Name);
        String firstChannelCommissionGroupName = firstChannelCommissionGroup.get("Group Name");
        String thirdChannelCommissionGroupName = thirdChannelCommissionGroup.get("Group Name");
        String fourthChannelCommissionGroupName = fourthChannelCommissionGroup.get("Group Name");

        LOGGER.info("Create Commission Strategy");
        TestData flatRuleTestData = tdSpecific().getTestData("Flat_CommissionRule")
                .adjust(TestData.makeKeyPath("AddCommissionRule", "Select Sales Channel", "Commission Channel Group Name"), firstChannelCommissionGroupName);
        TestData cumulativeTieredTestData = tdSpecific().getTestData("CumulativeTiered_CommissionRule")
                .adjust(TestData.makeKeyPath("AddCommissionRule", "Select Sales Channel", "Commission Channel Group Name"), thirdChannelCommissionGroupName);
        TestData subscriberCountTestData = tdSpecific().getTestData("SubscriberCount_CommissionRule")
                .adjust(TestData.makeKeyPath("AddCommissionRule", "Select Sales Channel", "Commission Channel Group Name"), fourthChannelCommissionGroupName);
        String productCodeName = createCommissionStrategy(tdSpecific().getTestData("TestData").adjust("GBCommissionRuleTab", ImmutableList.of(flatRuleTestData, cumulativeTieredTestData, subscriberCountTestData)), false);
        gbCommissionStrategy.edit().start(productCodeName);
        String commissionFlatOverrideOption = GBCommissionStrategyTab.tableCommissionOverrideOptions.getRow(COMMISSION_TYPE.getName(), "Flat").getCell(NAME.getName()).getValue();
        String commissionCumulativeTieredOverrideOption = GBCommissionStrategyTab.tableCommissionOverrideOptions.getRow(COMMISSION_TYPE.getName(), "Cumulative Tiered").getCell(NAME.getName()).getValue();
        String commissionSubscriberCountFlatOverrideOption = GBCommissionStrategyTab.tableCommissionOverrideOptions.getRow(COMMISSION_TYPE.getName(), "Subscriber Count - Flat").getCell(NAME.getName()).getValue();

        TestData addFirstAgencyTestData = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName").adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), AGENCY_NAME.getLabel()), agency_1_Name);
        TestData addThirdAgencyTestData = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName").adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), AGENCY_NAME.getLabel()), agency_3_Name);
        TestData addFourthAgencyTestData = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName").adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), AGENCY_NAME.getLabel()), agency_4_Name);

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(addFirstAgencyTestData, addThirdAgencyTestData, addFourthAgencyTestData)));
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile")
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), termLifeInsuranceMasterPolicy.getType());
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", "Coverage Effective Date"), currentDate));
        TestData addFirstAgencyToQuote = tdSpecific().getTestData("Add_Agency_To_Quote")
                .adjust("Agency / Producer", agency_1_Name).adjust("Primary Agency?", "Yes");
        TestData addThirdAgencyToQuote = tdSpecific().getTestData("Add_Agency_To_Quote").adjust("Agency / Producer", agency_3_Name);
        TestData addFourthAgencyToQuote = tdSpecific().getTestData("Add_Agency_To_Quote").adjust("Agency / Producer", agency_4_Name);
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(
                getDefaultTLMasterPolicyData()
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel()), ImmutableList.of(addFirstAgencyToQuote, addThirdAgencyToQuote, addFourthAgencyToQuote))
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS.getLabel()), "Yes"),
                TermLifeInsuranceMasterPolicyContext.premiumSummaryTab.getClass());

        LOGGER.info("REN-17088 Scenario 1, Step 1.4.1, REN-17104");
        assertSoftly(softly -> {
            ImmutableList.of(ADD, DEP_BTL, BTL).forEach(coverage ->
                    softly.assertThat(PremiumSummaryTab.getCommissionTableForAgencySection(agency_1_Name, coverage))
                            .hasMatchingRows(1, ImmutableMap.of(RATE.getName(), "20 %")));
            ImmutableList.of(ADD, DEP_BTL, BTL).forEach(coverage ->
                    softly.assertThat(PremiumSummaryTab.getCommissionTableForAgencySection(agency_3_Name, coverage))
                            .hasMatchingRows(1, ImmutableMap.of(RATE.getName(), "25 %")));
            ImmutableList.of(ADD, DEP_BTL, BTL).forEach(coverage ->
                    softly.assertThat(PremiumSummaryTab.getCommissionTableForAgencySection(agency_4_Name, coverage))
                            .hasMatchingRows(1, ImmutableMap.of(RATE.getName(), new Currency("25").toString())));
        });

        LOGGER.info("REN-17088 Scenario 1, Step 2, REN-17104");
        assertSoftly(softly -> {
            Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agency_1_Name, ADD);
            softly.assertThat(overrideCommissionButton).isPresent().isEnabled();
            overrideCommissionButton.click();
            DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(commissionFlatOverrideOption);
            commissionRateInfoTable.getRow(1).getCell(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.RATE.getName()).controls.textBoxes.getFirst().setValue("5 %");
            PremiumSummaryTab.dialogOverrideCommision.confirm();
            PremiumSummaryTab.getCommissionTableForAgencySection(agency_1_Name, ADD).getRows().forEach(row -> {
                softly.assertThat(row.getCell(RATE.getName())).hasValue("5 %");
                softly.assertThat(row.getCell(OVERRIDDEN.getName())).hasValue("Yes");
            });
        });

        assertSoftly(softly -> {
            Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agency_4_Name, ADD);
            softly.assertThat(overrideCommissionButton).isPresent().isEnabled();
            overrideCommissionButton.click();
            DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(commissionSubscriberCountFlatOverrideOption);
            DialogOverrideCommissionPremiumSummary.overrideCommissionForAllCoverages(new Currency("6").toString());
            PremiumSummaryTab.dialogOverrideCommision.confirm();
            PremiumSummaryTab.getCommissionTableForAgencySection(agency_4_Name, ADD).getRows().forEach(row -> {
                softly.assertThat(row.getCell(RATE.getName())).hasValue(new Currency("6").toString());
                softly.assertThat(row.getCell(OVERRIDDEN.getName())).hasValue("Yes");
            });
        });

        assertSoftly(softly -> {
            Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agency_3_Name, ADD);
            softly.assertThat(overrideCommissionButton).isPresent().isEnabled();
            overrideCommissionButton.click();
            DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(commissionCumulativeTieredOverrideOption);
            PremiumSummaryTab.dialogOverrideCommision.confirm();
            PremiumSummaryTab.getCommissionTableForAgencySection(agency_3_Name, ADD).getRows().forEach(row ->
                    softly.assertThat(row.getCell(RATE.getName())).hasValue("10 %"));
        });

        LOGGER.info("REN-17088 Scenario 1, Step 3, REN-17104");
        TermLifeInsuranceMasterPolicyContext.premiumSummaryTab.fillTab(getDefaultTLMasterPolicyData()).submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        proposeAcceptContractIssueDefaultTestData();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("REN-17088 Scenario 1, Step 4, REN-17104");
        termLifeInsuranceCertificatePolicy.createPolicyViaUI(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), currentDate));

        LOGGER.info("REN-17088 Scenario 1, Step 8, REN-17104");
        MainPage.QuickSearch.search(masterPolicyNumber);
        termLifeInsuranceMasterPolicy.endorse().perform(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), currentDate));
        PolicySummaryPage.buttonPendedEndorsement.click();
        termLifeInsuranceMasterPolicy.dataGather().start();
        TermLifeInsuranceMasterPolicyContext.premiumSummaryTab.navigate();

        ImmutableList.of(agency_3_Name, agency_4_Name).forEach(agencyName -> {
            Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, ADD);
            overrideCommissionButton.click();
            DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(commissionFlatOverrideOption);
            commissionRateInfoTable.getRow(1).getCell(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.RATE.getName()).controls.textBoxes.getFirst().setValue("19");
            PremiumSummaryTab.dialogOverrideCommision.confirm();
            PremiumSummaryTab.getCommissionTableForAgencySection(agencyName, ADD).getRows().forEach(row ->
                    assertSoftly(softly -> {
                        softly.assertThat(row.getCell(RATE.getName())).hasValue("19 %");
                        softly.assertThat(row.getCell(OVERRIDDEN.getName())).hasValue("Yes");
                        softly.assertThat(overrideCommissionButton).isPresent().isEnabled();
                    }));
        });
        PremiumSummaryTab.getCommissionTableForAgencySection(agency_1_Name, ADD).getRows().forEach(row ->
                assertSoftly(softly -> {
                    softly.assertThat(row.getCell(RATE.getName())).hasValue("5 %");
                    softly.assertThat(row.getCell(OVERRIDDEN.getName())).hasValue("Yes");
                }));
    }
}
