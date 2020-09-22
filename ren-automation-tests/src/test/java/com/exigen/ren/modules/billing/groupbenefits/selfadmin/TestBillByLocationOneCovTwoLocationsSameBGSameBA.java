package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.enums.TableConstants.BillingAdjustPremiumGB;
import com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGBByPeriod;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.pages.AdjustPremiumPage;
import com.exigen.ren.main.modules.billing.account.pages.CurrentPeriodPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage.ModalPremiums;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.ACTION;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.STATUS;
import static com.exigen.ren.main.modules.billing.account.pages.AdjustPremiumPage.buttonCancelBackUp;
import static com.exigen.ren.main.modules.billing.account.pages.CurrentPeriodPage.tableCurrentPeriod;
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

public class TestBillByLocationOneCovTwoLocationsSameBGSameBA extends GroupBenefitsBillingBaseTest implements BillingAccountContext {
    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-42271", component = BILLING_GROUPBENEFITS)
    public void testBillByLocationOneCovTwoLocationsSameBgSameBa() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(tdSpecific().getTestData("CaseProfile_TestData"), groupAccidentMasterPolicy.getType());

        //MP1 (Coverages - COV1 Payment Mode is 12, calendar Monthly).
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY);
        TestData tdMP1 = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()), "12")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"));

        groupAccidentMasterPolicy.createPolicy(tdMP1);
        Currency totalDue = new Currency(tableCoverageSummary.getRow(1).getCell(MODAL_PREMIUM.getName()).getValue());

        LOGGER.info("Step 1");
        String selfAdminMasterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(selfAdminMasterPolicyNumber);
        LOGGER.info("Navigate to Billing Account #" + billingAccountNumber);
        search(billingAccountNumber);
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForTwoLocs_TestData"));

        LOGGER.info("Step 2");
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String firstInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(firstInvoiceNumber);

        LOGGER.info("Step 3");
        assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRows(2);
        Currency firstHalfOfTotalDue = new Currency(totalDue.asBigDecimal().divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_DOWN));
        Currency secondHalfOfTotalDue = totalDue.subtract(firstHalfOfTotalDue);
        assertSoftly(softly -> {
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC1",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), firstHalfOfTotalDue.toString()));
            softly.assertThat(tableCurrentPeriodForBillCovBillGroupsByInvoice).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.LOCATION.getName(), "LOC2",
                    TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM.getName(), secondHalfOfTotalDue.toString()));
            softly.assertThat(tablePriorPeriodCharges.getRow(1).getCell(1)).hasValue("No records found.");
            softly.assertThat(tablePriorPeriodCharges.getHeader().getValue()).isEqualTo(tableCurrentPeriodForBillCovBillGroupsByInvoice.getHeader().getValue());
            softly.assertThat(tableAllocatedPayments.getHeader().getValue()).doesNotContain("Location");
            softly.assertThat(tableAllocatedPayments.getRow(1).getCell(1)).hasValue("No records found.");
        });

        LOGGER.info("Step 4");
        BillingSummaryPage.openBillsStatementsPeriodView();
        tableBillsAndStatementsByPeriod.getRow(ImmutableMap.of(BillingBillsAndStatementsGBByPeriod.INVOICE.getName(), firstInvoiceNumber))
                .getCell(BillingBillsAndStatementsGBByPeriod.INVOICE.getName()).controls.links.getFirst().click();
        assertThat(tableCurrentPeriod).hasRows(2);
        assertSoftly(softly -> {
            softly.assertThat(tableCurrentPeriod).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillingCurrentPeriod.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillingCurrentPeriod.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillingCurrentPeriod.LOCATION.getName(), "LOC1",
                    TableConstants.BillingCurrentPeriod.AMOUNT.getName(), firstHalfOfTotalDue.toString()));
            softly.assertThat(tableCurrentPeriod).hasRowsThatContain(1, ImmutableMap.of(
                    TableConstants.BillingCurrentPeriod.COVERAGE.getName(), "Enhanced Accident",
                    TableConstants.BillingCurrentPeriod.BILLING_GROUP.getName(), "BG001",
                    TableConstants.BillingCurrentPeriod.LOCATION.getName(), "LOC2",
                    TableConstants.BillingCurrentPeriod.AMOUNT.getName(), secondHalfOfTotalDue.toString()));
        });

        LOGGER.info("Step 5");
        CurrentPeriodPage.buttonBack.click();
        BillingSummaryPage.openBillsStatementsPeriodView();
        tableBillsAndStatementsByPeriod.getRow(1).getCell(ACTION.getName()).controls.links.get(ActionConstants.EDIT).click();
        assertThat(AdjustPremiumPage.tableAdjustPremium).hasRows(2);
        Row loc1Row = AdjustPremiumPage.tableAdjustPremium.getRow(ImmutableMap.of(
                BillingAdjustPremiumGB.LOCATION.getName(), "LOC1",
                BillingAdjustPremiumGB.COVERAGE.getName(), "ENHANCED",
                BillingAdjustPremiumGB.CLASS.getName(), "Employment",
                BillingAdjustPremiumGB.PERIOD_AMOUNT.getName(), firstHalfOfTotalDue.toString(),
                BillingAdjustPremiumGB.TOTAL_ADJUSTMENT.getName(), new Currency(0).toString()));
        Row loc2Row = AdjustPremiumPage.tableAdjustPremium.getRow(ImmutableMap.of(
                BillingAdjustPremiumGB.LOCATION.getName(), "LOC2",
                BillingAdjustPremiumGB.COVERAGE.getName(), "ENHANCED",
                BillingAdjustPremiumGB.CLASS.getName(), "Employment",
                BillingAdjustPremiumGB.PERIOD_AMOUNT.getName(), secondHalfOfTotalDue.toString(),
                BillingAdjustPremiumGB.TOTAL_ADJUSTMENT.getName(), new Currency(0).toString()));

        assertSoftly(softly -> {
            softly.assertThat(loc1Row).isPresent();
            softly.assertThat(loc2Row).isPresent();
        });

        assertSoftly(softly -> {
            softly.assertThat(loc1Row.getCell(BillingAdjustPremiumGB.BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(loc2Row.getCell(BillingAdjustPremiumGB.BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(loc1Row.getCell(BillingAdjustPremiumGB.ADJUSTED_AMOUNT.getName()).controls.textBoxes.getFirst().getValue()).isEqualTo(firstHalfOfTotalDue.toString());
            softly.assertThat(loc2Row.getCell(BillingAdjustPremiumGB.ADJUSTED_AMOUNT.getName()).controls.textBoxes.getFirst().getValue()).isEqualTo(secondHalfOfTotalDue.toString());
        });
        buttonCancelBackUp.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 6");
        billingAccount.viewModalPremium().start();
        assertThat(tableModalPremium).hasRows(3);
        assertThat(tableModalPremium.getHeader()).doesNotHaveValue(ImmutableList.of("History information"));
        assertSoftly(softly -> {
            softly.assertThat(tableModalPremium).hasMatchingRows(1, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Inactive"));
            softly.assertThat(tableModalPremium).hasMatchingRows(2, ImmutableMap.of(ModalPremiums.STATUS.getName(), "Active"));
        });

        Row inactiveRow = tableModalPremium.getRow(ImmutableMap.of(
                ModalPremiums.COVERAGE.getName(), "Enhanced Accident",
                ModalPremiums.STATUS.getName(), "Inactive"));
        inactiveRow.getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        TableExtended<ModalPremiumSummaryPage.BillingModalPremiumTable> modalPremiumsTableByWithInactiveStatus = ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0);
        assertThat(modalPremiumsTableByWithInactiveStatus).hasRows(2);
        assertSoftly(softly -> {
            Row firstRow = modalPremiumsTableByWithInactiveStatus.getRow(1);
            softly.assertThat(firstRow.getCell(MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(currentDate);
            softly.assertThat(firstRow.getCell(MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(selfAdminMasterPolicyNumber);
            softly.assertThat(firstRow.getCell(BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(firstRow.getCell(LOCATION.getName())).valueContains("");
            softly.assertThat(firstRow.getCell(TRANSACTION_TYPE_SUBTYPE_REASON.getName())).valueContains("Policy");
            softly.assertThat(firstRow.getCell(PAY_MODE.getName())).valueContains("12");
            softly.assertThat(firstRow.getCell(AMOUNT.getName())).valueContains(totalDue.toString());

            Row secondRow = modalPremiumsTableByWithInactiveStatus.getRow(2);
            softly.assertThat(secondRow.getCell(MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(currentDate);
            softly.assertThat(secondRow.getCell(MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(selfAdminMasterPolicyNumber);
            softly.assertThat(secondRow.getCell(BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(secondRow.getCell(LOCATION.getName())).valueContains("");
            softly.assertThat(secondRow.getCell(TRANSACTION_TYPE_SUBTYPE_REASON.getName())).valueContains("Billing Group Setup");
            softly.assertThat(secondRow.getCell(PAY_MODE.getName())).valueContains("12");
            softly.assertThat(secondRow.getCell(AMOUNT.getName())).valueContains("Transaction Moved");
        });

        inactiveRow.getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();
        assertSoftly(softly -> {
            Row secondRowForModalPremiumTable = tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC1");
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.COVERAGE.getName())).hasValue("Enhanced Accident");
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(currentDate);
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(selfAdminMasterPolicyNumber);
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.TRANSACTION_TYPE_SUBTYPE_REASON.getName())).valueContains("Billing Group Setup");
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.PAY_MODE.getName())).valueContains("12");
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.AMOUNT.getName())).valueContains(firstHalfOfTotalDue.toString());
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.STATUS.getName())).valueContains("Active");
            softly.assertThat(secondRowForModalPremiumTable.getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst()).isAbsent();

            Row thirdRowForModalPremiumTable = tableModalPremium.getRow(ModalPremiums.LOCATION.getName(), "LOC2");
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.COVERAGE.getName())).hasValue("Enhanced Accident");
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.MODAL_PREMIUM_EFFECTIVE_DATE.getName())).hasValue(currentDate);
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.MASTER_POLICY_CERTIFICATE_NUMBER.getName())).hasValue(selfAdminMasterPolicyNumber);
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.BILLING_GROUP.getName())).valueContains("BG001");
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.TRANSACTION_TYPE_SUBTYPE_REASON.getName())).valueContains("Billing Group Setup");
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.PAY_MODE.getName())).valueContains("12");
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.AMOUNT.getName())).valueContains(secondHalfOfTotalDue.toString());
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.STATUS.getName())).valueContains("Active");
            softly.assertThat(thirdRowForModalPremiumTable.getCell(ModalPremiums.COVERAGE.getName()).controls.links.getFirst()).isAbsent();
        });
    }
}