/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.caseprofile;

import com.exigen.istf.data.TestData;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileType;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.modules.BaseTest;

public class CaseProfileBaseTest extends BaseTest implements CaseProfileContext, CustomerContext {

    protected TestData tdCaseProfile = testDataManager.caseProfile.get(CaseProfileType.CASE_PROFILE);

    protected String caseProfileNumber, caseProfileName;

    void openAppCreateNonIndCustomerInitNewCaseProfile() {
        mainApp().reopen();
        createDefaultNonIndividualCustomer();
        caseProfile.initiate();
    }
}
