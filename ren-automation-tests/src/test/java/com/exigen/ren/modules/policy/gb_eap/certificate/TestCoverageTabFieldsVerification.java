package com.exigen.ren.modules.policy.gb_eap.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.ErrorPage.TableError;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.EmployeeAssistanceProgramCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.*;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN_NAME;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCoverageTabFieldsVerification extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {

    private static final String POLICY_PLAN_NAME = "CONTEST";
    private static final String DATE_OF_BIRTH_ERROR_MESSAGE = "'Date of Birth' is required";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25102", component = POLICY_GROUPBENEFITS)
    public void testCoverageTabFieldsVerification() {
        LOGGER.info("REN-25102 preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());
        employeeAssistanceProgramMasterPolicy.createPolicy(getDefaultEAPMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", PLAN_NAME.getLabel()), POLICY_PLAN_NAME));
        employeeAssistanceProgramCertificatePolicy.initiate(getDefaultEAPCertificatePolicyDataGatherData());
        employeeAssistanceProgramCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultEAPCertificatePolicyDataGatherData(), CoveragesTab.class, false);

        LOGGER.info("Step#1 verification");
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        assertThat(coveragesTab.getAssetList().getAsset(PLAN)).hasOptions(ImmutableList.of("", POLICY_PLAN_NAME));

        LOGGER.info("Step#2 verification");
        coveragesTab.getAssetList().getAsset(PLAN).setValue(POLICY_PLAN_NAME);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Current Effective %"))).isAbsent();

        LOGGER.info("Step#4 verification");
        coveragesTab.getAssetList().getAsset(ENROLLMENT_DATE).setValue(DateTimeUtils.MM_DD_YYYY.format(currentDate));

        LOGGER.info("Step#10 verification");
        coveragesTab.navigateToTab();
        coveragesTab.getAssetList().getAsset(ADD_PARTICIPANT).click();
        coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION).setValueByIndex(0);
        TextBox dateOfBirth = coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ParticipantGeneralInfoMetaData.DATE_OF_BIRTH);
        assertThat(dateOfBirth).isPresent().isRequired();

        LOGGER.info("Step#11 verification");
        dateOfBirth.setValue(ValueConstants.EMPTY);
        assertThat(dateOfBirth).hasWarningWithText("'Date of Birth' is required");

        LOGGER.info("Step#12 verification");
        EmployeeAssistanceProgramMasterPolicyContext.premiumSummaryTab.navigateToTab();
        Tab.buttonRate.click();
        Row errorRow1 = ErrorPage.tableError.getRowContains(TableError.MESSAGE.getName(), DATE_OF_BIRTH_ERROR_MESSAGE);
        assertThat(errorRow1).as(String.format("Row with error message: '%s'", DATE_OF_BIRTH_ERROR_MESSAGE)).isPresent();
        ErrorPage.tableError.getRowContains(TableError.CODE.getName(), "REN_EAP340915-a8Zuy_me").getCell(TableError.CODE.getName()).controls.links.getFirst().click();

        LOGGER.info("Step#13 verification");
        dateOfBirth.setValue("07/30/1978");

        LOGGER.info("Step#14 verification");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ParticipantGeneralInfoMetaData.TITLE)).hasOptions(ImmutableList.of("", "Doctor", "Mr", "Mrs", "Ms", "Estate Of"));

        LOGGER.info("Step#15 verification");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ParticipantGeneralInfoMetaData.SUFFIX)).hasOptions(ImmutableList.of("", "Jr.", "Sr.", "II", "III", "IV", "V", "PhD", "MD", "DDS"));

        LOGGER.info("Step#16 verification");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ParticipantGeneralInfoMetaData.TITLE).setValue("Doctor");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ParticipantGeneralInfoMetaData.SUFFIX).setValue("Sr.");

        LOGGER.info("Step#17, 19 verification");
        addressLinesVerification(ParticipantAddressInfoMetaData.ADDRESS_LINE_1);

        LOGGER.info("Step#20 verification");
        addressLinesVerification(ParticipantAddressInfoMetaData.ADDRESS_LINE_2);
        addressLinesVerification(ParticipantAddressInfoMetaData.ADDRESS_LINE_3);

        LOGGER.info("Step#21 verification");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.INSURED.get());
        stateProvinceVerification("United States", ImmutableList.of("AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DC", "DE", "FL",
                "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA",
                "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE",
                "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "PR", "RI",
                "SC", "SD", "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY"));
        stateProvinceVerification("Canada", ImmutableList.of("", "AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT"));
        stateProvinceVerification("Ireland", ImmutableList.of("", "Connacht", "Leinster", "Munster", "Ulster"));
        stateProvinceVerification("Colombia", ImmutableList.of("", "Capital District", "Amazonas", "Antioquia", "Arauca", "Atlántico", "Bolívar",
                "Boyacá", "Caldas", "Caquetá", "Casanare", "Cauca", "Cesar", "Chocó", "Córdoba", "Cundinamarca",
                "Guainía", "Guaviare", "Huila", "La Guajira", "Magdalena", "Meta", "Nariño", "Norte de Santander",
                "Putumayo", "Quindío", "Risaralda", "San Andrés y Providencia", "Santander",
                "Sucre", "Tolima", "Valle del Cauca", "Vaupés", "Vichada"));
        insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(AddressInformationMetaData.COUNTRY).setValue("United States");
        insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(AddressInformationMetaData.STATE_PROVINCE).setValue("AK");
        insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(ANNUAL_EARNINGS).setValue("0");

        LOGGER.info("Step#25 verification");
        EmployeeAssistanceProgramMasterPolicyContext.premiumSummaryTab.navigateToTab();
        Tab.buttonRate.click();
        assertThat(PremiumSummaryTab.tablePremiumSummary.getColumn(PolicyConstants.PolicyCoverageSummaryTable.PLAN).getCell(1)).hasValue("CONTEST");
    }

    private void addressLinesVerification(AssetDescriptor<TextBox> addressLineName) {
        String verificationText = "1234567890abcdefghijklmnopqrstuvwxy";
        coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(addressLineName).setValue(String.format("%sz", verificationText));
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(addressLineName)).hasValue(verificationText);
        coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(addressLineName).setValue(verificationText);
    }

    private void stateProvinceVerification(String country, ImmutableList<String> stateProvinceList) {
        insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(AddressInformationMetaData.COUNTRY).setValue(country);
        assertThat(insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(AddressInformationMetaData.STATE_PROVINCE)).hasOptions(stateProvinceList);
    }
}