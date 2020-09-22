/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.ScheduledUpdateActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.EmployeeInfoTab;
import com.exigen.ren.main.modules.customer.tabs.ScheduledUpdateActionTab;
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.customer.metadata.EmployeeInfoTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerParticipantEmploymentPendingUpdateHistory extends CustomerBaseTest {

    private TestData tdEmployeeInfo = tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData_Employee");
    private static final int V1_COLUMN = 2;
    private static final int V2_COLUMN = 3;

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-24885", component = CRM_CUSTOMER)
    public void testCustomerParticipantEmploymentPendingUpdateHistory() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));

        customerNonIndividual.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Employment"));
        assertThat(CustomerSummaryPage.tableEmploymentCensus).hasRows(1);
        String participantId = CustomerSummaryPage.tableEmploymentCensus.getRow(1).getCell(CustomerConstants.CustomerEmploymentCensusTable.PARTICIPANT_ID).getValue();

        MainPage.QuickSearch.search(participantId);

        LOGGER.info("TEST: Adding pending update for Employee #{}", participantId);
        customerNonIndividual.scheduledUpdate().perform(tdEmployeeInfo);

        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesCompareAll.click();
        ViewHistoryActionTab.linkHistoryPanel.click();
        verifyViewHistoryActionTab(V2_COLUMN);

        mainApp().close();
        LOGGER.info("TEST: Excecuting pendingUpdateJob for Employee #{}", participantId);
        TimeSetterUtil.getInstance().nextPhase(
                LocalDate.parse(tdEmployeeInfo.getValue(ScheduledUpdateActionTab.class.getSimpleName(),
                        ScheduledUpdateActionTabMetaData.UPDATE_EFFECTIVE_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).atStartOfDay());

        adminApp().reopen();
        JobRunner.executeJob(GeneralSchedulerPage.PENDING_UPDATE_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(participantId);

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell(ActivitiesAndUserNotesTable.DESCRIPTION))
                .hasValue(String.format(BamConstants.UPDATE_EMPLOYMENT_INFO, participantId));

        customerNonIndividual.viewHistory().perform(tdCustomerIndividual.getTestData("ViewHistoryAction", "TestData_Past"));
        ViewHistoryActionTab.linkEmployeeInformation.click();
        verifyViewHistoryActionTab(V1_COLUMN);
    }

    private void verifyViewHistoryActionTab(int columnForVerification) {
        assertSoftly(softly -> {
            //check values isn't equals to previous values
            softly.assertThat(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, DEPARTMENT_ID.getLabel()).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, DEPARTMENT_ID.getLabel()).getCell(V2_COLUMN).getValue());
            softly.assertThat(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, DIVISION_ID.getLabel()).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, DIVISION_ID.getLabel()).getCell(V2_COLUMN).getValue());
            softly.assertThat(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, LOCATION_ID.getLabel()).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, LOCATION_ID.getLabel()).getCell(V2_COLUMN).getValue());
            //check values updated to new values
            softly.assertThat(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, DEPARTMENT_ID.getLabel()).getCell(columnForVerification))
                    .hasValue(tdEmployeeInfo.getValue(EmployeeInfoTab.class.getSimpleName(), DEPARTMENT_ID.getLabel()));
            softly.assertThat(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, DIVISION_ID.getLabel()).getCell(columnForVerification))
                    .hasValue(tdEmployeeInfo.getValue(EmployeeInfoTab.class.getSimpleName(), DIVISION_ID.getLabel()));
            softly.assertThat(ViewHistoryActionTab.tableEmployeeInformation.getRow(1, LOCATION_ID.getLabel()).getCell(columnForVerification))
                    .hasValue(tdEmployeeInfo.getValue(EmployeeInfoTab.class.getSimpleName(), LOCATION_ID.getLabel()));
        });
    }
}
