/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.scheduler;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.general.scheduler.actions.CreateJobAction;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;

public class Job implements IJob {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/general/job");

    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CreateJobAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        navigate();
        initiate();
        getDefaultWorkspace().fill(td);
    }

    public void update(TestData td, String updatingJobName) {
        navigate();
        GeneralSchedulerPage.tableScheduledJobs.getRow(AdminConstants.AdminJobTable.JOB.getName(), updatingJobName).getCell(1).controls.links.getFirst().click();
        getDefaultWorkspace().fill(td);
    }

    @Override
    public void navigate() {
        GeneralSchedulerPage.open();
    }


    @Override
    public void initiate() {
        GeneralSchedulerPage.buttonAddJob.click();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
