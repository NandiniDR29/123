package com.exigen.ren.modules.claim.gb_ac.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.ApprovalPeriodsActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitPeriodActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ApprovalPeriodsActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitPeriodActionTab;
import com.exigen.ren.main.modules.mywork.tabs.MyWorkTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimTAAvailableBenefits.PREMIUM_WAIVER;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkStatus.ACTIVE;
import static com.exigen.ren.main.modules.mywork.MyWorkContext.myWork;
import static com.exigen.ren.main.modules.mywork.tabs.MyWorkTab.MyWorkTasks.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_END_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.SingleBenefitCalculation.SINGLE_BENEFIT_ID;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.UpcomingPremiumWaiverApprovalPeriods.STATUS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPremiumWaiverWorkflowNotifToBilling extends ClaimGroupBenefitsACBaseTest {

    private static final String CLAIM_PWA_REQUEST_TASK = "Claim Premium Waiver Approval Request";
    private static final String CLAIM_PWA_PERIOD_CREATION_REQUEST_TASK = "Claim Premium Waiver Approval Period Creation Request";
    private static final String CLAIM_PWB_PERIOD_COMPLETION_NOTIFICATION_TASK = "Claim Premium Waiver Benefit Period Completion Notification";
    private static final String CLAIM_PREMIUM_WAIVER_FOLLOWUP = "Claim Premium Waiver Followup";
    private static final String CLAIM_PREMIUM_WAIVER_EXPIRES = "Claim Premium Waiver Expires";
    private static final String CLAIM_PREMIUM_WAIVER_TERMINATION_TERMINATION = "Claim Premium Waiver Termination";

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-21514", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPremiumWaiverWorkflowNotifToBilling_TC01() {

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData());

        LocalDateTime bPStartDate = claimPreparation("To Be Determined");
        String claimNumber1 = ClaimSummaryPage.getClaimNumber();

        claimPreparation("Approved");
        String claimNumber2 = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Test: Step 1");
        myWork.navigate();
        myWork.filterTask().perform(claimNumber1, ACTIVE, CLAIM_PWA_REQUEST_TASK);

        assertThat(MyWorkTab.tableMyWorkTasks.findRow(TASK_NAME.getName(), CLAIM_PWA_REQUEST_TASK)).exists();

        MyWorkTab.tableMyWorkTasks.getRowContains(TASK_NAME.getName(), CLAIM_PWA_REQUEST_TASK).getCell(REFERENCE_ID.getName()).click();
        claim.viewSingleBenefitCalculation().start(1);

        assertThat(tableUpcomingPremiumWaiverApprovalPeriods.getRow(1).getCell(STATUS.getName())).hasValue("To Be Determined");

        LOGGER.info("Test: Step 3");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(bPStartDate.plusDays(1));
        mainApp().reopen();

        myWork.navigate();
        myWork.filterTask().perform(claimNumber2, ACTIVE, CLAIM_PWA_PERIOD_CREATION_REQUEST_TASK);
        MyWorkSummaryPage.openAllQueuesSection();

        assertThat(MyWorkTab.tableMyWorkTasks.findRow(TASK_NAME.getName(), CLAIM_PWA_PERIOD_CREATION_REQUEST_TASK)).exists();

        MyWorkTab.tableMyWorkTasks.getRowContains(TASK_NAME.getName(), CLAIM_PWA_PERIOD_CREATION_REQUEST_TASK).getCell(REFERENCE_ID.getName()).click();
        claim.viewSingleBenefitCalculation().start(1);

        assertThat(tableBenefitPeriod.getRow(1).getCell(BenefitPeriod.STATUS.getName())).hasValue("In Progress");

        LOGGER.info("Test: Step 2");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(bPStartDate.plusMonths(3));
        mainApp().reopen();

        myWork.navigate();
        myWork.filterTask().perform(claimNumber1, ACTIVE, CLAIM_PWB_PERIOD_COMPLETION_NOTIFICATION_TASK);
        MyWorkSummaryPage.openAllQueuesSection();

        assertThat(MyWorkTab.tableMyWorkTasks.findRow(TASK_NAME.getName(), CLAIM_PWB_PERIOD_COMPLETION_NOTIFICATION_TASK)).exists();

        MyWorkTab.tableMyWorkTasks.getRowContains(TASK_NAME.getName(), CLAIM_PWB_PERIOD_COMPLETION_NOTIFICATION_TASK).getCell(REFERENCE_ID.getName()).click();
        claim.viewSingleBenefitCalculation().start(1);

        assertSoftly(softly -> {
            softly.assertThat(tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD_END_DATE.getName())).hasValue(bPStartDate.plusMonths(3).minusDays(1).format(MM_DD_YYYY));
            softly.assertThat(tableBenefitPeriod.getRow(1).getCell(BenefitPeriod.STATUS.getName())).hasValue("Completed");
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-21515", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPremiumWaiverWorkflowNotifToBilling_TC02() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData());

        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_With_PremiumWaiverBenefit"));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), PREMIUM_WAIVER);

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bPStartDate = accHealthClaim.getBenefitPeriodStartDate(1);
        String singleBenefitId = tableSingleBenefitCalculation.getRow(1).getCell(SINGLE_BENEFIT_ID.getName()).getValue();

        TestData benefitPeriodTestData = new SimpleDataProvider().
                adjust(TestData.makeKeyPath(BenefitPeriodActionTab.class.getSimpleName()), DataProviderFactory.dataOf(
                        BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_END_DATE.getLabel(), bPStartDate.plusYears(1).minusDays(1).format(MM_DD_YYYY),
                        BenefitPeriodActionTabMetaData.OVERRIDE_REASON.getLabel(), "Reason"));

        claim.updateBenefitPeriodAction().perform(benefitPeriodTestData, 1);

        TestData premiumWaiverApprovalPeriodsTestData = new SimpleDataProvider().
                adjust(TestData.makeKeyPath(ApprovalPeriodsActionTab.class.getSimpleName()), DataProviderFactory.dataOf(
                        ApprovalPeriodsActionTabMetaData.APPROVAL_FREQUENCY.getLabel(), "Manually Specify",
                        ApprovalPeriodsActionTabMetaData.APPROVAL_STATUS.getLabel(), "Approved",
                        ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE.getLabel(), bPStartDate.format(MM_DD_YYYY),
                        ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE.getLabel(), bPStartDate.plusMonths(8).plusDays(12).format(MM_DD_YYYY)));

        claim.premiumWaiverApprovalPeriodsAction().perform(premiumWaiverApprovalPeriodsTestData, 1);
        String claimNumber1 = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Test: Step 2-4");
        commonStepsTC02(
                bPStartDate.plusMonths(7).plusDays(14),
                customerName,
                claimNumber1,
                CLAIM_PREMIUM_WAIVER_FOLLOWUP,
                singleBenefitId);

        LOGGER.info("Test: Steps 5-7");
        commonStepsTC02(
                bPStartDate.plusMonths(8).plusDays(12),
                customerName,
                claimNumber1,
                CLAIM_PREMIUM_WAIVER_EXPIRES,
                singleBenefitId);

        LOGGER.info("Test: Steps 8-10");
        commonStepsTC02(
                bPStartDate.plusMonths(9).plusDays(14),
                customerName,
                claimNumber1,
                CLAIM_PREMIUM_WAIVER_TERMINATION_TERMINATION,
                singleBenefitId);
    }

    private LocalDateTime claimPreparation(String approvalStatus) {
        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_With_PremiumWaiverBenefit"));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), PREMIUM_WAIVER);

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bPStartDate = accHealthClaim.getBenefitPeriodStartDate(1);

        TestData benefitPeriodTestData = new SimpleDataProvider().
                adjust(TestData.makeKeyPath(BenefitPeriodActionTab.class.getSimpleName()), DataProviderFactory.dataOf(
                        BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_END_DATE.getLabel(), bPStartDate.plusMonths(3).minusDays(1).format(MM_DD_YYYY),
                        BenefitPeriodActionTabMetaData.OVERRIDE_REASON.getLabel(), "Reason"));

        claim.updateBenefitPeriodAction().perform(benefitPeriodTestData, 1);

        TestData premiumWaiverApprovalPeriodsTestData = new SimpleDataProvider().
                adjust(TestData.makeKeyPath(ApprovalPeriodsActionTab.class.getSimpleName()), DataProviderFactory.dataOf(
                        ApprovalPeriodsActionTabMetaData.APPROVAL_FREQUENCY.getLabel(), "Manually Specify",
                        ApprovalPeriodsActionTabMetaData.APPROVAL_STATUS.getLabel(), approvalStatus,
                        ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE.getLabel(), bPStartDate.format(MM_DD_YYYY),
                        ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE.getLabel(), bPStartDate.plusDays(8).format(MM_DD_YYYY)));

        claim.premiumWaiverApprovalPeriodsAction().perform(premiumWaiverApprovalPeriodsTestData, 1);

        return bPStartDate;
    }

    private void commonStepsTC02(LocalDateTime nextPhaseDate, String customerName, String claimNumber, String taskName, String singleBenefitId) {
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate);
        mainApp().reopen();

        myWork.navigate();
        myWork.filterTask().perform(claimNumber, ACTIVE, taskName);

        assertThat(MyWorkTab.tableMyWorkTasks.findRow(TASK_NAME.getName(), taskName)).exists();

        assertThat(MyWorkTab.tableMyWorkTasks)
                .with(TASK_NAME, taskName)
                .with(PRIORITY, 3)
                .with(REFERENCE_ID, String.format("%s-%s", claimNumber, singleBenefitId))
                .with(CUSTOMER, customerName)
                .with(AGENCY_LOCATION_NAME, "QA Agency")
                .with(LAST_PERFORMER, "System")
                .with(QUEUE, "Life & DI Claim Management")
                .hasMatchingRows(1);

        MyWorkTab.tableMyWorkTasks.getRowContains(TASK_NAME.getName(), taskName).getCell(REFERENCE_ID.getName()).click();

        assertThat(NavigationPage.isSubTabSelected(NavigationEnum.ClaimTab.ADJUDICATION)).isTrue();
    }
}