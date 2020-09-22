/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.caseprofile;

public enum CaseProfileType {
    CASE_PROFILE("Case Profile", new CaseProfile());

    private String caseProfileType;
    private ICaseProfile caseProfile;

    CaseProfileType(String caseProfileType, ICaseProfile caseProfile) {
        this.caseProfileType = caseProfileType;
        this.caseProfile = caseProfile;
    }

    public ICaseProfile get() {
        return caseProfile;
    }

    public String getName() {
        return caseProfileType;
    }

    public String getKey() {
        return caseProfile.getClass().getSimpleName();
    }
}
