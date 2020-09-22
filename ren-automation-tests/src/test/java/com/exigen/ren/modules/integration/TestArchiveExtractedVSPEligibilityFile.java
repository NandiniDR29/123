package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.exigen.istf.utils.TestInfo;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYMMDD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.exigen.ren.utils.SFTPConnection;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class TestArchiveExtractedVSPEligibilityFile extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    private static final String FILE_NAME = "t0054051";
    private static final String FILE_PATH_OUTBOUND = "/home/eis/ren/shared/vsp/outbound/";
    private static final String FILE_PATH_ARCHIVE = "/home/eis/ren/archive/VSPeligibilityextract/archive";
    private static final String SENDER_ID = "470397286";
    private static final String RECEIVER_ID = "941632821";
    private static final String VERSIION_RELEASE_CODE = "005010X220A1";
    private static final String TRANSACTION_SET_CONTROL_ID = "0001";

    private SSHController sftp = SFTPConnection.getClient();

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-41149", "REN-36926"}, component = INTEGRATION)
    public void testArchiveExtractedVSPEligibilityFile() throws SftpException, JSchException, IOException {

        LOGGER.info("TEST: REN-41149: Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        createDefaultGroupVisionMasterPolicy();
        createDefaultGroupVisionCertificatePolicy();

        String participantName = PolicySummaryPage.tableVisionRoleSummary.getRow(1).getCell("Customer Name").getValue();
        String participantFirstName = participantName.split(" ")[0];
        String participantLastName = participantName.split(" ")[1];

        LOGGER.info("TEST: REN-41149: Step 1");
        //delete old files if exist before verification
        String fullOutboundFileName = String.format("%s.dat", FILE_NAME);
        File fileOutbound = new File(FILE_PATH_OUTBOUND, fullOutboundFileName);
        String regExpArchiveFileName = String.format("%s_%s%s", FILE_NAME, TimeSetterUtil.getInstance().getCurrentTime().format(YYYYMMDD), "\\d{6}.dat");
        if (sftp.pathExists(fileOutbound)) {
            sftp.deleteFile(fileOutbound);
        }
        assertThat(sftp.pathExists(fileOutbound)).isFalse();
        List<String> existingArchiveFilesNames = getArchiveFilesNames(regExpArchiveFileName);
        existingArchiveFilesNames.forEach(
                fileName -> {
                    try {
                        sftp.deleteFile(new File(FILE_PATH_ARCHIVE, fileName));
                    } catch (JSchException | SftpException e) {
                        LOGGER.info("Error removing file", e);
                    }
                }
        );
        assertThat(getArchiveFilesNames(regExpArchiveFileName)).isEmpty();

        JobRunner.executeJob(GeneralSchedulerPage.VSP_ELIGIBILITY_JOB);

        LOGGER.info("TEST: REN-41149: Step 2");
        assertThat(sftp.pathExists(fileOutbound)).isTrue();

        LOGGER.info("TEST: REN-41149: Steps 3-4");
        assertThat(getArchiveFilesNames(regExpArchiveFileName).size()).isGreaterThanOrEqualTo(1);

        LOGGER.info("TEST: REN-36926 Step 1");
        File file = new File(FileHelper.downloadFile(FILE_PATH_OUTBOUND, fullOutboundFileName));

        String fileContent = FileHelper.getFileContent(file);
        String[] segments = fileContent.split("~");

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        assertSoftly(softly -> {
            // ISA segment
            softly.assertThat(segments[0]).matches(String.format("ISA\\*00\\*          \\*00\\*\\          \\*ZZ\\*%s      \\*ZZ\\*%s      \\*%s\\*\\d{4}\\*=\\*00501\\*\\d{9}\\*0\\*P\\*>",
                    SENDER_ID, RECEIVER_ID, currentDate.format(YYMMDD)));
            // GS segment
            softly.assertThat(segments[1]).matches(String.format("GS\\*BE\\*%s\\*%s\\*%s\\*\\d{4,8}\\*\\d{1,9}\\*X\\*005010X220A1",
                    SENDER_ID, RECEIVER_ID, currentDate.format(YYYYMMDD)));
            //ST segment
            softly.assertThat(segments[2]).isEqualTo(String.format("ST*834*%s*%s", TRANSACTION_SET_CONTROL_ID, VERSIION_RELEASE_CODE));
            // BGN segment
            softly.assertThat(segments[3]).matches(String.format("BGN\\*00\\*%s\\*%s\\*\\d{4,8}\\*UT\\*\\*\\*4", TRANSACTION_SET_CONTROL_ID, currentDate.format(YYYYMMDD)));
            // REF segment
            softly.assertThat(segments[4]).isEqualTo("REF*38*8005112");
            // N1 Sponsor Name segment
            softly.assertThat(segments[5]).isEqualTo(String.format("N1*P5*Renaissance Life and Health*FI*%s", SENDER_ID));
            // N1 Payer segment
            softly.assertThat(segments[6]).isEqualTo(String.format("N1*IN*Vision Service Plan*FI*%s", RECEIVER_ID));

            LOGGER.info("TEST: REN-36926 Step 2");
            // Reference segment
            softly.assertThat(getSegmentsStartsWith(segments, "REF*0F")[0]).matches("REF\\*0F\\*\\d{1,50}");
            // NM1 segment
            softly.assertThat(getSegmentsStartsWith(segments, String.format("NM1*IL*1*%s*%s", participantLastName, participantFirstName))[0]).matches("NM1\\*IL\\*1\\*\\S{1,80}\\*\\S{1,35}\\*\\S{0,25}\\*\\S{0,10}\\*\\S{0,10}\\*34\\*\\d{2,80}");
            // HD segment
            softly.assertThat(getSegmentsStartsWith(segments, "HD*030**VIS*0")[0]).matches("HD\\*\\d{3}\\**VIS\\*0\\*\\S{3}");

            LOGGER.info("TEST: REN-36926 Step 3");
            softly.assertThat(fileContent.split("\n").length).isEqualTo(1);
        });

    }

    private List<String> getArchiveFilesNames(String regExpArchiveFileName) throws JSchException, SftpException {
        List<String> matchedFileNames = new ArrayList<>();
        List<String> fileListArchived = sftp.getFilesList(new File(FILE_PATH_ARCHIVE));
        fileListArchived.forEach(
                fileName -> {
                    if (fileName.matches(regExpArchiveFileName)) {
                        matchedFileNames.add(fileName);
                    }
                }
        );
        return matchedFileNames;
    }

    private String[] getSegmentsStartsWith(String[] segments, String startsWith) {
        return Arrays.stream(segments).filter(e -> e.startsWith(startsWith)).toArray(String[]::new);
    }
}
