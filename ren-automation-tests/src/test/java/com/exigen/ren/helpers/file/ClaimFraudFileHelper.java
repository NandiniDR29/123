package com.exigen.ren.helpers.file;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ren.helpers.file.entity.ClaimFraudLine;
import com.exigen.ren.helpers.file.parser.ClaimFraudFileParser;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ClaimFraudFileHelper {
    private static final String CLAIM_FRAUD_FILE_NAME = "DEV_RLHAREDFLAG_%s%s_%s.*.csv";
    public static final String OUTPUT_FOLDER = "/home/eis/ren/shared/redflagddmi/outbound/";

    public static List<ClaimFraudLine> getLinesInFile() throws JSchException, SftpException {
        File file = new File(FileHelper.downloadFile(OUTPUT_FOLDER, getPathToFile()));
        return ClaimFraudFileParser.parseRowContent(FileHelper.getFileContent(file));
    }

    public static String getPathToFile() throws JSchException, SftpException {
        String matcher = getClaimFraudFileName();
        Optional<String> fileName = SFTPConnection.getClient().getFilesList(new File(OUTPUT_FOLDER)).stream().filter(f -> f.matches(matcher)).findFirst();
        assertThat(fileName.isPresent()).isTrue();
        return fileName.get();
    }

    private static String getClaimFraudFileName(LocalDateTime localDateTime) {
        return String.format(CLAIM_FRAUD_FILE_NAME,
                localDateTime.format(DateTimeFormatter.ofPattern("MMM")).toUpperCase(),//this can be changed by REN-42281
                localDateTime.format(DateTimeFormatter.ofPattern("YYYY")),
                localDateTime.format(DateTimeFormatter.ofPattern("MMddyyyy")));
    }

    public static String getClaimFraudFileName() {
        return getClaimFraudFileName(TimeSetterUtil.getInstance().getCurrentTime());
    }
}