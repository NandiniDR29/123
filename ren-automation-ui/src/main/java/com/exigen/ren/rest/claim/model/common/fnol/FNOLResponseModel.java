/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.claim.model.common.fnol;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class FNOLResponseModel extends Model {

    private String identifier;
    private FNOLResponseMetadataModel metadata;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public FNOLResponseMetadataModel getMetadata() {
        return metadata;
    }

    public void setMetadata(FNOLResponseMetadataModel metadata) {
        this.metadata = metadata;
    }
}
