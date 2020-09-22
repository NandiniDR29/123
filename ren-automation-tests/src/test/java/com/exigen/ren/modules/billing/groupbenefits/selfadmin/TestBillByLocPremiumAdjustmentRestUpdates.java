package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Cell;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.BillingInvoiceCoveragePeriodDateModel;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.BILLING;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.DISCARDED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.STATUS;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CoverageSummary.MODAL_PREMIUM;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCoverageSummary;
import static com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage.tableBenefitAccounts;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillByLocPremiumAdjustmentRestUpdates extends GroupBenefitsBillingBaseTest implements BillingAccountContext {
    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-42278", component = BILLING_GROUPBENEFITS)
    public void testBillByLocPremiumAdjustmentRestUpdates() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(tdSpecific().getTestData("CaseProfile_TestData"), groupAccidentMasterPolicy.getType());

//        MP1 (Coverages - COV1 Payment Mode is 12, calendar Monthly).
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(MM_DD_YYYY);
        TestData tdMP1 = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Non-contributory")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()), "12")
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
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String firstInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDate = LocalDate.parse(billingPeriod.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDate, endDate);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel().getInvoiceNumber()).isEqualTo(firstInvoiceNumber);
        assertThat(response.getModel().getPremiumItems()).hasSize(1);
        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getPremiumItems().get(0).getBilledAmount()).isEqualTo(modalPremium.toPlainString());
            softly.assertThat(response.getModel().getPremiumItems().get(0).getPeriodAmount()).isEqualTo(modalPremium.toPlainString());
            softly.assertThat(response.getModel().getPremiumItems().get(0).getBillingGroupName()).isEqualTo("BG001");
            softly.assertThat(response.getModel().getPremiumItems().get(0).getBillingLocation().getBillingLocationCode()).isNull();
            softly.assertThat(response.getModel().getPremiumItems().get(0).getBillingLocation().getBillingLocationName()).isNull();
        });

        LOGGER.info("Step 2");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForTwoLocs_TestData"));

        LOGGER.info("Step 3");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseWithTwoLocs = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDate, endDate);
        assertThat(responseWithTwoLocs.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseWithTwoLocs.getModel().getInvoiceNumber()).isEqualTo(firstInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItems = responseWithTwoLocs.getModel().getPremiumItems();
        assertThat(premiumItems).hasSize(2);
        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg001Item = findItemWithBillingGroup(premiumItems, "BG001");
            softly.assertThat(bg001Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg001Item.getPeriodAmount()).isEqualTo(firstHalfOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC1");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg002Item = findItemWithBillingGroup(premiumItems, "BG002");

            softly.assertThat(bg002Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg002Item.getPeriodAmount()).isEqualTo(secondHalfOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC2");
        });

        LOGGER.info("Step 4");
        billingAccount.discardBill().perform(new SimpleDataProvider());
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(DISCARDED_ESTIMATED);

        LOGGER.info("Step 5");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseDiscarded = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDate, endDate);
        assertThat(responseDiscarded.getResponse().getStatus()).isEqualTo(422);
        assertSoftly(softly -> {
            softly.assertThat(responseDiscarded.getModel().getErrorCode()).isEqualTo("ONLY_DISCARDED_INVOICES_FOUND");
            softly.assertThat(responseDiscarded.getModel().getMessage()).isEqualTo("Specified Billing Account has only invoices with status ’Discarded’");
        });

        LOGGER.info("Step 6");
        billingAccount.regenerateBill().perform(new SimpleDataProvider());
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(2);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String regeneratedInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();


        LOGGER.info("Step 7");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseRegenerated = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDate, endDate);
        assertThat(responseRegenerated.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseRegenerated.getModel().getInvoiceNumber()).isEqualTo(regeneratedInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsRegenerated = responseRegenerated.getModel().getPremiumItems();
        assertThat(premiumItemsRegenerated).hasSize(2);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg001Item = findItemWithBillingGroup(premiumItemsRegenerated, "BG001");
            softly.assertThat(bg001Item.getBilledAmount()).isEqualTo(firstHalfOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getPeriodAmount()).isEqualTo(firstHalfOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC1");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg002Item = findItemWithBillingGroup(premiumItemsRegenerated, "BG002");

            softly.assertThat(bg002Item.getBilledAmount()).isEqualTo(secondHalfOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getPeriodAmount()).isEqualTo(secondHalfOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC2");
        });

        LOGGER.info("Step 8");
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(3);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String secondInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        String billingPeriodForSecondInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDateForSecondInvoice = LocalDate.parse(billingPeriodForSecondInvoice.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDateForSecondInvoice = LocalDate.parse(billingPeriodForSecondInvoice.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("Step 9");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseForThreeLOcs = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDateForSecondInvoice, endDateForSecondInvoice);
        assertThat(responseForThreeLOcs.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseForThreeLOcs.getModel().getInvoiceNumber()).isEqualTo(secondInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsSecondInvoice = responseForThreeLOcs.getModel().getPremiumItems();
        assertThat(premiumItemsSecondInvoice).hasSize(2);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg001Item = findItemWithBillingGroup(premiumItemsSecondInvoice, "BG001");
            softly.assertThat(bg001Item.getBilledAmount()).isEqualTo(firstHalfOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getPeriodAmount()).isEqualTo(firstHalfOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC1");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg002Item = findItemWithBillingGroup(premiumItemsSecondInvoice, "BG002");

            softly.assertThat(bg002Item.getBilledAmount()).isEqualTo(secondHalfOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getPeriodAmount()).isEqualTo(secondHalfOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC2");
        });

        LOGGER.info("Step 10");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForThreeLocs_TestData"));
        Currency firstThirdOfTotalDue = new Currency(modalPremium.asBigDecimal().divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_DOWN));
        Currency secondThirdOfTotalDue = modalPremium.subtract(firstThirdOfTotalDue).divide(2);

        LOGGER.info("Step 11");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseForThreeLocsSecondInvoice = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDateForSecondInvoice, endDateForSecondInvoice);
        assertThat(responseForThreeLocsSecondInvoice.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseForThreeLocsSecondInvoice.getModel().getInvoiceNumber()).isEqualTo(secondInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsThreeLocsSecondInvoice = responseForThreeLocsSecondInvoice.getModel().getPremiumItems();
        assertThat(premiumItemsThreeLocsSecondInvoice).hasSize(3);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg001Item = findItemWithBillingGroup(premiumItemsThreeLocsSecondInvoice, "BG001");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(bg001Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg001Item.getPeriodAmount()).isEqualTo(firstThirdOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC1");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg002Item = findItemWithBillingGroup(premiumItemsThreeLocsSecondInvoice, "BG002");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(bg002Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg002Item.getPeriodAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC2");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg003Item = findItemWithBillingGroup(premiumItemsThreeLocsSecondInvoice, "BG003");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(bg003Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg003Item.getPeriodAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(bg003Item.getBillingLocation().getBillingLocationCode()).isEqualTo("3");
            softly.assertThat(bg003Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC3");
        });

        LOGGER.info("Step 12");
        search(selfAdminMasterPolicyNumber);
        groupAccidentMasterPolicy.endorse().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusMonths(2).format(MM_DD_YYYY)));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupAccidentMasterPolicy.issue().perform(tdSpecific().getTestData("Endorsement_Issue_TestData"));

        LOGGER.info("Step 13");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseAfterRemoveTwoLocs = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDateForSecondInvoice, endDateForSecondInvoice);
        assertThat(responseAfterRemoveTwoLocs.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseAfterRemoveTwoLocs.getModel().getInvoiceNumber()).isEqualTo(secondInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsAfterRemoveTwoLocs = responseAfterRemoveTwoLocs.getModel().getPremiumItems();
        assertThat(premiumItemsAfterRemoveTwoLocs).hasSize(3);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg001Item = findItemWithBillingGroup(premiumItemsAfterRemoveTwoLocs, "BG001");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(bg001Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg001Item.getPeriodAmount()).isEqualTo(firstThirdOfTotalDue.toPlainString());
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(bg001Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC1");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg002Item = findItemWithBillingGroup(premiumItemsAfterRemoveTwoLocs, "BG002");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(bg002Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg002Item.getPeriodAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(bg002Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC2");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems bg003Item = findItemWithBillingGroup(premiumItemsAfterRemoveTwoLocs, "BG003");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(bg003Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(bg003Item.getPeriodAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(bg003Item.getBillingLocation().getBillingLocationCode()).isEqualTo("3");
            softly.assertThat(bg003Item.getBillingLocation().getBillingLocationName()).isEqualTo("LOC3");
        });

        LOGGER.info("Step 14");
        search(billingAccountNumber);
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(4);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String thirdInvoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        String billingPeriodForThirdInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDateForThirdInvoice = LocalDate.parse(billingPeriodForThirdInvoice.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDateForThirdInvoice = LocalDate.parse(billingPeriodForThirdInvoice.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("Step 15");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseThirdInvoice = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDateForThirdInvoice, endDateForThirdInvoice);
        assertThat(responseThirdInvoice.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseThirdInvoice.getModel().getInvoiceNumber()).isEqualTo(thirdInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsThirdInvoice = responseThirdInvoice.getModel().getPremiumItems();
        assertThat(premiumItemsThirdInvoice).hasSize(3);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems loc3Item = findItemWithLocationName(premiumItemsThirdInvoice, "LOC3");
            softly.assertThat(loc3Item.getBilledAmount()).isEqualTo(firstThirdOfTotalDue.toPlainString());
            softly.assertThat(loc3Item.getPeriodAmount()).isEqualTo(firstThirdOfTotalDue.toPlainString());
            softly.assertThat(loc3Item.getBillingLocation().getBillingLocationCode()).isEqualTo("3");
            softly.assertThat(loc3Item.getBillingGroupName()).isEqualTo("BG001");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems loc2Item = findItemWithLocationName(premiumItemsThirdInvoice, "LOC2");
            softly.assertThat(loc2Item.getBilledAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(loc2Item.getPeriodAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(loc2Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(loc2Item.getBillingGroupName()).isEqualTo("BG001");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems loc1Item = findItemWithLocationName(premiumItemsThirdInvoice, "LOC1");
            softly.assertThat(loc1Item.getBilledAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(loc1Item.getPeriodAmount()).isEqualTo(secondThirdOfTotalDue.toPlainString());
            softly.assertThat(loc1Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(loc1Item.getBillingGroupName()).isEqualTo("BG001");
        });

        LOGGER.info("Step 16");
        groupAccidentMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("SetupBillingGroups_ForThreeLocs_DiffBAsTestData"));
        NavigationPage.toMainTab(BILLING);
        assertThat(tableBenefitAccounts).hasRows(2);
        Cell secondBillingAccountCell = tableBenefitAccounts.getRow(1).getCell(BillingConstants.BillingBenefitAccountsTable.BILLING_ACCOUNT);
        String secondBillingAccountNumber = secondBillingAccountCell.getValue();

        LOGGER.info("Step 17");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseThirdInvoiceDiffBAs = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(billingAccountNumber, startDateForThirdInvoice, endDateForThirdInvoice);
        assertThat(responseThirdInvoiceDiffBAs.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseThirdInvoiceDiffBAs.getModel().getInvoiceNumber()).isEqualTo(thirdInvoiceNumber);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsThirdInvoiceDiffBAs = responseThirdInvoiceDiffBAs.getModel().getPremiumItems();
        assertThat(premiumItemsThirdInvoiceDiffBAs).hasSize(2);

        Currency firstFourthOfTotalDue = new Currency(firstHalfOfTotalDue.asBigDecimal().divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_DOWN));
        Currency secondFourthOfTotalDue = firstHalfOfTotalDue.subtract(firstFourthOfTotalDue);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems loc1Item = findItemWithLocationName(premiumItemsThirdInvoiceDiffBAs, "LOC1");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(loc1Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(loc1Item.getPeriodAmount()).isEqualTo(firstFourthOfTotalDue.toPlainString());
            softly.assertThat(loc1Item.getBillingLocation().getBillingLocationCode()).isEqualTo("1");
            softly.assertThat(loc1Item.getBillingGroupName()).isEqualTo("BG001");

            BillingInvoiceCoveragePeriodDateModel.PremiumItems loc2Item = findItemWithLocationName(premiumItemsThirdInvoiceDiffBAs, "LOC2");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(loc1Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(loc2Item.getPeriodAmount()).isEqualTo(secondFourthOfTotalDue.toPlainString());
            softly.assertThat(loc2Item.getBillingLocation().getBillingLocationCode()).isEqualTo("2");
            softly.assertThat(loc2Item.getBillingGroupName()).isEqualTo("BG001");
        });

        LOGGER.info("Step 18");
        search(secondBillingAccountNumber);
        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(3);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getColumn(STATUS).getCell(1)).hasValue(ISSUED_ESTIMATED);
        String firstInvoiceNumberSecondBA = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        String billingPeriodForFirstInvoiceSecondBA = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDateForFirstInvoiceSecondBA = LocalDate.parse(billingPeriodForFirstInvoiceSecondBA.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDateForFirstInvoiceSecondBA = LocalDate.parse(billingPeriodForFirstInvoiceSecondBA.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("Step 19");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> responseFirstInvoiceSecondBA = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(secondBillingAccountNumber, startDateForFirstInvoiceSecondBA, endDateForFirstInvoiceSecondBA);
        assertThat(responseFirstInvoiceSecondBA.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseFirstInvoiceSecondBA.getModel().getInvoiceNumber()).isEqualTo(firstInvoiceNumberSecondBA);
        List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> premiumItemsFirstInvoiceSecondBA = responseFirstInvoiceSecondBA.getModel().getPremiumItems();
        assertThat(premiumItemsFirstInvoiceSecondBA).hasSize(1);

        assertSoftly(softly -> {
            BillingInvoiceCoveragePeriodDateModel.PremiumItems loc3Item = findItemWithLocationName(premiumItemsFirstInvoiceSecondBA, "LOC3");
            // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-45194
//            softly.assertThat(loc3Item.getBilledAmount()).isEqualTo("0");
            softly.assertThat(loc3Item.getPeriodAmount()).isEqualTo(secondHalfOfTotalDue.toPlainString());
            softly.assertThat(loc3Item.getBillingLocation().getBillingLocationCode()).isEqualTo("3");
            softly.assertThat(loc3Item.getBillingGroupName()).isEqualTo("BG002");
        });


    }

    public BillingInvoiceCoveragePeriodDateModel.PremiumItems findItemWithBillingGroup(List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> items, String billingGroupName) {
        return items.stream().filter(item -> item.getBillingGroupName().equals(billingGroupName))
                .findFirst().orElseThrow(() -> new IstfException(String.format("Item with billingGroup='%s' is not found", billingGroupName)));
    }

    public BillingInvoiceCoveragePeriodDateModel.PremiumItems findItemWithLocationName(List<BillingInvoiceCoveragePeriodDateModel.PremiumItems> items, String locationName) {
        return items.stream().filter(item -> item.getBillingLocation().getBillingLocationName().equals(locationName))
                .findFirst().orElseThrow(() -> new IstfException(String.format("Item with location='%s' is not found", locationName)));
    }
}
