package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;

import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.INTEGRATION;

public class TestVSPClaimFileUpload extends BaseTest implements JobContext {

    private static final String SQL_QUERY_COUNT = "Select COUNT(*) from VSP_CLAIMS_UPLOAD_DATA";
    private static final String SQL_QUERY_CLIENTID = "Select COUNT(*) from VSP_CLAIMS_UPLOAD_DATA where CLIENTID not like '%8005112%'";
    private static final String DEST_FILE_PATH = "/mnt/ren/shared/vspclaimupload/import";
    private static final String ARCHIVED_FILE_PATH = "/mnt/ren/archive/vspclaimupload/archive";
    private static final String TEMPLATE_FILE_PATH = "src/test/resources/upload/CLMDTL_8005112_xxxxx_YYYYMMDD.xlsb";

    private SSHController sftp = SFTPConnection.getClient();

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28234, REN-25537", component = INTEGRATION)
    public void testVerifyVspEligibilityJob() throws SftpException, JSchException, IOException {

        int numberOfRecords = Integer.parseInt(DBService.get().getValue(SQL_QUERY_COUNT).orElse("0"));

        File templateFile = new File(TEMPLATE_FILE_PATH);
        File uploadedFile = new File(templateFile.getParentFile(), templateFile.getName().replace("xxxxx", RandomStringUtils.randomNumeric(5))
                .replace("YYYYMMDD", TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtilsHelper.YYYYMMDD)));
        FileUtils.copyFile(templateFile, uploadedFile);
        sftp.uploadFile(uploadedFile, new File(DEST_FILE_PATH));

        JobRunner.executeJob(GeneralSchedulerPage.VSP_CLAIMS_UPLOAD_JOB);

        assertThat(sftp.pathExists(new File(ARCHIVED_FILE_PATH, uploadedFile.getName()))).as("Processed file was archived on server").isTrue();
        assertThat(Integer.parseInt(DBService.get().getValue(SQL_QUERY_COUNT).orElse("0"))).as("Records from file were added to DB").isEqualTo(numberOfRecords + 27);
        assertThat(DBService.get().getValue(SQL_QUERY_CLIENTID).orElse("Error")).as("Batch Job does not process records other than containing '8005112'").isEqualTo("0");
    }
}
