package com.exigen.ren.modules.platform.admin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.POLICY;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.QUEUE;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.TASK_NAME;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.linkAllQueues;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerifyManualTaskForPolicy extends BaseTest implements CustomerContext, CaseProfileContext, MyWorkContext, GroupAccidentMasterPolicyContext {

    private final static String PARTICIPANT_ELIGIBILITY_VERIFICATION_SHORT = "Participant Eligibility Verif...";
    private final static String PARTICIPANT_ELIGIBILITY_VERIFICATION_FULL = "Participant Eligibility";
    private final static String PARTICIPANT_CONTACT_INFO_UPDATE = "Participant Contact Info Update";
    private final static String PARTICIPANT_COVERAGE_UPDATE = "Participant Coverage Update";
    private final static String EOI_APPROVAL = "EOI Approval";

    private final static String DESCR_PARTICIPANT_ELIGIBILITY_VERIFICATION = "Name of Caller (include title if calling from a company, broker or provider):\n\nName of Company, Broker, or Provider office:\n\nGroup #:\n\nMember’s Name:\n\nMember’s ID#:\n\nCaller’s contact information\n(phone number and email address:\n\nQuestion/Issue:";
    private final static String DESCR_PARTICIPANT_CONTACT_INFO_UPDATE = "Name of Caller (include title if calling from a company, broker or provider):\n\nName of Company, Broker, or Provider office:\n\nGroup #:\n\nMember’s Name:\n\nMember’s ID#:\n\nCaller’s contact information\n(phone number and email address):\n\nQuestion/Change Requested:";
    private final static String DESCR_PARTICIPANT_COVERAGE_UPDATE = "Name of Caller (include title if calling from a company, broker or provider):\n\nName of Company, Broker, or Provider office:\n\nGroup #:\n\nMember’s Name:\n\nMember’s ID#\n\nCaller’s contact information\n(phone number and email address\n\nQuestion/Change Requested:";
    private final static String DESCR_EOI_APPROVAL = "Customer Name:";

    private final static String REN_ADMIN_QUEUE = "Ren Admin";
    private final static String UW_QUEUE = "UW Queue";
    private final static String STATUS_ACTIVE = "Active";

    private String policyNumber;

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37228", component = Platform_Admin)
    public void testVerifyFourManualTaskForPolicy() {
        LOGGER.info("REN-37228 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("REN-37228 Step 2 to 3 Check Task Name 'Participant Eligibility Verification'");
        mainScenario(POLICY.get(), PARTICIPANT_ELIGIBILITY_VERIFICATION_FULL, DESCR_PARTICIPANT_ELIGIBILITY_VERIFICATION, "1", REN_ADMIN_QUEUE, true);

        LOGGER.info("REN-37228 Step 4 to 5 Check Task Name 'Participant Contact Info Update'");
        mainScenario(POLICY.get(), PARTICIPANT_CONTACT_INFO_UPDATE, DESCR_PARTICIPANT_CONTACT_INFO_UPDATE, "5", REN_ADMIN_QUEUE, true);

        LOGGER.info("REN-37228 Step 6 to 7 Check Task Name 'Participant Coverage Update'");
        mainScenario(POLICY.get(), PARTICIPANT_COVERAGE_UPDATE, DESCR_PARTICIPANT_COVERAGE_UPDATE, "4", REN_ADMIN_QUEUE, true);

        LOGGER.info("REN-37228 Step 8 to 9 Check Task Name 'EOI Approval'");
        mainScenario(POLICY.get(), EOI_APPROVAL, DESCR_EOI_APPROVAL, "1", UW_QUEUE, false);
    }

    private void mainScenario(String type, String taskName, String taskDescription, String priority, String queue, Boolean isDueDateFilled) {
        myWork.navigate();
        myWork.createTask().start();
        Tab createTaskActionTab = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TYPE).setValue(type);
        LOGGER.info("REN-37228 Step 1 Check all new task names present in 'Task Name' field");

        CustomAssertions.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME)).containsAllOptions(PARTICIPANT_ELIGIBILITY_VERIFICATION_SHORT, PARTICIPANT_CONTACT_INFO_UPDATE, PARTICIPANT_COVERAGE_UPDATE, EOI_APPROVAL);

        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME).setValueContains(taskName);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.REFERENCE_ID).setValue(policyNumber);

        assertSoftly(softly -> {
            if (isDueDateFilled) {
                softly.assertThat(LocalDateTime.parse(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).getValue(), MM_DD_YYYY_H_MM_A))
                        .isEqualTo(calculateDueDateTime(DateTimeUtils.getCurrentDateTime(), 2));
            } else {
                softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).getValue()).isEmpty();
                createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).setValue(calculateDueDateTime(DateTimeUtils.getCurrentDateTime(), 2).format(MM_DD_YYYY_H_MM_A));
            }
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.PRIORITY)).hasValue(priority);
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.WARNING_DATE_TIME).getValue()).isEmpty();

            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.QUEUE)).hasValue(queue);
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DESCRIPTION)).hasValue(taskDescription);
        });

        myWork.createTask().submit();
        linkAllQueues.click();
        myWork.filterTask().perform(policyNumber, STATUS_ACTIVE, taskName);
        tableTasks.getRowContains(ImmutableMap.of(TASK_NAME.getName(), taskName, QUEUE.getName(), queue)).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        assertSoftly(softly -> {
            softly.assertThat(MyWorkSummaryPage.buttonAssign).isEnabled();
            softly.assertThat(MyWorkSummaryPage.buttonComplete).isEnabled();
        });
    }
}
