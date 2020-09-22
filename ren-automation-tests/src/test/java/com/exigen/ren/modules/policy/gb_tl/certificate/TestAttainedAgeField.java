package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData.PRODUCT;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.BenefitSpouseMetaData.CURRENT_EFFECTIVE_AMOUNT_SPOUSE_COMBOBOX;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.BenefitSpouseMetaData.ELECTED_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.FIRST_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.LAST_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAttainedAgeField extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    private static final AssetList participantGeneralInfoAssetList = coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO);
    private static final ComboBox participantSelectionComboBox = coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-33784", component = POLICY_GROUPBENEFITS)
    public void testAttainedAgeField() {
        int age33 = 33;
        int age32 = 32;
        int age30 = 30;
        int age29 = 29;
        int age12 = 12;
        int age11 = 11;

        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile")
                .adjust(TestData.makeKeyPath(productAndPlanManagementTab.getMetaKey(), PRODUCT.getLabel()), termLifeInsuranceMasterPolicy.getType().getName()).resolveLinks());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData().adjust(tdSpecific().getTestData("TestDataPolicy").resolveLinks()).resolveLinks());

        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Preconditions");
        LocalDateTime todayDate = TimeSetterUtil.getInstance().getCurrentTime();
        TestData tdCertificateQuote = getDefaultCertificatePolicyDataGatherData()
                .adjust(tdSpecific().getTestData("TestDataCertificatePolicyCoveragesTab").resolveLinks())
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), todayDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), DATE_OF_BIRTH.getLabel()), todayDate.minusYears(age33).format(DateTimeUtils.MM_DD_YYYY))
                .resolveLinks();
        termLifeInsuranceCertificatePolicy.initiate(tdCertificateQuote);

        LOGGER.info("TC1 Step 1");
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificateQuote, coveragesTab.getClass(), true);
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).isPresent().isRequired().isDisabled().hasValue(String.valueOf(age33));

        LOGGER.info("TC1 Step 1.1 - 2");
        coveragesTab.buttonAddCoverage.click();
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_NAME).setValue(VOL_ADD);
        coveragesTab.getAssetList().getAsset(ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(CURRENT_EFFECTIVE_AMOUNT).setValueByIndex(1);

        coveragesTab.buttonAddParticipant.click();
        participantSelectionComboBox.setValueStarts("Self");
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).isPresent().isRequired().isDisabled().hasValue(String.valueOf(age33));

        LOGGER.info("TC1 Step 2.1");
        insuredTab.navigateToTab().getAssetList().getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(age33).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.navigateToTab();

        coveragesTab.openAddedPlan(VOL_BTL);
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age32));
        coveragesTab.openAddedPlan(VOL_ADD);
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age32));

        LOGGER.info("TC1 Step 2.2");
        insuredTab.navigateToTab().getAssetList().getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(age33).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.navigateToTab();
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age32));
        certificatePolicyTab.navigateToTab().getAssetList().getAsset(EFFECTIVE_DATE).setValue(todayDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.navigateToTab();

        coveragesTab.openAddedPlan(VOL_BTL);
        coveragesTab.buttonAddParticipant.click();
        participantSelectionComboBox.setValueStarts("Self");
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age32));

        coveragesTab.openAddedPlan(VOL_ADD);
        coveragesTab.buttonAddParticipant.click();
        participantSelectionComboBox.setValueStarts("Self");
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age32));

        LOGGER.info("TC1 Step 2.3");
        coveragesTab.removeExistingPlan(VOL_ADD);

        LOGGER.info("TC1 Step 3");
        insuredTab.navigateToTab().getAssetList().getAsset(ORIGINAL_HIRE_DATE).setValue(todayDate.minusYears(2).format(DateTimeUtils.MM_DD_YYYY));
        insuredTab.getAssetList().getAsset(ANNUAL_EARNINGS).setValue("10");
        TermLifeInsuranceCertificatePolicyContext.premiumSummaryTab.navigateToTab().submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ATTAINED_AGE.getLabel()))).isAbsent();

        LOGGER.info("TC1 Step 4");
        issueQuote(tdCertificateQuote);

        LOGGER.info("TC2 Step 1, 2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        termLifeInsuranceCertificatePolicy.initiate(tdCertificateQuote);
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificateQuote, coveragesTab.getClass(), true);
        coveragesTab.buttonAddCoverage.click();
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_NAME).setValue(SP_VOL_BTL);
        coveragesTab.getAssetList().getAsset(ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(BENEFIT_SPOUSE).getAsset(CURRENT_EFFECTIVE_AMOUNT_SPOUSE_COMBOBOX).setValueByIndex(1);
        coveragesTab.getAssetList().getAsset(BENEFIT_SPOUSE).getAsset(ELECTED_BENEFIT_AMOUNT).setValue("10");

        addNewPersonParticipantAndCheckAttainedAge(todayDate, age30, age30);

        LOGGER.info("TC2 Step 2.1");
        participantGeneralInfoAssetList.getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(age30).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age29));

        LOGGER.info("TC2 Step 2.2");
        participantGeneralInfoAssetList.getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(age30).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age30));

        certificatePolicyTab.navigateToTab().getAssetList().getAsset(EFFECTIVE_DATE).setValue(todayDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.navigateToTab();
        coveragesTab.openAddedPlan(SP_VOL_BTL);
        addNewPersonParticipantAndCheckAttainedAge(todayDate, age30, age29);

        coveragesTab.openAddedPlan(VOL_BTL);
        coveragesTab.buttonAddParticipant.click();
        participantSelectionComboBox.setValueStarts("Self");

        LOGGER.info("TC2 Step 3");
        insuredTab.navigateToTab().getAssetList().getAsset(ORIGINAL_HIRE_DATE).setValue(todayDate.minusYears(2).format(DateTimeUtils.MM_DD_YYYY));
        insuredTab.getAssetList().getAsset(ANNUAL_EARNINGS).setValue("10");
        TermLifeInsuranceCertificatePolicyContext.premiumSummaryTab.navigateToTab().submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ATTAINED_AGE.getLabel()))).isAbsent();

        LOGGER.info("TC2 Step 4");
        issueQuote(tdCertificateQuote);

        LOGGER.info("TC3 Step 1, 2");
        MainPage.QuickSearch.search(masterPolicyNumber);
        termLifeInsuranceCertificatePolicy.initiate(tdCertificateQuote);
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificateQuote, coveragesTab.getClass(), true);
        coveragesTab.buttonAddCoverage.click();
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_NAME).setValue(DEP_VOL_BTL);
        coveragesTab.getAssetList().getAsset(ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(BENEFIT_CHILD).getAsset(CoveragesTabMetaData.BenefitChildMetaData.BENEFIT_AMOUNT_COMBOBOX).setValueByIndex(1);
        coveragesTab.getAssetList().getAsset(BENEFIT_CHILD).getAsset(CoveragesTabMetaData.BenefitChildMetaData.ELECTED_BENEFIT_AMOUNT).setValue("10");

        addNewPersonParticipantAndCheckAttainedAge(todayDate, age12, age12);

        LOGGER.info("TC3 Step 2.1");
        participantGeneralInfoAssetList.getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(age12).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age11));

        LOGGER.info("TC3 Step 2.2");
        participantGeneralInfoAssetList.getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(age12).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).hasValue(String.valueOf(age12));

        certificatePolicyTab.navigateToTab().getAssetList().getAsset(EFFECTIVE_DATE).setValue(todayDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.navigateToTab();
        coveragesTab.openAddedPlan(DEP_VOL_BTL);
        addNewPersonParticipantAndCheckAttainedAge(todayDate, age12, age11);

        coveragesTab.openAddedPlan(VOL_BTL);
        coveragesTab.buttonAddParticipant.click();
        participantSelectionComboBox.setValueStarts("Self");

        LOGGER.info("TC3 Step 3");
        insuredTab.navigateToTab().getAssetList().getAsset(ORIGINAL_HIRE_DATE).setValue(todayDate.minusYears(2).format(DateTimeUtils.MM_DD_YYYY));
        insuredTab.getAssetList().getAsset(ANNUAL_EARNINGS).setValue("10");
        TermLifeInsuranceCertificatePolicyContext.premiumSummaryTab.navigateToTab().submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ATTAINED_AGE.getLabel()))).isAbsent();

        LOGGER.info("TC3 Step 4");
        issueQuote(tdCertificateQuote);
    }

    private void issueQuote(TestData td) {
        td.adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks();
        termLifeInsuranceCertificatePolicy.issue().perform(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ATTAINED_AGE.getLabel()))).isAbsent();
    }

    private void addNewPersonParticipantAndCheckAttainedAge(LocalDateTime todayDate, int dob, int attainedAge) {
        coveragesTab.buttonAddParticipant.click();
        participantSelectionComboBox.setValue("New Person");
        participantGeneralInfoAssetList.getAsset(FIRST_NAME).setValue("First Name1");
        participantGeneralInfoAssetList.getAsset(LAST_NAME).setValue("Last Name1oihk");
        participantGeneralInfoAssetList.getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(dob).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(participantGeneralInfoAssetList.getAsset(ATTAINED_AGE)).isPresent().isRequired().isDisabled().hasValue(String.valueOf(attainedAge));
    }
}

