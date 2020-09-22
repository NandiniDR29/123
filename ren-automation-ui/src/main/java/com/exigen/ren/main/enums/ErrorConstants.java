/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;

public final class ErrorConstants {

    private ErrorConstants() {
    }

    public static final class ErrorTable {
        public static final String DESCRIPTION = "Description";
        public static final String SEVERITY = "Severity";
        public static final String MESSAGE = "Message";
        public static final String CODE = "Code";
        public static final String ERROR = "Error";
        public static final String EMPTY_CODE = "";
    }

    public static final class ErrorMessages {
        public static final String NO_ACCESS_FOR_GET_ALERT = "Entity does not exist or is not accessible";
        public static final String NO_BATCH_JOB_FOUNDED = "At least one Batch Job must be added";
        public static final String ACTION_LOCKED = "The action is not allowed, because policy is locked for cascading transaction. Please try later";
        public static final String COMPARISON_VERSIONS_IMPOSSIBLE = "Only two versions can be selected";
        public static final String OPERATION_FAILED_DUE_ILLEGAL_ARGUMENTS = "Operation has failed due to illegal arguments";
        public static final String ONE_AGENCY_MUST_BE_MARKED_AS_DEFAULT = "One Agency/Location must be marked as the default";
        public static final String NO_ROLES_FOUND = "No Roles Found";
        public static final String NO_PRODUCT_ACCESS_ROLES_FOUND = "No Product Access Roles Found";
        public static final String INVALID_PHONE_NUMBER = "Digits entered are less than 10";
        public static final String INVALID_EMAIL_VALIDATION = "Value is not valid email";
        public static final String EXPIRATION_DATE_ERROR = "Expiration Date should be equal or later than Effective Date";
        public static final String NO_MANAGERS_FOUND = " No Managers Found";
        public static final String NO_USER_SUBORDINATES_FOUND = "No User Subordinates Found";
        public static final String PLAN_NAME_ERROR_MESSAGE = "'Plan Name' is required";
        public static final String PLAN_NAME_DUBLICATE_ERROR_MESSAGE = "'Plan Name' is duplicate with existing one, please enter a different Plan Name.";
        public static final String ERROR_MESSAGE_MUST_BE_WITHIN_30 = "State Requirement: %s must be within 30%% of %s.";
        public static final String ERROR_MESSAGE_TOTAL_MUST_BE_GREATER_THAN_0 = "Must be greater than 0";
        public static final String LINE_OF_AUTHORITY_IS_REQUIRED = "Line Of Authority is required.";
        public static final String GUARANTEED_ISSUE_AMOUNT_SHOULD_NOT_BE_GREATER_THAN_MAXIMUM_MONTHLY_BENEFIT_AMOUNT = "Guaranteed Issue Amount should not be greater than Maximum Monthly Benefit Amount";
        public static final String RATE_REQUEST_DATE_IS_REQUIRED = "'Rate Request Date' is required";
        public static final String RATE_REQUEST_DATE_ERROR = "Rate Request Date must be less than or equal to the current date and greater than or equal to the original Rate Request Date";
        public static final String APPROVED_PERCENT_ERROR = "Approved % cannot be greater than Benefit Percentage";
        public static final String ERROR_PATTERN = "'%s' is required";
        public static final String ROLE_PERCENT_PRIMARY_ERROR = "Primary Beneficiaries must total 100%";
        public static final String ROLE_PERCENT_SECONDARY_ERROR = "Secondary Beneficiaries must total 100%";
        public static final String SERVICE_ROLE_VALIDATION_MESSAGE = "Service Role 'Administrative', 'Billing' and 'Claims' must be assigned to the group at least once and have Mail or Email as Primary Contact Preference with corresponding Contact Details(Preferred = \"Yes\" to be used to identify one if more than one Contact Details for Address/Email and Email Type \"Work\" to be used for INDV as Group Contact).";
        public static final String EFFECTIVE_DATE_IS_PRIOR_TO_GROUP_COVERAGE_EFFECTIVE_DATE = "Effective date is prior to Group Coverage Effective date";
        public static final String EFFECTIVE_DATE_IS_AFTER_THE_DATE_OF_LOSS = "Effective date is after the Date of Loss";
        public static final String SITUS_STATE_ERROR_MESSAGE = "Plan selections have been reset due to change in Situs State.";
        public static final String TOTAL_NUMBER_ELIGABLE_LIVES_ERROR_MESSAGE = "Plan Selection has been reset due to change in Total Number of Eligible Lives. Please review Plan Definition tab to add desired plans";

    }
}
