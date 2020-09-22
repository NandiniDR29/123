package com.exigen.ren.modules.policy.gb_eap.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.EmployeeAssistanceProgramCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.*;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CertificatePolicyTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CoveragesTabMetaData.ParticipantAddressInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.RELATIONSHIP_TO_INSURED;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.PremiumSummaryTab.PremiumSummary.DAILY_PREMIUM;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPresentsOfModifiedFieldsOnAllTabs extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41913", "REN-42401"}, component = POLICY_GROUPBENEFITS)
    public void testPresentsOfModifiedFieldsOnAllTabs() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());
        employeeAssistanceProgramMasterPolicy.createPolicy(getDefaultEAPMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel(), PlanDefinitionTabMetaData.RatingMetaData.PLAN_TYPE.getLabel()), "Face to Face benefits"));
        employeeAssistanceProgramCertificatePolicy.initiate(getDefaultEAPCertificatePolicyDataGatherData());

        LOGGER.info("REN-42401 Step 1");
        assertSoftly(softly -> {
            ImmutableList.of(MASTER_POLICY_NUMBER, GROUP_CUSTOMER_NAME, POLICY_EFFECTIVE_DATE, CREATION_DATE, RENEWAL_DATE, ANNIVERSARY_DATE, CertificatePolicyTabMetaData.EFFECTIVE_DATE, SITUS_STATE, ENROLLMENT_TYPE, BILLING_LOCATION)
                    .forEach(element -> assertThat(certificatePolicyTab.getAssetList().getAsset(element)).isPresent());
        });

        LOGGER.info("REN-42401 Step 2");
        certificatePolicyTab.fillTab(getDefaultEAPCertificatePolicyDataGatherData());
        certificatePolicyTab.buttonNext.click();
        assertSoftly(softly -> {
            ImmutableList.of(CREATE_NEW_CUSTOMER, RELATIONSHIP_TYPE).forEach(element -> assertThat(insuredTab.getAssetList().getAsset(element)).isPresent());
            ImmutableList.of(GeneralInformationMetaData.FIRST_NAME, GeneralInformationMetaData.LAST_NAME, GeneralInformationMetaData.DATE_OF_BIRTH, GeneralInformationMetaData.TAX_IDENTIFICATION)
                    .forEach(element -> assertThat(insuredTab.getAssetList().getAsset(GENERAL_INFORMATION).getAsset(element)).isPresent());
            ImmutableList.of(AddressInformationMetaData.ADDRESS_TYPE, AddressInformationMetaData.COUNTRY, AddressInformationMetaData.ZIP_POST_CODE, AddressInformationMetaData.CITY,
                    AddressInformationMetaData.STATE_PROVINCE, AddressInformationMetaData.ADDRESS_LINE_1, AddressInformationMetaData.ADDRESS_LINE_2, AddressInformationMetaData.ADDRESS_LINE_3)
                    .forEach(element -> assertThat(insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(element)).isPresent());
            ImmutableList.of(JOB_CODE, EMPLOYMENT_STATUS, EXPATRIATE, PAY_TYPE, UNION_MEMBER, JOB_TITLE, ANNUAL_EARNINGS, ORIGINAL_HIRE_DATE,
                    REHIRE_DATE, EMPLOYMENT_TYPE, PAY_CLASS, PAYROLL_FREQUENCY, EMPLOYEE_ID, EARNING_DEFINITION, CLASSIFICATION_GROUP, CLASSIFICATION_SUBGROUP)
                    .forEach(element -> assertThat(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(element)).isPresent());
            ImmutableList.of("Title", "Middle Name", "Suffix", "Gender", "Marital Status", "Tobacco", "e-Mail")
                    .forEach(element -> assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(element))).isAbsent());
        });

        LOGGER.info("REN-41913 Step 1");
        insuredTab.fillTab(getDefaultEAPCertificatePolicyDataGatherData());
        certificatePolicyTab.buttonNext.click();
        coveragesTab.fillTab(getDefaultEAPCertificatePolicyDataGatherData());
        assertSoftly(softly -> {
            ImmutableList.of(CLASS_NAME, COVERAGE_NAME, CONTRIBUTION_TYPE, ORIGINAL_EFFECTIVE_DATE, CoveragesTabMetaData.EFFECTIVE_DATE, ENROLLMENT_DATE, SPONSOR_PAYMENT_MODE, PLAN_TYPE,
                    PARTICIPANT_SELECTION, ROLE_NAME).forEach(element -> softly.assertThat(coveragesTab.getAssetList().getAsset(element)).isPresent());
            ImmutableList.of(TITLE, FIRST_NAME, MIDDLE_NAME, LAST_NAME, SUFFIX, DATE_OF_BIRTH, GENDER, TOBACCO, MARITAL_STATUS, TAX_IDENTIFICATION, RELATIONSHIP_TO_INSURED)
                    .forEach(element -> softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(element)).isPresent());
            ImmutableList.of(ADDRESS_TYPE, COUNTRY, ZIP_POST_CODE, CITY, STATE_PROVINCE, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_LINE_3)
                    .forEach(element -> softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(element)).isPresent());

            ImmutableList.of("Sub-Group Name", "Benefit Type", "Benefit Percentage", "Earning Definition", "Annual Earnings", "Maximum Weekly Benefit Amount",
                    "Weekly Benefit Amount", "EOI Required?", "Prior Coverage %", "EOI Status", "Requested %", "Pending %", "Approved %", "Prior Coverage Amount",
                    "Current Effective Amount", "Approved Amount", "Benefit Amount (from Coverages table)", "Benefit Amount (from Participants table)")
                    .forEach(element -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(element))).isAbsent());

            LOGGER.info("REN-41913 Step 2");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PLAN_TYPE)).isRequired().isDisabled().hasValue("Face to Face benefits");
            softly.assertThat(coveragesTab.getAssetList().getAsset(ROLE_NAME)).isRequired().isDisabled().hasValue("Primary Participant");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).isRequired().isEnabled().hasOptions("Self");
        });

        LOGGER.info("REN-41913 Step 4");
        Tab.buttonNext.click();
        EmployeeAssistanceProgramCertificatePolicyContext.premiumSummaryTab.buttonRate.click();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Benefit Amount"))).isAbsent();
            softly.assertThat(EmployeeAssistanceProgramCertificatePolicyContext.premiumSummaryTab.tablePremiumSummary.getRow(1).getCell(DAILY_PREMIUM.getName()))
                    .isPresent().valueMatches("\\$\\d+\\.\\d{5}");

            LOGGER.info("REN-41913 Step 5");
            Tab.buttonSaveAndExit.click();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-41913 Step 10");
            ImmutableList.of("Benefit Type", "Benefit Amount", "Tier")
                    .forEach(element -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(element))).isAbsent());
            assertThat(PolicySummaryPage.tablePremiumSummary.getRow(1).getCell("Plan Type")).isPresent().hasValue("Face to Face benefits");
        });
    }
}
