/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.businessactivities;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.IAdmin;

import java.io.File;

public interface IBusinessActivity extends IAdmin {

    void change(TestData td);

    File saveReportToFile(TestData td, String fileName);
}
