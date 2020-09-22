/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.efolder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentTypesModel {
    private String CONTRCT;

    private String DEC;

    private String SIGN_PG;

    private String ID_CARD;

    private String NOTICE;

    private String PRIV_STMT;

    private String CNCL_LTR;

    private String BILL_OF_RGHT;

    private String CERTIF;

    private String ENDORSMNT;

    private String NONREN_LTR;

    private String MISC;

    private String CONS_COMP;

    public void setCONTRCT(String CONTRCT) {
        this.CONTRCT = CONTRCT;
    }

    public String getCONTRCT() {
        return this.CONTRCT;
    }

    public void setDEC(String DEC) {
        this.DEC = DEC;
    }

    public String getDEC() {
        return this.DEC;
    }

    public void setSIGN_PG(String SIGN_PG) {
        this.SIGN_PG = SIGN_PG;
    }

    public String getSIGN_PG() {
        return this.SIGN_PG;
    }

    public void setID_CARD(String ID_CARD) {
        this.ID_CARD = ID_CARD;
    }

    public String getID_CARD() {
        return this.ID_CARD;
    }

    public void setNOTICE(String NOTICE) {
        this.NOTICE = NOTICE;
    }

    public String getNOTICE() {
        return this.NOTICE;
    }

    public void setPRIV_STMT(String PRIV_STMT) {
        this.PRIV_STMT = PRIV_STMT;
    }

    public String getPRIV_STMT() {
        return this.PRIV_STMT;
    }

    public void setCNCL_LTR(String CNCL_LTR) {
        this.CNCL_LTR = CNCL_LTR;
    }

    public String getCNCL_LTR() {
        return this.CNCL_LTR;
    }

    public void setBILL_OF_RGHT(String BILL_OF_RGHT) {
        this.BILL_OF_RGHT = BILL_OF_RGHT;
    }

    public String getBILL_OF_RGHT() {
        return this.BILL_OF_RGHT;
    }

    public void setCERTIF(String CERTIF) {
        this.CERTIF = CERTIF;
    }

    public String getCERTIF() {
        return this.CERTIF;
    }

    public void setENDORSMNT(String ENDORSMNT) {
        this.ENDORSMNT = ENDORSMNT;
    }

    public String getENDORSMNT() {
        return this.ENDORSMNT;
    }

    public void setNONREN_LTR(String NONREN_LTR) {
        this.NONREN_LTR = NONREN_LTR;
    }

    public String getNONREN_LTR() {
        return this.NONREN_LTR;
    }

    public void setMISC(String MISC) {
        this.MISC = MISC;
    }

    public String getMISC() {
        return this.MISC;
    }

    public void setCONS_COMP(String CONS_COMP) {
        this.CONS_COMP = CONS_COMP;
    }

    public String getCONS_COMP() {
        return this.CONS_COMP;
    }
}
