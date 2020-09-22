package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerateProcessingSummaryForLockbox extends BaseTest implements JobContext, CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private static final String FILE_NAME ="BOA_DATA_REN_%s.txt";
    private static final String FILE_NAME_1 = "BOA_DATA_REN_%s";
    private static final String FILE_PATH = "/mnt/ren/shared/lockbox/inbound";
    private static final String FILE_PATH_LOCKBOX = "/mnt/ren/archive/lockbox/post_processing_report";

    //TODO: need add TC2 with mail SMTP
    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-32680"}, component = INTEGRATION)
    public void testVerifyVspEligibilityJob() throws JSchException, SftpException, IOException {
        String fileText = "100RENLockbox11100001%s                                               \n" +
                "2RENLockbox1110000120000000000040008000801                                      \n" +
                "50010000007569200228RENLockbox1110000120                                        \n" +
                "6001001000002600001100013886973400210000418002012020                            \n" +
                "40010016019                    80009                                            \n" +
                "6001002000003350001100013886973400210000417802012020                            \n" +
                "6001003000003000001100013886973400210000417302012020                            \n" +
                "4001003601                     80009                                            \n" +
                "40010036029                    80009                                            \n" +
                "6001004000003350001100013886973400210000417402012020                            \n" +
                "40010046010                    80011                                            \n" +
                "700100000075692002280040000123000                                               \n" +
                "8000000000756920010200040000123000                                              \n" +
                "2RENLockbox1110000120000000000040008000801                                      \n" +
                "50020000007571200228RENLockbox1110000120                                        \n" +
                "6002001000003000001100013886973400210000436202012020                            \n" +
                "4002001601                     80012                                            \n" +
                "4002001602                     80012                                            \n" +
                "4002001603                     80012                                            \n" +
                "40020016049                    80012                                            \n" +
                "6002002000005000001100013886973400210000413802012020                            \n" +
                "4002002601                                                                      \n" +
                "40020026029                                                                     \n" +
                "6002003000003490001100013886973400210000414002012020                            \n" +
                "6002004000003000001100013886973400210000424702012020                            \n" +
                "6002005000001800001100013886973400210000414102012020                            \n" +
                "700200000075712002280050000162900                                               \n" +
                "8000000000757120010200050000162900                                              \n" +
                "9000029                                                                         ";

        LocalDateTime currentDate = prepareAndUploadFile(fileText);
        JobRunner.executeJob(GeneralSchedulerPage.LOCKBOX_BILLING_PAYMENT_PROCESS_JOB);

        String refNum = currentDate.format(YYYYMMDD);
        String fileName = SFTPConnection.getClient().getFilesList(new File(FILE_PATH_LOCKBOX)).stream().filter(f -> f.contains(String.format(FILE_NAME_1, refNum))).findFirst()
                .orElseThrow(() -> new IstfException(String.format("File name %s is not found", String.format(FILE_NAME_1, refNum))));

        String destinationDir = PropertyProvider.getProperty(TestProperties.BROWSER_DOWNLOAD_FILES_LOCATION);
        SFTPConnection.getClient().downloadFile(new File(FILE_PATH_LOCKBOX, fileName), new File(destinationDir));
        List<String> list = FileHelper.readFile(destinationDir, fileName);
        LOGGER.info(String.valueOf(list));
        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            softly.assertThat(list.get(0)).contains(String.format("Report Date,%s", currentDate.format(MM_DD_YYYY)));

            LOGGER.info("---=={Step 4}==---");
            softly.assertThat(list.get(3)).contains(String.format("File Name,BOA_DATA_REN_%s", refNum));

            LOGGER.info("---=={Step 5}==---");
            softly.assertThat(list.get(1)).isEqualTo("Receipt Directory,/mnt/ren/shared/lockbox/inbound");

            LOGGER.info("---=={Step 6}==---");
            softly.assertThat(list.get(2)).isEqualTo("Archive Directory,/mnt/ren/archive/lockbox/archive");

            LOGGER.info("---=={Step 7}==---");
            softly.assertThat(list.get(4)).contains(String.format("File Receipt Date & Time,%s", TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(MM_DD_YYYY)));

            LOGGER.info("---=={Step 8}==---");
            softly.assertThat(list.get(5)).contains(String.format("File Process Date & Time,%s", TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(MM_DD_YYYY)));

            LOGGER.info("---=={Step 9}==---");
            softly.assertThat(list.get(6)).isEqualTo("Batch Count Lockbox CHI007569,1");

            LOGGER.info("---=={Step 10}==---");
            softly.assertThat(list.get(7)).isEqualTo("Total Transaction Count Lockbox CHI007569,4");
            softly.assertThat(list.get(8)).isEqualTo("Total Transaction Amount Lockbox CHI007569,123000.00");
            softly.assertThat(list.get(9)).isEqualTo("Batch Count Lockbox CHI007571,1");
            softly.assertThat(list.get(10)).isEqualTo("Total Transaction Count Lockbox CHI007571,5");
            softly.assertThat(list.get(11)).isEqualTo("Total Transaction Amount Lockbox CHI007571,162900.00");
        });
    }

    private LocalDateTime prepareAndUploadFile(String fileText) throws SftpException, JSchException {
        //Random timestamp to avoid similar filenames
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime().plusSeconds(new Random().nextInt(59));
        String currentDateString = currentDate.format(DateTimeUtils.TIME_STAMP);
        //No seconds inside file needed
        String dateForText = currentDateString.substring(0 , currentDateString.length()-2);
        File newFileName = new File(String.format(FILE_NAME, currentDateString));
        FileHelper.addTextToFile(newFileName, String.format(fileText, dateForText, null));
        SFTPConnection.getClient().uploadFile(newFileName, new File(FILE_PATH));
        return currentDate;
    }
}