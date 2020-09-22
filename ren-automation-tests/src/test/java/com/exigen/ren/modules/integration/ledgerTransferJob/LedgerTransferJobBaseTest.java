/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.integration.ledgerTransferJob;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsBaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableMap;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MMDDYYYY;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static java.util.stream.Collectors.toList;

public abstract class LedgerTransferJobBaseTest extends ClaimGroupBenefitsBaseTest {

    protected static final String STATE_NY = "NY";
    protected static final String STATE_NJ = "NJ";
    protected static final String STATE_GA = "GA";
    protected static final String FILE_NAME_NY = "DEV_GL_PS_NY001%s";
    protected static final String FILE_NAME_PS = "DEV_GL_PS_AM001%s";
    protected static final String DIR_PATH = "/mnt/ren/shared/ledger-transfer/outbound/";

    private SSHController sftp = SFTPConnection.getClient();

    protected void runLedgerTransferJob(LocalDateTime accountingDateFrom) {
        LOGGER.info("TEST: Job process payment transactions in the specified period");
        LOGGER.info("TEST: accountingDateFrom < payment date < accountingDateFrom + 3d(accountingDateTo)");
        synchronized (LedgerTransferJobBaseTest.class) {
            createGeneralLedgerFolderIfNotExist();
            clearFolder();
        }
        Job job = new Job(GeneralSchedulerPage.LEDGER_TRANSFER_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("accountingDateFrom=%s & accountingDateTo=%s",
                        accountingDateFrom.plusDays(1).toLocalDate().format(YYYYMMDD),
                        TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(YYYYMMDD))));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.LEDGER_TRANSFER_JOB.getGroupName(), job));
    }

    public void createGeneralLedgerFolderIfNotExist() {
        File generalLedgerFolder = new File(DIR_PATH);
        try {
            if (!sftp.pathExists(generalLedgerFolder)) {
                sftp.createDirectory(generalLedgerFolder);
            }
        } catch (JSchException | SftpException e) {
            throw new IstfException("Folder 'GeneralLedger' hasn't created!!!", e);
        }
    }

    public static List<String> readFile(String destinationDir) {
        List<String> list = null;
        try {
            list = Arrays.stream(FileUtils.readFileToString(new File(destinationDir), StandardCharsets.UTF_8)
                    .split("\\n"))
                    .collect(toList());
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return list;
    }

    protected String getFullFileName(String fileName) {
        try {
            RetryService.run(predicate -> getFiles().stream().anyMatch(file -> file.contains(fileName)),
                    StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("The file(" + fileName + ") hasn't been found", e);
        }
        return getFiles().stream().filter(file -> file.contains(fileName)).findFirst().get();
    }

    private List<String> getFiles() {
        List<String> fileList = null;
        try {
            fileList = sftp.getFilesList(new File(DIR_PATH));
        } catch (SftpException | JSchException e) {
            LOGGER.error(e.toString());
        }
        return fileList;
    }

    private void clearFolder() {
        getFiles().forEach(file -> {
            try {
                sftp.deleteFile(new File(DIR_PATH + file));
            } catch (SftpException | JSchException e) {
                throw new IstfException("The folder hasn't been cleared", e);
            }
        });
    }

    protected void checkCountOfLines(List<String> list, String state, String productType, String paymentAmount, int countExpected) {
        long actualCount = getAppropriateNumberLines(list, state, productType, paymentAmount).size();
        assertThat(actualCount)
                .withFailMessage("Wrong counts of lines Actual = " + actualCount + ", Expected = " + countExpected).isEqualTo(countExpected);
    }

    protected void checkJournalHeaderSection(List<String> list, String state, LocalDateTime currentDate) {
        StringBuilder header = new StringBuilder(list.get(0));
        assertSoftly(softly -> {
            softly.assertThat(header.substring(0, 1).trim()).isEqualTo("H");
            if (state.equals(STATE_NY)) {
                softly.assertThat(header.substring(1, 6).trim()).isEqualTo("NY001");
            } else {
                softly.assertThat(header.substring(1, 6).trim()).isEqualTo("AM001");
            }
            softly.assertThat(header.substring(6, 16).trim()).isEqualTo("NEXT");
            softly.assertThat(header.substring(16, 24).trim()).isEqualTo(currentDate.format(MMDDYYYY));
            softly.assertThat(header.substring(24, 25).trim()).isEqualTo("N");
            softly.assertThat(header.substring(36, 47).trim()).isEqualTo("ACTUALS");
            softly.assertThat(header.substring(56, 57).trim()).isEqualTo("N");
            softly.assertThat(header.substring(77, 80).trim()).isEqualTo("EIS");
            if (state.equals(STATE_NY)) {
                softly.assertThat(header.substring(88, 118).trim()).matches("EIS_[0-9]{8}_[0-9]{8}NY");
            } else {
                softly.assertThat(header.substring(88, 118).trim()).matches("EIS_[0-9]{8}_[0-9]{8}AM");
            }
            softly.assertThat(header.substring(118, 121).trim()).isEqualTo("USD");
            softly.assertThat(header.substring(121)).isBlank();
        });
    }

    protected void checkJournalLineSection(List<String> list, String state, String productType, Currency paymentAmount) {
        AtomicInteger index = new AtomicInteger(0);
        getAppropriateNumberLines(list, state, productType, paymentAmount.toPlainString()).forEach(lineNumber -> {
            StringBuilder line = new StringBuilder(list.get(lineNumber - 1));
            assertSoftly(softly -> {
                softly.assertThat(line.substring(0, 1).trim()).isEqualTo("L");
                if (state.equals(STATE_NY)) {
                    softly.assertThat(line.substring(1, 6).trim()).isEqualTo("NY001");
                } else {
                    softly.assertThat(line.substring(1, 6).trim()).isEqualTo("AM001");
                }
                softly.assertThat(line.substring(6, 15).trim()).isEqualTo(String.format("%09d", lineNumber - 1));
                softly.assertThat(line.substring(15, 25).trim()).isEqualTo("ACTUALS");
                softly.assertThat(line.substring(25, 34).trim()).isEqualTo(StringUtils.EMPTY);
                softly.assertThat(line.substring(45, 54).trim()).isEqualTo("720");
                softly.assertThat(line.substring(63, 70).trim()).isEqualTo(productType);
                softly.assertThat(line.substring(117, 127).trim()).isEqualTo(state);
                softly.assertThat(line.substring(233, 243).trim()).isEqualTo("CLAIM");
                softly.assertThat(line.substring(243, 273).trim()).contains("C");
                softly.assertThat(line.substring(273, 276).trim()).isEqualTo("USD");
                if (index.get() == 0) {
                    softly.assertThat(line.substring(281, 309).trim()).isEqualTo(paymentAmount.negate().toPlainString());
                } else {
                    softly.assertThat(line.substring(281, 309).trim()).isEqualTo(paymentAmount.toPlainString());
                }
            });
            index.getAndIncrement();
        });
    }

    private List<Integer> getAppropriateNumberLines(List<String> list, String state, String productType, String paymentAmount) {
        return IntStream.range(0, list.size())
                .filter(i -> list.get(i).contains(state) && list.get(i).contains(productType) && list.get(i).contains(paymentAmount))
                .mapToObj(i -> i + 1)
                .collect(toList());
    }

    protected double getRandomPaymentValue() {
        return 1 + new SecureRandom().nextFloat() * (3 - 1);
    }

    protected static final class PeopleSoftProductCode {
        protected static final String EILIF = "EILIF";
        protected static final String EIDBL = "EIDBL";
        protected static final String EIDEN = "EIDEN";
        protected static final String EISTD = "EISTD";
        protected static final String EITDB = "EITDB";
        protected static final String EILTD = "EILTD";
        protected static final String EIACC = "EIACC";
        protected static final String EIVIS = "EIVIS";
    }
}