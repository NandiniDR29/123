package com.exigen.ren.modules.docgen.gb_st;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.table.Cell;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.UpdateBillingAccountActionTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Strings;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderBA.INVOICES_AND_BILLS;
import static com.exigen.ren.common.enums.NavigationEnum.AdminAppMainTabs.BILLING;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NY;
import static com.exigen.ren.main.enums.PolicyConstants.SubGroups.*;
import static com.exigen.ren.main.enums.TableConstants.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_CONTACT_NAME;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICE_DOCUMENT_TEMPLATE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs.ViewRateHistoryDialog.openRateHistoryTableForSubGroup;
import static com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs.ViewRateHistoryDialog.tableRateHistoryForSubGroup;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetadata.MAXIMUM_WEEKLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.STATEWIDE_MAX_COVERED_PAYROLL;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.UNDER_FIFTY_LIVES_WORKFLOW;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.labelCustomerNumber;
import static com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage.labelBillingAccountName;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInvoiceTypes extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext, PaidFamilyLeaveMasterPolicyContext {

    private static final AbstractContainer<?, ?> updateBillingAssetList = billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28185"}, component = POLICY_GROUPBENEFITS)
    public void testInvoiceType5_NewJerseyTemporaryDisabilityBenefitsQuaterlyPremiumReport() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = labelCustomerNumber.getValue();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        TestData tdPolicy = getDefaultSTMasterPolicyData()
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NJ"))
                .adjust(TestData.makeKeyPath(StatutoryDisabilityInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(), ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS.getLabel()), VALUE_NO)
                .adjust(BillingAccountTab.class.getSimpleName(), tdSpecific().getTestData("BillingAccountTab_Type5").resolveLinks())
                .resolveLinks();

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(tdPolicy);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        statutoryDisabilityInsuranceMasterPolicy.policyInquiry().start();
        StatutoryDisabilityInsuranceMasterPolicyContext.premiumSummaryTab.navigateToTab();
        Tab.doubleWaiter.go();
        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        String finalRate = new Currency(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getRow(1).getCell(RateHistoryTable.FINAL_RATE.getName()).getValue())
                .toPlainString().concat("000");
        RateDialogs.ViewRateHistoryDialog.close();
        Tab.buttonTopCancel.click();

        NavigationPage.toMainTab(BILLING);
        billingAccount.generateFutureStatement().perform();
        String billingAccountNumber = getBillingAccountNumber(policyNumber);
        String invoiceNumber = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtilsHelper.YYYY_MM_DD);
        LocalDateTime billingEndDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BILING_PERIOD).getValue().substring(13),
                DateTimeUtils.MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(billingAccountNumber, DBHelper.EntityType.BILLING);
        XmlValidator payloadSection = getNeededSection(xmlValidator, PAYLOAD);

        Map<DocGenEnum.AllSections, String> mapPayloadSectionItems = getPayloadSectionGeneralValues(invoiceNumber, currentDate, customerNumber, billingAccountNumber);
        payloadSection.checkDocument(mapPayloadSectionItems);

        assertThat(payloadSection.getNodeValue(GENERATION_DATE)).matches(String.format("%sT\\d{2}:\\d{2}:\\d{2}\\.[\\d]{1,3}Z$", currentDate));

        Map<DocGenEnum.AllSections, String> mapRootSection = getStartAndEndDates(billingEndDate);
        mapRootSection.put(STATIC_BIL_DOC_INFO_TDB_CLASS_FINAL_RATE_CURRENT, finalRate);
        xmlValidator.checkDocument(mapRootSection);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28253", "REN-33308"}, component = POLICY_GROUPBENEFITS)
    public void testInvoiceType6_NewJerseyMandatedBenefitsQuaterlyPremiumReport() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = labelCustomerNumber.getValue();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup"), statutoryDisabilityInsuranceMasterPolicy.getType());

        TestData tdPolicy = getDefaultSTMasterPolicyData()
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NJ"))
                .adjust(TestData.makeKeyPath(StatutoryDisabilityInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(), ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS.getLabel()), VALUE_NO)
                .adjust(StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.getMetaKey(), tdSpecific().getTestData("PlanDefinitionTab_NJ").resolveLinks())
                .adjust(StatutoryDisabilityInsuranceMasterPolicyContext.classificationManagementMpTab.getMetaKey(), tdSpecific().getTestData("ClassificationManagementTab_NJ").resolveLinks())
                .adjust(BillingAccountTab.class.getSimpleName(), tdSpecific().getTestData("BillingAccountTab_Type6").resolveLinks())
                .resolveLinks();

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(tdPolicy);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        statutoryDisabilityInsuranceMasterPolicy.policyInquiry().start();
        StatutoryDisabilityInsuranceMasterPolicyContext.premiumSummaryTab.navigateToTab();
        Tab.doubleWaiter.go();
        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        String finalRate = new Currency(RateDialogs.ViewRateHistoryDialog.tableRateHistory.getRow(1).getCell(RateHistoryTable.FINAL_RATE.getName()).getValue())
                .toPlainString().concat("000");
        RateDialogs.ViewRateHistoryDialog.close();
        Tab.buttonTopCancel.click();
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(policyNumber);
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        String employeeRate = new BigDecimal(responseFromLog.get("planCalcs[0].tierRates[0].rate")).setScale(5, RoundingMode.HALF_UP).toString();
        String employerRate = new BigDecimal(responseFromLog.get("planCalcs[0].tierRates[1].rate")).setScale(5, RoundingMode.HALF_UP).toString();
        String annualTaxableWagePerPerson = requestFromLog.get("plans[0].maxSalaryPEPY");

        NavigationPage.toMainTab(BILLING);
        billingAccount.generateFutureStatement().perform();
        String billingAccountNumber = getBillingAccountNumber(policyNumber);
        String invoiceNumber = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        LocalDateTime billingEndDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BILING_PERIOD).getValue().substring(13),
                DateTimeUtils.MM_DD_YYYY).atStartOfDay();
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtilsHelper.YYYY_MM_DD);

        LOGGER.info("Step 1, 2");
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(INVOICES_AND_BILLS.getName(), customerNumber,
                String.format("%s-Billing Statement-%s-%s-%s", customerNumber, invoiceNumber, billingEndDate.plusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD), currentDate)),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("String step 3");
        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(billingAccountNumber, DBHelper.EntityType.BILLING);
        XmlValidator payloadSection = getNeededSection(xmlValidator, PAYLOAD);

        Map<DocGenEnum.AllSections, String> mapPayloadSectionItems = getPayloadSectionGeneralValues(invoiceNumber, currentDate, customerNumber, billingAccountNumber);
        payloadSection.checkDocument(mapPayloadSectionItems);

        assertThat(payloadSection.getNodeValue(GENERATION_DATE)).matches(String.format("%sT\\d{2}:\\d{2}:\\d{2}\\.[\\d]{1,3}Z$", currentDate));

        Map<DocGenEnum.AllSections, String> mapRootSection = getStartAndEndDates(billingEndDate);
        xmlValidator.checkDocument(mapRootSection);

        LOGGER.info("REN-33308: Step 3");
        //20 Plan Definition -> Rating -> Annual Taxable Wage Per Person or value from rating request 'plans[0].maxSalaryPEPY'
        xmlValidator.checkDocument(String.format("%s%s", REN_INVOICE_PAYLOAD_STAT_BILL_DOC_INFO.get(), MAX_PAYROLL_WITH_HOLDING_EMPLOYEE_TDB.get()), annualTaxableWagePerPerson);
        //21 Policy > Classification Managemant > Rate for each Tier or values from rating response log 'planCalcs[*].tierRates[*].rate'
        //   Policy > Premium Summary > View Rate History -> Tiers > Employee - Rate History > Final Rate;
        xmlValidator.checkDocument(String.format("%s%s", REN_INVOICE_PAYLOAD_STAT_BILL_DOC_INFO.get(), TDB_EMPLOYEE_FINAL_RATE_CURRENT.get()), employeeRate);
        xmlValidator.checkDocument(String.format("%s%s", REN_INVOICE_PAYLOAD_STAT_BILL_DOC_INFO.get(), TDB_EMPLOYER_FINAL_RATE_CURRENT.get()), employerRate);
        assertThat(new BigDecimal(xmlValidator.getNodeValue(String.format("%s%s", REN_INVOICE_PAYLOAD_STAT_BILL_DOC_INFO.get(), TDB_CLASS_FINAL_RATE_CURRENT.get())))
                .setScale(2, RoundingMode.HALF_UP).setScale(5, RoundingMode.HALF_UP).toString()).isEqualTo(finalRate);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28107"}, component = POLICY_GROUPBENEFITS)
    public void testInvoiceType2_NewYorkDblPflQuaterlyPremiumReport() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = labelCustomerNumber.getValue();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        TestData tdPolicy = getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_Type2").resolveLinks()).resolveLinks();

        statutoryDisabilityInsuranceMasterPolicy.initiateAndFillUpToTab(tdPolicy, StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.getClass(), true);
        StatutoryDisabilityInsuranceMasterPolicyContext.policyInformationTab.navigateToTab();
        StatutoryDisabilityInsuranceMasterPolicyContext.policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_NO);
        StatutoryDisabilityInsuranceMasterPolicyContext.classificationManagementMpTab.navigateToTab();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFrom(tdPolicy, ClassificationManagementTab.class);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(tdPolicy);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(tdPolicy);
        statutoryDisabilityInsuranceMasterPolicy.issue().perform(tdPolicy);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        statutoryDisabilityInsuranceMasterPolicy.policyInquiry().start();
        StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.navigateToTab();
        StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.changeCoverageTo(PFL_NY);
        String maxCoveredPayroll = new Currency(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(STATEWIDE_MAX_COVERED_PAYROLL.getLabel())).getValue()).toPlainString();
        StatutoryDisabilityInsuranceMasterPolicyContext.premiumSummaryTab.navigateToTab();
        Tab.doubleWaiter.go();
        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        openRateHistoryTableForSubGroup(STAT_NY, "1", MALE);
        Cell rateCell = tableRateHistoryForSubGroup.getRow(RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Final Rate")
                .getCell(RateHistoryForSubGroupTable.RATE.getName());
        String finalRateNYMale = new Currency(rateCell.getValue()).toPlainString();
        openRateHistoryTableForSubGroup(STAT_NY, "1", FEMALE);
        String finalRateNYFemale = new Currency(rateCell.getValue()).toPlainString();
        openRateHistoryTableForSubGroup(STAT_NY, "1", PROPRIETOR);
        String finalRateNYProprietor = new Currency(rateCell.getValue()).toPlainString();
        openRateHistoryTableForSubGroup(PFL_NY, "1", MALE);
        String finalRatePFL = rateCell.getValue();

        RateDialogs.ViewRateHistoryDialog.close();
        Tab.buttonTopCancel.click();

        NavigationPage.toMainTab(BILLING);
        billingAccount.generateFutureStatement().perform();
        String billingAccountNumber = getBillingAccountNumber(policyNumber);
        String invoiceNumber = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        LocalDateTime billingEndDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BILING_PERIOD).getValue().substring(13),
                DateTimeUtils.MM_DD_YYYY).atStartOfDay();
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtilsHelper.YYYY_MM_DD);

        LOGGER.info("Step 1, 2");
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(INVOICES_AND_BILLS.getName(), customerNumber,
                String.format("%s-Billing Statement-%s-%s-%s", customerNumber, invoiceNumber, billingEndDate.plusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD), currentDate)),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("String step 3");
        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(billingAccountNumber, DBHelper.EntityType.BILLING);
        XmlValidator payloadSection = getNeededSection(xmlValidator, PAYLOAD);

        Map<DocGenEnum.AllSections, String> mapPayloadSectionItems = getPayloadSectionGeneralValues(invoiceNumber, currentDate, customerNumber, billingAccountNumber);
        payloadSection.checkDocument(mapPayloadSectionItems);

        assertThat(payloadSection.getNodeValue(GENERATION_DATE)).matches(String.format("%sT\\d{2}:\\d{2}:\\d{2}\\.[\\d]{1,3}Z$", currentDate));

        Map<DocGenEnum.AllSections, String> mapRootSection = getStartAndEndDates(billingEndDate);
        mapRootSection.put(DBL_MALE_FINAL_RATE_CURRENT, new DecimalFormat("0.00000").format(new BigDecimal(finalRateNYMale)).replace(",", "."));
        mapRootSection.put(DBL_FEMALE_FINAL_RATE_CURRENT, new DecimalFormat("0.00000").format(new BigDecimal(finalRateNYFemale)).replace(",", "."));
        mapRootSection.put(DBL_PROP_FINAL_RATE_CURRENT, new DecimalFormat("0.00000").format(new BigDecimal(finalRateNYProprietor)).replace(",", "."));
        mapRootSection.put(WITH_PFL, VALUE_TRUE);
        finalRatePFL = new DecimalFormat("0.00000").format(new BigDecimal(finalRatePFL)).replace(",", ".");
        mapRootSection.put(PFL_MALE_FINAL_RATE_CURRENT, finalRatePFL);
        mapRootSection.put(PFL_FEMALE_FINAL_RATE_CURRENT, finalRatePFL);
        mapRootSection.put(MAX_PAYROLL_WITH_HOLDING_PFL, maxCoveredPayroll);
        xmlValidator.checkDocument(mapRootSection);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28312"}, component = POLICY_GROUPBENEFITS)
    public void testInvoiceType3_NewYorkDblPflQuaterlyPremiumReport() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = labelCustomerNumber.getValue();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        TestData tdPolicy = getDefaultSTMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Type2").resolveLinks())
                .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName(), CREATE_NEW_BILLING_ACCOUNT.getLabel(),
                        INVOICE_DOCUMENT_TEMPLATE.getLabel()), "STATUTORY_INVOICE_TYPE_3").resolveLinks();

        statutoryDisabilityInsuranceMasterPolicy.initiateAndFillUpToTab(tdPolicy, StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.getClass(), true);
        StatutoryDisabilityInsuranceMasterPolicyContext.policyInformationTab.navigateToTab();
        StatutoryDisabilityInsuranceMasterPolicyContext.policyInformationTab.getAssetList().getAsset(UNDER_FIFTY_LIVES_WORKFLOW).setValue(VALUE_NO);
        StatutoryDisabilityInsuranceMasterPolicyContext.classificationManagementMpTab.navigateToTab();
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fillFrom(tdPolicy, ClassificationManagementTab.class);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(tdPolicy);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(tdPolicy);
        statutoryDisabilityInsuranceMasterPolicy.issue().perform(tdPolicy);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        statutoryDisabilityInsuranceMasterPolicy.policyInquiry().start();
        StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.navigateToTab();
        StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.changeCoverageTo(STAT_NY);
        String maxWeeklyBenefit = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(MAXIMUM_WEEKLY_BENEFIT_AMOUNT.getLabel())).getValue();
        String benefitPercentage = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(PlanDefinitionTabMetaData.BenefitScheduleMetadata.BENEFIT_PERCENTAGE.getLabel())).getValue().replace("%", "");
        String maxPayrollWithHolding = new Currency(maxWeeklyBenefit).divide(new Currency(benefitPercentage).divide(100)).toPlainString();
        StatutoryDisabilityInsuranceMasterPolicyContext.planDefinitionTab.changeCoverageTo(PFL_NY);
        String maxCoveredPayroll = new Currency(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(STATEWIDE_MAX_COVERED_PAYROLL.getLabel())).getValue()).toPlainString();

        StatutoryDisabilityInsuranceMasterPolicyContext.premiumSummaryTab.navigateToTab();
        Tab.doubleWaiter.go();
        PremiumSummaryIssueActionTab.buttonViewRateHistory.click();
        String dblFinalRate = new Currency(RateDialogs.ViewRateHistoryDialog.tableRateHistory
                .getRow(RateHistoryTable.COVERAGE_NAME.getName(), STAT_NY).getCell(RateHistoryTable.FINAL_RATE.getName()).getValue())
                .toPlainString().concat("000");
        openRateHistoryTableForSubGroup(PFL_NY, "1", MALE);
        String finalRatePFL = tableRateHistoryForSubGroup.getRow(RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Final Rate")
                .getCell(RateHistoryForSubGroupTable.RATE.getName()).getValue();

        RateDialogs.ViewRateHistoryDialog.close();
        Tab.buttonTopCancel.click();

        NavigationPage.toMainTab(BILLING);
        billingAccount.generateFutureStatement().perform();
        String billingAccountNumber = getBillingAccountNumber(policyNumber);
        String invoiceNumber = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        LocalDateTime billingEndDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BILING_PERIOD).getValue().substring(13),
                DateTimeUtils.MM_DD_YYYY).atStartOfDay();
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtilsHelper.YYYY_MM_DD);

        LOGGER.info("Step 1, 2");
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(INVOICES_AND_BILLS.getName(), customerNumber,
                String.format("%s-Billing Statement-%s-%s-%s", customerNumber, invoiceNumber, billingEndDate.plusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD), currentDate)),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("String step 3");
        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(billingAccountNumber, DBHelper.EntityType.BILLING);
        XmlValidator payloadSection = getNeededSection(xmlValidator, PAYLOAD);

        Map<DocGenEnum.AllSections, String> mapPayloadSectionItems = getPayloadSectionGeneralValues(invoiceNumber, currentDate, customerNumber, billingAccountNumber);
        payloadSection.checkDocument(mapPayloadSectionItems);

        assertThat(payloadSection.getNodeValue(GENERATION_DATE)).matches(String.format("%sT\\d{2}:\\d{2}:\\d{2}\\.[\\d]{1,3}Z$", currentDate));

        Map<DocGenEnum.AllSections, String> mapRootSection = getStartAndEndDates(billingEndDate);
        mapRootSection.put(MAX_PAYROLL_WITH_HOLDING_DBL, maxPayrollWithHolding);
        mapRootSection.put(DBL_CLASS_FINAL_RATE_CURRENT, dblFinalRate);
        mapRootSection.put(WITH_PFL, VALUE_TRUE);
        finalRatePFL = new DecimalFormat("0.00000").format(new BigDecimal(finalRatePFL)).replace(",", ".");
        mapRootSection.put(PFL_MALE_FINAL_RATE_CURRENT, finalRatePFL);
        mapRootSection.put(PFL_FEMALE_FINAL_RATE_CURRENT, finalRatePFL);
        mapRootSection.put(MAX_ANNUAL_SALARY_PFL, maxCoveredPayroll);
        xmlValidator.checkDocument(mapRootSection);
    }

    private Map<DocGenEnum.AllSections, String> getPayloadSectionGeneralValues(String invoiceNumber, String currentDate, String customerNumber, String billingAccountNumber) {
        Map<DocGenEnum.AllSections, String> mapClassificationItems = new HashMap<>();

        mapClassificationItems.put(INVOICE_NUMBER, invoiceNumber);

        mapClassificationItems.put(CUSTOMER_SUMMARY_CUSTOMER_NUMBER, customerNumber);

        String dueDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY).atStartOfDay()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        mapClassificationItems.put(DocGenEnum.AllSections.DUE_DATE, dueDate);

        mapClassificationItems.put(BILLING_ACCOUNT_NAME, labelBillingAccountName.getValue());

        billingAccount.updateBillingAccount().start();
        String billingContactName = updateBillingAssetList.getAsset(BILLING_CONTACT_NAME).getValue();
        mapClassificationItems.put(NAME_INFO_SUMMARY_OTHER_NAME, billingContactName);

        String billingStreetAddress1 = updateBillingAssetList.getAsset(BillingAccountTabMetaData.BillingAccountGeneralOptions.ADDRESS_LINE_1).getValue();
        mapClassificationItems.put(BILLING_STREET_ADDRESS_1, billingStreetAddress1);

        String billingStreetAddress2 = updateBillingAssetList.getAsset(BillingAccountTabMetaData.BillingAccountGeneralOptions.ADDRESS_LINE_2).getValue();
        mapClassificationItems.put(BILLING_STREET_ADDRESS_2, billingStreetAddress2);

        String billingStreetAddress3 = updateBillingAssetList.getAsset(BillingAccountTabMetaData.BillingAccountGeneralOptions.ADDRESS_LINE_3).getValue();
        mapClassificationItems.put(BILLING_STREET_ADDRESS_3, billingStreetAddress3);

        String billingCity = updateBillingAssetList.getAsset(BillingAccountTabMetaData.BillingAccountGeneralOptions.CITY).getValue();
        mapClassificationItems.put(BILLING_ADDRESS_SUMMARY_CITY, billingCity);

        String billingState = updateBillingAssetList.getAsset(BillingAccountTabMetaData.BillingAccountGeneralOptions.STATE_PROVINCE).getValue();
        mapClassificationItems.put(BILLING_ADDRESS_SUMMARY_STATE_CD, billingState);

        String billingPostalCode = updateBillingAssetList.getAsset(BillingAccountTabMetaData.BillingAccountGeneralOptions.ZIP_POSTAL_CODE).getValue();
        mapClassificationItems.put(BILLING_ADDRESS_SUMMARY_POSTAL_CD, billingPostalCode);

        mapClassificationItems.put(BILLING_ACCOUNT_NUMBER, billingAccountNumber);

        Tab.buttonCancel.click();

        Currency billedAmount = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());
        mapClassificationItems.put(BILLING_OCR, calculateOCRScanLine(billingAccountNumber, billedAmount));

        return mapClassificationItems;
    }

    private Map<DocGenEnum.AllSections, String> getStartAndEndDates(LocalDateTime billingEndDate) {
        Map<DocGenEnum.AllSections, String> mapClassificationItems = new HashMap<>();

        String billingStartDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BILING_PERIOD).getValue().substring(0, 10), DateTimeUtils.MM_DD_YYYY).atStartOfDay()
                .format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        mapClassificationItems.put(BILLING_PERIOD_START, billingStartDate);

        mapClassificationItems.put(BILLING_PERIOD_END, billingEndDate.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));

        return mapClassificationItems;
    }

    private String calculateOCRScanLine(String billingAccountNumber, Currency billedAmount) {
        String ocrBilledAmount = billedAmount.toPlainString().replace(".", "");
        ocrBilledAmount = Strings.padStart(ocrBilledAmount, 10, '0');
        String ocrBillingAccount = Strings.padStart(billingAccountNumber, 9, '0');

        StringBuilder ocrWithoutCheckDigit = new StringBuilder(ocrBillingAccount).append(ocrBilledAmount).append("2");
        char[] argOcrWithoutCheckDigit = ocrWithoutCheckDigit.toString().toCharArray();
        StringBuilder multipliedDigits = new StringBuilder();
        for (int i = 0; i < argOcrWithoutCheckDigit.length; i++) {
            if (i % 2 == 0) {
                multipliedDigits.append(String.valueOf(Character.getNumericValue(argOcrWithoutCheckDigit[i]) * 2));
                continue;
            }
            multipliedDigits.append(String.valueOf(argOcrWithoutCheckDigit[i]));
        }
        int sumOfMultipliedDigits = 0;
        for (char c : multipliedDigits.toString().toCharArray()) {
            sumOfMultipliedDigits += Character.getNumericValue(c);
        }
        int checkDigit = 0;
        if (sumOfMultipliedDigits % 10 != 0) {
            checkDigit = 10 - (sumOfMultipliedDigits % 10);
        }
        String OCRScanLine = ocrWithoutCheckDigit.append(checkDigit).toString();
        return OCRScanLine;
    }
}
