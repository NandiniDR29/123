package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.enums.TableConstants.BillingAdjustPremiumGB;
import com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGBByPeriod;
import com.exigen.ren.main.enums.TableConstants.BillingPremiumByCoverageSegmentClassifier;
import com.exigen.ren.main.enums.TableConstants.PriorPeriodCharges;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage.ModalPremiums;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.STATUS;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGBByPeriod.ACTION;
import static com.exigen.ren.main.enums.TableConstants.BillingPremiumByCoverageSegmentClassifier.LOCATION;
import static com.exigen.ren.main.enums.TableConstants.BillingPremiumByCoverageSegmentClassifier.*;
import static com.exigen.ren.main.modules.billing.account.pages.AdjustPremiumPage.buttonCancelBackUp;
import static com.exigen.ren.main.modules.billing.account.pages.AdjustPremiumPage.tableAdjustPremium;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CoverageSummary.MODAL_PREMIUM;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCoverageSummary;
import static com.exigen.ren.main.pages.summary.billing.BillingSummaryPage.*;
import static com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage.BillingModalPremiumTable.*;
import static com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage.tableModalPremium;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInvoiceSplittingAddingMovingToOtherBG extends GroupBenefitsBillingBaseTest implements BillingAccountContext {
    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-42272", component = BILLING_GROUPBENEFITS)
    public void testInvoiceSplittingAddingMovingToOtherBg() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(tdSpecific().getTestData("CaseProfile_TestData"), groupAccidentMasterPolicy.getType());

        //MP1 (Coverages - COV1 Payment Mode is 12, calendar Monthly).
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY);
        TestData tdMP1 = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()), "12")
                .adjust(TestData.makeKeyPath(classificationManagementMPTab.getMetaKey(), NUMBER_OF_PARTICIPANTS.getLabel()), "100")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"));
        groupAccidentMasterPolicy.createPolicy(tdMP1);
        Currency totalDue = new Currency(tableCoverageSummary.getRow(1).getCell(MODAL_PREMIUM.getName()).getValue());
        String selfAdminMasterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(selfAdminMasterPolicyNumber);

        LOGGER.info("Navigate to Billing Account #" + billingAccountNumber);
        search(billingAccountNumber);

        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String firstInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        LOGGER.info("Step 1");
        checkInvoiceByInvoice(firstInvoiceNumber, totalDue);

        LOGGER.info("Step 2");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(firstInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(1);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG001",
                LOCATION.getName(), "",
                COVERAGE.getName(), "Enhanced Accident",
                ESTIMATED_AMOUNT.getName(), totalDue.toString(),
                ADJUSTED_AMOUNT.getName(), totalDue.toString()));

        LOGGER.info("Step 3");
        String billingPeriod = tableBillsAndStatementsByPeriod.getRow(ImmutableMap.of(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), firstInvoiceNumber))
                .getCell(BillingBillsAndStatementsGBByPeriod.BILLING_PERIOD.getName()).controls.links.getFirst().getValue();
        tableBillsAndStatementsByPeriod.getRow(1).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(By.xpath("//td[@class='section_header' and contains(text(), 'Billing Period')]"))).valueContains(billingPeriod);
            softly.assertThat(tableAdjustPremium).hasRows(1);
        });
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), ""), totalDue, totalDue, new Currency(0), "BG001");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 4");
        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(1);
        checkTableModalPremiumRow(tableModalPremium.getRow(1), currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue, "Active");
        Tab.buttonBack.click();

        LOGGER.info("Step 5");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForTwoLocs_TestData"));
        Currency firstHalfOfTotalDue = new Currency(totalDue.asBigDecimal().divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_DOWN));
        Currency secondHalfOfTotalDue = totalDue.subtract(firstHalfOfTotalDue);

        LOGGER.info("Step 6");
        checkInvoiceByInvoice(firstInvoiceNumber, totalDue);

        LOGGER.info("Step 7");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(firstInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(3);

        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC1"), firstHalfOfTotalDue, new Currency(0), "BG001", "LOC1");
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC2"), secondHalfOfTotalDue, new Currency(0), "BG002", "LOC2");
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(ESTIMATED_AMOUNT.getName(), totalDue.toString()), new Currency(0), totalDue, "BG001", "");

        LOGGER.info("Step 8");
        String billingPeriodForTwoLocs = tableBillsAndStatementsByPeriod.getRow(ImmutableMap.of(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), firstInvoiceNumber))
                .getCell(BillingBillsAndStatementsGBByPeriod.BILLING_PERIOD.getName()).controls.links.getFirst().getValue();
        tableBillsAndStatementsByPeriod.getRow(1).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        assertSoftly(softly -> {
            softly.assertThat(tableAdjustPremium).hasRows(2);
            softly.assertThat(new StaticElement(By.xpath("//td[@class='section_header' and contains(text(), 'Billing Period')]"))).valueContains(billingPeriodForTwoLocs);
        });

        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC1"), new Currency(0), firstHalfOfTotalDue, firstHalfOfTotalDue, "BG001");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC2"), new Currency(0), secondHalfOfTotalDue, secondHalfOfTotalDue, "BG002");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 9");
        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(3);
        assertSoftly(softly -> {
            softly.assertThat(tableModalPremium).hasMatchingRows(1, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive"));
            softly.assertThat(tableModalPremium).hasMatchingRows(2, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Active"));
        });

        Row inactiveRowForTwoLocs = tableModalPremium.getRow(ImmutableMap.of(
                ModalPremiums.COVERAGE.getName(), "Enhanced Accident",
                ModalPremiums.STATUS.getName(), "Inactive"));
        checkTableModalPremiumRow(inactiveRowForTwoLocs, currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue, "Inactive");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC1"), currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstHalfOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC2"), currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondHalfOfTotalDue, "Active");

        inactiveRowForTwoLocs.getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableWithInactiveStatusTwoLocs = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0);
        assertThat(modalPremiumsTableWithInactiveStatusTwoLocs).hasRows(2);

        checkModalPremiumHistoryRow(modalPremiumsTableWithInactiveStatusTwoLocs.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Policy"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithInactiveStatusTwoLocs.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Billing Group Setup"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Billing Group Setup", "Transaction Moved");
        Tab.buttonBack.click();

        LOGGER.info("Step 10");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForThreeLocs_TestData"));
        Currency firstThirdOfTotalDue = new Currency(totalDue.asBigDecimal().divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_DOWN));
        Currency secondThirdOfTotalDue = totalDue.subtract(firstThirdOfTotalDue).divide(2);

        LOGGER.info("Step 11");
        checkInvoiceByInvoice(firstInvoiceNumber, totalDue);

        LOGGER.info("Step 12");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(firstInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(4);

        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC1"), firstThirdOfTotalDue, new Currency(0), "BG001", "LOC1");
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC2"), secondThirdOfTotalDue, new Currency(0), "BG002", "LOC2");
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC3"), secondThirdOfTotalDue, new Currency(0), "BG003", "LOC3");
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(ESTIMATED_AMOUNT.getName(), totalDue.toString()), new Currency(0), totalDue, "BG001", "");

        LOGGER.info("Step 13");
        String billingPeriodForThreeLocs = tableBillsAndStatementsByPeriod.getRow(ImmutableMap.of(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), firstInvoiceNumber))
                .getCell(BillingBillsAndStatementsGBByPeriod.BILLING_PERIOD.getName()).controls.links.getFirst().getValue();
        tableBillsAndStatementsByPeriod.getRow(1).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        assertSoftly(softly -> {
            softly.assertThat(tableAdjustPremium).hasRows(3);
            softly.assertThat(new StaticElement(By.xpath("//td[@class='section_header' and contains(text(), 'Billing Period')]"))).valueContains(billingPeriodForThreeLocs);
        });

        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC1"), new Currency(0), firstThirdOfTotalDue, firstThirdOfTotalDue, "BG001");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC2"), new Currency(0), secondThirdOfTotalDue, secondThirdOfTotalDue, "BG002");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC3"), new Currency(0), secondThirdOfTotalDue, secondThirdOfTotalDue, "BG003");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 14");
        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(4);
        assertSoftly(softly -> {
            softly.assertThat(tableModalPremium).hasMatchingRows(1, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive"));
            softly.assertThat(tableModalPremium).hasMatchingRows(3, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Active"));
        });

        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.STATUS.getName(), "Inactive"), currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue, "Inactive");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC1"), currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstThirdOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC2"), currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondThirdOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC3"), currentDate, selfAdminMasterPolicyNumber, "BG003", "LOC3", "Billing Group Setup", secondThirdOfTotalDue, "Active");

        tableModalPremium.getRow(ModalPremiums.STATUS.getName(), "Inactive").getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableForThreeLocsWithInactiveStatus = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0);
        assertThat(modalPremiumsTableForThreeLocsWithInactiveStatus).hasRows(2);

        checkModalPremiumHistoryRow(modalPremiumsTableForThreeLocsWithInactiveStatus.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Policy"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableForThreeLocsWithInactiveStatus.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Billing Group Setup"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Billing Group Setup", "Transaction Moved");

        tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC1").getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableForThreeLocsWithLoc1 = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(1);
        assertThat(modalPremiumsTableForThreeLocsWithLoc1).hasRows(2);
        checkModalPremiumHistoryRow(modalPremiumsTableForThreeLocsWithLoc1.getRow(1),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstHalfOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableForThreeLocsWithLoc1.getRow(2),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstThirdOfTotalDue.toString());

        tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC2").getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableForThreeLocsWithLoc2 = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(2);
        assertThat(modalPremiumsTableForThreeLocsWithLoc2).hasRows(2);
        checkModalPremiumHistoryRow(modalPremiumsTableForThreeLocsWithLoc2.getRow(1),
                currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondHalfOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableForThreeLocsWithLoc2.getRow(2),
                currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondThirdOfTotalDue.toString());

        assertThat((tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC3").getCell(ModalPremiums.COVERAGE.getName()).controls.links).getFirst()).isAbsent();
        Tab.buttonBack.click();

        LOGGER.info("Step 15");
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(2);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(2)).hasValue(PAID_IN_FULL_ESTIMATED);
        String secondInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        LOGGER.info("Step 16");
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(secondInvoiceNumber);
        assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRows(3);
        assertSoftly(softly -> {
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC1",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), firstThirdOfTotalDue.toString()));
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG002",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC2",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), secondThirdOfTotalDue.toString()));
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG003",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC3",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), secondThirdOfTotalDue.toString()));
        });

        assertThat(tablePriorPeriodCharges).hasRows(4);
        assertSoftly(softly -> {
            softly.assertThat(tablePriorPeriodCharges).hasRowsThatContain(1, ImmutableMap.of(
                    PriorPeriodCharges.COVERAGE.getName(), "Enhanced Accident",
                    PriorPeriodCharges.BILLING_GROUP.getName(), "BG001",
                    PriorPeriodCharges.LOCATION.getName(), "",
                    PriorPeriodCharges.PREMIUM.getName(), totalDue.negate().toString()));
            softly.assertThat(tablePriorPeriodCharges).hasRowsThatContain(1, ImmutableMap.of(
                    PriorPeriodCharges.COVERAGE.getName(), "Enhanced Accident",
                    PriorPeriodCharges.BILLING_GROUP.getName(), "BG001",
                    PriorPeriodCharges.LOCATION.getName(), "LOC1",
                    PriorPeriodCharges.PREMIUM.getName(), firstThirdOfTotalDue.toString()));
            softly.assertThat(tablePriorPeriodCharges).hasRowsThatContain(1, ImmutableMap.of(
                    PriorPeriodCharges.COVERAGE.getName(), "Enhanced Accident",
                    PriorPeriodCharges.BILLING_GROUP.getName(), "BG002",
                    PriorPeriodCharges.LOCATION.getName(), "LOC2",
                    PriorPeriodCharges.PREMIUM.getName(), secondThirdOfTotalDue.toString()));
            softly.assertThat(tablePriorPeriodCharges).hasRowsThatContain(1, ImmutableMap.of(
                    PriorPeriodCharges.COVERAGE.getName(), "Enhanced Accident",
                    PriorPeriodCharges.BILLING_GROUP.getName(), "BG003",
                    PriorPeriodCharges.LOCATION.getName(), "LOC3",
                    PriorPeriodCharges.PREMIUM.getName(), secondThirdOfTotalDue.toString()));
        });

        assertThat(tableAllocatedPayments.getRow(1).getCell(1)).hasValue("No records found.");

        LOGGER.info("Step 17");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(firstInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(4);
        assertSoftly(softly -> {
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG001",
                    LOCATION.getName(), "",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), totalDue.toString(),
                    ADJUSTED_AMOUNT.getName(), new Currency(0).toString()));
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG001",
                    LOCATION.getName(), "LOC1",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), new Currency(0).toString(),
                    ADJUSTED_AMOUNT.getName(), firstThirdOfTotalDue.toString()));
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG002",
                    LOCATION.getName(), "LOC2",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), new Currency(0).toString(),
                    ADJUSTED_AMOUNT.getName(), secondThirdOfTotalDue.toString()));
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG003",
                    LOCATION.getName(), "LOC3",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), new Currency(0).toString(),
                    ADJUSTED_AMOUNT.getName(), secondThirdOfTotalDue.toString()));
        });

        LOGGER.info("Step 18");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(secondInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(3);
        assertSoftly(softly -> {
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG001",
                    LOCATION.getName(), "LOC1",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), firstThirdOfTotalDue.toString(),
                    ADJUSTED_AMOUNT.getName(), firstThirdOfTotalDue.toString()));
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG002",
                    LOCATION.getName(), "LOC2",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), secondThirdOfTotalDue.toString(),
                    ADJUSTED_AMOUNT.getName(), secondThirdOfTotalDue.toString()));
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG003",
                    LOCATION.getName(), "LOC3",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), secondThirdOfTotalDue.toString(),
                    ADJUSTED_AMOUNT.getName(), secondThirdOfTotalDue.toString()));
        });

        LOGGER.info("Step 19");
        tableBillsAndStatementsByPeriod.getRow(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), firstInvoiceNumber).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC1"), firstThirdOfTotalDue, firstThirdOfTotalDue, new Currency(0), "BG001");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC2"), secondThirdOfTotalDue, secondThirdOfTotalDue, new Currency(0), "BG002");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC3"), secondThirdOfTotalDue, secondThirdOfTotalDue, new Currency(0), "BG003");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        BillingSummaryPage.openBillsStatementsPeriodView();
        tableBillsAndStatementsByPeriod.getRow(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), secondInvoiceNumber).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC1"), firstThirdOfTotalDue, firstThirdOfTotalDue, new Currency(0), "BG001");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC2"), secondThirdOfTotalDue, secondThirdOfTotalDue, new Currency(0), "BG002");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC3"), secondThirdOfTotalDue, secondThirdOfTotalDue, new Currency(0), "BG003");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 20");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_RemoveThirdLocation_TestData"));

        LOGGER.info("Step 21");
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(3);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String thirdInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        LOGGER.info("Step 22");
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(thirdInvoiceNumber);
        assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRows(2);
        assertSoftly(softly -> {
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC2",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), firstHalfOfTotalDue.toString()));
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG002",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC1",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), secondHalfOfTotalDue.toString()));
        });
        assertSoftly(softly -> {
            softly.assertThat(tablePriorPeriodCharges.getRow(1).getCell(1)).hasValue("No records found.");
            softly.assertThat(tableAllocatedPayments.getRow(1).getCell(1)).hasValue("No records found.");
        });

        LOGGER.info("Step 23");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(thirdInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(2);
        assertSoftly(softly -> {
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG001",
                    LOCATION.getName(), "LOC2",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), secondHalfOfTotalDue.toString(),
                    ADJUSTED_AMOUNT.getName(), secondHalfOfTotalDue.toString()));
            softly.assertThat(tablePremiumByCoverageSegmentClassifier).hasRowsThatContain(1, ImmutableMap.of(
                    BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName(), "BG002",
                    LOCATION.getName(), "LOC1",
                    COVERAGE.getName(), "Enhanced Accident",
                    ESTIMATED_AMOUNT.getName(), secondHalfOfTotalDue.toString(),
                    ADJUSTED_AMOUNT.getName(), secondHalfOfTotalDue.toString()));
        });

        LOGGER.info("Step 24");
        tableBillsAndStatementsByPeriod.getRow(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), thirdInvoiceNumber).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        assertThat(tableAdjustPremium).hasRows(2);

        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC1"), firstHalfOfTotalDue, firstHalfOfTotalDue, new Currency(0), "BG002");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC2"), secondHalfOfTotalDue, secondHalfOfTotalDue, new Currency(0), "BG001");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 25");
        String currentDatePlus2M = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2).format(MM_DD_YYYY);

        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(6);
        assertSoftly(softly -> {
            softly.assertThat(tableModalPremium).hasMatchingRows(3, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive"));
            softly.assertThat(tableModalPremium).hasMatchingRows(3, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Active"));
        });

        checkTableModalPremiumRow(tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue, "Inactive");
        checkTableModalPremiumRow(tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC1", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstThirdOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC2", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)),
                currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondThirdOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC3", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)),
                currentDate, selfAdminMasterPolicyNumber, "BG003", "LOC3", "Billing Group Setup", secondThirdOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC1", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDatePlus2M)),
                currentDatePlus2M, selfAdminMasterPolicyNumber, "BG002", "LOC1", "Billing Group Setup", firstHalfOfTotalDue, "Inactive");
        checkTableModalPremiumRow(tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC2", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDatePlus2M)),
                currentDatePlus2M, selfAdminMasterPolicyNumber, "BG001", "LOC2", "Billing Group Setup", secondHalfOfTotalDue, "Inactive");


        tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)).getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableInactiveWithCurrentDate = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0);
        assertThat(modalPremiumsTableInactiveWithCurrentDate).hasRows(2);

        checkModalPremiumHistoryRow(modalPremiumsTableInactiveWithCurrentDate.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Policy"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", totalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableInactiveWithCurrentDate.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Billing Group Setup"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Billing Group Setup", "Transaction Moved");

        tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC1", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)).getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableWithLoc1CurrentDate = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(1);
        assertThat(modalPremiumsTableWithLoc1CurrentDate).hasRows(3);
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc1CurrentDate.getRow(1),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstHalfOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc1CurrentDate.getRow(2),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstThirdOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc1CurrentDate.getRow(3),
                currentDatePlus2M, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", "Transaction Moved");

        tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC2", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)).getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableWithLoc2CurrentDate = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(2);
        assertThat(modalPremiumsTableWithLoc2CurrentDate).hasRows(3);
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc2CurrentDate.getRow(1),
                currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondHalfOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc2CurrentDate.getRow(2),
                currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondThirdOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc2CurrentDate.getRow(3),
                currentDatePlus2M, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", "Transaction Moved");

        tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC3", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDate)).getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableWithLoc3CurrentDate = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(3);
        assertThat(modalPremiumsTableWithLoc3CurrentDate).hasRows(2);
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc3CurrentDate.getRow(1),
                currentDate, selfAdminMasterPolicyNumber, "BG003", "LOC3", "Billing Group Setup", secondThirdOfTotalDue.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithLoc3CurrentDate.getRow(2),
                currentDatePlus2M, selfAdminMasterPolicyNumber, "BG003", "LOC3", "Billing Group Setup", "Transaction Moved");

        assertSoftly(softly -> {
            softly.assertThat((tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC1", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDatePlus2M)).getCell(ModalPremiums.COVERAGE.getName()).controls.links).getFirst()).isAbsent();
            softly.assertThat((tableModalPremium.getRow(ImmutableMap.of(ModalPremiums.LOCATION.getName(), "LOC2", ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName(), currentDatePlus2M)).getCell(ModalPremiums.COVERAGE.getName()).controls.links).getFirst()).isAbsent();
        });

    }

    public void checkTableAdjustPremiumRow(Row row, Currency periodAmount, Currency adjustedAmount, Currency totalAdjustment, String billingGroup) {
        assertSoftly(softly -> {
            softly.assertThat(row.getCell(BillingAdjustPremiumGB.BILLING_GROUP.getName())).valueContains(billingGroup);
            softly.assertThat(row.getCell(BillingAdjustPremiumGB.COVERAGE.getName())).hasValue("ENHANCED");
            softly.assertThat(row.getCell(BillingAdjustPremiumGB.CLASS.getName())).hasValue("Employment");
            softly.assertThat(row.getCell(BillingAdjustPremiumGB.PERIOD_AMOUNT.getName())).hasValue(periodAmount.toString());
            softly.assertThat(row.getCell(BillingAdjustPremiumGB.ADJUSTED_AMOUNT.getName()).controls.textBoxes.getFirst()).hasValue(adjustedAmount.toString());
            softly.assertThat(row.getCell(BillingAdjustPremiumGB.TOTAL_ADJUSTMENT.getName())).hasValue(totalAdjustment.toString());
        });
    }

    public void checkTablePremiumByCoverageSegmentClassifierRow(Row row, Currency adjustedAmount, Currency estimatedAmount, String billingGroup, String location) {
        assertSoftly(softly -> {
            softly.assertThat(row.getCell(BillingPremiumByCoverageSegmentClassifier.BILLING_GROUP.getName())).valueContains(billingGroup);
            softly.assertThat(row.getCell(BillingPremiumByCoverageSegmentClassifier.COVERAGE.getName())).valueContains("Enhanced Accident");
            softly.assertThat(row.getCell(LOCATION.getName())).hasValue(location);
            softly.assertThat(row.getCell(ESTIMATED_AMOUNT.getName())).hasValue(estimatedAmount.toString());
            softly.assertThat(row.getCell(ADJUSTED_AMOUNT.getName())).hasValue(adjustedAmount.toString());
        });
    }

    public void checkModalPremiumHistoryRow(Row row, String date, String policyNumber, String billingGroup, String location, String transactionTypeSubtypeReason, String amount) {
        assertSoftly(softly -> {
            softly.assertThat(row.getCell(MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(date);
            softly.assertThat(row.getCell(MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(policyNumber);
            softly.assertThat(row.getCell(ModalPremiumSummaryPage.BillingModalPremiumTable.BILLING_GROUP.getName())).valueContains(billingGroup);
            softly.assertThat(row.getCell(LOCATION.getName())).hasValue(location);
            softly.assertThat(row.getCell(TRANSACTION_TYPE_SUBTYPE_REASON.getName())).hasValue(transactionTypeSubtypeReason);
            softly.assertThat(row.getCell(PAY_MODE.getName())).hasValue("12");
            softly.assertThat(row.getCell(AMOUNT.getName())).hasValue(amount);
        });
    }

    public void checkTableModalPremiumRow(Row row, String currentDate, String policyNumber, String billingGroup, String location, String transactionTypeSubtypeReason, Currency totalDue, String status) {
        assertSoftly(softly -> {
            softly.assertThat(row.getCell(ModalPremiums.COVERAGE.getName())).hasValue("Enhanced Accident");
            softly.assertThat(row.getCell(ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(currentDate);
            softly.assertThat(row.getCell(ModalPremiums.MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(policyNumber);
            softly.assertThat(row.getCell(ModalPremiums.BILLING_GROUP.getName())).valueContains(billingGroup);
            softly.assertThat(row.getCell(ModalPremiums.LOCATION.getName())).hasValue(location);
            softly.assertThat(row.getCell(ModalPremiums.TRANSACTION_TYPE_SUBTYPE_REASON.getName())).hasValue(transactionTypeSubtypeReason);
            softly.assertThat(row.getCell(ModalPremiums.PAY_MODE.getName())).hasValue("12");
            softly.assertThat(row.getCell(ModalPremiums.AMOUNT.getName())).hasValue(totalDue.toString());
            softly.assertThat(row.getCell(ModalPremiums.STATUS.getName())).hasValue(status);
        });
    }

    public void checkInvoiceByInvoice(String invoiceNumber, Currency totalDue) {
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber);
        assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRows(1);
        assertSoftly(softly -> {
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), totalDue.toString()));
            softly.assertThat(tablePriorPeriodCharges.getRow(1).getCell(1)).hasValue("No records found.");
            softly.assertThat(tablePriorPeriodCharges.getHeader().getValue()).isEqualTo(tableCurrentPeriodForBillCovBillGroupsByInvoice.getHeader().getValue());
            softly.assertThat(tableAllocatedPayments.getHeader().getValue()).doesNotContain("Location");
            softly.assertThat(tableAllocatedPayments.getRow(1).getCell(1)).hasValue("No records found.");
        });
    }
}
