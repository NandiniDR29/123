package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.EfolderConstants;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.module.efolder.EfolderContext;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.metadata.FilterTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.AssignTaskToActionTab;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.modules.mywork.tabs.FilterTaskActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.MyWorkSummaryPage;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderDentalClaimOutCorresp.DENTAL_RECEIPT_OF_CLAIM_LETTERS;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderIndCustDentalClaims.RECEIPT_OF_CLAIM_LETTERS;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithPrivilege;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestIntegrationWithDocGenReviewNewDocument extends ClaimGroupBenefitsDNBaseTest implements MyWorkContext, EfolderContext {

    private final String REVIEW_NEW_DOCUMENT = "Review New Document";
    private final String REVIEW_DENTAL_DOCUMENT = "Review Dental Document";
    private final AbstractContainer<?, ?> createTaskActionAssetList = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class).getAssetList();
    private final DialogSingleSelector assignedToAsset = createTaskActionAssetList.getAsset(CreateTaskActionTabMetaData.ASSIGNED_TO);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36819", component = CLAIMS_GROUPBENEFITS)
    public void testReviewNewDocTaskAssignmentNewQueueAndPrivileges() {
        LOGGER.info("REN-36819 Precondition");
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        createDefaultGroupDentalClaimForCertificatePolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("REN-36819 STEP#2");
        String taskId1 = createTask("ISBA");

        LOGGER.info("REN-36819 STEP#3");
        createUserWithPrivilege(ImmutableList.of("ALL", "EXCLUDE " + Privilege.CONTROL_REVIEW_DENTAL_DOCUMENT_QUEUE.get()));
        CustomAssertions.assertThat(myWorkTab.getAssetList().getAsset(MyWorkTabMetaData.MY_WORK_TASKS).getAsset(MyWorkTabMetaData.MyWorkTasksMetaData.QUEUE))
                .containsOption(REVIEW_DENTAL_DOCUMENT);

        LOGGER.info("REN-36819 STEP#4");
        MyWorkSummaryPage.openAllQueuesSection();
        myWorkTab.getAssetList().getAsset(MyWorkTabMetaData.MY_WORK_TASKS).getAsset(MyWorkTabMetaData.MyWorkTasksMetaData.QUEUE).setValue(REVIEW_DENTAL_DOCUMENT);
        CustomAssertions.assertThat(MyWorkSummaryPage.tableTasks).hasRowsThatContain(TASK_ID.getName(), taskId1);

        LOGGER.info("REN-36819 STEP#5");
        int taskId1Index = MyWorkSummaryPage.tableTasks.getRow(TASK_ID.getName(), taskId1).getIndex();
        MyWorkSummaryPage.expandTask(taskId1Index);
        assertSoftly(softly -> {
            softly.assertThat(MyWorkSummaryPage.getAssignLink(taskId1Index)).isDisabled();
            softly.assertThat(MyWorkSummaryPage.getUpdateLink(taskId1Index)).isDisabled();
            softly.assertThat(MyWorkSummaryPage.getCompleteLink(taskId1Index)).isDisabled();
        });

        LOGGER.info("REN-36819 STEP#6");
        MyWorkSummaryPage.openTaskDetails(taskId1Index);
        assertSoftly(softly -> {
            softly.assertThat(TaskDetailsSummaryPage.buttonAssign).isDisabled();
            softly.assertThat(TaskDetailsSummaryPage.buttonUpdate).isDisabled();
            softly.assertThat(TaskDetailsSummaryPage.buttonUpdate).isDisabled();
        });
        TaskDetailsSummaryPage.buttonCancel.click();

        LOGGER.info("REN-36819 STEP#9-10");
        MainPage.QuickSearch.search(claimNumber);
        String taskId2 = createTask("FName");

        LOGGER.info("REN-36819 STEP#14");
        createUserWithPrivilege(ImmutableList.of("ALL", "EXCLUDE " + Privilege.ACCESS_REVIEW_DENTAL_DOCUMENT_QUEUE.get()));
        CustomAssertions.assertThat(myWorkTab.getAssetList().getAsset(MyWorkTabMetaData.MY_WORK_TASKS).getAsset(MyWorkTabMetaData.MyWorkTasksMetaData.QUEUE))
                .doesNotContainOption(REVIEW_DENTAL_DOCUMENT);

        LOGGER.info("REN-36819 STEP#15");
        myWork.filterTask().start();
        myWork.filterTask().getWorkspace().getTab(FilterTaskActionTab.class).getAssetList().getAsset(FilterTaskActionTabMetaData.TASK_ID).setValue(taskId1);
        MyWorkSummaryPage.openAllQueuesSection();
        myWork.filterTask().submit();
        CustomAssertions.assertThat(MyWorkSummaryPage.tableTasks.getRow(1).getCell(1)).hasValue("No records found.");

        LOGGER.info("REN-36819 STEP#16");
        MainPage.QuickSearch.search(claimNumber);
        CreateTaskActionTab.buttonTasks.click();
        int taskId1Index1 = MyWorkSummaryPage.tableTasks.getRow(TASK_ID.getName(), taskId1).getIndex();
        int taskId1Index2 = MyWorkSummaryPage.tableTasks.getRow(TASK_ID.getName(), taskId2).getIndex();

        ImmutableList.of(taskId1Index1, taskId1Index2).forEach(index ->{
            MyWorkSummaryPage.expandTask(index);
            assertSoftly(softly -> {
                softly.assertThat(MyWorkSummaryPage.getAssignLink(index)).isDisabled();
                softly.assertThat(MyWorkSummaryPage.getUpdateLink(index)).isDisabled();
                softly.assertThat(MyWorkSummaryPage.getCompleteLink(index)).isDisabled();
            });
            Tab.buttonTopRefresh.click();
        });
        MyWorkSummaryPage.openAllQueuesSection();
        CustomAssertions.assertThat(MyWorkSummaryPage.tableTasks.getColumn(TASK_ID.getName())).doesNotHaveValue(ImmutableList.of(taskId1, taskId2));

        LOGGER.info("REN-36819 STEP#17-18");
        myWork.createTask().start();
        createTaskActionAssetList.getAsset(CreateTaskActionTabMetaData.TASK_NAME).setValue(REVIEW_NEW_DOCUMENT);
        assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.BUTTON_OPEN_POPUP).click();
        CustomAssertions.assertThat(assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.QUEUE)).doesNotContainOption(REVIEW_DENTAL_DOCUMENT);
    }

    private String createTask(String lastPerformer) {
        ClaimSummaryPage.buttonCreateTask.click();
        createTaskActionAssetList.getAsset(CreateTaskActionTabMetaData.TASK_NAME).setValue(REVIEW_NEW_DOCUMENT);
        assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.BUTTON_OPEN_POPUP).click();
        CustomAssertions.assertThat(assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.QUEUE)).containsOption(REVIEW_DENTAL_DOCUMENT);
        assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.QUEUE).setValue(REVIEW_DENTAL_DOCUMENT);
        AssignTaskToActionTab.buttonAssign.click();
        createTaskActionAssetList.getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        CreateTaskActionTab.buttonCreate.click();
        CreateTaskActionTab.buttonTasks.click();
        return MyWorkSummaryPage.tableTasks.getRowContains(LAST_PERFORMER.getName(), lastPerformer).getCell(TASK_ID.getName()).getValue();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37225", component = CLAIMS_GROUPBENEFITS)
    public void testReviewNewDocTaskAssignmentCreateTaskAutomatically() {
        LOGGER.info("REN-37225 Precondition");
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("REN-37225 STEP#2");
        createDefaultGroupDentalClaimForCertificatePolicy();

        LOGGER.info("REN-37225 STEP#3");
        Efolder.expandFolder(EfolderConstants.EFolderDentalClaim.OUTBOUND_CORRESPONDENCE.getName());
        efolder.addDocument(efolder.getDefaultTestData(DATA_GATHER).getTestData("TestData_TXT"), DENTAL_RECEIPT_OF_CLAIM_LETTERS.getName());
        CreateTaskActionTab.buttonTasks.click();

        LOGGER.info("REN-37225 STEP#4");
        CustomAssertions.assertThat(MyWorkSummaryPage.tableTasks.getRow(LAST_PERFORMER.getName(), "System"))
                .hasCellWithValue(TASK_NAME.getName(), REVIEW_NEW_DOCUMENT)
                .hasCellWithValue(QUEUE.getName(), REVIEW_DENTAL_DOCUMENT);
    }
}
