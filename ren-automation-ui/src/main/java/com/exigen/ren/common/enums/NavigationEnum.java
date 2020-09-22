/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.enums;

public class NavigationEnum {

    public interface Navigational {
        String get();
    }

    public enum WebStudioHeaderNavigation implements Navigational {
        REPOSITORY_PAGE("REPOSITORY"),
        EDITOR_PAGE("EDITOR"),
        ADMIN_PAGE("ADMIN");

        private String name;

        WebStudioHeaderNavigation(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return name;
        }
    }

    public enum WebStudioEditorPageLeftMenuNavigation implements Navigational {
        DECISION("Decision"),
        DATA("Data"),
        VOCABULARY("Vocabulary"),
        CONFIGURATION("Configuration"),
        PROPERTIES("Properties");

        private String name;

        WebStudioEditorPageLeftMenuNavigation(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return name;
        }
    }

    public enum WebStudioRepositoryPageLeftMenu implements Navigational {
        PROJECTS("Projects"),
        DEPLOY_CONFIGURATION("Deploy Configurations");

        private String name;

        WebStudioRepositoryPageLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return name;
        }
    }

    public enum AppMainTabs implements Navigational {
        ACCOUNT("Account"),
        BILLING("Billing"),
        CASE("Case"),
        CLAIM("Claim"),
        CUSTOMER("Customer"),
        MY_WORK("My Work"),
        POLICY("Policy"),
        QUOTE("Quote"),
        REPORTS("Reports");

        String name;

        AppMainTabs(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return name;
        }
    }

    public enum AdminAppMainTabs implements Navigational {
        AGENCY_VENDOR("Agency/Vendor"),
        BILLING("Billing"),
        CEM("CEM"),
        CMS("CMS"),
        COMMISSION("Commission"),
        CRM("CRM"),
        GENERAL("General"),
        PRODUCT("Product"),
        REPORTS("Reports"),
        SECURITY("Security"),
        TAXES_FEES("Taxes/Fees"),
        WORK_FLOW("Work Flow");

        String name;

        AdminAppMainTabs(String name) {
            this.name = name;
        }

        public String get() {
            return name;
        }
    }

    public enum GeneralTabLeftMenu implements Navigational {
        DB_LOOKUPS("DB Lookups"),
        NUMBER_OF_RANGES("# Ranges"),
        BULLETIN("Bulletin"),
        SCHEDULER("Scheduler"),
        ASYNC_TASKS("Async Tasks"),
        NOTES("Notes"),
        CATASTROPHE_REGISTRY("Catastrophe Registry");

        String name;

        GeneralTabLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return name;
        }
    }

    public enum WorkFlowLeftMenu implements Navigational {
        PROCESS_MANAGEMENT("Process Management"),
        WORK_GROUPS("Work Groups"),
        BUSINESS_EVENT_REPOSITORY("Business Event Repository");

        String name;

        WorkFlowLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum AgencyVendorLeftMenu implements Navigational {

        CARRIER_INFO("Carrier Info"),
        AGENCY("Agency"),
        BRAND("Brand"),
        VENDOR("Vendor"),
        FINANCIAL_INSTITUTION("Financial Institution"),
        PARTNER("Partner"),
        AGENCY_TRANSFER("Agency Transfer");

        String name;

        AgencyVendorLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum AgencyVendorVendorSubTab implements Navigational {
        VENDOR_INFO("Vendor Info");

        String name;

        AgencyVendorVendorSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum SecurityLeftMenu implements Navigational {
        PROFILE("Profile"),
        GROUP("Group"),
        ROLE("Role"),
        PRODUCT_ACCESS_ROLE("Product Access Role");

        String name;

        SecurityLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum SecurityProfileSubTabMenu implements Navigational {
        GENERAL("General"),
        AUTHORITY_LEVELS("Authority Levels");

        String name;

        SecurityProfileSubTabMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum CMSLeftMenu implements Navigational {
        TEMPLATE_MGMT("Template Mgmt"),
        EFOLDER_MGMT("Efolder Mgmt"),
        PRINT_MONITOR("Print Monitor");

        String name;

        CMSLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum CMSTemplateMgmtSubTab implements Navigational {
        DOC_TEMPLATES("Doc Templates"),
        FILES("Files"),
        DOC_PACKAGES("Doc Packages");

        String name;

        CMSTemplateMgmtSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum CMSEfolderMgmtSubTab implements Navigational {

        EFOLDER_TREE("Efolder Tree"),
        DOCUMENT_TYPES("Document Types"),
        PRODUCT_SUPPORT("Product Support");

        String name;

        CMSEfolderMgmtSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum CMSPrintMonitorSubTab implements Navigational {

        PRINT_JOBS("Print Jobs"),
        PRINT_REQS("Print Reqs"),
        PRINT_DOCS("Print Docs");

        String name;

        CMSPrintMonitorSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum ProductLeftMenu implements Navigational {
        PRODUCT_FACTORY("Product Factory"),
        SCHEME("Scheme"),
        SHORT_RATE_CANCELLATION("Short Rate Cancellation"),
        POLICY_PLAN("Policy Plan"),
        ENDORSEMENT_FORM_RELATIONSHIP("Endorsement Form Relationship"),
        AUTOMATED_PROCESSING("Automated Processing"),
        MORATORIUM("Moratorium"),
        CASCADING_PROCESSING("Cascading Processing");

        String name;

        ProductLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum CommissionLeftMenu implements Navigational {
        BULK_STRATEGY("Bulk Strategy"),
        COMMISSION_BONUS("Commission Bonus"),
        COMMISSION_CLAWBACK("Commission Clawback"),
        COMMISSION_GROUP("Commission Group"),
        COMMISSION_TEMPLATE("Commission Template"),
        COMMISSION_HOLD("Commission Hold"),
        COMMISSION_REFERRAL("Commission Referral"),
        GB_COMMISSION_STRATEGY("GB Commission Strategy"),
        PC_COMMISSION_STRATEGY("P&C Commission Strategy"),
        COMMISSION_CORRECTION("Commission Correction"),
        BULK_ADJUSTMENT("Bulk Adjustment");

        String name;

        CommissionLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingLeftMenu implements Navigational {
        BILLING_CYCLE("Billing Cycle"),
        GLOBAL("Global"),
        RULES("Rules");

        String name;

        BillingLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingBillingCycleSubTab implements Navigational {
        BILLS("Bills"),
        CALENDARS("Calendars"),
        CANCELLATIONS("Cancellations"),
        EFT_RECURRING("EFT/Recurring"),
        WRITE_OFF("Write-Off"),
        BILLING_CALENDAR("Billing Calendar"),
        PAYROLL_CALENDAR("Payroll Calendar"),
        DEFAULT_INVOICING_CALENDAR("Default Invoicing Calendar");

        String name;

        BillingBillingCycleSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingBillingCycleCancellationsSubTab implements Navigational {
        PERSONAL_COMMERCIAL("Personal/Commercial"),
        BENEFITS("Benefits");

        String name;

        BillingBillingCycleCancellationsSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingBillingCycleCalendarsSubTab implements Navigational {
        BILLING_CALENDAR("Billing Calendar"),
        PAYROLL_CALENDAR("Payroll Calendar"),
        DEFAULT_INVOICING_CALENDAR("Default Invoicing Calendar");

        String name;

        BillingBillingCycleCalendarsSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingGlobalSubTab implements Navigational {
        AUTOMATIC_BILLING_ACCOUNT("Automatic Billing Account"),
        BILLING_HOLD_MORATORIUM_REASONS("Billing Hold & Moratorium Reasons"),
        CROSS_POLICY_PAYMENT_TRANSFERS("Cross Policy Payment Transfers"),
        DECLINE_PAYMENT_REASON_TYPE("Decline Payment Reason & Type"),
        DOWN_PAYMENT_REASON("Down Payment Reason"),
        OTHER_TRANSACTIONS("Other Transactions"),
        PAYMENT_TIME("Payment Time"),
        WRITE_OFF_REASONS("Write-Off Reasons"),
        TRANSFER_PAYMENT_REASON_TYPE("Transfer Payment Reason & Type");

        String name;

        BillingGlobalSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingRulesSubTab implements Navigational {
        BILLING_FEES("Billing Fees"),
        NO_APPROVAL_REQUIRED_MAX("No Approval Required Max"),
        OFF_CYCLE("Off-Cycle"),
        OUTSTANDING_AMOUNT("Outstanding Amount"),
        PAYMENT_DEFICIENCY("Payment Deficiency"),
        PAYMENT_PLANS("Payment Plans"),
        PAYMENT_TOLERANCE("Payment Tolerance"),
        REFUND_APPROVAL_WAIT_DAYS("Refund Approval & Wait Days"),
        RENEWAL_HOLD_("Renewal Hold "),
        RENEWAL_PARAMETERS("Renewal Parameters"),
        SKIPPED_BILLS("Skipped Bills"),
        WRITE_OFF("Write-Off"),
        INSTALLMENT_START_DATE("Installment Start Date"),
        OTHER_TRANSACTION("OTher Transaction");

        String name;

        BillingRulesSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum BillingRulesWriteOffSubTab implements Navigational {
        PERSONAL_COMMERCIAL("Personal/Commercial"),
        BENEFITS("Benefits");

        String name;

        BillingRulesWriteOffSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum CEMLeftMenu implements Navigational {
        CUSTOMER_CORE_ADMIN("Customer Core Admin"),
        LEAD_IMPORT("Lead Import"),
        CAMPAIGNS("Campaigns"),
        MAJOR_LARGE_ACCOUNT("Major/Large Account"),
        GROUPS_INFORMATION("Groups Information"),
        CEM_CONFIGURATION("CEM Configuration");

        String name;

        CEMLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum TaxesAndFeesLeftMenu implements Navigational {
        FEE_AND_TAX_REGISTRY("Fee and Tax Registry"),
        FEE_AND_TAX_STRATEGY("Fee and Tax Strategy"),
        FEE_GROUP("Fee Group");

        String name;

        TaxesAndFeesLeftMenu(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum AgencyVendorCarrierInfoSubTab implements Navigational {
        BANKING_DETAILS("Banking Details"),
        COMPANIES("Companies"),
        CORPORATE_INFO("Corporate Info");

        private String name;

        AgencyVendorCarrierInfoSubTab(String name) {
            this.name = name;
        }

        @Override
        public String get() {
            return this.name;
        }
    }

    public enum AgencyVendorTab implements Navigational {
        AGENCY_INFO("Agency Info"),
        BANKING_DETAILS("Banking Details"),
        CHILDREN("Children"),
        COMMISSION("Commission"),
        CONTACT_INFO("Contact Info"),
        SUPPORT_TEAM("Support Team"),
        USERS("Users"),
        LICENSE_INFO("License Info");

        String id;

        AgencyVendorTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum ClaimTab implements Navigational {
        ADJUDICATION("Adjudication"),
        COMPLETE_NOTIFICATION("Complete Notification"),
        DAMAGES("Damages"),
        EVALUATION("Evaluation"),
        EVENT("Event"),
        FNOL("FNOL"),
        GENERAL("General"),
        HISTORY("History"),
        PATIENT_HISTORY("Patient History"),
        LOSS_CONTEXT("Loss Context"),
        LOSS_EVENT("Loss Event"),
        OTHER_PARTIES("Other Parties"),
        OVERVIEW("Overview"),
        PAYMENTS("Payments"),
        POLICY_SUMMARY("Policy Summary"),
        RELATED_CLAIMS("Related Claims"),
        REPORTING_PARTY_AND_CLAIM_CONTACT("Reporting Party and Claim Contact"),
        SECURITY("Security"),
        SERVICE_REQ("Service Req."),
        SUBROGATION("Subrogation"),
        FINANCIALS("Financials"),
        EDIT_CLAIM("Edit Claim"),
        CLAIM_HISTORY("Claim History");

        String id;

        ClaimTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum ClaimFNOLLeftMenu implements Navigational {
        REPORTING_PARTY_AND_CLAIM_CONTACT("Reporting Party and Claim Contact"),
        POLICY_INFORMATION("Policy Information"),
        PARTICIPANT_INFORMATION("Participant Information"),
        PARTICIPANT_COVERAGE("Participant Coverage"),
        DEPENDENT_INFORMATION("Dependent Information"),
        DEPENDENT_COVERAGE("Dependent Coverage"),
        SPONSOR("Sponsor"),
        EVENT_INFORMATION("Event Information"),
        LOSS_EVENT("Loss Event"),
        BENEFITS("Benefits"),
        BENEFIT_SUMMARY("Benefit Summary"),
        COVERAGE_DETERMINATION("Coverage Determination"),
        PFL_PARTICIPANT_INFORMATION("PFL Participant Information"),
        PFL_QUALIFYING_EVENT("PFL Qualifying Event"),
        ADDITIONAL_PARTIES("Additional Parties"),
        WITNESS("Witness"),
        ADDITIONAL_PARTY("Additional Party"),
        COVERAGE_EVALUATION("Coverage Evaluation"),
        COMPLETE_NOTIFICATION("Complete Notification");

        String id;

        ClaimFNOLLeftMenu(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum ClaimCalculateSingleBenefitTab implements Navigational {
        COVERAGE_DETERMINATION_AND_RESERVES("Coverage Determination and Reserves"),
        OTHER_INCOME_BENEFIT("Other Income Benefit"),
        DEDUCTIONS("Deductions"),
        TAX("Tax"),
        FEATURE_HANDING("Feature Handing"),
        CALCULATOR("Calculator");

        String id;

        ClaimCalculateSingleBenefitTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum ClaimPaymentsTab implements Navigational {
        PAYMENT_DETAILS("Payment Details"),
        PAYMENT_ALLOCATION("Payment Allocation"),
        ADDITIONAL_PAYEE("Additional Payee"),
        INVOICE_FROM("Invoice From"),
        INVOICE_DETAILS("Invoice Details"),
        PAYMENT_SERIES_PROFILE("Payment Series Profile"),
        CALCULATOR("Calculator");

        String id;

        ClaimPaymentsTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum ClaimRecoveryTab implements Navigational {
        RECOVERY_DETAILS("Recovery Details"),
        RECOVERY_ALLOCATION("Recovery Allocation"),
        RECOVERED_FROM("Recovered From");

        String id;

        ClaimRecoveryTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum CustomerSummaryTab implements Navigational {
        ACCOUNT("Account"),
        CAMPAIGN("Campaign"),
        CENSUS("Census"),
        COMMUNICATION("Communication"),
        CONTACTS_RELATIONSHIPS("Contacts & Relationships"),
        CUSTOMER("Customer Info"),
        OPPORTUNITY("Opportunity"),
        PORTFOLIO("Portfolio");

        String id;

        CustomerSummaryTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum CustomerTab implements Navigational {
        EMPLOYEE_INFO("Employee Info"),
        GENERAL("General"),
        PROVIDER("Provider"),
        RELATIONSHIP("Relationship"),
        DIVISIONS("Divisions"),
        BUSINESS_ENTITY("Business Entity");

        String id;

        CustomerTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum CaseProfileTab implements Navigational {
        CASE_PROFILE("Case Profile"),
        CASE_PROFILE_DETAILS("Case Profile Details"),
        PRODUCT_AND_PLAN_MANAGMENT("Product & Plan Managment"),
        CLASSIFICATION_MANAGEMENT("Classification Management"),
        LOCATION_MANAGEMENT("Location Management"),
        FILE_INTAKE_MANAGEMENT("File Intake Management"),
        PROPOSALS("Proposals");

        String id;

        CaseProfileTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum FileIntakeManagementTab implements Navigational {
        PROFILE_DEFINITION("Profile Definition"),
        PROCESSING_RESULTS("Processing Results");

        String id;

        FileIntakeManagementTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum QuoteProposalTab implements Navigational {
        PROPOSAL("Proposal"),
        QUOTES_SELECTION("Quotes Selection");

        String id;

        QuoteProposalTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum AccountTab implements Navigational {
        ACCOUNT_INFO("Acct. Info"),
        CONTACTS("Designated Contacts"),
        GROUPS("Affinity Groups");

        String id;

        AccountTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum GroupBenefitsTab implements Navigational {
        POLICY_INFORMATION("Policy Information"),
        PLAN_DEFINITION("Plan Definition"),
        CLASSIFICATION_MANAGEMENT("Classification Management"),
        COVERAGE_DEFINITION("Coverage Definition"),
        COVERAGES("Coverages"),
        ENROLLMENT("Enrollment"),
        INSURED("Insured"),
        MASTER_POLICY("Master Policy"),
        PREMIUM_SUMMARY("Premium Summary"),
        PLAN_GENERIC_INFO("Plan Generic Info"),
        CASE_INSTALLATION("Case Installation"),
        OPTIONAL_BENEFITS("Optional Benefits"),
        ISSUE("Issue"),
        CERTIFICATE_POLICY("Certificate Policy");

        String id;

        GroupBenefitsTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum PlanGenericInfoTab implements Navigational {
        PLAN_GENERIC_INFO("Plan Generic Info"),
        PLAN_DEFINITION("Plan Definition"),
        OPTIONAL_BENEFITS("Optional Benefits"),
        BASIC_BENEFITS("Basic Benefits"),
        ENHANCED_BENEFITS_A_TO_C("Enhanced Benefits (A to C)"),
        ENHANCED_BENEFITS_D_TO_F("Enhanced Benefits (D to F)"),
        ENHANCED_BENEFITS_H_TO_L("Enhanced Benefits (H to L)"),
        ENHANCED_BENEFITS_M_TO_T("Enhanced Benefits (M to T)");

        String id;

        PlanGenericInfoTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum ReportsTab implements Navigational {
        BUSINESS_ACTIVITIES("Business Activities"),
        OPERATIONAL_REPORTS("Operational Reports"),
        DASHBOARD("Dashboard"),
        TEMPLATES("Templates");

        String id;

        ReportsTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

    public enum AgencyVendorAgencyTab implements Navigational {
        AGENCY("Agency"),
        LICENSE("License");

        private String id;

        AgencyVendorAgencyTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return this.id;
        }
    }

    public enum ClaimFNOLTab implements Navigational {
        COVERAGE_DETERMINATION("Coverage Determination"),
        CLAIM_HANDLING("Claim Handling"),
        SPECIAL_HANDLING("Special Handling");

        String id;

        ClaimFNOLTab(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }


    public enum ClaimBenefitLeftMenu implements Navigational {
        DAMAGE_DETAILS("Damage Details"),
        PREMIUM_WAIVER("Premium Waiver"),
        INJURY_PARTY_INFORMATION("Injury Party Information"),
        INCIDENT("Incident"),
        COVERAGE_EVALUATION("Coverage Evaluation"),
        VENDOR_PROVIDER_SERVICES("Vendor/Provider Services"),
        SERVICE_REQUEST("Service Request");

        String id;

        ClaimBenefitLeftMenu(String id) {
            this.id = id;
        }

        @Override
        public String get() {
            return id;
        }
    }

}
