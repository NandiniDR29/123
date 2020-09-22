package com.exigen.ren.modules.platform.admin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.QUEUE;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.TASK_NAME;
import static com.exigen.ren.main.modules.mywork.MyWorkContext.myWork;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.linkAllQueues;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerDefineThreeManualTask extends BaseTest implements CustomerContext, MyWorkContext {

    private final static String GROUP_CONTACT_INFO_UPDATE = "Group Contact Info Update";
    private final static String ID_CARD_REQUEST = "ID Card Request";
    private final static String UW_QUESTION = "UW Question";
    private final static String TASK_DESCRIPTION_GROUP_CONTACT = "Name of Caller (include title if calling from a company, broker or provider):\n\nName of Company, Broker, or Provider office:\n\nGroup #:\n\nCaller’s contact information\n(phone number and email address:\n\nQuestion/Change Requested:";
    private final static String TASK_DESCRIPTION_ID_CARD_REQUEST = "Name of Caller (include title if calling from a company, broker or provider):\n\nName of Company, Broker, or Provider office:\n\nGroup #:\n\nMember’s Name:\n\nMember’s ID#:\n\nCaller’s contact information\n(phone number and email address):\n\nQuestion/Issue:";
    private final static String TASK_DESCRIPTION_UW_QUESTION = "Customer Name:\nCase:\nQuote/Policy:\nQuestion/Comment:";
    private final static String REN_ADMIN_QUEUE = "Ren Admin";
    private final static String UW_QUEUE = "UW Queue";
    private String customerNumber;

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37251", component = Platform_Admin)
    public void testDefineThreeManualTask() {
        LOGGER.info("REN-37251 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("REN-37251 Step 1 to 3 Check Task Name 'Group Contact Info Update'");
        mainScenario(GROUP_CONTACT_INFO_UPDATE, TASK_DESCRIPTION_GROUP_CONTACT, "5", REN_ADMIN_QUEUE);

        LOGGER.info("REN-37251 Step 4 to 5 Check Task Name 'ID Card Request'");
        mainScenario(ID_CARD_REQUEST, TASK_DESCRIPTION_ID_CARD_REQUEST, "5", REN_ADMIN_QUEUE);

        LOGGER.info("REN-37251 Step 6 to 7 Check Task Name 'UW Question'");
        mainScenario(UW_QUESTION, TASK_DESCRIPTION_UW_QUESTION, "3", UW_QUEUE);
    }

    private void mainScenario(String taskName, String taskDescription, String priority, String queue) {
        myWork.navigate();
        myWork.createTask().start();
        Tab createTaskActionTab = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TYPE).setValue(CUSTOMER.get());
            CustomAssertions.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME)).containsAllOptions(GROUP_CONTACT_INFO_UPDATE, ID_CARD_REQUEST, UW_QUESTION);

        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME).setValue(taskName);
        createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.REFERENCE_ID).setValue(customerNumber);

        assertSoftly(softly -> {
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.PRIORITY)).hasValue(priority);
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.WARNING_DATE_TIME).getValue()).isEmpty();
            softly.assertThat(LocalDateTime.parse(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).getValue(), MM_DD_YYYY_H_MM_A))
                    .isEqualTo(calculateDueDateTime(DateTimeUtils.getCurrentDateTime(), 2));
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.QUEUE)).hasValue(queue);
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DESCRIPTION)).hasValue(taskDescription);
        });

        myWork.createTask().submit();
        linkAllQueues.click();
        myWork.filterTask().perform(customerNumber);
        tableTasks.getRow(ImmutableMap.of(TASK_NAME.getName(), taskName, QUEUE.getName(), queue)).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        assertSoftly(softly -> {
            softly.assertThat(MyWorkSummaryPage.buttonAssign).isEnabled();
            softly.assertThat(MyWorkSummaryPage.buttonComplete).isEnabled();
        });
    }


}
