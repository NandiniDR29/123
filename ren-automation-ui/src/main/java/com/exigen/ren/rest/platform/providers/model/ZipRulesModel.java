/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.providers.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;

public class ZipRulesModel extends Model {

    private ZipRangeModel zipRange;
    private TermModel term;

    public void setZipRange(ZipRangeModel zipRange) {
        this.zipRange = zipRange;
    }

    public ZipRangeModel getZipRange() {
        return zipRange;
    }

    public void setTerm(TermModel term) {
        this.term = term;
    }

    public TermModel getTerm() {
        return term;
    }
}
