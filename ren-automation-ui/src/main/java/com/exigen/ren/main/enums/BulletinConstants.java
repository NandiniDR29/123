/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;

public class BulletinConstants {
    private BulletinConstants(){}

    public static final class BulletinDeailsTable {
        public static final String EFFECTIVE_DATE = "Effective Date";
        public static final String EXPIRATION_DATE = "Expiration Date";
        public static final String PRIORTY = "Priority";
        public static final String STATUS = "Status";
        public static final String BULLETIN_NAME= "Bulletin Name";
        public static final String BULLETIN_ID = "Bulletin ID";
    }

    public static final class BulletinStatus {
        public static final String ACTIVE = "Active";
        public static final String DELETED = "Deleted";
        public static final String SUBMETED = "Submitted";
        public static final String UNSUBMETED = "Un-submitted";
        public static final String EXPIRED = "Expired";
    }
}
