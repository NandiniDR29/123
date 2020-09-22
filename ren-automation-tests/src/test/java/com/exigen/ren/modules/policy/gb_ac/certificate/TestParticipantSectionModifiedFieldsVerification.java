package com.exigen.ren.modules.policy.gb_ac.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
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
import static com.exigen.ren.main.enums.AccountConstants.AccountConfidential.YES;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestParticipantSectionModifiedFieldsVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext {

    private final AssetList participantsGeneralInfoAssetList = coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PARTICIPANTS).getAsset(CoveragesTabMetaData.ParticipantsMetaData.PARTICIPANT_GENERAL_INFO);
    private final AssetList participantsAddressInfoAssetList = coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PARTICIPANTS).getAsset(CoveragesTabMetaData.ParticipantsMetaData.PARTICIPANT_ADDRESS_INFO);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-27820", component = POLICY_GROUPBENEFITS)
    public void testParticipantSectionModifiedFieldsVerification() {
        LOGGER.info("REN-27820 preconditions");
        String addressLine2Value = "Address Line 2";
        String addressLine3Value = "Address Line 3";

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicyViaUI(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataWithBasicBenefitsTab")
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS.getLabel())).resolveLinks()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", "Coverage Tiers Change Confirmation")).resolveLinks()
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        groupAccidentCertificatePolicy.initiate(getDefaultACCertificatePolicyDataGatherData());
        groupAccidentCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultACCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.ADDRESS_LINE_2.getLabel()), addressLine2Value)
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.ADDRESS_LINE_3.getLabel()), addressLine3Value)
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), CoveragesTabMetaData.COVERAGE_TIER.getLabel()), "Employee + Family"), CoveragesTab.class, true);

        NavigationPage.toLeftMenuTab(INSURED);
        String addressLine1 = insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.ADDRESS_LINE_1).getValue();
        String addressType = insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.ADDRESS_TYPE).getValue();
        NavigationPage.toLeftMenuTab(COVERAGES);

        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("REN-27820 Scenario#1 step#1 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN)).isRequired().hasOptions(ImmutableList.of(EMPTY, BASE_BUY_UP));

            LOGGER.info("REN-27820 Scenario#1 step#2 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PARTICIPANTS).getAsset(CoveragesTabMetaData.ParticipantsMetaData.ROLE_NAME)).isDisabled().hasValue("Primary Participant");

            LOGGER.info("REN-27820 Scenario#1 step#3, 4 verification");
            titleSuffixVerification();

            LOGGER.info("REN-27820 Scenario#1 step#5 verification");
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.GENDER))
                    .isOptional().hasOptions(ImmutableList.of(EMPTY, "Female", "Male", "Unspecified"));

            participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.TITLE).setValue("Doctor");
            participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.SUFFIX).setValue("Sr.");

            LOGGER.info("REN-27820 Scenario#1 step#7 verification");
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.TAX_IDENTIFICATION)).isRequired();

            LOGGER.info("REN-27820 Scenario#1 step#11 verification");
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.RELATIONSHIP_TO_INSURED)).isRequired().hasValue("Self");

            LOGGER.info("REN-27820 Scenario#1 step#12 verification");
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.USE_PRIMARY_INSUREDS_ADDRESS)).isAbsent();

            LOGGER.info("REN-27820 Scenario#1 step#14, 15, 16, 17, 24, 25 verification");
            addressInfoVerification(addressType, addressLine1);

            LOGGER.info("REN-27820 Scenario#1 step#20 verification");
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.ADDRESS_LINE_2)).isDisabled().hasValue(addressLine2Value);

            LOGGER.info("REN-27820 Scenario#1 step#22 verification");
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.ADDRESS_LINE_3)).isDisabled().hasValue(addressLine3Value);
        });

        LOGGER.info("REN-27820 Scenario#2 steps#1, 2 verification");
        groupAccidentCertificatePolicy.getDefaultWorkspace().fill(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant"));
        NavigationPage.toLeftMenuTab(COVERAGES);
        titleSuffixVerification();

        LOGGER.info("REN-27820 Scenario#2 step#3 verification");
        assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.GENDER))
                .isOptional().hasValue(EMPTY).hasOptions(ImmutableList.of(EMPTY, "Female", "Male", "Unspecified"));

        LOGGER.info("REN-27820 Scenario#2 steps#5, 6, 9 verification");
        taxUsePrimaryAndRelationshipVerification("Spouse/Domestic Partner");
        assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.RELATIONSHIP_TO_INSURED)).hasValue("Spouse/Domestic Partner");

        LOGGER.info("REN-27820 Scenario#2 step#11, 12, 13 verification");
        groupAccidentCertificatePolicy.getDefaultWorkspace().fill(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), CoveragesTabMetaData.PARTICIPANTS.getLabel(), CoveragesTabMetaData.ParticipantsMetaData.ROLE_NAME.getLabel()), "Child Participant").resolveLinks());
        NavigationPage.toLeftMenuTab(COVERAGES);
        taxUsePrimaryAndRelationshipVerification("Dependent Child");

        LOGGER.info("REN-27820 Scenario#2 step#15, 17, 18, 20, 29, 30 verification");
        addressInfoVerification(addressType, addressLine1);
    }

    private void titleSuffixVerification() {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.TITLE))
                    .isOptional().hasOptions(ImmutableList.of(EMPTY, "Doctor", "Mr", "Mrs", "Ms", "Estate Of"));
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.SUFFIX))
                    .isOptional().hasOptions(ImmutableList.of(EMPTY, "Jr.", "Sr.", "II", "III", "IV", "V", "PhD", "MD", "DDS"));
        });
    }

    private void taxUsePrimaryAndRelationshipVerification(String relationshipValue) {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.RELATIONSHIP_TO_INSURED)).hasValue(relationshipValue);
            softly.assertThat(participantsGeneralInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantGeneralInfoMetaData.TAX_IDENTIFICATION)).isOptional();
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.USE_PRIMARY_INSUREDS_ADDRESS)).isPresent().hasValue(YES);
        });
    }

    private void addressInfoVerification(String addressType, String addressLine1) {
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.ADDRESS_TYPE))
                    .isRequired().hasValue(addressType);
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.STATE_PROVINCE))
                    .isRequired().hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.STATE_PROVINCE.getLabel()));
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.COUNTRY))
                    .isRequired().hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.ADDRESS_INFORMATION.getLabel(), InsuredTabMetaData.AddressInformationMetaData.COUNTRY.getLabel()));
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.ADDRESS_LINE_1))
                    .isRequired().hasValue(addressLine1);
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.ZIP_POST_CODE))
                    .isRequired().hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.ZIP_POST_CODE.getLabel()));
            softly.assertThat(participantsAddressInfoAssetList.getAsset(CoveragesTabMetaData.ParticipantAddressInfoMetaData.CITY))
                    .isRequired().hasValue(groupAccidentCertificatePolicy.getDefaultTestData().getValue(TestDataKey.DATA_GATHER, InsuredTab.class.getSimpleName(), InsuredTabMetaData.CITY.getLabel()));
        });
    }
}