/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.ScheduledUpdateActionTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerSchedulePendingUpdate extends CustomerBaseTest implements CustomerContext {

    private TestData tdCustomer = tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData");

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-24915", component = CRM_CUSTOMER)
    public void testCustomerSchedulePendingUpdate() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        customerIndividual.scheduledUpdate().perform(tdCustomer);

        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isPresent();

        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(LocalDate.parse(tdCustomer.getValue(scheduledUpdateActionTab.getMetaKey(),
                ScheduledUpdateActionTabMetaData.UPDATE_EFFECTIVE_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).atStartOfDay());

        adminApp().open();
        JobRunner.executeJob(GeneralSchedulerPage.PENDING_UPDATE_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(customerNumber);

        assertThat(CustomerSummaryPage.labelCustomerName).hasValue(tdCustomer.getValue(generalTab.getMetaKey(),
                GeneralTabMetaData.FIRST_NAME.getLabel()) + " " + tdCustomer.getValue(generalTab.getMetaKey(),
                GeneralTabMetaData.LAST_NAME.getLabel()));

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", String.format(BamConstants.UPDATE_PENDING, customerNumber));
    }
}
