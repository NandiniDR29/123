/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.notes;

public enum RESTNoteType {

    CUSTOMER("Customer"), QUOTE("Quote"), POLICY("Policy"), CLAIM("claim");

    private String noteType;

    RESTNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String get() {
        return noteType;
    }
}
