/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;

public final class ClaimConstants {

    private ClaimConstants() {
    }

    public static final class ClaimStatus {
        public static final String NOTIFICATION = "Notification";
        public static final String PREDETERMINED = "Predetermined";
        public static final String INITIAL = "Initial";
        public static final String LOGGED_INTAKE = "Logged - Intake";
        public static final String LOGGED_BASIC_REVIEW= "Logged - Basic Review";
        public static final String LOGGED = "Logged";
        public static final String OPEN = "Open";
        public static final String REOPENED = "Reopened";
        public static final String CLOSED = "Closed - Closed";
        public static final String CLOSED_CLOSED = "Closed - Closed";
        public static final String SIU_POTENTIAL = "SIU Potential";
        public static final String SIU_REVIEW = "SIU Review";
        public static final String CLOSED_ADJUDICATED = "Closed - Adjudicated";
        public static final String PAID_02 = "02 Paid";
        public static final String CLOSED_PAID = "Closed - Paid";
        public static final String ADJUDICATED = "Adjudicated";
        public static final String PAID = "Paid";
        public static final String PENDED_TEAM_LEAD = "Pended - Team Lead";
        public static final String PENDED = "Pended";
        public static final String ADJUDICATED_ORTHO = "Adjudicated - Ortho";
        public static final String CANCELED = "Canceled";
        public static final String SUSPENDED = "Suspended";
    }

    public static final class ClaimPolicyStatus {
        public static final String ACTIVE = "Active";
        public static final String NO_POLICY = "No Policy";
    }

    public static final class ClaimVersionType {
        public static final String UPDATED = "Updated";
        public static final String FNOL = "FNOL";
    }

    public static final class ClaimReason {
        public static final String UPDATED = "Claim Updated";
        public static final String CREATED = "Claim Created";
    }

    public static final class SpecialHandlingStatus {
        public static final String SUBROGATION = "Subrogation";
        public static final String SECURE_CLAIM = "Secure Claim";
        public static final String PRE_EXISTING = "Pre-Existing";
    }

    public static final class FeatureStatus {
        public static final String OPEN = "Open";
        public static final String CLOSED = "Closed";
        public static final String PENDING = "Pending";
    }

    public static final class ClaimDBStatus {
        public static final String OPEN = "OPEN";
        public static final String CLOSED = "CLOSED";
        public static final String INITIAL = "INITIAL";
        public static final String NOTIFICATION = "NOTIFICATION";
    }

    public static final class PaymentsAndRecoveriesTransactionStatus {
        public static final String ISSUED = "Issued";
        public static final String DECLINED = "Declined";
        public static final String DISAPPROVED = "Disapproved";
        public static final String CLEARED = "Cleared";
        public static final String VOIDED = "Voided";
        public static final String APPROVED = "Approved";
        public static final String PENDING = "Pending";
        public static final String STOP_REQUESTED = "Stop Requested";
        public static final String STOPPED = "Stopped";
    }

    public static final class PaymentsAndRecoveriesNote {
        public static final String NONE = "";
        public static final String FINAL = "Final";
        public static final String SUPPLEMENTAL = "Supplemental";
    }

    public static final class ClaimResponsiblePartiesTable {
        public static final String NAME = "Name";
    }

    public static final class ClaimSummaryOfPaymentsAndRecoveriesTable {
        public static final String TRANSACTION_STATUS = "Transaction Status";
        public static final String TOTAL_PAYMENT_AMOUNT = "Total Payment Amount";
        public static final String TOTAL_RECOVERY_AMOUNT = "Total Recovery Amount";
        public static final String TOTAL_REMAINING_AMOUNT = "Total Remaining Amount";
        public static final String PAYMENT_NUMBER = "Payment Number";
        public static final String NOTE = "Note";
        public static final String CHECK_EFT = "Check # / EFT #";
    }

    public static final class ClaimCreationResultTable {
        public static final String CLAIM_SEQ = "Claim Seq. #";
        public static final String CLAIM = "Claim #";
        public static final String CREATION_STATUS = "Creation Status";
    }

    public static final class ClaimAllBenefitsTable {
        public static final String BENEFIT_NUMBER = "Benefit Number";
        public static final String BENEFIT_TYPE = "Benefit Type";
        public static final String ASSOCIATED_PARTY = "Associated Party";
    }

    public static final class ClaimAllSingleBenefitCalculationsTable {
        public static final String SINGLE_BENEFIT_NUMBER = "Single Benefit Number";
        public static final String SINGLE_BENEFIT_ID = "Single Benefit ID";
        public static final String CLAIMANT = "Claimant";
        public static final String EMPTY = "";
    }

    public static final class ClaimBeneficiaryPaymentsTable {
        public static final String TRANSACTION_STATUS = "Transaction Status";
        public static final String TOTAL_PAYMENT_AMOUNT = "Total Payment Amount";
    }

    public static final class ClaimSummaryOfPaymentSeriesTable {
        public static final String SERIES_NUMBER = "Series Number";
        public static final String STATUS = "Status";
    }

    public static final class ClaimSingleBenefitCalculationReserveHistoryTable {
        public static final String RESERVE_TYPE = "Reserve Type";
        public static final String OLD_RESERVE = "Old Reserve";
        public static final String NEW_RESERVE = "New Reserve";
        public static final String TRANSACTION = "Transaction";
    }

    public static final class ClaimHistoryTable {
        public static final String DATE = "Date";
    }

    public static final class ClaimLossEventTable {
        public static final String CAUSE_OF_LOSS = "Cause of Loss";
        public static final String DESCRIPTION_OF_LOSS_EVENT = "Description of Loss Event";
        public static final String CLAIM_FILE_OWNER = "Claim File Owner";
        public static final String LOSS_LOCATION = "Loss Location";
        public static final String DESCRIPTION_OF_LOSS = "Description of Loss";
    }

    public static final class ClaimPartiesTable {
        public static final String PARTY_NAME = "Party Name";
        public static final String ZIP_POSTAL_CODE = "Zip/Postal Code";
        public static final String CITY = "City";
        public static final String ADDRESS_LINE = "Address Line 1";
        public static final String STATE_PROVINCE = "State/Province";
        public static final String ROLE = "Role";
        public static final String ADDRESS = "Address";
        public static final String PAYEE_TYPE = "Payee Type";
        public static final String SOURCE = "Source";
    }

    public static final class ClaimAccessControlListTable {
        public static final String NAME = "Name";
        public static final String TYPE = "Type";
        public static final String ACTION = "Action";
    }

    public static final class ClaimVendorsTable {
        public static final String COMPANY_NAME = "Company Name";
        public static final String SERVICE_TYPE = "Service Type";
    }

    public static final class ClaimSponsorTable {
        public static final String COMPANY_NAME = "Company Name";
        public static final String SPONSOR_PHONE = "Sponsor Phone";
        public static final String SPONSOR_ADDRESS = "Sponsor Address";
    }

    public static final class ClaimPartyItemTreeTable {
        public static final String INSURABLE_PARTY = "Insurable Party";
        public static final String COVERAGE = "Coverage";
        public static final String COVERAGE_TYPE = "Coverage Type";
        public static final String LIMIT_LEVEL = "Limit Level";
        public static final String LIMIT_AMOUNT = "Limit Amount";
        public static final String NOTES = "Notes";
    }

    public static final class ClaimBenefitReservesTable {
        public static final String RESERVE = "Reserve";
        public static final String UNALLOCATED_AMOUNT = "Unallocated Amount";
        public static final String ALLOCATED_AMOUNT = "Allocated Amount";
        public static final String TOTAL_AMOUNT = "Total Amount";
    }

    public static final class ClaimListTable {
        public static final String CLAIM = "Claim #";
    }

    public static final class ClaimPolicyPartiesTable {
        public static final String FULL_NAME = "Full Name";
        public static final String ROLE_TYPE = "Role Type";
        public static final String PARTY_NAME = "Party Name";
        public static final String ADDRESS = "Address";
        public static final String COMMUNICATION = "Communication Info";
    }

    public static final class ClaimProductInfoTable {
        public static final String TYPE_OF_CLAIM = "Type of Claim";
        public static final String POLICY_STATUS = "Policy Status";
        public static final String TOTAL_INCURRED = "Total Incurred";
        public static final String INSURED_NAME = "Insured Name";
        public static final String CLAIM_STATUS = "Claim Status";
        public static final String SPECIAL_HANDLING = "Special Handling";
        public static final String DATW_OF_LOSS = "Date of Loss";
        public static final String UNDERWRITING_COMPANY = "Underwriting Company";
        public static final String INSURED = "Insured";
        public static final String PATIENT = "Patient";
        public static final String POLICY_NUMBER = "Policy #";
        public static final String POLICY_PLAN = "Policy Plan";
        public static final String POLICY_PRODUCT = "Policy Product";

    }

    public static final class ClaimSTATAvailableBenefits {
        public static final String SHORT_TERM_DISABILITY = "Short Term Disability";
        public static final String LONG_TERM_DISABILITY = "Long Term Disability";
        public static final String STATUTORY_SHORT_TERM_DISABILITY = "Statutory Short Term Disability";
        public static final String PAID_FAMILY_LEAVE = "Paid Family Leave";
    }

    public static final class ClaimTAAvailableBenefits {
        public static final String DEATH = "Death";
        public static final String ACCELERATED_DEATH = "Accelerated Death";
        public static final String PREMIUM_WAIVER = "Premium Waiver";
        public static final String ACCIDENTAL_DISMEMBERMENT = "Accidental Dismemberment";
        public static final String ACCIDENTAL_DEATH = "Accidental Death";
    }

    public static final class ClaimGAAvailableBenefits {
        public static final String ACCIDENTAL_DEATH = "Accidental Death";
        public static final String ACCIDENTAL_DISMEMBERMENT = "Accidental Dismemberment";
        public static final String CRITICAL_ILLNESS = "Critical Illness";
        public static final String DIAGNOSIS_AND_TREATMENT = "Diagnosis and Treatment";
    }

    public static final class ClaimACAvailableBenefits {
        public static final String ACCIDENTAL_DEATH = "Accidental Death";
        public static final String PREMIUM_WAIVER = "Premium Waiver";
        public static final String ACCIDENTAL_DISMEMBERMENT = "Accidental Dismemberment";
        public static final String CRITICAL_ILLNESS = "Critical Illness";
        public static final String DIAGNOSIS_AND_TREATMENT = "Diagnosis and Treatment";
    }

    public static final class Plan {
        public static final String STATUTORY_NEW_YORK = "Statutory New York";
    }

    public static final class CDTCodes {
        public static final String DENIED = "D1310";
        public static final String REVIEW_REQUIRED_1 = "D2971";
        public static final String REVIEW_REQUIRED_2 = "D3351";
        public static final String DISALLOWED = "D0601";
        public static final String ALLOWED = "D0160";
    }

    public static final class ClaimSpecialHandlingStatus {
        public static final String ADJUSTED = "Adjusted";
    }

    public static final class ClaimRecoveryTransactionStatus {
        public static final String ISSUE_RECOVERY = "Issue Recovery";
        public static final String ISSUED_RECOVERY_UPDATE = "Issued Recovery Update";
        public static final String VOID_RECOVERY = "Void Recovery";

    }
}
