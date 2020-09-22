/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.majorlargeaccount;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.cem.ICem;
import com.exigen.ren.common.Workspace;

public interface IMajorLargeAccount extends ICem {

    Workspace getDefaultWorkspace();

    void create(TestData td);

    void initiate();
}
