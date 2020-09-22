package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.testng.annotations.Test;

import java.io.File;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.VSP_ELIGIBILITY_JOB;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUploadingMappedVSPExtractToSFTP extends BaseTest implements JobContext {

    private static final String FILE_NAME = "t0054051.dat";
    private static final String FILE_PATH = "/mnt/ren/shared/vsp/outbound";
    private static final File file = new File(FILE_PATH, FILE_NAME);

    private SSHController sftp = SFTPConnection.getClient();

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14958", component = INTEGRATION)
    public void testVerifyVspEligibilityJob() throws JSchException, SftpException {

        deleteFileIfExist();

        JobRunner.executeJob(GeneralSchedulerPage.VSP_ELIGIBILITY_JOB);

        adminApp().open();
        assertThat(GeneralSchedulerPage.isJobExist(VSP_ELIGIBILITY_JOB)).isTrue();
        assertThat(sftp.pathExists(file)).isTrue();
    }

    private void deleteFileIfExist() throws JSchException, SftpException {
        if (sftp.pathExists(file)) {
            sftp.deleteFile(file);
        }
    }
}