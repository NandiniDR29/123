/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.utils;

import com.exigen.ipb.eisa.utils.report.EmailReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class EmailReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailReport.class);

    public static void main(String[] args) {

        EmailReport emailReport = new EmailReport();

        try {
            emailReport.generate();
        } catch (URISyntaxException | IOException e) {
            LOGGER.error("{}", e);
        }
    }
}
