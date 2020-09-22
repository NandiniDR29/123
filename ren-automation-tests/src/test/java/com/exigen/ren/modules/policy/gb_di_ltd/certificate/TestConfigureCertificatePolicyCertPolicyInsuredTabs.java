package com.exigen.ren.modules.policy.gb_di_ltd.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CertificatePolicyTabMetaData.ENROLLMENT_TYPE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureCertificatePolicyCertPolicyInsuredTabs extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityCertificatePolicyContext, LongTermDisabilityMasterPolicyContext {
    private static final AssetList relationshipInformationAssetList = insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23732", "REN-23736"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureCertificatePolicyCertPolicyInsuredTabs() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityCertificatePolicy.initiate(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertSoftly(softly -> {
            LOGGER.info("REN-23732, Step 1 , Step 3 , Step 5 and Step 7");
            softly.assertThat(certificatePolicyTab.getAssetList().getAsset(ENROLLMENT_TYPE)).hasValue("New Hire");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.INSURED.get());
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.TITLE)).isPresent().isOptional().hasOptions(StringUtils.EMPTY, "Doctor", "Mr", "Mrs", "Ms", "Estate Of");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.SUFFIX)).isPresent().isOptional().hasOptions(StringUtils.EMPTY, "Jr.", "Sr.", "II", "III", "IV", "V", "PhD", "MD", "DDS");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.GENDER)).isPresent().isOptional().hasValue(StringUtils.EMPTY);
            LOGGER.info("REN-23732 Step 9 and Step 11");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.ADDRESS_TYPE)).isPresent().isRequired().hasValue("Mailing");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.COUNTRY)).isPresent().isRequired().hasValue("United States");
            LOGGER.info("REN-23732 Step 15, Step 17 and Step 21");
            ImmutableList.of(JOB_CODE, EMPLOYMENT_STATUS, PAYROLL_FREQUENCY).forEach(control -> softly.assertThat(relationshipInformationAssetList
                    .getAsset(control)).isPresent().isOptional().hasValue(StringUtils.EMPTY));
            softly.assertThat(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(InsuredTabMetaData.RelationshipInformationMetaData.PAYROLL_FREQUENCY)).hasOptions(StringUtils.EMPTY, "Weekly", "Bi-Weekly", "Monthly", "Semi-Monthly", "Annual", "Hourly", "Unknown");
            LOGGER.info("REN-23736 Step 1, Step 6 and Step 7");
            softly.assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH)).isPresent().isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS)).isPresent().hasValue(StringUtils.EMPTY);
            longTermDisabilityCertificatePolicy.getDefaultWorkspace().fillFromTo(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY), insuredTab.getClass(), coveragesTab.getClass());
            coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValue(NC);
            softly.assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.BENEFIT_TYPE)).isDisabled().hasValue("Percentage of Monthly Salary - Single Value");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.INSURED.get());
            softly.assertThat(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS)).isRequired();
        });
    }
}