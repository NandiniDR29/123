/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.EntityService;
import com.exigen.ren.common.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IAdmin extends EntityService {
    Logger LOGGER = LoggerFactory.getLogger(IAdmin.class);

    Workspace getDefaultWorkspace();

    void create(TestData td);

    void navigate();
}
