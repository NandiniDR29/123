/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.EnrollmentProcessingResults.NAME;

public class PolicySummaryPage extends SummaryPage {

    public static StaticElement labelPolicyNumber = new StaticElement(By.id("productContextInfoForm:policyDetail_policyNumTxt"));
    public static StaticElement labelCustomerName = new StaticElement(By.id("productContextInfoForm:policyDetail_customerDisplayNameText"));
    public static StaticElement labelPolicyStatus = new StaticElement(By.id("productContextInfoForm:policyDetail_policyStatusCdText"));
    public static StaticElement labelPolicyEffectiveDate = new StaticElement(By.id("productContextInfoForm:policyDetail_policyStatusCdText_txEffectiveDate"));
    public static StaticElement labelPolicyBillingAccount = new StaticElement(By.id("productContextInfoForm:policyDetail_policyBilling"));
    public static StaticElement labelProductName = new StaticElement(By.id("productContextInfoForm:policyDetail_productCdText"));
    public static StaticElement labelPendedEndorsement = new StaticElement(By.id("productContextInfoForm:lnkPendedEndorsements"));
    public static StaticElement labelRenewals = new StaticElement(By.id("productContextInfoForm:lnkRenewals"));
    public static StaticElement labelCancelNotice = new StaticElement(By.id("productContextInfoForm:cancelNoticeFlag"));
    public static StaticElement labelDoNotRenew = new StaticElement(By.id("productContextInfoForm:doNotRenewFlag"));
    public static StaticElement labelTermIncludesLapsePeriod = new StaticElement(By.id("productContextInfoForm:lapseExistsFlag"));
    public static StaticElement labelManualRenew = new StaticElement(By.id("productContextInfoForm:manualRenewFlag"));
    public static StaticElement labelPremiumWaived = new StaticElement(By.id("productContextInfoForm:premiumWaivedFlag"));
    public static StaticElement labelStatus = new StaticElement(By.id("productContextInfoForm:policyDetail_policyStatusCdText"));
    public static StaticElement labelCascadingTransaction = new StaticElement(By.id("productContextInfoForm:cascadingTransactionInProgressFlag"));
    public static StaticElement labelSubContent = new StaticElement(By.id("productContextInfoForm:flagSubContents"));
    public static StaticElement labelStatusInquiryMode = new StaticElement(By.xpath("//li[contains(@id, 'policyDataGatherForm:bannerGroup')]/span[contains(text(), 'Status:')]"));

    public static Button buttonTransactionHistory = new Button(By.id("productContextInfoForm:lnkTransactionHistory"));
    public static Button buttonPendedEndorsement = new Button(By.id("productContextInfoForm:lnkPendedEndorsements"));
    public static Button buttonRenewals = new Button(By.id("productContextInfoForm:lnkRenewals"));
    public static Button buttonPolicyOverview = new Button(By.id("historyForm:backToSummary"));
    public static Button buttonQuoteOverview = new Button(By.id("quoteVersionHistoryForm:backToSummary"));
    public static Button buttonAddCertificatePolicy = new Button(By.xpath("//*[@id='productConsolidatedViewForm:createChildRoot_Certificate' "
            + "or @id='productConsolidatedViewForm:createChildRoot_certificate']"));
    public static Button buttonQuoteVersionHistory = new Button(By.id("productContextInfoForm:lnkQuoteHistoryHistory"));
    public static Button buttonCreateVersion = new Button(By.id("topCreateQuoteVersionLink"));
    public static Button buttonCompare = new Button(By.id("quoteVersionHistoryForm:compareSelectedVersions_footer"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
    public static Button buttonProceed = new Button(By.xpath("//button[.//span[@class='ui-button-text ui-c' and text()='Proceed'] or @value='Proceed']"));
    public static Button buttonPendedRateGuaranteeRenewal = new Button(By.id("productContextInfoForm:lnkPendedRateGuaranteeRenewals"));

    public static Link linkPolicy = new Link(By.id("productContextInfoForm:policyDetail_policyNumLnk"));
    public static Link linkMasterPolicy = new Link(By.id("productContextInfoForm:policyDetail_masterPolicyNumLnk"));
    public static Link linkGroupCoverageNext = new Link(By.id("productConsolidatedViewForm:body_scolumn_GroupCoverageSplitComponent:dataScrollerscolumn_GroupCoverageSplitComponent_ds_next"));

    public static ComboBox comboboxPolicyTerm = new ComboBox(By.id("historyForm:transactionsFilter"));

    public static TableExtended<TableConstants.Empty> tableTransactionHistory = new TableExtended<>("TransactionHistory", By.xpath("//table[@id='historyForm:body_historyTable' or @id='quoteVersionHistoryForm:body_historyTable' or @id='historyForm:historyTable']"));
    public static Table tableQuoteVersionHistory = new Table(By.xpath("//table[@id='quoteVersionHistoryForm:body_historyTable']"));
    public static Table tableReinsuranceStatus = new Table(By.id("productConsolidatedViewForm:body_scolumn_CLReinsuranceConsolidatedView"));
    public static TableExtended<Endorsements> tableEndorsements = new TableExtended<>("Endorsements", By.id("endorsementForm:endorsementList"));
    public static Table tableRenewals = new Table(By.id("renewalForm:renewals_list_table"));
    public static Table tableRiskItemAutoInfo = new Table(By.xpath("//*[@id='productConsolidatedViewForm:consolidatedInfoPanelPreconfigAutoPolicyPremium_body']/table"));
    public static Table tableSelectQuote = new Table(By.id("quotePageContents:body_quote_list_table"));
    public static Table tableSelectPolicy = new Table(By.id("policyForm:body_policy_list_table_holder"));
    public static Table tablePremiumAudit = new Table(By.xpath(".//div[@id='productConsolidatedViewForm:scolumn_CLPolicyPremiumAuditConsolidatedView']//table"));
    public static Table tablePolicyDrivers = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_PreconfigAutoDriver']//table"));
    public static Table tablePolicyVehicles = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_PreconfigVehicle']//table"));
    public static Table tableCoverageSummary = new Table(By.id("productConsolidatedViewForm:body_scolumn_GroupCoverageSplitComponent"));
    public static Table tableASOFeeBasis = new Table(By.id("productConsolidatedViewForm:body_scolumn_RenAsoFeeBasisTableComponent"));
    public static Table tablePolicyList = new Table(By.id("policyForm:body_policy_list_table_holder"));
    public static Table tableCertificatePolicies = new Table(By.id("productConsolidatedViewForm:rootInstancesTable_certificate"));
    public static Table tableGeneralInformation = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_PolicyCLInfoConsolidatedView' "
            + "or @id='productConsolidatedViewForm:consolidatedInfoPanelPolicy' or @id='productConsolidatedViewForm:scolumn_Policy']//table"));
    public static Table tableInsuredInformation = new Table(By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelPreconfigInsured_body' "
            + "or @id='productConsolidatedViewForm:scolumn_InsuredInformation' or @id='productConsolidatedViewForm:scolumn_CLPolicyIndInsuredConsolidatedView'"
            + "or @id='productConsolidatedViewForm:scolumn_CLPolicyInsuredConsolidatedView']//table"));
    public static Table tableCoveragePremiumSummary = new Table(By.id("policyDataGatherForm:policySummary_ListCLGLPremOpsProdCoveragePremiumSummary"));
    public static Table tablePremiumCoveragePanel = new Table(By.xpath("//*[@id='productConsolidatedViewForm:premiumCoveragePanel']/following-sibling::table"));
    public static StaticElement totalTaxes = new StaticElement(By.xpath("//div[@id='productConsolidatedViewForm:premiumCoveragePanel']//div[3]//tbody[2]//tr[@class='columnBorderBottom'][4]/td[2]"));
    public static StaticElement totalFees = new StaticElement(By.xpath("//*[@id='productConsolidatedViewForm:j_id_23_5j_j_3_1:content']/table/tbody/tr/td[3]/table/tbody[2]/tr[3]/td[2]"));
    public static Table tableCompare = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table[@role='treegrid']"));
    public static Table tableLocationsCatastropheMoratorium = new Table(By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelCLLocation_body' or " +
            "@id='productConsolidatedViewForm:consolidatedInfoPanelPolicyMoratoriumLocationsConsolidatedView_body']//table"));
    public static Table tableInsuredName = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_PreconfigInsured']//table"));
    public static Table tableWarningRecords = new Table(By.xpath("//form[@id='productContextInfoForm']//table[tbody//span[contains(text(), 'Warning')]]"));
    public static TableExtended<TableConstants.ProductRenewals> tableProductRenewals = new TableExtended<>("Renewals", By.id("renewalForm:renewals_list_table"));
    public static TableExtended<ClassInformation> tableClassInformation = new TableExtended<>("Class Information", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListCLGLAuditablePremOpsProdCoClassRiskItem']//table"));
    public static TableExtended<PremiumAuditTransactions> tablePremiumAuditTransactions = new TableExtended<>("Policy Premium Audit Transactions", By.xpath("//div[@id='policyDataGatherForm:componentView_CLPremiumAuditSummary_body']//table"));
    public static TableExtended<ArchivedTransactions> tableArchivedTransactions = new TableExtended<>("Archived Transactions", By.xpath("//div[@id='productConsolidatedViewForm:archivedTransactionsTogglePanel:content']//table"));
    public static TableExtended<PremiumSummary> tablePremiumSummary = new TableExtended<>("Premium Summary", By.id("productConsolidatedViewForm:body_scolumn_GroupCertificatePremiumSummary"));
    public static TableExtended<InsuredPrincipalInformation> tableInsuredPrincipalInformation = new TableExtended<>("tableInsuredPrincipalInformation", By.id("productConsolidatedViewForm:body_scolumn_GroupCertificatePrimaryParticipant"));
    public static TableExtended<EnrollmentProcessingResults> tableEnrollmentProcessingResults = new TableExtended<>("Enrollment Processing Results", By.xpath("//div[@id='productConsolidatedViewForm:enrollmentRecordResultTable']/div/table"));
    public static TableExtended<RoleSummary> tableRoleSummary = new TableExtended<>("Role Summary",
            By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelGroupCertificateParticipantGroup_body']/div//table"));
    public static TableExtended<RoleSummary> tableEmployeeBasicLifeInsuranceRoleSummary = new TableExtended<>("Employee Basic Life Insurance - Role Summary",
            By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelGroupCertificateParticipantGroup_body']/div[1]//table"));
    public static TableExtended<RoleSummary> tableEmployeeBADIRoleSummary = new TableExtended<>("Employee Basic Accidental Death and Dismemberment Insurance - Role Summary",
            By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelGroupCertificateParticipantGroup_body']/div[2]//table"));
    public static TableExtended<RoleSummary> tableDependentBasicLifeInsuranceRoleSummary = new TableExtended<>("Dependent Basic Life Insurance (Spouse & Child) - Role Summary",
            By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelGroupCertificateParticipantGroup_body']/div[3]//table"));
    public static TableExtended<RoleSummary> tableSTDCoreRoleSummary = new TableExtended<>("STD Core - Role Summary",
            By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelGroupCertificateParticipantGroup_body']/div//table"));
    public static TableExtended<EnrolleeRecordDetails> tableEnrolleeRecordDetails = new TableExtended<>("Enrollee Record Details", By.xpath("//div[@id='productConsolidatedViewForm:logTable']/div/table"));

    public static TableExtended<RoleSummary> tableEmployeeVoluntaryLifeInsuranceRoleSummary = new TableExtended<>("Employee Basic Life Insurance - Role Summary",
            By.xpath("//td[text()='Employee Voluntary Life Insurance - Role Summary']//ancestor::table[1]//following-sibling::div[1]//table"));
    public static TableExtended<RoleSummary> tableEmployeeVoluntaryADDRoleSummary = new TableExtended<>("Employee Basic Life Insurance - Role Summary",
            By.xpath("//td[text()='Employee Voluntary Accidental Death and Dismemberment Insurance - Role Summary']//ancestor::table[1]//following-sibling::div[1]//table"));
    public static TableExtended<RoleSummary> tableSpouseVoluntaryLifeInsuranceRoleSummary = new TableExtended<>("Employee Basic Life Insurance - Role Summary",
            By.xpath("//td[text()='Spouse Voluntary Life Insurance - Role Summary']//ancestor::table[1]//following-sibling::div[1]//table"));
    public static TableExtended<RoleSummary> tableChildVoluntaryLifeInsuranceRoleSummary = new TableExtended<>("Employee Basic Life Insurance - Role Summary",
            By.xpath("//td[text()='Child Voluntary Life Insurance - Role Summary']//ancestor::table[1]//following-sibling::div[1]//table"));

    public static Table tableVisionRoleSummary = new Table(By.xpath("//table[@id='productConsolidatedViewForm:body_participantList_0']"));

    public static Dialog dialogRemoveSuspense = new Dialog("//div[@id='validateActionPopup_container']");

    private static final String AUDIT_TRANSACTION_HISTORY_TMPL = "//span[@id='policyDataGatherForm:configPanel_CLGLAuditRiskItemTransactions']//label[contains(text(),'%s')]/following::table[1]";
    private static final ByT VIEW_ENROLLEE_RECORD_DETAILS = ByT.id("productConsolidatedViewForm:enrollmentRecordResultTable:%s:viewDetailsAction");

    public enum Endorsements implements Named {
        STATUS("Status"),
        ACTION("Action");
        private String name;

        Endorsements(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumAuditTransactions implements Named {
        AUDIT_NUMBER("Audit #"),
        AUDIT_TRANSACTION_TYPE("Audit Transaction Type"),
        AUDIT_PROCESS_DATE("Audit Process Date"),
        APRP("AP/RP"),
        TAX("Tax"),
        FEE("Fee"),
        FINAL_APRP("Final AP/RP"),
        COMMISSION("Commission");

        private String name;

        PremiumAuditTransactions(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassInformation implements Named {
        NUMBER(""),
        LOC_NUMBER("Loc #"),
        RATING_PERIOD("Rating Period"),
        CLASS("Class"),
        DESCRIPTION("Description"),
        EXPOSURE_BASE("Exposure Base"),
        ESTIMATED_EXPOSURE_AMOUNT("Estimated Exposure Amount");

        private String name;

        ClassInformation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum AuditTransactionHistory implements Named {
        AUDIT_NUMBER("Audit #"),
        AUDIT_PERIOD("Audit Period"),
        TRANSACTION_TYPE("Transaction Type"),
        AUDIT_PROCESS_DATE("Audit Process Date"),
        ORIGINAL_WRITTEN_PREMIUM("Original Written Premium"),
        ACTUAL_EXPOSURE_AMOUNT("Actual Exposure Amount"),
        CHANGE_IN_EXPOSURE("Change in Exposure"),
        EARNED_PREMIUM("Earned Premium"),
        AP_RP("AP/RP");

        private String name;

        AuditTransactionHistory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ArchivedTransactions implements Named {
        TRANSACTION_TYPE("Transaction Type"),
        PREMIUM("Premium"),
        STATUS("Status"),
        EFFECTIVE_DATE("Effective Date"),
        LAST_CHANGE_DATE("Last Change Date"),
        PRIMARY_BROKER("Primary Broker"),
        LAST_PERFORMER("Last Performer");

        private String name;

        ArchivedTransactions(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum EnrollmentProcessingResults implements Named {
        NAME("Name"),
        ENROLLMENT_FILE("Enrollment File"),
        REFERENCE_ID("Reference ID"),
        UPDATED_DATE_TIME("Updated Date/Time"),
        ENROLLMENT_TRANSACTION_TYPE("Enrollment Transaction Type"),
        CERTIFICATE_POLICY_NUMBER("Certificate Policy #"),
        ENROLLMENT_STATUS("Enrollment Status");

        private String name;

        EnrollmentProcessingResults(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum EnrolleeRecordDetails implements Named {
        NAME("Name"),
        ENROLLMENT_FILE("Enrollment File"),
        REFERENCE_ID("Reference ID"),
        UPDATED_DATE_TIME("Updated Date/Time"),
        ERROR_TYPE("Error Type"),
        ERROR_CODE("Error Code"),
        ERROR_MESSAGE("Error Message");

        private String name;

        EnrolleeRecordDetails(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum RoleSummary implements Named {
        ROLE_TYPE("Role Type"),
        ROLE_NAME("Role Name"),
        CUSTOMER_NAME("Customer Name"),
        RELATIONSHIP_TO_INSURED_PRINCIPAL("Relationship to Insured Principal");

        private String name;

        RoleSummary(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumSummary implements Named {
        COVERAGE_NAME("Coverage Name"),
        EFFECTIVE_DATE("Effective Date"),
        CONTRIBUTION_TYPE("Contribution Type"),
        CONTRIBUTION("Contribution %"),
        PAYMENT_MODE("Payment Mode"),
        MODAL_PREMIUM("Modal Premium"),
        ANNUAL_PREMIUM("Annual Premium");
        private String name;

        PremiumSummary(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum InsuredPrincipalInformation implements Named {
        NAME("Name"),
        EFFECTIVE_DATE("Effective Date"),
        DATE_OF_BIRTH("Date of Birth"),
        TAX_ID("Tax ID"),
        ADDRESS("Address");
        private String name;

        InsuredPrincipalInformation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static Currency getProposedRenewalPremium() {
        buttonRenewals.click();
        Currency renewalPremium = new Currency(tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.PREMIUM).getValue());
        Tab.buttonBack.click();

        return renewalPremium;
    }

    public static LocalDateTime getExpirationDate() {
        return LocalDate.parse(tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EXPIRATION_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).atStartOfDay();
    }

    public static LocalDateTime getEffectiveDate() {
        LocalDate dateEffective;
        if ((!tableGeneralInformation.isPresent()) || (tableGeneralInformation.isPresent() && !tableGeneralInformation.getHeader().getValue().contains("Effective Date"))) {
            dateEffective = LocalDate.parse(labelPolicyEffectiveDate.getValue(), DateTimeUtils.MM_DD_YYYY);
        } else {
            dateEffective = LocalDate.parse(tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
        }

        return dateEffective.atStartOfDay();
    }

    public static String getMoratoriumWarningMessage() {
        return PolicySummaryPage.tableWarningRecords.getRow(1).getCell(3).getValue();
    }

    public static String getPolicySeries() {
        return labelPolicyNumber.getValue().replaceAll("\\d", "");
    }

    public static Long getPolicyNumber() {
        return Long.parseLong(StringUtils.remove(labelPolicyNumber.getValue(), getPolicySeries()));
    }

    public static int getRenewalFrequency(String policyNumber) {
        return Integer.parseInt(DBService.get().getRow(String.format("select ps.renewalFrequency from PolicySummary ps where ps.policyNumber = '%s';",
                policyNumber)).get("renewalFrequency"));
    }

    public static void expandArchivedTransactionsTable() {
        if (new StaticElement(By.xpath("//input[@id='productConsolidatedViewForm:archivedTransactionsTogglePanel-value']")).getAttribute("value").equals("false")) {
            new Link(By.xpath("//div[@id='productConsolidatedViewForm:archivedTransactionsTogglePanel']")).click();
        }
    }

    public static void expandEnrollmentProcessingResultsTable() {
        if (new StaticElement(By.xpath("//input[@id='productConsolidatedViewForm:enrollmentProcessingTogglePanel-value']")).getAttribute("value").equals("false")) {
            new Link(By.xpath("//div[@id='productConsolidatedViewForm:enrollmentProcessingTogglePanel']")).click(Waiters.SLEEP(1000));
        }
    }

    public static class TransactionHistory {
        public static Button buttonCompareVersions = new Button(By.id("historyForm:compareVersions_footer"));

        public static void open() {
            buttonTransactionHistory.click();
        }

        public static void selectTransactionsToCompare(String... types) {
            open();
            Arrays.asList(types).forEach(action ->
                    tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, action).getCell(1)
                            .controls.checkBoxes.getFirst().setValue(true));
            buttonCompareVersions.click();
        }

        public static void close() {
            Tab.buttonTopCancel.click();
        }

        public static Link provideLinkExpandComparisonTree(int i) {
            return new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i + "']" +
                    "/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
        }

        public static LocalDateTime readEffectiveDate(int row) {
            return LocalDate.parse(tableTransactionHistory.getRow(row).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY).atStartOfDay();
        }

        public static LocalDateTime readEffectiveDate(String nameType) {
            return LocalDate.parse(tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, nameType).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE)
                    .getValue(), DateTimeUtils.MM_DD_YYYY).atStartOfDay();
        }

        public static LocalDateTime readTransactionDate(String nameType) {
            return LocalDate.parse(tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, nameType).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRANSACTION_DATE)
                    .getValue(), DateTimeUtils.MM_DD_YYYY).atStartOfDay();
        }

        public static Currency readEndingPremium(int rowNumber) {
            return new Currency(tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
        }

        public static Currency readTranPremium(int rowNumber) {
            return new Currency(tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRAN_PREMIUM).getValue());
        }

        public static String readReason(int rowNumber) {
            try {
                return tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON)
                        .applyConfiguration("TransactionHistoryHintValue").getHintValue();
            } catch (IstfException e) {
                LOGGER.debug("Hint was not found for value in Transaction History", e);
                return tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON).getValue();
            }
        }

        public static String readType(int rowNumber) {
            return tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE).getValue();
        }

        public static LocalDateTime getTransactionEffectiveDate(String nameType) {
            open();
            LocalDateTime date = readEffectiveDate(nameType);
            close();

            return date;
        }

        public static LocalDateTime getEffectiveDate(int row) {
            open();
            LocalDateTime date = readEffectiveDate(row);
            close();

            return date;
        }

        public static Currency getEndingPremium(int row) {
            open();
            Currency value = readEndingPremium(row);
            close();

            return value;
        }

        public static Currency getTranPremium(int row) {
            open();
            Currency value = readTranPremium(row);
            close();

            return value;
        }

        public static String getReason(int row) {
            open();
            String content = readReason(row);
            close();

            return content;
        }

        public static String getType(int row) {
            open();
            String content = readType(row);
            close();

            return content;
        }

        public static TableExtended getAuditTransactionHistoryTable(String tableName) {
            return new TableExtended<AuditTransactionHistory>("", By.xpath(String.format(AUDIT_TRANSACTION_HISTORY_TMPL, tableName)));
        }

        public static LocalDateTime getEffectiveDate() {
            return getEffectiveDate(1);
        }

        public static Currency getTranPremium() {
            return getTranPremium(1);
        }

        public static Currency getEndingPremium() {
            return getEndingPremium(1);
        }

        public static String getReason() {
            return getReason(1);
        }

        public static String getType() {
            return getType(1);
        }
    }

    public static class TransactionHistoryDifferences {
        public static Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));

        public static void expandDifferencesTable() {
            ImmutableList.copyOf(tableDifferences.getRows()).reverse().stream()
                    .filter(row -> row.getAttribute("aria-expanded").equals("false"))
                    .forEach(row -> new Button(row.getCell(1), By.xpath("./span")).click());
        }
    }

    public static void waitEnrollmentStatusByName(String name, String enrollmentStatus) {
        try {
            RetryService.run(row -> row.isPresent() && row.getCell(8).getValue().equals(enrollmentStatus),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        expandEnrollmentProcessingResultsTable();
                        return tableEnrollmentProcessingResults.getRow(NAME.getName(), name);
                    }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            if (tableEnrollmentProcessingResults.getRow(NAME.getName(), name).isPresent()) {
                throw new IstfException(String.format("'Enrollment Status': Expected: 'Completed', but Actual '%s'",
                        tableEnrollmentProcessingResults.getRow(NAME.getName(), name).getCell(8).getValue()), e);
            } else {
                throw new IstfException("Enrollment was not processed for Master policy", e);
            }
        }
    }

    public static void waitEnrollmentStatusByRowNumber(int rowNumber, String enrollmentStatus) {
        try {
            RetryService.run(row -> row.isPresent() && row.getCell(8).getValue().equals(enrollmentStatus),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        expandEnrollmentProcessingResultsTable();
                        return tableEnrollmentProcessingResults.getRow(rowNumber);
                    }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            if (tableEnrollmentProcessingResults.getRow(rowNumber).isPresent()) {
                throw new IstfException(String.format("'Enrollment Status': Expected: 'Completed', but Actual '%s'",
                        tableEnrollmentProcessingResults.getRow(rowNumber).getCell(8).getValue()), e);
            } else {
                throw new IstfException("Enrollment was not processed for Master policy", e);
            }
        }
    }

    public static String waitCertificatePolicyIssued(int rowNumber) {
        try {
            RetryService.run(row -> row.isPresent() && row.getCell(POLICY_NUMBER).isPresent(),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        return tableCertificatePolicies.getRow(rowNumber);
                    }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Certificate Policies was not issued", e);
        }
        return tableCertificatePolicies.getRow(rowNumber).getCell(POLICY_NUMBER).getValue();
    }

    public static void openEnrolleeRecordDetailsByCustomerName(String customerName) {
        expandEnrollmentProcessingResultsTable();
        PolicySummaryPage.tableEnrollmentProcessingResults.getRow("Name", customerName).getCell(9)
                .controls.links.get("View Details").click();
    }

    public static LocalDateTime getQuoteCreationDate() {
        return LocalDate.parse(tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.QUOTE_CREATION_DATE).getValue(),
                DateTimeUtils.MM_DD_YYYY).atStartOfDay();
    }
}
