package com.exigen.ren.common.enums;

import com.google.common.collect.ImmutableList;

public class UsersConsts {

    public final static String USER_ISBA_FIRST_NAME = "ISBA";
    public final static String USER_ISBA_LAST_NAME = "ISBA";
    public final static String USER_ISBA = String.format("%s %s", USER_ISBA_FIRST_NAME, USER_ISBA_LAST_NAME);

    public static final String USER_QA_QA_FIRST_NAME = "QA";
    public static final String USER_QA_QA_LAST_NAME = "QA user";
    public static final String USER_QA_QA = String.format("%s %s", USER_QA_QA_FIRST_NAME, USER_QA_QA_LAST_NAME);
    public final static String USER_QA_QA_LOGIN = "qa";

    // User1 (Available, Eff Date is today - 1) with 'Policy Sales &amp; Service - Basic (Sales Reps)' group, created through liquibase
    public final static String USER_10001_FIRST_NAME = "User10001_FirstName";
    public final static String USER_10001_LAST_NAME = "User10001_LastName";
    public final static String USER_10001 = String.format("%s %s", USER_10001_FIRST_NAME, USER_10001_LAST_NAME);
    public final static String USER_10001_LOGIN = "User10001";

    // User2 (Unavailable, Eff Date = today + 1 year) with 'Policy Sales &amp; Service - Basic (Sales Reps)s' group, created through liquibase
    public final static String USER_2 = "User10002_FirstName User10002_LastName";

    // User3 (Disabled, Expiration Date = today - 1) with 'Policy Sales &amp; Service - Basic (Sales Reps)' group, created through liquibase
    public final static String USER_3 = "User10003_FirstName User10003_LastName";

    // User4 (Available, Eff Date is today - 1) with 'Policy Sales &amp; Service - Intermediate (Sales Support)' group, created through liquibase
    public final static String USER_10004_FIRST_NAME = "User10004_FirstName";
    public final static String USER_10004_LAST_NAME = "User10004_LastName";
    public final static String USER_10004 = String.format("%s %s", USER_10004_FIRST_NAME, USER_10004_LAST_NAME);
    public final static String USER_10004_LOGIN = "User10004";

    // User5 (Unavailable, Eff Date = today + 1 year) with 'Policy Sales &amp; Service - Intermediate (Sales Support)' group, created through liquibase
    public final static String USER_5 = "User10005_FirstName User10005_LastName";

    // User6 (Disabled, Expiration Date = today - 1) with 'Policy Sales &amp; Service - Intermediate (Sales Support)' group, created through liquibase
    public final static String USER_6 = "User10006_FirstName User10006_LastName";

    // User7 (Available, Eff Date is today - 1) with 'Policy Sales &amp; Service -Advanced (Underwriting)' group, created through liquibase
    public final static String USER_7 = "User10007_FirstName User10007_LastName";

    // User8 (Unavailable, Eff Date = today + 1 year) with 'Policy Sales &amp; Service -Advanced (Underwriting)' group, created through liquibase
    public final static String USER_8 = "User10008_FirstName User10008_LastName";

    // User9 (Disabled, Expiration Date = today - 1) with 'Policy Sales &amp; Service -Advanced (Underwriting)' group, created through liquibase
    public final static String USER_9 = "User10009_FirstName User10009_LastName";

    // User10 with Underwriting Authority Level = 0, created through liquibase
    public final static String USER_10 = "User100010_FirstName User100010_LastName";
    public final static String USER_10_LOGIN = "User100010";

    // User11 (First Name + Last Name is more than 19 symbols) with 'Policy Sales &amp; Service - Basic (Sales Reps)' group, created through liquibase
    public final static String USER_11 = "User100011_FirstName_123456789 User100011_LastName_123456789";

    // User12 (First Name + Last Name is more than 19 symbols) with 'Policy Sales &amp; Service - Intermediate (Sales Support)' group, created through liquibase
    public final static String USER_12 = "User100012_FirstName_123456789 User100012_LastName_123456789";

    // User13 (First Name + Last Name is more than 19 symbols) with 'Policy Sales &amp; Service -Advanced (Underwriting)' group, created through liquibase
    public final static String USER_13 = "User100013_FirstName_123456789 User100013_LastName_123456789";

    // Expired EIS user with id=11002 with 'Policy Sales &amp; Service -Advanced (Underwriting)' and 'Policy Sales &amp; Service - Intermediate (Sales Support)' groups, created through liquibase
    public static final String EXP_USER_2 = "User11002_FirstName User11002_LastName";

    // Expired EIS user with id=11003 with 'Policy Sales &amp; Service - Basic (Sales Reps)' group, created through liquibase
    public static final String EXP_USER_3 = "User11003_FirstName User11003_LastName";

    public final static ImmutableList<String> SALES_REPRESENTATIVE_USERS = ImmutableList.of(USER_10001, USER_2, USER_3, USER_11);
    public final static ImmutableList<String> SALES_SUPPORT_ASSOCIATE_USERS = ImmutableList.of(USER_10004, USER_5, USER_6, USER_12);
    public final static ImmutableList<String> UNDERWRITER_USERS = ImmutableList.of(USER_7, USER_8, USER_9, USER_13);

}
