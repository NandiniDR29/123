package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.controls.ClickComboBox;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.PRIORITY;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.TASK_NAME;
import static com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData.QUEUE;
import static com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData.REFERENCE_ID;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateCertificatePolicyTask extends ClaimGroupBenefitsLTDBaseTest implements MyWorkContext {
    private static final String UPDATE_CERTIFICATE_POLICY = "Update Certificate Policy";
    private static final String QUEUE_NAME = "Ren Admin";


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23605", component = CLAIMS_GROUPBENEFITS)
    public void testCWCPUpdateCertificatePolicyTask() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_LTD);
        navigateToCustomer();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        createDefaultLongTermDisabilityClaimForCertificatePolicy();

        LOGGER.info("Step 1");
        claim.claimOpen().perform();
        myWork.createTask().start();

        LOGGER.info("Step 2");
        Tab createTaskActionTab = myWork.createTask().getWorkspace().getTab(CreateTaskActionTab.class);
        ClickComboBox taskName = createTaskActionTab.getAssetList().getAsset(CreateTaskActionTabMetaData.TASK_NAME);
        assertSoftly(softly -> {
            softly.assertThat(taskName).containsOption(UPDATE_CERTIFICATE_POLICY);
            taskName.setValue(UPDATE_CERTIFICATE_POLICY);
            softly.assertThat(createTaskActionTab.getAssetList().getAsset(QUEUE)).hasValue(QUEUE_NAME);
        });
        String referenceId = createTaskActionTab.getAssetList().getAsset(REFERENCE_ID).getValue();
        myWork.createTask().submit();

        LOGGER.info("Step 3");
        myWork.navigate();
        myWork.filterTask().perform(referenceId, "Active", UPDATE_CERTIFICATE_POLICY);

        assertSoftly(softly -> {
            softly.assertThat(tableTasks).hasRows(1);
            Row row = tableTasks.getRow(1);
            softly.assertThat(row.getCell(TASK_NAME.getName())).hasValue(UPDATE_CERTIFICATE_POLICY);
            softly.assertThat(row.getCell(PRIORITY.getName())).hasValue("1");
            softly.assertThat(row.getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID.getName())).hasValue(referenceId);
            softly.assertThat(row.getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID.getName()).controls.links.getFirst()).isNotNull();
            softly.assertThat(row.getCell(MyWorkConstants.MyWorkTasksTable.CUSTOMER.getName())).hasValue(customerName);
            softly.assertThat(row.getCell(MyWorkConstants.MyWorkTasksTable.CUSTOMER.getName()).controls.links.getFirst()).isNotNull();
            softly.assertThat(row.getCell(MyWorkConstants.MyWorkTasksTable.QUEUE.getName())).hasValue(QUEUE_NAME);
            softly.assertThat(row.getCell(MyWorkConstants.MyWorkTasksTable.LAST_PERFORMER.getName())).hasValue("ISBA ISBA");
        });
    }
}
