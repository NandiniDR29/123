/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;

import com.exigen.istf.webdriver.controls.Named;

/**
 * @author ikisly
 */
public class TableConstants {

    /**
     * Named Table Enum for table where column names is optional<br/> and that table doesn't take part in assert<br/> verifications for NamedTable
     */
    public enum Empty implements Named {
        ;
        private String name;

        Empty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ProductsOwned implements Named {
        POLICY_TYPE("Policy Type"),
        CARRIER_NAME("Carrier Name");
        private String name;

        ProductsOwned(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum MyWorkTasks implements Named {
        TASK_NAME("Task Name"),
        PRIORITY("Priority"),
        QUEUE("Queue"),
        CUSTOMER("Customer");
        private String name;

        MyWorkTasks(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ProductRenewals implements Named {
        STATUS("Status");
        private String name;

        ProductRenewals(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Providers implements Named {
        PROVIDER_ID("Provider ID"),
        PROVIDER_NAME("Provider Name"),
        PRACTICE_ID("Practice ID");
        private String name;

        Providers(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ActivitiesAndUserNotesTable implements Named {
        DESCRIPTION("Description"),
        STATUS("Status");
        private String name;

        ActivitiesAndUserNotesTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingNewInstallmentsSchedule implements Named {
        DESCRIPTION("Description"),
        INSTALLMENT_DUE("Installment Due"),
        BILLED_STATUS("Billed Status");
        private String name;

        BillingNewInstallmentsSchedule(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingInstallmentsSchedule implements Named {
        DESCRIPTION("Description"),
        SCHEDULE_DUE_AMOYNT("Schedule Due Amount"),
        BILLED_STATUS("Billed Status");

        BillingInstallmentsSchedule(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingBenefitsAccounts implements Named {
        BILLING_ACCOUNT("Billing Account #"),
        BILLING_ACCOUNT_NAME("Billing Account Name"),
        BILLING_ACCOUNT_NAME_TYPE("Billing Account Name Type"),
        CONSOLIDATED_STATEMENT("Consolidated Statement"),
        STATUS("Status"),
        CURRENT_DUE("Current Due");
        private String name;

        BillingBenefitsAccounts(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingGeneralInformationGB implements Named {
        INVOICING_CALENDAR("Invoicing Calendar"),
        TOTAL_PAID("Total Paid"),
        TYPE("Bill Type"),
        AMOUNT("Amount"),
        CURRENT_DUE("Current Due"),
        PRIOR_DUE("Prior Due"),
        TOTAL_DUE("Total Due"),
        UNALLOCATED_INVOICE_PREMIUM("Unallocated Invoice Premium"),
        SUSPENSE_AMOUNT("Suspense Amount");
        private String name;

        BillingGeneralInformationGB(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingBillableCoveragesGB implements Named {
        POLICY("Policy"),
        POLICY_PLAN("Policy Plan"),
        BILLING_GROUP("Billing Group"),
        TYPE("Type"),
        SUBTYPE("Subtype"),
        MASTER_POLICY_EFF_DATE("Master Policy Eff. Date"),
        COVERAGE_EFF_DATE("Coverage Eff. Date"),
        COVERAGE_CODE("Coverage Code"),
        TOTAL_DUE("Total Due");

        private String name;

        BillingBillableCoveragesGB(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingAdjustPremiumGB implements Named {
        BILLING_GROUP("Billing Group"),
        LOCATION("Location"),
        POLICY_PLAN("Policy Plan"),
        COVERAGE("Coverage"),
        CLASS("Class"),
        SUB_GROUP("Sub-Group"),
        BILLABLE_TERM("Billable Term"),
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        RATE("Rate"),
        RATE_BASIS("Rate Basis"),
        PERIOD_AMOUNT("Period Amount"),
        ADJUSTED_AMOUNT("Adjusted Amount"),
        TOTAL_ADJUSTMENT("Total Adjustment");
        private String name;

        BillingAdjustPremiumGB(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingCurrentPeriod implements Named {
        BILLING_GROUP("Billing Group"),
        LOCATION("Location"),
        POLICY_PLAN("Policy Plan"),
        COVERAGE("Coverage"),
        TIER("Tier"),
        PAYOR("Payor"),
        CLASS("Class"),
        SUB_GROUP("Sub-Group"),
        BILLABLE_TERM("Billable Term"),
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        RATE("Rate"),
        RATE_BASIS("Rate Basis"),
        AMOUNT("Amount");
        private String name;

        BillingCurrentPeriod(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum BillingBillsAndStatementsGB implements Named {
        ISSUE_DATE("Issue Date"),
        DUE_DATE("Due Date"),
        INVOICE("Invoice #"),
        STATUS("Status"),
        CURRENT_DUE("Current Due"),
        BILLING_PERIOD("Billing Period"),
        PRIOR_DUE("Prior Due"),
        UNALLOCATED_INVOICE_PREMIUM("Unallocated Invoice Premium"),
        TOTAL_DUE("Total Due"),
        REMAINING_DUE("Remaining Due"),
        ACTION("Action");

        private String name;

        BillingBillsAndStatementsGB(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingBillsAndStatementsGBByPeriod implements Named {
        INVOICE("Invoice #"),
        BILLING_PERIOD("Billing Period"),
        CURRENT_INVOICE_STATUS("Current Invoice Status"),
        PERIOD_AMOUNT("Period Amount"),
        ACTION("Action");
        private String name;

        BillingBillsAndStatementsGBByPeriod(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingPremiumByCoverageSegmentClassifier implements Named {
        BILLING_GROUP("Billing Group"),
        LOCATION("Location"),
        POLICY_PLAN("Policy Plan"),
        COVERAGE("Coverage"),
        PAYOR("Payor"),
        COVERAGE_SEGMENT_CLASSIFIER("Coverage Segment Classifier"),
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        RATE("Rate"),
        RATE_BASIS("Rate Basis"),
        ESTIMATED_AMOUNT("Estimated Amount"),
        ADJUSTED_AMOUNT("Adjusted Amount");

        private String name;

        BillingPremiumByCoverageSegmentClassifier(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillableCoveragesBillingGroupsByInvoice implements Named {
        BILLING_GROUP("Billing Group"),
        LOCATION("Location"),
        POLICY_PLAN("Policy Plan"),
        COVERAGE("Coverage"),
        PAYOR("Payor"),
        COVERAGE_SEGMENT_CLASSIFIER("Coverage Segment Classifier"),
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        RATE("Rate"),
        RATE_BASIS("Rate Basis"),
        PREMIUM("Premium");

        private String name;

        BillableCoveragesBillingGroupsByInvoice(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PriorPeriodCharges implements Named {
        BILLING_GROUP("Billing Group"),
        LOCATION("Location"),
        POLICY_PLAN("Policy Plan"),
        COVERAGE("Coverage"),
        PAYOR("Payor"),
        COVERAGE_SEGMENT_CLASSIFIER("Coverage Segment Classifier"),
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        RATE("Rate"),
        RATE_BASIS("Rate Basis"),
        PREMIUM("Premium");

        private String name;

        PriorPeriodCharges(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum AllocatedPayments implements Named {
        TRANSACTION_DATE("Transaction Date"),
        EFFECTIVE_DATE("Effective Date"),
        POLICY("Policy #"),
        TYPE("Type"),
        SUBTYPE("Subtype"),
        AMOUNT("Amount");

        private String name;

        AllocatedPayments(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillableCoveragesBillingGroupsByLocation implements Named {
        BILLING_GROUP("Billing Group"),
        PAYOR("Payor"),
        PREMIUM("Premium");

        private String name;

        BillableCoveragesBillingGroupsByLocation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillableCoveragesByGroupByLocation implements Named {
        COVERAGE("Coverage"),
        POLICY_PLAN("Policy Plan"),
        COVERAGE_SEGMENT_CLASSIFIER("Coverage Segment Classifier"),
        PREMIUM("Premium");

        private String name;

        BillableCoveragesByGroupByLocation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillableCoveragesByBillingGroup implements Named {
        COVERAGE("Coverage"),
        POLICY_PLAN("Policy Plan"),
        BILLING_GROUP("Billing Group"),
        PAYOR("Payor"),
        COVERAGE_SEGMENT_CLASSIFIER("Coverage Segment Classifier"),
        PREMIUM("Premium");

        private String name;

        BillableCoveragesByBillingGroup(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillableCoveragesParticipantsByCoverageByLocation implements Named {
        PARTICIPANT("Participant"),
        CERTIFICATES("Certificate(s)"),
        VOLUME("Volume"),
        RATE("Rate"),
        RATE_BASIS("Rate Basis"),
        PREMIUM("Premium");

        private String name;

        BillableCoveragesParticipantsByCoverageByLocation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CurrentPeriod implements Named {
        POLICY_NUMBER("Policy#"),
        EMPLOYEE_NAME("Employee Name"),
        CLAIM_NUMBER("Claim#"),
        CLAIM_PAYMENT_NUMBER("Claim Payment#"),
        TRANSACTION_DATE("Transaction Date"),
        TYPE("Type"),
        SUBTYPE("Subtype"),
        AMOUNT("Amount");

        private String name;

        CurrentPeriod(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingPaymentsAndTransactionsGB implements Named {
        TRANSACTION_DATE("Transaction Date"),
        EFFECTIVE_DATE("Effective Date"),
        POLICY_NUMBER("Policy #"),
        TYPE("Type"),
        SUBTYPE("Subtype"),
        REASON("Reason"),
        AMOUNT("Amount"),
        ACTION("Action"),
        SUSPENSE_AMOUNT("Suspense Amount");
        private String name;

        BillingPaymentsAndTransactionsGB(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingPaymentsAndAdjustmentsGB implements Named {
        TYPE("Type"),
        SUBTYPE("Subtype"),
        REASON("Reason"),
        AMOUNT("Amount"),
        STATUS("Status"),
        ACTION("Action");
        private String name;

        BillingPaymentsAndAdjustmentsGB(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BillingPaymentDeficiency implements Named {
        ;

        BillingPaymentDeficiency(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CoverageRelationships implements Named {
        CLASS_NUMBER("Class Number"),
        CLASS_NAME("Class Name"),
        NUMBER_OF_PARTICIPANTS("Number of Participants"),
        TOTAL_VOLUME("Total Volume"),
        RATE("Rate");
        private String name;

        CoverageRelationships(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CoverageDefinition implements Named {
        COVERAGE_NAME("Coverage Name"),
        PLAN("Plan"),
        CONTRIBUTION_TYPE("Contribution Type"),
        RATE_BASIS("Rate Basis"),
        BENEFIT_TYPE("Benefit Type");
        private String name;

        CoverageDefinition(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PlansAndCoverages implements Named {
        COVERAGE_NAME("Coverage Name"),
        PLAN("Plan"),
        SELF_ADMINISTERED("Self-administered"),
        COVERAGE_EFFECTIVE_DATE("Coverage Effective Date"),
        CLASSIFICATION_GROUPS("Classification Groups");

        private String name;

        PlansAndCoverages(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CertificateCoverages implements Named {
        COVERAGE_NAME("Coverage Name"),
        EFFECTIVE_DATE("Effective Date"),
        BENEFIT_AMOUNT("Benefit Amount"),
        CANCELLATION_DATE("Cancellation Date");

        private String name;

        CertificateCoverages(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CertificateParticipants implements Named {
        ROLE_TYPE("Role Type"),
        ROLE_NAME("Role Name"),
        CUSTOMER_NAME("Customer Name"),
        RELATIONSHIP_TO_INSURED("Relationship to Insured"),
        BENEFIT_AMOUNT("Benefit Amount");

        private String name;

        CertificateParticipants(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CertificateBeneficiaries implements Named {
        ROLE_TYPE("Role Type"),
        ROLE_NAME("Role Name"),
        CUSTOMER_NAME("Customer Name"),
        RELATIONSHIP_TO_INSURED("Relationship to Insured"),
        ROLE_PERCENT("Role Percent");

        private String name;

        CertificateBeneficiaries(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Plans implements Named {
        COVERAGE_NAME("Coverage Name"),
        PLAN("Plan"),
        COVERAGE_TIERS("Coverage Tiers"),
        CONTRIBUTION_TYPE("Contribution Type"),
        RATE_BASIS("Rate Basis");

        private String name;

        Plans(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum FileIntakeManagement implements Named {
        INTAKE_PROFILE_NUMBER("Intake Profile Number"),
        INTAKE_PROFILE_NAME("Intake Profile Name"),
        FILE_TYPE("File Type"),
        RECORD_TYPE("Record Type"),
        STATUS("Status");

        private String name;

        FileIntakeManagement(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum OverrideErrorsTable implements Named {
        OVERRIDE("Override"),
        APPROVAL("Approval"),
        CODE("Code"),
        MESSAGE("Message"),
        DURATION("Duration"),
        REASON_FOR_OVERRIDE("Reason for override");

        private String name;

        OverrideErrorsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryOfPaymentsAndRecoveriesTableExtended implements Named {
        TRANSACTION_STATUS("Transaction Status"),
        TOTAL_PAYMENT_AMOUNT("Total Payment Amount"),
        TOTAL_RECOVERY_AMOUNT("Total Recovery Amount"),
        TOTAL_REMAINING_AMOUNT("Total Remaining Amount"),
        PAYMENT_NUMBER("Payment Number"),
        CHECK_EFT("Check # / EFT #"),
        NOTE("Note"),
        PAYEE_RECOVERED_FROM("Payee / Recovered From"),
        PAYMENT_NET_AMOUNT("Payment Net Amount"),
        PAYMENT_RECOVERY_NUMBER("Payment / Recovery #"),
        POST_DATE("Post Date"),
        RECOVERY_AMOUNT("Recovery Amount"),
        STATUS("Status");

        private String name;

        ClaimSummaryOfPaymentsAndRecoveriesTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryOfPaymentSeriesTableExtended implements Named {
        SERIES_NUMBER("Series Number"),
        PAYEE("Payee"),
        EFFECTIVE_DATE("Effective Date"),
        EXPIRATION_DATE("Expiration Date"),
        FREQUENCY("Frequency"),
        STATUS("Status");

        private String name;

        ClaimSummaryOfPaymentSeriesTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryOfScheduledPaymentsSeriesTableExtended implements Named {
        PAYMENT_SCHEDULE_DATE("Payment Schedule Date"),
        GROSS_AMOUNT("Gross Amount"),
        ADJUSTED_GROSS_BENEFIT_AMOUNT("Adjusted Gross Benefit Amount"),
        OFFSET_AMOUNT("Offset Amount"),
        PAYMENT_AMOUNT("Payment Amount"),
        PAYMENT_FROM_THROUGH_DATE("Payment From-Through Date");

        private String name;

        ClaimSummaryOfScheduledPaymentsSeriesTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryOfPostedPaymentsOfSeriesTableExtended implements Named {
        PAYMENT_NUMBER("Payment Number"),
        PAYMENT_POST_DATE("Payment Post Date"),
        GROSS_AMOUNT("Gross Amount");

        private String name;

        ClaimSummaryOfPostedPaymentsOfSeriesTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryClaimDataTableExtended implements Named {
        TYPE_OF_TRANSACTION("Type of Transaction"),
        RECEIVED_DATE("Received Date"),
        PREDETERMINATION_NUM("Predetermination #"),
        SOURCE("Source"),
        CLEAN_CLAIM_DATE("Clean Claim Date"),
        PAYEE_TYPE("Payee Type"),
        ORTHO_MONTHS("Ortho Months"),
        OTHER_COVERAGE("Other Coverage?");

        private String name;

        ClaimSummaryClaimDataTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryDigitalImageTableExtended implements Named {
        DIGITAL_IMAGE("Digital Image #");

        private String name;

        ClaimSummaryDigitalImageTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimTablePatientHistory implements Named {
        POLICY_NUM("Policy #"),
        CLAIM_NUM("Claim #"),
        PROVIDER("Provider"),
        DOS("DOS"),
        SUBMITTED_CDT_CODE("Submitted CDT Code"),
        COVERED_CDT_CODE("Covered CDT Code"),
        CATEGORY("Category"),
        TOOTH("Tooth"),
        ORAL_CAVITY("Oral Cavity"),
        SURFACE("Surface"),
        QUANTITY("Quantity"),
        CHARGE("Charge"),
        DEDUCTIBLE("Deductible");

        private String name;

        ClaimTablePatientHistory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimDentalClaimBalanceExtendedTable implements Named {

        DATE_OF_TRANSACTION("Date of Transaction"),
        CLAIM_ID("Claim #"),
        PAYMENT_RECOVERY("Payment / Recovery #"),
        CHECK_EFT("Check # / EFT"),
        PAYEE_RECOVERED_FROM("Payee / Recovered From"),
        TRANSACTION_AMOUNT("Transaction Amount"),
        TRANSACTION_TYPE("Transaction Type"),
        TRANSACTION_COMMENT("Transaction Comment");

        private String name;

        ClaimDentalClaimBalanceExtendedTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimSummaryResultsOfAdjudicationTableExtended implements Named {
        LINE_ID("Line ID"),
        COVERED_CDT_CODE("Covered CDT Code"),
        EHB("EHB"),
        CATEGORY("Category"),
        COVERED("Covered"),
        COPAY("Copay"),
        DEDUCTIBLE("Deductible"),
        COINSURANCE_AMOUNT("Coinsurance Amount"),
        COB_APPLIED("COB Applied"),
        BENEFIT_AMOUNT("Benefit Amount"),
        PATIENT_RESPONSIBILITY("Patient Responsibility"),
        DECISION("Decision"),
        REMARK_CODE("Remark Code"),
        INN_OON("INN / OON"),
        PLAN_TO_PAY("Plan To Pay"),
        ACTIONS("Actions"),
        IR_CODE("IR Code");

        private String name;

        ClaimSummaryResultsOfAdjudicationTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClaimPaymentViewTableExtended implements Named {
        PAYMENT_NUMBER("Payment Number"),
        GROSS_AMOUNT("Gross Amount"),
        ADJUSTED_GROSS_BENEFIT_AMOUNT("Adjusted Gross Benefit Amount"),
        OFFSET_AMOUNT("Offset Amount"),
        TAX_AMOUNT("Tax Amount"),
        PAYMENT_AMOUNT("Payment Amount");

        private String name;

        ClaimPaymentViewTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CommissionsTable implements Named {
        COVERAGE_NAME("Coverage Name"),
        PREMIUM_RECEIVED_PER_POLICY_YEAR("Premium Received per Policy Year ($)"),
        NUMBER_OF_SUBSCRIBERS("Number of Subscribers"),
        RATE("Rate"),
        OVERRIDDEN("Overridden?");
        private String name;

        CommissionsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumSummaryCoveragesTable implements Named {
        COVERAGE_NAME("Coverage Name"),
        PLAN("Plan"),
        PLAN_NAME("Plan Name"),
        CONTRIBUTION_TYPE("Contribution Type"),
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        MANUAL_RATE("Manual Rate"),
        EXPERIENCE_RATE("Experience Rate"),
        EXPERIENCE_ADJUSTMENT_FACTOR("Experience Adjustment Factor"),
        FORMULA_RATE("Formula Rate"),
        ANNUAL_PREMIUM("Annual Premium"),
        MODAL_PREMIUM("Modal Premium"),
        QUOTE_RATE("Quote Rate");

        private String name;

        PremiumSummaryCoveragesTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum AgenciesProducersTable implements Named {
        AGENCY_PRODUCER("Agency/Producer"),
        PRIMARY("Primary?"),
        AGENT_SUBPRODUCER("Agent/Sub-Producer"),
        ICP("ICP?"),
        COMMISSION_SPLIT("Commission Split?"),
        AGENCY_TYPE("Agency Type");

        private String name;

        AgenciesProducersTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PlanTierAndRatingSelection implements Named {
        COVERAGE_TIER("Coverage Tier"),
        NUMBER_OF_PARTICIPANTS("Number of Participants"),
        RATE("Rate");

        private String name;

        PlanTierAndRatingSelection(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumSummaryASOFeeTable implements Named {
        PARTICIPANTS("Participants"),
        ASO_FEE_BASIS("ASO Fee Basis"),
        UNDERWRITTEN_ASO_FEE("Underwritten ASO Fee"),
        UNDERWRITTEN_ANNUAL_ASO_FEE("Underwritten Annual ASO Fee"),
        PROPOSED_ANNUAL_ASO_FEE("Proposed Annual ASO Fee");

        private String name;

        PremiumSummaryASOFeeTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ProposalASOFeeTable implements Named {
        PARTICIPANTS("Participants"),
        ASO_FEE_BASIS("ASO Fee Basis"),
        UNDERWRITTEN_ASO_FEE("Underwritten ASO Fee"),
        UNDERWRITTEN_ANNUAL_ASO_FEE("Underwritten Annual ASO Fee"),
        PROPOSED_ASO_FEE("Proposed ASO Fee"),
        ASO_FEE_ADJUSTMENT("ASO Fee Adjustment %"),
        PROPOSED_ANNUAL_ASO_FEE("Proposed Annual ASO Fee"),
        FEE_UPDATE_REASON("Fee Update Reason");

        private String name;

        ProposalASOFeeTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum SearchResultsTable implements Named {
        CLAIM_NUMBER("Claim #"),
        DATE_OF_LOSS("Date of Loss"),
        TYPE_OF_CLAIM("Type of Claim"),
        POLICY_NUMBER("Policy #"),
        STATUS("Status"),
        INSURED("Insured"),
        CLAIM_FILE_OWNER("Claim File Owner"),
        TOTAL_INCURRED("Total Incurred");

        private String name;

        SearchResultsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassificationSubGroupSelection implements Named {
        CLASSIFICATION_SUB_GROUP_NAME("Classification Sub-Group Name");

        private String name;

        ClassificationSubGroupSelection(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CoveragePremiumTable implements Named {
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        RATE_ADJUSTMENT_PERCENTAGE("Rate Adjustment %"),
        RATE_UPDATE_REASON("Rate Update Reason"),
        UNDERWRITTEN_TERM_RATE("Underwritten Term Rate"),
        PROPOSED_ANNUAL_PREMIUM("Proposed Annual Premium"),
        UNDERWRITTEN_ANNUAL_PREMIUM("Underwritten Annual Premium"),
        PROPOSED_TERM_RATE("Proposed Term Rate");

        private String name;

        CoveragePremiumTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassificationSubGroupsAndRatingColumns implements Named {
        CLASSIFICATION_SUB_GROUP_NAME("Classification Sub-Group Name"),
        CRITERIA("Criteria"),
        NUMBER_OF_PARTICIPANTS("Number of Participants"),
        TOTAL_VOLUME("Total Volume"),
        TYPE("Type"),
        RATE("Rate");

        private String name;

        ClassificationSubGroupsAndRatingColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum AsoFeeTable implements Named {
        ASO_FEE_BASIS("ASO Fee Basis"),
        ASO_FEE("ASO Fee"),
        DAILY_ASO_FEE("Daily ASO Fee"),
        ANNUAL_ASO_FEE("Annual ASO Fee"),
        PROPOSED_ASO_FEE("Proposed ASO Fee");

        private String name;

        AsoFeeTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum RateDetailsTable implements Named {
        COVERAGE_NAME("Coverage Name"),
        CLASS_NAME("Class Name"),
        SUB_GROUP_NAME("Sub-Group Name"),
        TIER("Tier"),
        GENDER("Gender"),
        TOBACCO("Tobacco"),
        MIN_AGE("Min Age"),
        MAX_AGE("Max Age"),
        RATE_BASIS("Rate Basis"),
        RATE("Rate"),
        IN_FORCE_RATE("In Force Rate");

        private String name;

        RateDetailsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum RateHistoryTable implements Named {
        COVERAGE_NAME("Coverage Name"),
        PLAN_NAME("Plan Name"),
        MANUAL_RATE("Manual Rate"),
        QUOTE_RATE("Quote Rate"),
        PROPOSED_RATE("Proposed Rate"),
        QUOTE_PROPOSAL("Quote Proposal"),
        PROPOSAL_RATE("Proposal Rate"),
        SOLD_RATE("Sold Rate"),
        MANUAL_RATE_AT_ISSUE("Manual Rate (At Issue)"),
        FINAL_RATE("Final Rate");

        private String name;

        RateHistoryTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ManagePaymentMethodsTable implements Named {
        BILLING_ACCOUNT("Billing Account"),
        POLICY_NUMBER("Policy #"),
        EFFECTIVE_DATE("Effective Date"),
        STATUS("Status"),
        PAYMENT_METHOD_FOR_RECURRING_PAYMENTS("Payment Method for Recurring Payments");

        private String name;

        ManagePaymentMethodsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum IncludeBAInTheConsolidatedStatement implements Named {
        BILLING_ACCOUNT("Billing Account #"),
        BILLING_ACCOUNT_NAME("Billing Account Name"),
        ADDRESS("Address"),
        ACTIONS("Actions");
        private String name;

        IncludeBAInTheConsolidatedStatement(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum RelatedBillingAccounts implements Named {
        BILLING_ACCOUNT("Billing Account #"),
        BILLING_ACCOUNT_NAME("Billing Account Name"),
        ADDRESS("Address");
        private String name;

        RelatedBillingAccounts(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassificationGroupPlanRelationships implements Named {
        CLASS_NAME("Class Name"),
        NUMBER_OF_PARTICIPANTS("Number of Participants"),
        RATE("Rate"),
        PLAN("Plan");
        private String name;

        ClassificationGroupPlanRelationships(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum AgencyCommission implements Named {
        PREMIUM_RECEIVED_PER_P_YEAR("Premium Received per Policy Year ($)"),
        NUMBER_OF_SUBSCRIBERS("Number of Subscribers"),
        COVERAGE_NAME("Coverage Name"),
        RATE("Rate");

        private String name;

        AgencyCommission(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumSummaryRatingReportTierColumns implements Named {
        ADJUSTED_MANUAL_RATES("ADJUSTED MANUAL RATES"),
        ADJUSTED_FORMULA_RATES("ADJUSTED FORMULA RATES"),
        CURRENT_ENROLLMENT("CURRENT ENROLLMENT"),
        CURRENT_RATES("CURRENT RATES"),
        RENEWAL_RATES("RENEWAL RATES"),
        REDISTRIBUTED_RATES("REDISTRIBUTED RATES");

        private String name;

        PremiumSummaryRatingReportTierColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum LifeAndDisabilityClaims implements Named {
        CLAIM_NUM("Claim #"),
        DATE_REPORTED("Date Reported"),
        DATE_OF_LOSS("Date of Loss"),
        CAUSE_OF_LOSS("Cause of Loss"),
        TYPE_OF_CLAIM("Type of Claim"),
        POLICY_PRODUCT("Policy Product"),
        DESCRIPTION_OF_LOSS_EVENT("Description of Loss Event"),
        CLAIM_FILE_OWNER("Claim File Owner"),
        STATUS("Status");

        private String name;

        LifeAndDisabilityClaims(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum DentalClaims implements Named {
        CLAIM_NUM("Claim #"),
        STATUS("Status"),
        POLICY_NUM("Policy #"),
        INSURED("Insured"),
        PATIENT("Patient"),
        DOS("DOS"),
        TIN("TIN"),
        LICENSE_NPI("License NPI"),
        BENEFIT_AMOUNT("Benefit Amount");

        private String name;

        DentalClaims(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassificationGroupsTable implements Named {
        CLASS_NAME("Class Name");

        private String name;

        ClassificationGroupsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassificationSubGroupsTable implements Named {
        SUB_GROUP_NAME("Sub-group Name"),
        GENDER("Gender"),
        MIN_AGE("Min Age"),
        MAX_AGE("Max Age"),
        TOBACCO("Tobacco");

        private String name;

        ClassificationSubGroupsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum RateHistoryForSubGroupTable implements Named {
        RATE_TYPE("Rate Type"),
        RATE("Rate"),
        ANNUAL_PREMIUM("Annual Premium"),
        VOLUME("Volume"),
        NUMBER_OF_PARTICIPANTS("Number of Participants"),
        REASON("Reason");

        private String name;

        RateHistoryForSubGroupTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
