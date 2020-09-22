package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.VOLUNTARY_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.BenefitSpouseMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.RELATIONSHIP_TO_INSURED;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestImprovingAgeCalculation extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-34355"}, component = POLICY_GROUPBENEFITS)
    public void testImprovingAgeCalculation() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData().adjust(tdSpecific().getTestData("TestDataPolicy").resolveLinks())
                .resolveLinks());

        LOGGER.info("TC1 Step 1");
        LocalDateTime todayDate = TimeSetterUtil.getInstance().getCurrentTime();
        TestData tdCertificateQuote = getDefaultCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.DATE_OF_BIRTH.getLabel()), todayDate.minusYears(70).format(DateTimeUtils.MM_DD_YYYY))
                .resolveLinks();
        termLifeInsuranceCertificatePolicy.initiate(tdCertificateQuote);
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificateQuote, CoveragesTab.class);

        LOGGER.info("TC1 Step 2");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValue(VOLUNTARY_LIFE_PLAN);
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_NAME).setValue(VOL_BTL);
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.APPROVED_AMOUNT).setValue("1");
        CoveragesTab.buttonAddParticipant.click();
        coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION).setValueContains("John");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).hasValue(SELF);

        LOGGER.info("TC1 Step 3");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.CURRENT_EFFECTIVE_AMOUNT).setValueByIndex(2);
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.APPROVED_AMOUNT).setValue("1");
        CoveragesTab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Employee age exceeds termination age, unable to add.");

        LOGGER.info("TC1 Step 4");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        CoveragesTab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Employee age exceeds termination age, unable to add.");

        LOGGER.info("TC1 Step 5");
        TermLifeInsuranceCertificatePolicyContext.premiumSummaryTab.navigateToTab().buttonRate.click();
        assertThat(ErrorPage.tableError.getRow(MESSAGE.getName(), "Employee age exceeds termination age, unable to add.")).isPresent();

        LOGGER.info("TC1 Step 6");
        Tab.buttonCancel.click();
        coveragesTab.navigateToTab().getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        CoveragesTab.buttonNext.click();
        assertThat(PremiumSummaryTab.buttonRate).isPresent();

        LOGGER.info("TC1 Step 7");
        coveragesTab.navigateToTab();
        CoveragesTab.buttonAddCoverage.click();
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_NAME).setValue(SP_VOL_BTL);
        CoveragesTab.buttonAddParticipant.click();
        coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION).setValue("New Person");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).hasValue(SPOUSE_DOMESTIC_PARTNER);

        LOGGER.info("TC1 Step 8");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(BENEFIT_SPOUSE).getAsset(ELECTED_BENEFIT_AMOUNT).setValue("10");
        coveragesTab.getAssetList().getAsset(BENEFIT_SPOUSE).getAsset(CURRENT_EFFECTIVE_AMOUNT_SPOUSE).setValue("0.01");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(FIRST_NAME).setValue("First Name1");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(LAST_NAME).setValue("Last Name1asdfwe");
        CoveragesTab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Spouse age exceeds termination age, unable to add.");

        LOGGER.info("TC1 Step 9");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        CoveragesTab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Spouse age exceeds termination age, unable to add.");

        LOGGER.info("TC1 Step 11");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        CoveragesTab.buttonNext.click();
        assertThat(PremiumSummaryTab.buttonRate).isPresent();

        LOGGER.info("TC1 Step 12");
        coveragesTab.navigateToTab();
        CoveragesTab.buttonAddCoverage.click();
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_NAME).setValue(DEP_VOL_BTL);
        CoveragesTab.buttonAddParticipant.click();
        coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION).setValue("New Person");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).hasValue(DEPENDENT_CHILD);

        LOGGER.info("TC1 Step 13");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(BENEFIT_CHILD).getAsset(CoveragesTabMetaData.BenefitChildMetaData.ELECTED_BENEFIT_AMOUNT).setValue("10");
        coveragesTab.getAssetList().getAsset(BENEFIT_CHILD).getAsset(CoveragesTabMetaData.BenefitChildMetaData.BENEFIT_AMOUNT).setValue("0.01");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(25).format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(FIRST_NAME).setValue("First Name1");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(LAST_NAME).setValue("Last Name1jhgn");
        CoveragesTab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Overage dependent, unable to add.");

        LOGGER.info("TC1 Step 14");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(25).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        CoveragesTab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Overage dependent, unable to add.");

        LOGGER.info("TC1 Step 17");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(25).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        CoveragesTab.buttonNext.click();
        TermLifeInsuranceCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("TC3 Step 1");
        //ToDo rznamerovskyi: finish test when REN-394 will be ready
    }
}
