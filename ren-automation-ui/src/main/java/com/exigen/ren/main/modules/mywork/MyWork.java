/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.mywork;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.mywork.tabs.MyWorkTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWork implements IMyWork {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/mywork");

    private Workspace defaultView = new Workspace.Builder()
            .registerTab(MyWorkTab.class).build();

    protected static final Logger LOGGER = LoggerFactory.getLogger(MyWork.class);

    @Override
    public Workspace getDefaultWorkspace() {
        return defaultView;
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

}
