package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitStatutorySTDInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.APPROVED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.STATUS;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_SCHEDULE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab.EliminationQualificationPeriod.ELIMINATION_PERIOD;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData.EMPLOYMENT_TYPE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsStatutorySTDInjuryPartyInformationTabMetaData.WORK_STATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimEliminationPeriod extends ClaimGroupBenefitsSTBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20798", component = CLAIMS_GROUPBENEFITS)
    public void testCWMPEliminationPeriodPostPayment() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_AdditionalParties")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), WORK_STATE.getLabel()), "NJ")
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()),
                        TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(MM_DD_YYYY)));

        LOGGER.info("Step 1");
        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_FROM_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusDays(28).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                        PAYMENT_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusDays(29).format(DateTimeUtils.MM_DD_YYYY)));

        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TRANSACTION_STATUS)).hasValue(APPROVED);
        });

        LOGGER.info("Step 2");
        claim.viewSingleBenefitCalculation().perform(1);
        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD.getName())).hasValue("0");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-20798", component = CLAIMS_GROUPBENEFITS)
    public void testCWMPEliminationPeriodPaymentSeries() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), WORK_STATE.getLabel()), "NJ")
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()),
                        TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(MM_DD_YYYY))
                .adjust(additionalPartiesAdditionalPartyTab.getMetaKey(), new SimpleDataProvider()));
        String claimNumber = getClaimNumber();
        claim.claimOpen().perform();
        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .mask(TestData.makeKeyPath(BenefitStatutorySTDInjuryPartyInformationTab.class.getSimpleName(), EMPLOYMENT_TYPE.getLabel())));

        LOGGER.info("Step 3");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);
        claim.createPaymentSeries().perform(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(),
                        EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().plusDays(21).format(MM_DD_YYYY)));

        Row activePaymentSeries = tableSummaryOfClaimPaymentSeries.getRow(STATUS.getName(), "Active");
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentSeries).hasRows(1);
            softly.assertThat(activePaymentSeries).isPresent();
        });

        activePaymentSeries.getCell(SERIES_NUMBER.getName()).controls.links.getFirst().click();
        String firstPaymentDate = tableScheduledPaymentsOfSeries.getRow(1).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue().split(" ")[0];
        String secondPaymentDate = tableScheduledPaymentsOfSeries.getRow(2).getCell(PAYMENT_SCHEDULE_DATE.getName()).getValue().split(" ")[0];

        LOGGER.info("Step 4");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(LocalDate.parse(firstPaymentDate, DateTimeUtils.MM_DD_YYYY).atStartOfDay().plusDays(1));
        mainApp().open();
        search(claimNumber);
        toSubTab(PAYMENTS);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                    .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Approved");
        });

        claim.viewSingleBenefitCalculation().perform(1);
        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD.getName())).hasValue("7");

        LOGGER.info("Step 5");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(LocalDate.parse(secondPaymentDate, DateTimeUtils.MM_DD_YYYY).atStartOfDay().plusDays(1));
        mainApp().open();
        search(claimNumber);
        toSubTab(PAYMENTS);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);
            softly.assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2)
                    .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Approved");
        });
        claim.viewSingleBenefitCalculation().perform(1);
        assertThat(SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD.getName())).hasValue("0");
    }
}
