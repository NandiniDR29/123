package com.exigen.ren.modules.externalInterfaces.fee;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.DD_MM_YYYY_H_MM_SS;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestValidateIncrementalFeeDataChanges extends BaseTest implements JobContext {

    private static final String FILE_NAME ="Ren_Fee_History_As_Of_2021_%s.txt";
    private static final String FILE_PATH = "/mnt/ren/shared/fee-schedule/inbound";
    private static final String FILE_PATH_ARCHIVE = "/mnt/ren/shared/fee-schedule/Archive";

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-21503", component = INTEGRATION)
    public void testValidateIncrementalFeeDataChanges() throws JSchException, SftpException {

        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();
        Map<String, String> dbRow = DBService.get().getRow("SELECT * FROM dbo.FEE_TABLE WHERE PROCEDURE_CODE = 'D2331'");
        LocalDateTime newStartDate =  currentTime.plusDays(1);

        String fileText = String.format("NAME\tFEE_ID\tFEE_TABLE_ID\tIN_FILE_LOG_ID\tPENDING_IND\tNO_MAX_AMT_IND\tSTART_DATE\tAMOUNT\tPROCEDURE_CODE\tUSER_NAME\tAPPL\n" +
                "%s\t%s\t%s\t\t0\t0\t%s\t%s\t%s\t%s\t%s", dbRow.get("NAME"), dbRow.get("FEE_ID"), dbRow.get("FEE_TABLE_ID"),
                newStartDate.format(DD_MM_YYYY_H_MM_SS).toUpperCase(), dbRow.get("AMOUNT"), dbRow.get("PROCEDURE_CODE"), dbRow.get("USER_NAME"), dbRow.get("APPL") );

        //Random timestamp to avoid similar filenames
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime().minusHours(new Random().nextInt(5)+1).plusMinutes(new Random().nextInt(59)).plusSeconds(new Random().nextInt(59));
        String currentDateString = currentDate.format(DateTimeUtils.TIME_STAMP);
        //No seconds inside file needed
        String dateForText = currentDateString.substring(0 , currentDateString.length()-2);
        File newFileName = new File(String.format(FILE_NAME, currentDateString));
        FileHelper.addTextToFile(newFileName, String.format(fileText, dateForText));
        SFTPConnection.getClient().uploadFile(newFileName, new File(FILE_PATH));

        LOGGER.info("---=={Step 1-3}==---");
        JobRunner.executeJob(GeneralSchedulerPage.FEE_SCHEDULE_UPLOAD_JOB);
        JobRunner.executeJob(GeneralSchedulerPage.FEE_SCHEDULE_PROCESSED_JOB);

        LOGGER.info("---=={Step 4}==---");
        Map<String, String> dbRow2 = DBService.get().getRow(String.format("select count(a.id) from\n" +
                "RECORDLEVEL_STATUS a, FILELEVEL_STATUS b\n" +
                "where a.fileId = b.id\n" +
                "and a.statusCd in ('SUCCESS', 'FAILED')\n" +
                "and fileName = '%s'\n" +
                "group by a.statusCd", newFileName));

        assertThat(dbRow2).hasSize(1);

        LOGGER.info("---=={Step 5}==---");
        Map<String, String> dbRow3 = DBService.get().getRow(String.format("SELECT distinct(NAME), FEE_TABLE_ID,START_DATE \n" +
                "FROM dbo.FEE_TABLE\n" +
                "WHERE [START_DATE] = (SELECT max([START_DATE])\n" +
                "FROM dbo.FEE_TABLE WHERE FEE_TABLE_ID = '%s')\n" +
                "and FEE_TABLE_ID = '%s'", dbRow.get("FEE_TABLE_ID"), dbRow.get("FEE_TABLE_ID")));

        assertThat(dbRow3.get("NAME")).isEqualTo(dbRow.get("NAME"));
        assertThat(dbRow3.get("FEE_TABLE_ID")).isEqualTo(dbRow.get("FEE_TABLE_ID"));
        assertThat(dbRow3.get("START_DATE")).isEqualTo(newStartDate.format(YYYY_MM_DD));


        LOGGER.info("---=={Step 6}==---");
        SFTPConnection.getClient().getFilesList(new File(FILE_PATH_ARCHIVE)).stream().filter(f -> f.contains(newFileName.getName())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", newFileName.getName())));

        LOGGER.info("---=={Step 7}==---");
        assertThat(SFTPConnection.getClient().getFilesList(new File(FILE_PATH))).hasSize(0);
    }



}