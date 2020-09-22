package com.exigen.ren.modules.policy.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PlansTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PlansTab.ParticipantsMultiAssetList;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.*;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.CHILD_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.SPOUSE_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.*;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UPLOAD_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.GeneralInformationMetaData.RELATIONSHIP_TO_INSURED;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.GeneralInformationMetaData.TAX_IDENTIFICATION;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.RoleInformationMetaData.ROLE_NAME;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PlansTab.addNewPersonParticipant;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PlansTab.updateExistingParticipantByRoleName;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCertificationPolicyFieldsModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalCertificatePolicyContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25815", component = POLICY_GROUPBENEFITS)
    public void testCertificationPolicyFieldsModificationVerification() {
        LOGGER.info("General preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                        .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", UPLOAD_FILE.getLabel(), FILE_UPLOAD.getLabel()), "$<file:REN_Rating_Census_File_All.xlsx>"),
                groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData").resolveLinks())
                .adjust(tdSpecific().getTestData("TestDataProposal").resolveLinks())
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_AllPlans")
                        .adjust(tdSpecific().getTestData("TestData_Issue").resolveLinks()).resolveLinks()).resolveLinks());

        LOGGER.info("TC#1 Step#1 verification");
        groupDentalCertificatePolicy.initiate(getDefaultGroupDentalCertificatePolicyData());
        groupDentalCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultGroupDentalCertificatePolicyData(), PlansTab.class, false);

        LOGGER.info("TC#1 Step#2 verification");
        String baseEposPlan = tdSpecific().getValue("PlanDefinitionTab_BASEPOS", PlansTabMetaData.PLAN_NAME.getLabel());
        assertSoftly(softly -> {
            softly.assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.PLAN_NAME).getAllValues())
                    .isEqualTo(ImmutableList.of(StringUtils.EMPTY, ALACARTE, baseEposPlan, FLEX_PLUS, MAJEPOS));

            LOGGER.info("TC#1 Step#3 verification");
            coveragesTab.getAssetList().getAsset(PlansTabMetaData.PLAN_NAME).setValue(baseEposPlan);
            softly.assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.MEMBER_PAYMENT_MODE)).isAbsent();

            LOGGER.info("TC#1 Step#5 verification");
            coveragesTab.getAssetList().getAsset(PlansTabMetaData.PLAN_NAME).setValue(ALACARTE);
            softly.assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.MEMBER_PAYMENT_MODE)).isPresent().isRequired();

            LOGGER.info("TC#1 Step#7 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.COVERAGE_TIER))
                    .isPresent().isRequired().hasOptions(ImmutableList.of(EMPTY, EMPLOYEE_PLUS_ONE, EMPLOYEE_PLUS_FAMILY, EMPLOYEE_ONLY));

            LOGGER.info("TC#1 Steps#8, 9 verification");
            coveragesTab.getAssetList().getAsset(PlansTabMetaData.COVERAGE_TIER).setValue(EMPLOYEE_ONLY);
            softly.assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.CLASSIFICATION_SUB_GROUP)).hasValue("DEFAULT").isDisabled();

            LOGGER.info("TC#1 Step#10 verification");
            coveragesTab.getAssetList().getAsset(PlansTabMetaData.PLAN_NAME).setValue(baseEposPlan);
            updateInsuredDataAndSubGroupValueCheck("99517", "Area 8", softly);

            LOGGER.info("TC#1 Step#11 verification");
            updateInsuredDataAndSubGroupValueCheck("99817", "Area 7", softly);

            LOGGER.info("TC#1 Step#15 verification");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Full Time Student Minimum Age"))).isAbsent();

            LOGGER.info("TC#1 Step#22 verification");
            coveragesTab.getAssetList().getAsset(PlansTabMetaData.COVERAGE_TIER).setValue(EMPLOYEE_PLUS_FAMILY);

            addNewPersonParticipant(CHILD_PARTICIPANT);
            ParticipantsMultiAssetList participantsAssetList = coveragesTab.getAssetList().getAsset(PlansTabMetaData.PARTICIPANTS);
            ComboBox relationshipToInsured = participantsAssetList.getAsset(GENERAL_INFORMATION).getAsset(RELATIONSHIP_TO_INSURED);
            softly.assertThat(relationshipToInsured).hasValue(DEPENDENT_CHILD);

            LOGGER.info("TC#1 Step#23 verification");
            addNewPersonParticipant(SPOUSE_PARTICIPANT);
            softly.assertThat(relationshipToInsured).hasValue(SPOUSE_DOMESTIC_PARTNER);

            LOGGER.info("TC#1 Step#27 verification");
            relationshipToInsured.setValue(SELF);
            softly.assertThat(participantsAssetList.getAsset(GENERAL_INFORMATION).getAsset(TAX_IDENTIFICATION)).isRequired();
            softly.assertThat(relationshipToInsured).hasWarningWithText(String.format("Only Primary Insured can have Self %s.", RELATIONSHIP_TO_INSURED.getLabel()));

            LOGGER.info("TC#1 Step#28 verification");
            coveragesTab.submitTab();
            softly.assertThat(participantsAssetList.getAsset(GENERAL_INFORMATION).getAsset(TAX_IDENTIFICATION))
                    .hasWarningWithText(String.format(ERROR_PATTERN, TAX_IDENTIFICATION.getLabel()));

            LOGGER.info("TC#2 Step#1 verification");
            updateExistingParticipantByRoleName(CHILD_PARTICIPANT);
            softly.assertThat(relationshipToInsured).isRequired().hasOptions(DEPENDENT_CHILD, DISABLED_DEPENDENT, SELF, SPOUSE_DOMESTIC_PARTNER);

            LOGGER.info("TC#2 Step#2 verification");
            softly.assertThat(participantsAssetList.getAsset(ROLE_INFORMATION).getAsset(ROLE_NAME)).isDisabled().hasValue(CHILD_PARTICIPANT);

            LOGGER.info("TC#2 Step#6 verification");
            softly.assertThat(participantsAssetList.getAsset(ADDRESS_INFORMATION).getAsset(PlansTabMetaData.AddressInformationMetaData.ADDRESS_TYPE))
                    .isRequired().isDisabled().hasValue("Mailing");

            LOGGER.info("TC#2 Step#7 verification");
            softly.assertThat(participantsAssetList.getAsset(ADDRESS_INFORMATION).getAsset(PlansTabMetaData.AddressInformationMetaData.COUNTRY))
                    .isDisabled().isRequired().hasValue("United States");
        });
    }

    private void updateInsuredDataAndSubGroupValueCheck(String zipCodeValue, String subGroupValue, ETCSCoreSoftAssertions softly) {
        insuredTab.navigateToTab();
        insuredTab.getAssetList().getAsset(InsuredTabMetaData.ZIP_POST_CODE).setValue(zipCodeValue);
        insuredTab.getAssetList().getAsset(InsuredTabMetaData.STATE_PROVINCE).setValue("AK");
        coveragesTab.navigateToTab();
        softly.assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.CLASSIFICATION_SUB_GROUP)).hasValue(subGroupValue).isDisabled();
    }
}