/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.enums;

public class EfolderConstants {

    private EfolderConstants(){}

    public static final class DocumentOparetions {
        public static final String ADD_DOCUMENT = "Add Document";
        public static final String ADD_EXT_DOCUMENT = "Add Ext. Document";
        public static final String REFRESH = "Refresh";
        public static final String RETRIEVE = "Retrieve";
        public static final String RENAME = "Rename";
        public static final String REINDEX = "Reindex";
    }

    public static final class Efolders {
        public static final String IMPORT = "Import";
        public static final String POLICY_PAPERS = "Policy Papers";
        public static final String INVOICES_AND_BILLS = "Invoices and Bills";
        public static final String MISCELLANEOUS = "Miscellaneous";
    }

    //names of document types that occur several times for different e-folders
    public static final class DocumentTypes {
        public static final String CORRESPONDENCE = "Correspondence";
        public static final String POLICY = "Policy";
        public static final String AMENDMENT = "Amendment";
        public static final String CERTIFICATE = "Certificate";
        public static final String EOB = "EOB";
        public static final String CLAIM_FORMS = "Claim Forms";
        public static final String CLAIM_LETTER = "Claim Letter";
        public static final String MISCELLANEOUS_DOCUMENT = "Miscellaneous Document";
        public static final String ACKNOWLEDGEMENT = "Acknowledgement";
        public static final String APPROVAL = "Approval";
        public static final String DENIAL = "Denial";
        public static final String CLOSURE = "Closure";
        public static final String DOI_INQUIRIES = "DOI Inquiries";
        public static final String INFORMATION_REQUEST = "Information Request";
    }

    public enum EFolderIndCustomer {
        CORRESPONDENCE("Correspondence"),
        DENTAL_CLAIMS("Dental Claims"),
        ENROLLMENT_BENEFICIARY_FORMS("Enrollment & Beneficiary Forms"),
        EOI("EOI"),
        NON_DENTAL_CLAIMS("Non-Dental Claims");

        private String label;

        EFolderIndCustomer(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderIndCustDentalClaims {
        EOBS("EOBs"),
        FORMS("Forms"),
        OVERPAYMENT_LETTERS("Overpayment Letters"),
        PREDETERMINATIONS("Predeterminations"),
        RECEIPT_OF_CLAIM_LETTERS("Receipt of Claim Letters"),
        STALEDATED_LETTERS("Staledated Letters"),
        OUTBOUND_CORRESPONDENCE("Outbound Correspondence");

        private String label;

        EFolderIndCustDentalClaims(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderIndCustNonDentalClaims {
        EOBS("EOBs"),
        FORMS("Forms"),
        OVERPAYMENT_LETTERS("Overpayment Letters"),
        RECEIPT_OF_CLAIM_LETTERS("Receipt of Claim Letters"),
        STALEDATED_LETTERS("Staledated Letters");

        private String label;

        EFolderIndCustNonDentalClaims(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustomer {
        ADMINISTRATION("Administration"),
        CASE_INSTALLATION("Case Installation"),
        ENROLLMENT_UPLOAD("Enrollment Upload"),
        POLICY_AND_CERT("Policy and Cert"),
        PORTAL_XML_UPLOAD("Portal XML Upload"),
        SALES_SUPPORT("Sales Support"),
        TAX_INFORMATION("Tax Information"),
        UNDERWRITING("Underwriting");

        private String label;

        EFolderNonIndCustomer(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustAdministration {
        ADMINISTRATION_KIT("Administration Kit"),
        AUDIT("Audit"),
        CORRESPONDENCE("Correspondence"),
        TERMINATION("Termination");

        private String label;

        EFolderNonIndCustAdministration(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustCaseInstallation {
        APPLICATION("Application"),
        BINDER("Binder"),
        CHECKLIST("Checklist"),
        CORRESPONDENCE("Correspondence"),
        ENROLLMENT_FORMS("Enrollment Forms"),
        FINAL_CENSUS("Final Census"),
        PRIOR_CARRIER("Prior Carrier"),
        WCB_FILING_DBL_ONLY("WCB Filing (DBL Only)"),
        WELCOME_PACKET("Welcome Packet");

        private String label;

        EFolderNonIndCustCaseInstallation(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustPolicyAndCert {
        ARCHIVE("Archive"),
        BENEFIT_SUMMARIES("Benefit Summaries"),
        CERTIFICATE("Certificate"),
        POLICY("Policy"),
        POLICY_AMENDMENT("Policy Amendment");

        private String label;

        EFolderNonIndCustPolicyAndCert(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustSalesSupport {
        SALES("Sales"),
        SOLD_QUOTE("Sold Quote");

        private String label;

        EFolderNonIndCustSalesSupport(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustUnderwriting {
        ASSUMPTION_OF_RISK_AOR("Assumption of Risk - AOR"),
        POST_ISSUE_UNDERWRITING_INFO("Post-Issue Underwriting Info"),
        REINSURANCE_INFORMATION("Reinsurance Information"),
        RENEWAL("Renewal"),
        UW_REPORTS("UW Reports");

        private String label;

        EFolderNonIndCustUnderwriting(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonIndCustTaxInformation {
        FICA_REPORTS("FICA reports"),
        EMPLOYEE_TAX_FORMS("Employee tax forms");

        private String label;

        EFolderNonIndCustTaxInformation(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderCase {
        NEW_BUSINESS_CENSUS_FILES("New Business Census Files"),
        NEW_BUSINESS_EXPERIENCE("New Business Experience"),
        NEW_BUSINESS_UNDERWRITING_DOCUMENTS("New Business Underwriting Documents"),
        PRIOR_CARRIER_PLANS("Prior Carrier Plans"),
        PROPOSALS("Proposals");

        private String label;

        EFolderCase(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderCaseNBE {
        COMBINED("Combined"),
        DENTAL("Dental"),
        GAC("GAC"),
        LIFE("Life"),
        LTD("LTD"),
        PFL("PFL"),
        STAT("STAT"),
        STD("STD"),
        VISION("Vision");

        private String label;

        EFolderCaseNBE(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderCasePCP {
        COMBINED("Combined"),
        DENTAL("Dental"),
        GAC("GAC"),
        LIFE("Life"),
        LTD("LTD"),
        PFL("PFL"),
        STAT("STAT"),
        STD("STD"),
        VISION("Vision");

        private String label;

        EFolderCasePCP(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderPolicy {
        POLICY_AND_CERT("Policy and Cert"),
        RATE_AREAS_MAPPING("Rate Areas Mapping"),
        RENEWAL_AND_ENDORSEMENT_CENSUS("Renewal and Endorsement Census"),
        RENEWAL_CLAIM_EXPERIENCE("Renewal Claim Experience");

        private String label;

        EFolderPolicy(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderBA {
        AGING("Aging"),
        CANCELLATION("Cancellation"),
        CORRESPONDENCE("Correspondence"),
        INVOICES_AND_BILLS("Invoices and Bills"),
        MISCELLANEOUS("Miscellaneous"),
        PAYMENTS("Payments"),
        RETURN_PREMIUMS("Return Premiums");

        private String label;

        EFolderBA(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderBACancellation {
        TERM_LETTER("Term Letter");

        private String label;

        EFolderBACancellation(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderBAAging {
        REMINDER_LETTER("Reminder Letter"),
        GRACE_LETTER("Grace Letter");

        private String label;

        EFolderBAAging(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderBAPaymBillMaint {
        AGENCY("Agency"),
        MISC("Misc"),
        MORATORIUMS("Moratoriums"),
        PAYMENTS("Payments");

        private String label;

        EFolderBAPaymBillMaint(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderDentalClaim {
        INBOUND_CORRESPONDENCE("Inbound Correspondence"),
        OUTBOUND_CORRESPONDENCE("Outbound Correspondence");

        private String label;

        EFolderDentalClaim(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderDentalClaimInbCorresp {
        ADJUSTMENT_REQUEST("Adjustment Request"),
        DOI_INQUIRIES("DOI Inquiries"),
        INFORMATION_REQUEST("Information Request"),
        MEMBER_APPEAL("Member Appeal"),
        NEW_CLAIM("New Claim"),
        ORTHO("Ortho"),
        OTHER("Other"),
        PROVIDER_RECONSIDERATION("Provider Reconsideration"),
        RETURNED_CHECKS("Returned Checks");

        private String label;

        EFolderDentalClaimInbCorresp(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderDentalClaimOutCorresp {
        DENTAL_RECEIPT_OF_CLAIM_LETTERS("Dental Receipt of Claim Letters"),
        DENTAL_STALEDATE_LETTERS("Dental Staledate Letters"),
        EOBS("EOBs"),
        INFORMATION_REQUEST("Information Request"),
        OVERPAYMENT_LETTERS("Overpayment Letters"),
        PREDETERMINATIONS("Predeterminations");

        private String label;

        EFolderDentalClaimOutCorresp(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaim {
        APPEALS_AND_INQUIRIES("Appeals and Inquiries"),
        SUPPORTING_CLAIM_DOCUMENTATION("Supporting Claim Documentation"),
        CHECKS("Checks"),
        RETURNED_MAIL("Returned Mail"),
        EMPLOYER_NOTIFICATIONS("Employer Notifications"),
        CLAIMANT_NOTIFICATIONS("Claimant Notifications"),
        UNFILED_DOCUMENT("Unfiled Document"),
        GENERAL("General"),
        OUTBOUND_CORRESPONDENCE("Outbound Correspondence"),
        EOB("EOB");

        private String label;

        EFolderNonDentalClaim(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimAppealsInq {
        REQUESTS_FOR_RECONSIDERATION("Requests for Reconsideration"),
        APPEALS("Appeals"),
        DOI_INQUIRIES("DOI Inquiries"),
        INFORMATION_REQUEST("Information Request");

        private String label;

        EFolderNonDentalClaimAppealsInq(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimChecks {
        REFUND_CHECKS("Refund Checks"),
        RETURNED_CHECKS("Returned Checks");

        private String label;

        EFolderNonDentalClaimChecks(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimNotif {
        CLAIMANT_REQUEST_FOR_INFORMATION("Claimant Request for Information"),
        CLAIMANT_STATUS_NOTICE("Claimant Status Notice");

        private String label;

        EFolderNonDentalClaimNotif(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimEmplNotif {
        EMPLOYER_REQUEST_FOR_INFORMATION("Employer Request for Information"),
        EMPLOYER_STATUS_NOTICE("Employer Status Notice");

        private String label;

        EFolderNonDentalClaimEmplNotif(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimGeneral {
        MISCELLANEOUS("Miscellaneous"),
        REINSURANCE("Reinsurance");

        private String label;

        EFolderNonDentalClaimGeneral(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimOutboundCorresp {
        RECEIPT_OF_CLAIM_LETTERS("Receipt of Claim Letters"),
        STALEDATE_LETTERS("Staledate Letters");

        private String label;

        EFolderNonDentalClaimOutboundCorresp(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderNonDentalClaimSuppClaimDoc {
        BENEFICIARY_DOCUMENTATION("Beneficiary Documentation"),
        CLAIM_FORMS("Claim Forms"),
        DEATH_RECORDS("Death Records"),
        ENROLLMENT_DOCUMENTATION("Enrollment Documentation"),
        INTERMITTENT_PFL_CALENDAR("Intermittent PFL Calendar"),
        INVESTIGATION("Investigation"),
        MEDICAL_RECORDS("Medical Records"),
        PAY_AND_TIMECARD_RECORDS("Pay and Timecard Records"),
        SUPPLEMENTAL_DOCUMENTS("Supplemental Documents");

        private String label;

        EFolderNonDentalClaimSuppClaimDoc(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderAgency {
        ACCOUNT_MAINTENANCE("Account Maintenance"),
        APPOINTMENT_DOCS("Appointment Docs"),
        COMMISSION_STATEMENTS("Commission Statements"),
        CORRESPONDENCE("Correspondence"),
        MISCELLANEOUS("Miscellaneous");

        private String label;

        EFolderAgency(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderAgencyAccountMaint {
        ACQUISITIONS("Acquisitions"),
        AOR("AOR");

        private String label;

        EFolderAgencyAccountMaint(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

    public enum EFolderAgencyAppointDocs {
        APPLICATION_PAYMENT_INFO("Application & Payment Info"),
        APPOINTMENT_CONFIRMATION_LETTER("Appointment Confirmation Letter"),
        FFM("FFM"),
        MKT_AGREEMENT("MKT Agreement"),
        NPN("NPN"),
        STATE_CONFIRMATIONS("State Confirmations"),
        STATE_LICENSES("State Licenses"),
        W9("W9");

        private String label;

        EFolderAgencyAppointDocs(String label) {
            this.label = label;
        }

        public String getName() {
            return label;
        }
    }

}
