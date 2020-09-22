/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.file;

import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.modules.integration.ledgerTransferJob.LedgerTransferJobBaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.exigen.ren.main.modules.mywork.IMyWork.LOGGER;
import static java.util.stream.Collectors.toList;


public class FileHelper {
    private static SSHController sftp = SFTPConnection.getClient();

    public static File createFile(String fileName, List<String> list)
    {
        File downloadDir = new File(PropertyProvider.getProperty(TestProperties.BROWSER_DOWNLOAD_FILES_LOCATION));

        if (!downloadDir.exists()) {
            if (!downloadDir.mkdirs()) {
                throw new IstfException("Folder hasn't created!!!");
            }
        }

        File file = new File(downloadDir, fileName);
        try {
            FileWriter fw = new FileWriter(file);
            list.forEach(s -> {
                try {
                    fw.append(s).append("\n");
                } catch (IOException e) {
                    throw new IstfException("File is not created", e);
                }
            });
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            throw new IstfException("File is not created", e);
        }
        return file;
    }

    public static void addTextToFile(File fileName, String text){
        try
        {
            FileWriter fw = new FileWriter(fileName,true); //the true will append the new data
            fw.write(text);//appends the string to the file
            fw.close();
        }
        catch (IOException e) {
            throw new IstfException("File is not created", e);
        }
    }

    public static synchronized String getFileContent(File customLogFile) {
        try (InputStream is = new FileInputStream(customLogFile)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            return IOUtils.toString(br);
        } catch (IOException e) {
            throw new IstfException("Unable to get content from file: " + customLogFile.getPath(), e);
        }
    }

    public static String downloadFile(String dirPath, String fullFileName) {
        String destinationDir;

        synchronized (LedgerTransferJobBaseTest.class) {
            File downloadDir = new File(PropertyProvider.getProperty(TestProperties.BROWSER_DOWNLOAD_FILES_LOCATION));
            
            if (!downloadDir.exists()) {
                if (!downloadDir.mkdirs()) {
                    throw new IstfException("Folder wasn't created!");
                }
            }
            destinationDir = downloadDir.getAbsolutePath();

            try {
                sftp.downloadFile(new File(dirPath + fullFileName), new File(destinationDir));
            } catch (JSchException | SftpException | IOException e) {
                throw new IstfException("File(" + fullFileName + ") wasn't downloaded", e);
            }
        }
        return StringUtils.join(Arrays.asList(destinationDir, fullFileName), "/");
    }

    public static List<String> getFilesList(String path) {
        try {
            return sftp.getFilesList(new File(path));
        } catch (SftpException | JSchException e) {
            throw new IstfException("Couldn't get files list from path " + path, e);
        }
    }

    public static void clearFolder(String path) {
        getFilesList(path).forEach(file -> {
            try {
                sftp.deleteFile(new File(path + file));
            } catch (SftpException | JSchException e) {
                throw new IstfException("The folder hasn't been cleared", e);
            }
        });
    }

    public static List<String> readFile(String destinationDir, String fileName) {
        List<String> list = null;
        try {
            list = Arrays.stream(FileUtils.readFileToString(new File(destinationDir, fileName), StandardCharsets.UTF_8)
                    .split("\\n"))
                    .collect(toList());
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return list;
    }

}
