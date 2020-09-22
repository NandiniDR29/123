/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionsModel {
    private String add_document;

    private String refresh;

    private String retrieve;

    private String rename;

    private String reindex;

    public void setRetrieve(String retrieve) {
        this.retrieve = retrieve;
    }

    public String getRetrieve() {
        return this.retrieve;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public String getRename() {
        return this.rename;
    }

    public void setReindex(String reindex) {
        this.reindex = reindex;
    }

    public String getReindex() {
        return this.reindex;
    }

    public void setAdd_document(String add_document) {
        this.add_document = add_document;
    }

    public String getAdd_document() {
        return this.add_document;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getRefresh() {
        return this.refresh;
    }
}
