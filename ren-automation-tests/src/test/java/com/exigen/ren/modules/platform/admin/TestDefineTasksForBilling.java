package com.exigen.ren.modules.platform.admin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.BILLING;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CLAIM;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.QUEUE;
import static com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData.MY_WORK_TASKS;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDefineTasksForBilling extends ClaimGroupBenefitsACBaseTest implements MyWorkContext{

    private final static String CHECK_VOID_REISSUE_OTHER = "Check Void/Reissue - Other";
    private final static String DENTAL_CHECK_REISSUE = "Dental-Check Reissue";
    private final static String CHECK_VOID_REISSUE_NON_DENTAL_CLAIMS = "Check Void/Reissue – Non-Dent...";
    private final static String TASK_BILLING_CHECK_VOID_REISSUE_OTHER = "Caller Name:\nCaller Contact Info:\nCheck Date:\nCheck Number:\nCheck Amount:\nGroup ID or Agent #:\nPayee:\nPayment address:\nReason:";
    private final static String TASK_CLAIM_DENTAL_CHECK_REISSUE = "Caller Name:\nCaller Contract Info:\nClaim number:\nCheck number:\nProvider Tax ID#:\nMember’s name:\nMember ID:\nCheck for member or provider:\nHas dentist advised networks of any needed updates?\nPayee:\nService address:\nPayment address:\nReason:";
    private final static String TASK_CLAIM_NON_DENTAL_CHECK_REISSUE = "Caller Name:\nCaller Contact Info:\nClaim number:\nCheck Date:\nCheck Number:\nCheck Amount:\nGroup or Member’s name:\nGroup or Member ID:\nPayee:\nService address:\nPayment address:\nReason:";

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39507", component = Platform_Admin)
    public void testDefineTasksForBilling() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        String ba = getBillingAccountNumber(PolicySummaryPage.labelPolicyNumber.getValue());

        createDefaultGroupAccidentClaimForMasterPolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST: Step 1");
        myWork.navigate();
        myWork.createTask().start();
        Tab createTaskActionTab = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TYPE).setValue(BILLING.get());

        assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME)).containsAllOptions(CHECK_VOID_REISSUE_OTHER);

        LOGGER.info("TEST: Step 2");
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TYPE).setValue(CLAIM.get());

        assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME)).containsAllOptions(DENTAL_CHECK_REISSUE, CHECK_VOID_REISSUE_NON_DENTAL_CLAIMS);

        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST: Step 3, 4");
        commonSteps(BILLING.get(), ba, CHECK_VOID_REISSUE_OTHER, TASK_BILLING_CHECK_VOID_REISSUE_OTHER, "Accounting");

        LOGGER.info("TEST: Step 5, 6");
        commonSteps(CLAIM.get(), claimNumber, DENTAL_CHECK_REISSUE, TASK_CLAIM_DENTAL_CHECK_REISSUE, "Dental Claims");

        LOGGER.info("TEST: Step 7, 8");
        commonSteps(CLAIM.get(), claimNumber, CHECK_VOID_REISSUE_NON_DENTAL_CLAIMS, TASK_CLAIM_NON_DENTAL_CHECK_REISSUE, "Accounting");
    }

    private void commonSteps(String type, String referenceId, String taskName, String taskDescription, String queue) {
        Tab createTaskActionTab = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class);

        myWork.createTask().start();
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TYPE).setValue(type);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME).setValue(taskName);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.REFERENCE_ID).setValue(referenceId);

        assertSoftly(softly -> {
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.PRIORITY)).hasValue("5");
            LocalDateTime warningDateTime = LocalDateTime.parse(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.WARNING_DATE_TIME).getValue(), MM_DD_YYYY_H_MM_A);
            softly.assertThat(warningDateTime.toLocalDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).toLocalDate());
            softly.assertThat(LocalDateTime.parse(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).getValue(), MM_DD_YYYY_H_MM_A))
                    .isEqualTo(calculateDueDateTime(warningDateTime, 10));
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.QUEUE)).hasValue(queue);
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DESCRIPTION)).hasValue(taskDescription);
        });

        LOGGER.info("TEST: Step 8");
        myWork.createTask().submit();

        myWork.filterTask().perform(referenceId);
        MyWorkSummaryPage.openAllQueuesSection();
        myWorkTab.getAssetList().getAsset(MY_WORK_TASKS).getAsset(MyWorkTabMetaData.MyWorkTasksMetaData.QUEUE).setValue(queue);

        tableTasks.getRow(ImmutableMap.of(QUEUE.getName(), queue)).getCell(1).controls.checkBoxes.getFirst().setValue(true);

        assertSoftly(softly -> {
            softly.assertThat(MyWorkSummaryPage.buttonAssign).isEnabled();
            softly.assertThat(MyWorkSummaryPage.buttonComplete).isEnabled();
        });
    }
}