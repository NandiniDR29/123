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
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.ScheduledUpdateActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.ScheduledUpdateActionTab;
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerParticipantMembershipPendingUpdateHistory extends CustomerBaseTest {

    private TestData tdMembershipInfo = tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData_Membership");

    private String membershipStatus = GeneralTabMetaData.MEMBERSHIP_STATUS.getLabel();
    private String membershipId = GeneralTabMetaData.MEMBERSHIP_ID.getLabel();
    private static final int V1_COLUMN = 2;
    private static final int V2_COLUMN = 3;

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-24893", component = CRM_CUSTOMER)
    public void testCustomerParticipantMembershipPendingUpdateHistory() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));

        customerNonIndividual.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Membership"));
        assertThat(CustomerSummaryPage.tableMembershipCensus).hasRows(1);
        String participantId = CustomerSummaryPage.tableMembershipCensus.getRow(1).getCell(CustomerConstants.CustomerMembershipCensusTable.PARTICIPANT_ID).getValue();

        MainPage.QuickSearch.search(participantId);

        LOGGER.info("TEST: Adding pending update for Membership #{}", participantId);
        customerNonIndividual.scheduledUpdate().perform(tdMembershipInfo);
        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesCompareAll.click();
        ViewHistoryActionTab.linkHistoryPanel.click();

        assertSoftly((softly) -> {
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipStatus).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipStatus).getCell(V2_COLUMN).getValue());
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipId).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipId).getCell(V2_COLUMN).getValue());
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipStatus).getCell(V2_COLUMN))
                    .hasValue(tdMembershipInfo.getValue(GeneralTab.class.getSimpleName(), membershipStatus));
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipId).getCell(V2_COLUMN))
                    .hasValue(tdMembershipInfo.getValue(GeneralTab.class.getSimpleName(), membershipId));
        });
        mainApp().close();

        LOGGER.info("TEST: Excecuting pendingUpdateJob for Membership #{}", participantId);
        TimeSetterUtil.getInstance().nextPhase(
                LocalDate.parse(tdMembershipInfo.getValue(ScheduledUpdateActionTab.class.getSimpleName(),
                        ScheduledUpdateActionTabMetaData.UPDATE_EFFECTIVE_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).atStartOfDay());

        adminApp().reopen();
        JobRunner.executeJob(GeneralSchedulerPage.PENDING_UPDATE_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(participantId);

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell(ActivitiesAndUserNotesTable.DESCRIPTION))
                .hasValue(String.format(BamConstants.UPDATE_MEMBERSHIP_INFO, participantId));

        customerNonIndividual.viewHistory().perform(tdCustomerIndividual.getTestData("ViewHistoryAction", "TestData_Past"));
        ViewHistoryActionTab.linkMembershipInformation.click();

        assertSoftly((softly) -> {
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipStatus).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipStatus).getCell(V2_COLUMN).getValue());
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipId).getCell(V1_COLUMN).getValue())
                    .isNotEqualTo(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipId).getCell(V2_COLUMN).getValue());
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipStatus).getCell(V1_COLUMN))
                    .hasValue(tdMembershipInfo.getValue(GeneralTab.class.getSimpleName(), membershipStatus));
            softly.assertThat(ViewHistoryActionTab.tableMembershipInformation.getRow(1, membershipId).getCell(V1_COLUMN))
                    .hasValue(tdMembershipInfo.getValue(GeneralTab.class.getSimpleName(), membershipId));

        });

    }

}
