/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;


import com.exigen.istf.webdriver.controls.Named;

public final class MyWorkConstants {

    public static final String APPROVE_FEATURE = "Approve Feature";
    public static final String CLAIM_MANAGEMENT = "Claim Management";
    public static final String PROCESS_CLAIM = "Process Claim";
    public static final String PROCESS_FEATURE = "Process Feature";
    public static final String ASSIGN_NEW_CLAIM = "Assign New Claim";
    public static final String REVIEW_CLAIM = "Review Claim";

    public static final String TASKS_WITH_LINKING_PROCESS_ID = "com.exigen.eis.bpm.definitions.TasksWithLinking.bpmn";
    public static final String SIMPLE_REVIEW_PROCESS_ID = "com.exigen.eis.bpm.definitions.SimpleReviewProcess.bpmn";
    public static final String TASKS_WITH_THREE_KEYS_PROCESS_ID = "com.exigen.eis.bpm.definitions.TasksWithThreeKeys.bpmn";
    public static final String TASKS_WITH_ONE_KEY_PROCESS_ID = "com.exigen.eis.bpm.definitions.TasksWithOneKey.bpmn";
    public static final String TASKS_WITH_NO_KEY_PROCESS_ID = "com.exigen.eis.bpm.definitions.TasksWithNoKey.bpmn";
    public static final String TEST_PROCESS_ID = "com.exigen.eis.bpm.definitions.TestProcess.bpmn";

    private MyWorkConstants() {
    }

    public enum MyWorkTasksTable implements Named {
        TASK_NAME("Task Name"),
        REFERENCE_ID("Reference ID"),
        CUSTOMER("Customer"),
        TASK_ID("Task ID"),
        WARNING_DATE("Warning Date"),
        DUE_DATE("Due Date"),
        QUEUE("Queue"),
        NOTE_DESCRIPTION("Note Description"),
        AGENCY_LOCATION_ID("Agency / Location ID"),
        PRIORITY("Priority"),
        LAST_PERFORMER("Last Performer");

        private String name;

        MyWorkTasksTable(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }

    public static final class MyWorkTaskName {
        public static final String REFUND_APPROVAL = "Refund Approval";
        public static final String REFUND_ISSUE = "Refund\u00a0Issue";
        public static final String CLEAR_REFUND = "Clear Refund, Decline or Stop";
    }

    public static final class MyWorkSuspendInformation {
        public static final String SUSPENCE_REASON = "Suspense Reason";
        public static final String FOLLOW_UP_NEEDED = "Follow up needed";
        public static final String SUSPENDED = "Suspended";
        public static final String DATE_TIME = "Date/Time";
    }

    public enum MyWorkActivitiesAndUserNotesTable implements Named {
        STATUS("Status"), DESCRIPTION("Description");

        private String label;

        MyWorkActivitiesAndUserNotesTable(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public static final class MyWorkTaskDetailsStatus {
        public static final String COMPLETED = "Completed";
    }

    public static final class MyWorkStatus {
        public final static String SUSPEND = "Suspend";
        public final static String FINISHED = "Finished";
        public final static String ACTIVE = "Active";
    }

    public enum MyWorkFilterTaskStatus {
        ACTIVE,
        COMPLETED,
        SUSPENDED
    }

}
