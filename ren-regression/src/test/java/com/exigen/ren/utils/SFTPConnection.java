/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.utils;

import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.istf.config.PropertyProvider;

public class SFTPConnection {

    private static String sftpHost = PropertyProvider.getProperty("app.host");
    private static String sftpLogin = PropertyProvider.getProperty("sftp.login");
    private static String sftpPassword = PropertyProvider.getProperty("sftp.password");
    private static String sftpCommonHost = PropertyProvider.getProperty("sftp.common.host");
    private static String sftpCommonLogin = PropertyProvider.getProperty("sftp.common.login");
    private static String sftpCommonPassword = PropertyProvider.getProperty("sftp.common.password");

    private static String getSftpHost() {
        return sftpHost;
    }

    private static String getSftpLogin() {
        return sftpLogin;
    }

    private static String getSftpPassword() {
        return sftpPassword;
    }

    private static String getSftpCommonHost() {
        return sftpCommonHost;
    }

    private static String getSftpCommoLogin() {
        return sftpCommonLogin;
    }


    private static String getSftpCommoPassword() {
        return sftpCommonPassword;
    }

    public static SSHController getClient() {
        return new SSHController(getSftpHost(), getSftpLogin(), getSftpPassword());
    }

    public static SSHController getClientForCommonRepository() {
        return new SSHController(getSftpCommonHost(), getSftpCommoLogin(), getSftpCommoPassword());
    }
}