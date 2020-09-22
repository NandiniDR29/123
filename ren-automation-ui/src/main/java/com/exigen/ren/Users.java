/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.ren.rest.RenUriResolver;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author ikisly
 */
public class Users {
    public static final User DEFAULT = new User(PropertyProvider.getProperty(TestProperties.APP_USER),
            PropertyProvider.getProperty(TestProperties.APP_PASSWORD));
    public static final User ISBA = new User("isba", "isba");
    public static final User QA = new User("qa", "qa");
    public static final User TEST_USER_ALL_1 = new User("TestUserAll1", "isba");
    public static final User TEST_USER_ALL_2 = new User("TestUserAll2", "isba");
    public static final User NOTES_RS_NO_ADD_USER_PRIVILEGE = new User("TestUserForRestNotes2", "qa");
    public static final User NOTES_RS_NO_SET_AS_CONFIDENTIAL_PRIVILEGE = new User("TestUserForRestNotes1", "qa");
    public static final User NOTES_RS_NO_NOTES_INQUIRY_ROLE = new User("TestUserForRestNotes3", "qa");
    public static final User NOTES_RS_NO_UPDATE_NOTES_ROLE = new User("TestUserForRestNotes4", "qa");
    public static final User NOTES_RS_NO_ADD_UPDATE_INQUIRY_NOTES_ROLE = new User("TestUserForRestNotes5", "qa");
    public static final User NOTES_RS_BOB_ROLE_1 = new User("BOBNotesRSUser1", "qa");
    public static final User NOTES_RS_BOB_ROLE_2 = new User("BOBNotesRSUser2", "qa");
    public static final User NOTES_RS_BOB_ROLE_3 = new User("BOBNotesRSUser3", "qa");
    public static final User NOTES_RS_BOB_ROLE_4 = new User("BOBNotesRSUser4", "qa");
    public static final User NOTES_RS_BOB_ROLE_5 = new User("BOBNotesRSUser5", "qa");
    public static final User NOTES_RS_BOB_ROLE_6 = new User("BOBNotesRSUser6", "qa");
    public static final User TASKS_RS_USER_SAME_AS_QA_USER = new User("TestUserForRestTasks", "qa");
    public static final User TASKS_RS_USER = new User("TestUserForRestTasks1", "qa");
    public static final User TASKS_RS_USER_NO_CREATE_MT_PRIV = new User("CTRUser1", "qa");
    public static final User CUSTOMER_RS_USER = new User("CCRUser1", "qa");

    private static boolean rewritePropsFlag = false;
    protected static final Logger LOGGER = LoggerFactory.getLogger(Users.class);

    /**
     * Select global user based on liquibase applying.
     */
    public static void setGlobalUser() {
        if (!rewritePropsFlag) {
            try {
                String target = new RenUriResolver().getRestServiceTarget("security-rs").concat("/users/isba");
                Response response = Jsoup
                        .connect(target)
                        .header("Authorization", Users.QA.getBasicAuthString())
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true).execute();

                if (response.statusCode() != 200) {
                    LOGGER.debug("GET " + target);
                    LOGGER.debug("\n" + response.body());
                    LOGGER.info("ISBA User does not exist, using qa qa");
                    rewriteProperties();
                } else {
                    LOGGER.info("Using ISBA user");
                }
            } catch (Exception e) {
                rewriteProperties();
                LOGGER.warn("Unexpected error: ", e);
            } finally {
                rewritePropsFlag = true;
            }
        }
    }

    private static void rewriteProperties() {
        Properties prop;
        try {
            Field pp = PropertyProvider.class.getDeclaredField("propsStore");
            pp.setAccessible(true);
            prop = (Properties) pp.get(pp);
            prop.setProperty(TestProperties.APP_USER, "qa");
            prop.setProperty(TestProperties.APP_PASSWORD, "qa");
            prop.setProperty("currentUser", "QA QA user");
            prop.setProperty("currentUserFirstName", "QA");
            prop.setProperty("currentUserLastName", "QA user");
            prop.setProperty("currentUserName", "qa");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.warn("Error while rewriting properties, Error:", e);
        }
    }
}
