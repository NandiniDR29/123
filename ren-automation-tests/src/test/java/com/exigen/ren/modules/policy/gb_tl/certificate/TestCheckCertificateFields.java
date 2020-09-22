package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.ENROLLMENT_TYPE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_TYPE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckCertificateFields extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-23760", component = POLICY_GROUPBENEFITS)
    public void testCheckCertificateFields() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceCertificatePolicy.initiate(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("Step 1");
        assertThat(certificatePolicyTab.getAssetList().getAsset(ENROLLMENT_TYPE)).hasValue("New Hire");

        assertSoftly(softly -> {
            insuredTab.navigateToTab();
            LOGGER.info("Step 3");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.TITLE)).hasOptions("", "Doctor", "Mr", "Mrs", "Ms", "Estate Of");

            LOGGER.info("Step 5");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.SUFFIX)).isPresent().isOptional().hasOptions("", "Jr.", "Sr.", "II", "III", "IV", "V", "PhD", "MD", "DDS");

            LOGGER.info("Step 7");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENDER)).isPresent().isRequired().hasValue("");

            LOGGER.info("Step 9");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_TYPE)).isPresent().isRequired().hasValue("Mailing");

            LOGGER.info("Step 11");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.COUNTRY)).isPresent().isRequired().hasValue("United States");

            LOGGER.info("Step 17");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.JOB_CODE)).isPresent().isOptional().hasValue("Any Work");

            LOGGER.info("Step 18");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.JOB_TITLE)).isPresent().isOptional().hasValue("");

            LOGGER.info("Step 19");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.EMPLOYMENT_TYPE)).isPresent().isOptional().hasValue("");

            LOGGER.info("Step 20");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.PAY_CLASS)).isPresent().isOptional().hasValue("Full-Time");

            LOGGER.info("Step 21");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.PAYROLL_FREQUENCY)).isPresent().isOptional().hasOptions("", "Weekly", "Bi-Weekly", "Monthly", "Semi-Monthly", "Annual", "Hourly", "Unknown");
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-23764", component = POLICY_GROUPBENEFITS)
    public void testCheckCertificateFields_2() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", BENEFIT_SCHEDULE.getLabel()), DataProviderFactory.dataOf(BENEFIT_TYPE.getLabel(), "Salary Multiplier - Single Value")));
        termLifeInsuranceCertificatePolicy.initiate(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));

        insuredTab.navigateToTab();

        LOGGER.info("Step 1");
        assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.DATE_OF_BIRTH)).isPresent().isRequired().hasValue("");

        LOGGER.info("Step 12");
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillFromTo(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .mask(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.ANNUAL_EARNINGS.getLabel())),
                insuredTab.getClass(), coveragesTab.getClass());
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValue(BASIC_LIFE_PLAN);
        assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.BENEFIT_TYPE)).isDisabled().hasValue("Salary Multiplier - Single Value");

        LOGGER.info("Step 13");
        insuredTab.navigateToTab();
        assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.ANNUAL_EARNINGS)).isPresent().isRequired().hasValue(ValueConstants.EMPTY);
    }
}