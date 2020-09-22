package com.exigen.ren.modules.platform.admin;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.TaskDetailsSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import com.exigen.ren.utils.AdminActionsHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.QUEUE;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.linkAllQueues;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAdjustWorkflowQueues extends ClaimGroupBenefitsTLBaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, MyWorkContext {

    private final String PROVIDER_SERVICES_INQUIRY = "Provider Services Inquiry";
    private final String DENTAL_CLAIM_INQUIRY = "Dental Claim Inquiry";
    private final String LIFE_AND_DI_CLAIM_MANAGEMENT = "Life & DI Claim Management";
    private final String LIFE_AND_DI_CLAIM_APPROVER = "Life & DI Claim Approver";
    private final String DENTAL_CLAIMS = "Dental Claims";
    private final String ACCOUNTING = "Accounting";

    @Test(groups = {PLATFORM, PLATFORM_ADMIN, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39670", component = Platform_Admin)
    public void testQueuesVerification() {
        LOGGER.info("REN-39670 Precondition");
        ImmutableMap<String, User> users = setUpUsers();
        mainApp().reopen();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceClaimForMasterPolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        mainApp().reopen("qa", "qa");

        LOGGER.info("REN-39670 STEP#1-2");
        ImmutableList<String> queues = ImmutableList
                .of(PROVIDER_SERVICES_INQUIRY, DENTAL_CLAIM_INQUIRY, LIFE_AND_DI_CLAIM_MANAGEMENT, LIFE_AND_DI_CLAIM_APPROVER, DENTAL_CLAIMS, ACCOUNTING);
        queues.forEach(queue -> {
            myWork.createTask().start();
            Tab createTaskActionTab = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class);
            createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TYPE).setValue(NavigationEnum.AppMainTabs.CLAIM.get());
            createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME).setValue("Review New Document");
            createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.REFERENCE_ID).setValue(claimNumber);
            createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.DUE_DATE_TIME).setValue(DateTimeUtils.getCurrentDateTime().plusMonths(1).format(MM_DD_YYYY));


            DialogSingleSelector assignedToAsset = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class).getAssetList().getAsset(CreateTaskActionTabMetaData.ASSIGNED_TO);
            assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.BUTTON_OPEN_POPUP).click();
            assertThat(assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.QUEUE)).containsAllOptions(PROVIDER_SERVICES_INQUIRY, DENTAL_CLAIM_INQUIRY, LIFE_AND_DI_CLAIM_MANAGEMENT, LIFE_AND_DI_CLAIM_APPROVER, DENTAL_CLAIMS, ACCOUNTING);
            assignedToAsset.getAsset(CreateTaskActionTabMetaData.AssignTo.QUEUE).setValue(queue);
            myWork.assignTaskTo().submit();
            myWork.createTask().submit();
        });

        LOGGER.info("REN-39670 STEP#3-8");
        users.forEach((queue, user) -> {
            mainApp().reopen(user.getLogin(), user.getPassword());
            myWork.filterTask().perform(claimNumber);
            linkAllQueues.click();
            assertSoftly(softly -> {
                softly.assertThat(tableTasks).hasRows(1);
                softly.assertThat(tableTasks.getRow(1).getCell(QUEUE.getName())).hasValue(queue);
            });
            tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.QUEUE.getName(), queue)
                    .getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID.getName()).controls.links.getFirst().click();
            assertSoftly(softly -> {
                softly.assertThat(TaskDetailsSummaryPage.buttonUpdate).isEnabled();
                softly.assertThat(TaskDetailsSummaryPage.buttonAssign).isEnabled();
                softly.assertThat(TaskDetailsSummaryPage.buttonComplete).isEnabled();
            });
            TaskDetailsSummaryPage.buttonCancel.click();
        });
    }

    private ImmutableMap<String, User> setUpUsers() {
        User userProviderServicesInquiry = AdminActionsHelper.createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_ACCOUNTING_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_ACCOUNTING_QUEUE.get()));

        User userDentalClaimInquiry = AdminActionsHelper.createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_ACCOUNTING_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_ACCOUNTING_QUEUE.get()));

        User userLifeAndDiClaimManagement = AdminActionsHelper.createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_ACCOUNTING_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_ACCOUNTING_QUEUE.get()));

        User userLifeAndDiClaimApprover = AdminActionsHelper.createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_ACCOUNTING_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_ACCOUNTING_QUEUE.get()));

        User userDentalClaims = AdminActionsHelper.createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_ACCOUNTING_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_ACCOUNTING_QUEUE.get()));

        User userAccounting = AdminActionsHelper.createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIM_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_MANAGEMENT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_LIFE_AND_DI_CLAIM_APPROVER_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_DENTAL_CLAIMS_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_PROVIDER_SERVICES_INQUIRY_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_PROVIDER_SERVICES_INQUIRY_QUEUE.get()));

        return new ImmutableMap.Builder<String, User>()
                .put(PROVIDER_SERVICES_INQUIRY, userProviderServicesInquiry)
                .put(DENTAL_CLAIM_INQUIRY, userDentalClaimInquiry)
                .put(LIFE_AND_DI_CLAIM_MANAGEMENT, userLifeAndDiClaimManagement)
                .put(LIFE_AND_DI_CLAIM_APPROVER, userLifeAndDiClaimApprover)
                .put(DENTAL_CLAIMS, userDentalClaims)
                .put(ACCOUNTING, userAccounting)
                .build();
    }
}
