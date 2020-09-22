/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren;

import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UIJobListener extends BaseJobListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIJobListener.class);

    @Override
    public void beforeJobCreation(JobGroup jobGroup) {}

    @Override
    public void afterJobCreation(JobGroup jobGroup) {
        GeneralSchedulerPage.waitForJobIsCreated(jobGroup);
        if (!GeneralSchedulerPage.isJobExist(jobGroup)) {
            throw new IstfException(String.format("Job %s hasn't been created or couldn't be found on UI", jobGroup.getGroupName()));
        }
        LOGGER.info("[JOBS] Job {} was created", jobGroup.getGroupName());
    }

    @Override
    public void beforeJobExecution(JobGroup jobGroup) {
        super.beforeJobExecution(jobGroup);
        NavigationPage.toLeftMenuTab(NavigationEnum.GeneralTabLeftMenu.SCHEDULER);
        GeneralSchedulerPage.verifyScheduledJobsPresence();
    }

    @Override
    public void afterJobExecution(JobGroup jobGroup) {

    }
}
