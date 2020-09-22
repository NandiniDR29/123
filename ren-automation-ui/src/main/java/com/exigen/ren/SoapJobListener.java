/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren;

import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapJobListener extends BaseJobListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(SoapJobListener.class);

	@Override
    public void beforeJobCreation(JobGroup jobGroup) {
    }

    @Override
    public void afterJobCreation(JobGroup jobGroup) {
    }

    @Override
    public void beforeJobExecution(JobGroup jobGroup) {
    }

    @Override
	public void afterJobExecution(JobGroup jobGroup) {
	}

}
