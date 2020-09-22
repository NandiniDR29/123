package com.exigen.ren.modules.policy.gb_ac.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.tabs.InsuredTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.COVERAGES;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.INSURED;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ROLE_PERCENT_PRIMARY_ERROR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ROLE_PERCENT_SECONDARY_ERROR;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.BENEFICIARIES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class TestBeneficiarySectionModifiedFieldsVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext {

    private AssetList beneficiaryCorpGeneralInfoAssetList = coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_GENERAL_INFO_CORP);
    private AssetList beneficiaryIndGeneralInfoAssetList = coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_GENERAL_INFO);
    private AssetList beneficiaryIndGAddressInfoAssetList = coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_ADDRESS_INFO);

    private final String NEW_PERSON = "New Person";
    private final String NEW_NON_PERSON = "New Non-Person";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-27821", component = POLICY_GROUPBENEFITS)
    public void testBeneficiarySectionModifiedFieldsVerificationTC1() {

        LOGGER.info("REN-27821 scenario#1 preconditions");
        loginAndIssueACMasterPolicyWithBaseBuyUpPlan();
        groupAccidentCertificatePolicy.initiate(getDefaultACCertificatePolicyDataGatherData());
        groupAccidentCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultACCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), CoveragesTabMetaData.COVERAGE_TIER.getLabel()), "Employee + Family"), CoveragesTab.class, true);

        LOGGER.info("REN-27821 scenario#1 step#1 verification");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_SELECTION)).containsAllOptions(EMPTY, NEW_PERSON, NEW_NON_PERSON);

        LOGGER.info("REN-27821 scenario#1 step#3 verification");
        warningTextVerification("Primary Beneficiary", ROLE_PERCENT_PRIMARY_ERROR);

        LOGGER.info("REN-27821 scenario#1 step#7 verification");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
        warningTextVerification("Secondary Beneficiary", ROLE_PERCENT_SECONDARY_ERROR);

        LOGGER.info("REN-27821 scenario#1 step#11, 12, 16, 18, 19, 20, 22, 24, 26, 29 verification");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT).setValue("35");
        corpGeneralInfoVerification();

        CoveragesTab.openExistingBeneficiary("40");
        corpGeneralInfoVerification();

        LOGGER.info("REN-27821 scenario#1 step#14 verification");
        CoveragesTab.openExistingBeneficiary("60");
        assertThat(beneficiaryIndGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.FIRST_NAME)).isPresent().isRequired();
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT).setValue("55");

        CoveragesTab.openExistingBeneficiary("60");
        assertThat(beneficiaryIndGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.FIRST_NAME)).isPresent().isRequired();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-27821", component = POLICY_GROUPBENEFITS)
    public void testBeneficiarySectionModifiedFieldsVerificationTC2() {
        LOGGER.info("REN-27821 scenario#2 preconditions");
        mainApp().open();
        loginAndIssueACMasterPolicyWithBaseBuyUpPlan();

        groupAccidentCertificatePolicy.initiate(getDefaultACCertificatePolicyDataGatherData());
        groupAccidentCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultACCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.ADDRESS_LINE_3.getLabel()), "Address Line 3")
                .adjust(tdSpecific().getTestData("TestDataCertificatePolicyWithBeneficiaries").resolveLinks()).resolveLinks(), CoveragesTab.class, true);
        NavigationPage.toLeftMenuTab(INSURED);
        String addressLine1 = insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.ADDRESS_LINE_1).getValue();
        String addressLine3 = insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.ADDRESS_LINE_3).getValue();
        NavigationPage.toLeftMenuTab(COVERAGES.get());

        LOGGER.info("REN-27821 scenario#2 step#1, 9, 15, 17, 19, 20, 24 verification");
        generalAndAddressPersonVerification("60", addressLine1, addressLine3);
        assertThat(beneficiaryIndGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.RELATIONSHIP_TO_INSURED)).containsOption("Self");

        generalAndAddressPersonVerification("65", addressLine1, addressLine3);

        LOGGER.info("REN-27821 scenario#1 steps#6, 11, 17, 20, 23 verification");
        nameTaxAddressNonPersonVerification("40");
        assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.RELATIONSHIP_TO_INSURED)).containsAllOptions("", "Trust/Charity/Other");

        nameTaxAddressNonPersonVerification("35");
        Tab.buttonTopSave.click();
    }

    private void loginAndIssueACMasterPolicyWithBaseBuyUpPlan() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicyViaUI(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataWithBasicBenefitsTab")
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS.getLabel())).resolveLinks()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", "Coverage Tiers Change Confirmation")).resolveLinks()
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }

    private void warningTextVerification(String roleName, String warningText) {
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_SELECTION).setValue(NEW_PERSON);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_NAME).setValue(roleName);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT).setValue("60");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_SELECTION).setValue(NEW_NON_PERSON);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_NAME).setValue(roleName);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT).setValue("30");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT)).hasWarningWithText(warningText);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT).setValue("40");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ROLE_PERCENT)).hasNoWarning();
    }

    private void corpGeneralInfoVerification() {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.RELATIONSHIP_TO_INSURED)).hasValue("Trust/Charity/Other");
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.TITLE)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.FIRST_NAME)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.LAST_NAME)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.MIDDLE_NAME)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.SUFFIX)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.DATE_OF_BIRTH)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.MARITAL_STATUS)).isAbsent();
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.GENDER)).isAbsent();
        });
    }

    private void nameTaxAddressNonPersonVerification(String rolePercent) {
        CoveragesTab.openExistingBeneficiary(rolePercent);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.NAME_LEGAL)).isPresent().isRequired().hasValue(EMPTY);
            softly.assertThat(beneficiaryCorpGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.TAX_IDENTIFICATION)).isPresent().isOptional().hasValue(EMPTY);
            softly.assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_ADDRESS_INFO_CORP)
                    .getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.ADDRESS_TYPE)).hasValue("Mailing");
        });
    }

    private void generalAndAddressPersonVerification(String rolePercent, String addressLine1, String addressLine3) {
        CoveragesTab.openExistingBeneficiary(rolePercent);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(beneficiaryIndGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.TAX_IDENTIFICATION)).isPresent().isRequired().hasValue(EMPTY);
            softly.assertThat(beneficiaryIndGeneralInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.NAME_LEGAL)).isAbsent();
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.USE_PRIMARY_INSUREDS_ADDRESS)).isPresent().isOptional().hasValue(VALUE_YES);
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.STATE_PROVINCE))
                    .hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.STATE_PROVINCE.getLabel()));
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.COUNTRY))
                    .hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.ADDRESS_INFORMATION.getLabel(), InsuredTabMetaData.AddressInformationMetaData.COUNTRY.getLabel()));
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.ADDRESS_LINE_1)).hasValue(addressLine1);
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.ADDRESS_LINE_3)).hasValue(addressLine3);
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.ZIP_POSTAL_CODE))
                    .hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.ZIP_POST_CODE.getLabel()));
            softly.assertThat(beneficiaryIndGAddressInfoAssetList.getAsset(CoveragesTabMetaData.BeneficiaryAddressInfoMetaData.CITY))
                    .hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.CITY.getLabel()));
        });
    }
}