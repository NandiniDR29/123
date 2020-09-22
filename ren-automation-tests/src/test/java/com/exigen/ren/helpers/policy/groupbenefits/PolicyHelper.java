package com.exigen.ren.helpers.policy.groupbenefits;

import com.exigen.ipb.eisa.base.application.ApplicationFactory;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.TimeUnit;

public class PolicyHelper {

    public static void executeCascadingTransactionJobs(String masterPolicyNum) {

        ApplicationFactory.getInstance().getMainApplication().close();

        JobRunner.executeJob(GeneralSchedulerPage.CASCADING_TRANSACTION_PROCESSING_INITIATION_JOB);
        JobRunner.executeJob(GeneralSchedulerPage.CASCADING_TRANSACTION_CERTIFICATE_PROCESSING_DISPATCHING_JOB, 30000);
        JobRunner.executeJob(GeneralSchedulerPage.CASCADING_TRANSACTION_COMPLETION_DETECTION_JOB, 30000);

        ApplicationFactory.getInstance().getMainApplication().reopen();
        MainPage.QuickSearch.search(masterPolicyNum);

        RetryService.run(predicate -> !PolicySummaryPage.labelCascadingTransaction.isPresent(),
                () -> {
                    MainPage.QuickSearch.search(masterPolicyNum);
                    return null;
                }, StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
    }

}
