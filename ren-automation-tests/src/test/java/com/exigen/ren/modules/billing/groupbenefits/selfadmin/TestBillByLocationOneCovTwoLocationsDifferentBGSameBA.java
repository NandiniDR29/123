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
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.STATUS;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGBByPeriod.ACTION;
import static com.exigen.ren.main.enums.TableConstants.BillingPremiumByCoverageSegmentClassifier.ADJUSTED_AMOUNT;
import static com.exigen.ren.main.enums.TableConstants.BillingPremiumByCoverageSegmentClassifier.ESTIMATED_AMOUNT;
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

public class TestBillByLocationOneCovTwoLocationsDifferentBGSameBA extends GroupBenefitsBillingBaseTest implements BillingAccountContext {
    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-42273", component = BILLING_GROUPBENEFITS)
    public void testBillByLocationOneCovTwoLocationsDifferentBgSameBa() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(tdSpecific().getTestData("CaseProfile_TestData"), groupAccidentMasterPolicy.getType());

//        MP1 (Coverages - COV1 Payment Mode is 12, calendar Monthly).
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
        Currency modalPremium = new Currency(tableCoverageSummary.getRow(1).getCell(MODAL_PREMIUM.getName()).getValue());
        Currency firstHalfOfTotalDue = new Currency(modalPremium.asBigDecimal().divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_DOWN));
        Currency secondHalfOfTotalDue = modalPremium.subtract(firstHalfOfTotalDue);

        String selfAdminMasterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(selfAdminMasterPolicyNumber);

        LOGGER.info("Navigate to Billing Account #" + billingAccountNumber);
        search(billingAccountNumber);

        LOGGER.info("Step 1");
        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(1);
        assertSoftly(softly -> {
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.COVERAGE.getName())).hasValue("Enhanced Accident");
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(currentDate);
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(selfAdminMasterPolicyNumber);
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.LOCATION.getName())).hasValue("");
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.TRANSACTION_TYPE_SUBTYPE_REASON.getName())).hasValue("Policy");
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.PAY_MODE.getName())).hasValue("12");
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.AMOUNT.getName())).hasValue(modalPremium.toString());
            softly.assertThat(tableModalPremium.getRow(1).getCell(ModalPremiums.STATUS.getName())).hasValue("Active");
        });
        Tab.buttonBack.click();

        LOGGER.info("Step 2");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForTwoLocs_TestData"));

        LOGGER.info("Step 3");
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String firstInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        LOGGER.info("Step 4");
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(firstInvoiceNumber);
        assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRows(2);
        assertSoftly(softly -> {
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC1",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), firstHalfOfTotalDue.toString()));
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG002",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC2",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), firstHalfOfTotalDue.toString()));

            softly.assertThat(tablePriorPeriodCharges.getRow(1).getCell(1)).hasValue("No records found.");
            softly.assertThat(tablePriorPeriodCharges.getHeader().getValue()).isEqualTo(tableCurrentPeriodForBillCovBillGroupsByInvoice.getHeader().getValue());
            softly.assertThat(tableAllocatedPayments.getHeader().getValue()).doesNotContain("Location");
            softly.assertThat(tableAllocatedPayments.getRow(1).getCell(1)).hasValue("No records found.");
        });

        LOGGER.info("Step 5");
        BillingSummaryPage.openBillsStatementsPeriodView();
        BillingSummaryPage.expandBillsStatementsPeriodViewByInvoice(firstInvoiceNumber);
        assertThat(tablePremiumByCoverageSegmentClassifier).hasRows(2);
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC1"),
                firstHalfOfTotalDue, firstHalfOfTotalDue, "BG001", "LOC1");
        checkTablePremiumByCoverageSegmentClassifierRow(tablePremiumByCoverageSegmentClassifier.getRow(LOCATION.getName(), "LOC2"),
                secondHalfOfTotalDue, secondHalfOfTotalDue, "BG002", "LOC2");

        LOGGER.info("Step 6");
        String billingPeriodForTwoLocs = tableBillsAndStatementsByPeriod.getRow(ImmutableMap.of(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), firstInvoiceNumber))
                .getCell(BillingBillsAndStatementsGBByPeriod.BILLING_PERIOD.getName()).controls.links.getFirst().getValue();
        tableBillsAndStatementsByPeriod.getRow(1).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        assertSoftly(softly -> {
            softly.assertThat(tableAdjustPremium).hasRows(2);
            softly.assertThat(new StaticElement(By.xpath("//td[@class='section_header' and contains(text(), 'Billing Period')]"))).valueContains(billingPeriodForTwoLocs);
        });

        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC1"), firstHalfOfTotalDue, firstHalfOfTotalDue, new Currency(0), "BG001");
        checkTableAdjustPremiumRow(tableAdjustPremium.getRow(BillingAdjustPremiumGB.LOCATION.getName(), "LOC2"), secondHalfOfTotalDue, secondHalfOfTotalDue, new Currency(0), "BG002");
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 7");
        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(3);
        assertSoftly(softly -> {
            softly.assertThat(tableModalPremium).hasMatchingRows(1, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive"));
            softly.assertThat(tableModalPremium).hasMatchingRows(2, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Active"));
        });

        Row inactiveRowForTwoLocs = tableModalPremium.getRow(ImmutableMap.of(
                ModalPremiums.COVERAGE.getName(), "Enhanced Accident",
                ModalPremiums.STATUS.getName(), "Inactive"));
        checkTableModalPremiumRow(inactiveRowForTwoLocs, currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", modalPremium, "Inactive");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC1"), currentDate, selfAdminMasterPolicyNumber, "BG001", "LOC1", "Billing Group Setup", firstHalfOfTotalDue, "Active");
        checkTableModalPremiumRow(tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC2"), currentDate, selfAdminMasterPolicyNumber, "BG002", "LOC2", "Billing Group Setup", secondHalfOfTotalDue, "Active");

        inactiveRowForTwoLocs.getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableWithInactiveStatusTwoLocs = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0);
        assertThat(modalPremiumsTableWithInactiveStatusTwoLocs).hasRows(2);

        checkModalPremiumHistoryRow(modalPremiumsTableWithInactiveStatusTwoLocs.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Policy"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Policy", modalPremium.toString());
        checkModalPremiumHistoryRow(modalPremiumsTableWithInactiveStatusTwoLocs.getRow(TRANSACTION_TYPE_SUBTYPE_REASON.getName(), "Billing Group Setup"),
                currentDate, selfAdminMasterPolicyNumber, "BG001", "", "Billing Group Setup", "Transaction Moved");
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

    public void checkModalPremiumHistoryRow(Row row, String date, String policyNumber, String billingGroup, String location, String transactionTypeSubtypeReason, String amount) {
        assertSoftly(softly -> {
            softly.assertThat(row.getCell(MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(date);
            softly.assertThat(row.getCell(MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(policyNumber);
            softly.assertThat(row.getCell(BILLING_GROUP.getName())).valueContains(billingGroup);
            softly.assertThat(row.getCell(LOCATION.getName())).hasValue(location);
            softly.assertThat(row.getCell(TRANSACTION_TYPE_SUBTYPE_REASON.getName())).hasValue(transactionTypeSubtypeReason);
            softly.assertThat(row.getCell(PAY_MODE.getName())).hasValue("12");
            softly.assertThat(row.getCell(AMOUNT.getName())).hasValue(amount);
        });
    }
}
