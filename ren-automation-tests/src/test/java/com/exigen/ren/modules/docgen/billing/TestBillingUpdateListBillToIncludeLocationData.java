package com.exigen.ren.modules.docgen.billing;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CertificatePolicyTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimAllSingleBenefitCalculationsTable.EMPTY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.ClassificationGroupPlanSelection.CLASS_NAME;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.PlanTierAndRatingSelection.COVERAGE_TIER;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.classificationGroupPlanRelationshipsTable;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.planTierAndRatingInfoTable;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.BILLING_LOCATION;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.utils.DBHelper.EntityType.BILLING;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestBillingUpdateListBillToIncludeLocationData extends GroupBenefitsBillingBaseTest implements TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext,
        GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-37858", component = BILLING_GROUPBENEFITS)
    public void testBillingUpdateListBillToIncludeLocationData() {
        LocalDateTime effectiveDate = DateTimeUtils.getCurrentDateTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(GroupBenefitsMasterPolicyType.GB_TL, GroupBenefitsMasterPolicyType.GB_DN)
                .adjust(tdSpecific().getTestData("TestData_Case_Profile").resolveLinks()));
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(makeKeyPath(PolicyInformationTab.class.getSimpleName(), POLICY_EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY)));
        String tlMasterPolicy = PolicySummaryPage.labelPolicyNumber.getValue();
        termLifeInsuranceCertificatePolicy.createPolicyViaUI(termLifeInsuranceCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(CertificatePolicyTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(CertificatePolicyTab.class.getSimpleName(), BILLING_LOCATION.getLabel()), "Billing Location 1")
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String tlInsuredFullName = PolicySummaryPage.labelCustomerName.getValue();
        String tlFirstName = tlInsuredFullName.substring(0, 12);
        String tlLastName = tlInsuredFullName.substring(13);

        search(tlMasterPolicy);
        groupDentalMasterPolicy.createPolicy(groupDentalMasterPolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(PolicyInformationTab.class.getSimpleName(), POLICY_EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), "index=1")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithExistingBA")));

        groupDentalMasterPolicy.policyInquiry().start();
        GroupDentalMasterPolicyContext.classificationManagementMpTab.navigateToTab();
        String tierName = planTierAndRatingInfoTable.getRow(1).getCell(COVERAGE_TIER.getName()).getValue();
        String className = classificationGroupPlanRelationshipsTable.getRow(1).getCell(CLASS_NAME.getName()).getValue();
        GroupDentalMasterPolicyContext.classificationManagementMpTab.buttonCancel.click();

        groupDentalCertificatePolicy.createPolicyViaUI(groupDentalCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(CertificatePolicyTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(CertificatePolicyTab.class.getSimpleName(), BILLING_LOCATION.getLabel()), "Billing Location 2")
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String dentalCertificatePolicy = PolicySummaryPage.labelPolicyNumber.getValue();

        navigateToBillingAccount(tlMasterPolicy);
        billingAccount.generateFutureStatement().perform();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(effectiveDate.plusMonths(1));

        mainApp().reopen();
        search(dentalCertificatePolicy);
        groupDentalCertificatePolicy.cancel().perform(groupDentalCertificatePolicy.getDefaultTestData(CANCELLATION, DEFAULT_TEST_DATA_KEY));
        String dnInsuredFullName = PolicySummaryPage.labelCustomerName.getValue();
        String dnFirstName = dnInsuredFullName.substring(0, 12);
        String dnLastName = dnInsuredFullName.substring(13);

        navigateToBillingAccount(tlMasterPolicy);
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-37858: Step 6");
        XmlValidator xmlValidator = DBHelper.getDocument(getBillingAccountNumber(tlMasterPolicy), BILLING);
        String premium1;
        String premium2;
        String volume1;
        String volume2;

        if ((xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_PRODUCT_CD.get(), 1))).equals("GB_DN")) {
            premium1 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_PREMIUM.get(), 1));
            premium2 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_PREMIUM.get(), 2));
            volume1 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_VOLUME.get(), 1));
            volume2 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_VOLUME.get(), 2));
        } else {
            premium1 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_PREMIUM.get(), 3));
            premium2 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_PREMIUM.get(), 1));
            volume1 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_VOLUME.get(), 3));
            volume2 = xmlValidator.getNodeValue(String.format(PAYLOAD_ITEM_VOLUME.get(), 1));
        }

        //31
        xmlValidator.checkDocument(ADJUSTMENT_TABLE_SECTION_NAME, String.format("%s, %s", dnLastName, dnFirstName));
        //32
        xmlValidator.checkDocument(ADJUSTMENT_TABLE_ADJUSTMENT_REASON, "TERM");
        //33
        xmlValidator.checkDocument(ADJUSTMENT_TABLE_COVERAGE_NAME, "Dental");
        //34
        xmlValidator.checkDocument(ADJUSTMENT_TABLE_TIER_NAME, tierName);
        //35
        assertThat(xmlValidator.getNodeValue(ADJUSTMENT_TABLE_MODAL_PREMIUM)).isEqualTo(premium1);
        //36
        assertThat(xmlValidator.getNodeValue(ADJUSTMENT_TABLE_VOLUME)).isEqualTo(volume1);
        //37
        xmlValidator.checkDocument(ADJUSTMENT_TABLE_STATEMENT_PERIOD, String.format("%s - %s", effectiveDate.format(DateTimeFormatter.ofPattern("MMMM, YYYY")),
                effectiveDate.plusMonths(1).format(DateTimeFormatter.ofPattern("MMMM, YYYY"))));
        //38
        assertThat(xmlValidator.getNodeValue(ADJUSTMENT_TABLE_AMOUNT)).isEqualTo(premium1);
        //39
        assertThat(xmlValidator.getNodeValue(ADJUSTMENT_TABLE_TOTAL_AMOUNT)).isEqualTo(premium1);

        LOGGER.info("TEST REN-37858: Step 7");
        //41
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_NAME.get(), 1), "Billing Location 1");
        //42
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_NUMBER.get(), 1), "1");
        //43
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_SECTION_NAME.get(), 1), String.format("%s, %s", tlLastName, tlFirstName));
        //44
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_CLASS_SEQ_NUMBER.get(), 1), className);
        //45
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_COVERAGE_NAME.get(), 1), "Employee Basic Accidental Death and Dismemberment Insurance");
        //46
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_TIER_NAME.get(), 1), EMPTY);
        //47
        xmlValidator.checkDocument(String.format(INVOICE_DATA_ON_LOCATIONS_AMOUNT.get(), 1), premium2);
        //48
        assertThat(xmlValidator.getNodeValue(String.format(INVOICE_DATA_ON_LOCATIONS_VOLUME.get(), 1))).isEqualTo(volume2);
        //49-50
        assertThat(xmlValidator.getNodeValue(String.format(INVOICE_DATA_ON_LOCATIONS_CURRENT_PREMIUM_TABLE_TOTAL_AMOUNT.get(), 1)))
                .isEqualTo(xmlValidator.getNodeValue(String.format(INVOICE_DATA_ON_LOCATIONS_TOTAL_AMOUNT.get(), 1)));

    }
}
