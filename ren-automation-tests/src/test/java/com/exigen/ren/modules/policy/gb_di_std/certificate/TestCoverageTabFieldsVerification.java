package com.exigen.ren.modules.policy.gb_di_std.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.COVERAGES;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorTable.CODE;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.BENEFIT_AMOUNT;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData.ParticipantAddressInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.ADDRESS_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCoverageTabFieldsVerification extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext {

    private static final String POLICY_PLAN_NAME = "CONTEST";
    private static final String APPROVED_PERCENTAGE_ERROR_MESSAGE = "Approved % cannot be greater than Benefit Percentage";
    private static final String DATE_OF_BIRTH_ERROR_MESSAGE = "'Date of Birth' is required";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25102", component = POLICY_GROUPBENEFITS)
    public void testCoverageTabFieldsVerification() {
        LOGGER.info("REN-25102 preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", PLAN.getLabel()), "CON")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", PLAN_NAME.getLabel()), POLICY_PLAN_NAME)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", PARTICIPANT_CONTRIBUTION.getLabel()), "10")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", MEMBER_PAYMENT_MODE.getLabel()), "12").resolveLinks()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", BENEFIT_PERCENTAGE.getLabel()), "60").resolveLinks());
        shortTermDisabilityCertificatePolicy.initiate(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        shortTermDisabilityCertificatePolicy.getDefaultWorkspace().fillUpTo((shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)), CoveragesTab.class, false);

        LOGGER.info("Step#1 verification");
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN)).hasOptions(ImmutableList.of("", POLICY_PLAN_NAME));

        LOGGER.info("Step#2 verification");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValue(POLICY_PLAN_NAME);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Current Effective %"))).isAbsent();

        LOGGER.info("Step#4 verification");
        coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_YES);
        assertSoftly(softly -> {
            ImmutableList.of(APPROVED_PERCENT, REQUESTED_PERCENT, PENDING_PERCENT).forEach(control -> {
                coveragesTab.getAssetList().getAsset(control).setValue("0");
                softly.assertThat(coveragesTab.getAssetList().getAsset(control)).hasNoWarning();
            });
        });
        coveragesTab.getAssetList().getAsset(ENROLLMENT_DATE).setValue(DateTimeUtils.MM_DD_YYYY.format(currentDate));
        coveragesTab.getAssetList().getAsset(EOI_STATUS).setValueByIndex(1);

        LOGGER.info("Step#5 verification");
        coveragesTab.getAssetList().getAsset(APPROVED_PERCENT).setValue("65");
        assertThat(coveragesTab.getAssetList().getAsset(APPROVED_PERCENT)).hasWarningWithText("Approved % cannot be greater than Benefit Percentage");

        LOGGER.info("Step#6 verification");
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());
        Tab.buttonRate.click();
        Row errorRow = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), APPROVED_PERCENTAGE_ERROR_MESSAGE);
        assertThat(errorRow).as(String.format("Row with error message: '%s'", APPROVED_PERCENTAGE_ERROR_MESSAGE)).isPresent();
        errorRow.getCell(CODE).controls.links.getFirst().click();

        LOGGER.info("Step#7,8 verification");
        benefitPercentageErrorMessageVerification("60");

        LOGGER.info("Step#9 verification");
        NavigationPage.PolicyNavigation.leftMenu(COVERAGES.get());
        benefitPercentageErrorMessageVerification("55");

        LOGGER.info("Step#10 verification");
        NavigationPage.PolicyNavigation.leftMenu(COVERAGES.get());
        coveragesTab.getAssetList().getAsset(ADD_PARTICIPANT).click();
        coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION).setValueByIndex(0);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).isPresent().isRequired();

        LOGGER.info("Step#11 verification");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(StringUtils.EMPTY);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasWarningWithText("'Date of Birth' is required");

        LOGGER.info("Step#12 verification");
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());
        Tab.buttonRate.click();
        Row errorRow1 = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), DATE_OF_BIRTH_ERROR_MESSAGE);
        assertThat(errorRow1).as(String.format("Row with error message: '%s'", DATE_OF_BIRTH_ERROR_MESSAGE)).isPresent();
        ErrorPage.tableError.getRowContains(ErrorPage.TableError.CODE.getName(), "GB_DI_STD340915-a8Zuy_me").getCell(CODE).controls.links.getFirst().click();

        LOGGER.info("Step#13 verification");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue("07/30/1978");

        LOGGER.info("Step#14 verification");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(TITLE)).hasOptions((ImmutableList.of("", "Doctor", "Mr", "Mrs", "Ms", "Estate Of")));

        LOGGER.info("Step#15 verification");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(SUFFIX)).hasOptions(ImmutableList.of("", "Jr.", "Sr.", "II", "III", "IV", "V", "PhD", "MD", "DDS"));

        LOGGER.info("Step#16 verification");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(TITLE).setValue("Doctor");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(SUFFIX).setValue("Sr.");

        LOGGER.info("Step#17, 19 verification");
        addressLinesVerification(ADDRESS_LINE_1);

        LOGGER.info("Step#20 verification");
        addressLinesVerification(ADDRESS_LINE_2);
        addressLinesVerification(ADDRESS_LINE_3);

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
        insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(COUNTRY).setValue("United States");
        insuredTab.getAssetList().getAsset(InsuredTabMetaData.STATE_PROVINCE).setValue("AK");

        LOGGER.info("Step#23 verification");
        insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(ANNUAL_EARNINGS).setValue("0");
        NavigationPage.PolicyNavigation.leftMenu(COVERAGES.get());
        assertThat(coveragesTab.getAssetList().getAsset(WEEKLY_BENEFIT_MOUNT)).hasValue(new Currency().toString());

        LOGGER.info("Step#24 verification");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.INSURED.get());
        insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(ANNUAL_EARNINGS).setValue("42119");
        NavigationPage.PolicyNavigation.leftMenu(COVERAGES.get());
        assertThat(coveragesTab.getAssetList().getAsset(WEEKLY_BENEFIT_MOUNT)).hasValue(new Currency("446").toString());

        LOGGER.info("Step#25 verification");
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());
        Tab.buttonRate.click();
        assertSoftly(softly -> {
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getColumn(PolicyConstants.PolicyCoverageSummaryTable.PLAN).getCell(1)).hasValue("CONTEST");
            softly.assertThat(PremiumSummaryTab.tablePremiumSummary.getColumn(BENEFIT_AMOUNT).getCell(1)).hasValue(new Currency("446").toString());
        });
    }

    private void benefitPercentageErrorMessageVerification(String approvedPercentValue) {
        coveragesTab.getAssetList().getAsset(APPROVED_PERCENT).setValue(approvedPercentValue);
        assertThat(coveragesTab.getAssetList().getAsset(APPROVED_PERCENT)).hasNoWarning();
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());
        Tab.buttonRate.click();
        Row errorRow = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), APPROVED_PERCENTAGE_ERROR_MESSAGE);
        assertThat(errorRow).as(String.format("Row with error message: '%s'", APPROVED_PERCENTAGE_ERROR_MESSAGE)).isAbsent();
        Tab.buttonCancel.click();
    }

    private void addressLinesVerification(AssetDescriptor<TextBox> addressLineName) {
        String verificationText = "1234567890abcdefghijklmnopqrstuvwxy";
        coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(addressLineName).setValue(verificationText + "z");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(addressLineName)).hasValue(verificationText);
        coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(addressLineName).setValue(verificationText);
    }

    private void stateProvinceVerification(String country, ImmutableList<String> stateProvinceList) {
        insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(InsuredTabMetaData.AddressInformationMetaData.COUNTRY).setValue(country);
        assertThat(insuredTab.getAssetList().getAsset(InsuredTabMetaData.STATE_PROVINCE)).hasOptions(stateProvinceList);
    }
}