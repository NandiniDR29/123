package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.common.claimbody.ClaimBodyModel;
import com.exigen.ren.rest.claim.model.common.claimbody.claim.ClaimCoordinationOfBenefitsModel;
import com.exigen.ren.rest.claim.model.common.claimbody.claim.ClaimDataModel;
import com.exigen.ren.rest.claim.model.dental.processediclaim.ProcessEdiClaimResponseModel;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.ActionConstants.LINE_VIEW;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.ACTIONS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.CoordinationOfBenefitsSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.TOOTH;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.OVERRIDE_LINE_VALUES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.OverrideLineValuesSection.COB_APPLIED;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.REASON;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableClaimData;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;

public class TestClaimCoordinationOfBenefits extends ClaimGroupBenefitsDNBaseTest implements CustomerContext, ClaimRestContext {


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15685", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCoordinationOfBenefits() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_TwoServices")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[0]", CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[0]", TOOTH.getLabel()), "3")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[1]", CDT_CODE.getLabel()), "D1110"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Steps 1,2");
        assertThat(tableClaimData).hasMatchingRows(TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName(), "");
        assertRestResponse(claimNumber, null, null, null, 0.0, null);

        LOGGER.info("Step 3");
        NavigationPage.toSubTab(EDIT_CLAIM);
        AbstractContainer<?, ?> intakeInformationTabAssetList = intakeInformationTab.getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTabAssetList.getAsset(OTHER_COVERAGE)).isEnabled().isOptional().hasValue("");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Coordination of Benefits"))).isAbsent();
        });

        LOGGER.info("Step 4");
        intakeInformationTabAssetList.getAsset(OTHER_COVERAGE).setValue("Yes");
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS)).isPresent();
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_NAME)).isEnabled().isOptional().hasValue("");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_DOB)).isEnabled().isOptional().hasValue("");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_NAME)).isEnabled().isOptional().hasValue("");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT)).isEnabled().isOptional().hasValue("$0.00");
        });

        LOGGER.info("Step 9");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT).setValue("abc");
        assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT)).hasWarningWithText("abc: not a valid monetary value");

        LOGGER.info("Step 11");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT).setValue("-50");
        assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT)).hasWarningWithText("Other Carrier Amount cannot be negative");

        LOGGER.info("Step 12");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT).setValue("$100,0001.00");
        assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT)).hasWarningWithText("Other Carrier Amount must not exceed $100,000.00");

        LOGGER.info("Steps 14,15");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT).setValue("1.1251");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_NAME).setValue("Other Insured Name");
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_DOB).setValue(todayDate);
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_NAME).setValue("Other Carrier Name");
        Tab.buttonSaveAndExit.click();
        assertThat(tableClaimData).hasMatchingRows(TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName(), "Yes");
        String todayDateTime = LocalDate.parse(todayDate, MM_DD_YYYY).atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        assertRestResponse(claimNumber, "Other Insured Name", "Other Carrier Name", todayDateTime, 1.13, "true");

        LOGGER.info("Step 17");
        NavigationPage.toSubTab(EDIT_CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Claim Data"))).isPresent();
            softly.assertThat(intakeInformationTabAssetList.getAsset(OTHER_COVERAGE)).isEnabled().isOptional().hasValue("Yes");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS)).isPresent();
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_NAME)).isEnabled().isOptional().hasValue("Other Insured Name");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_DOB)).isEnabled().isOptional().hasValue(todayDate);
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_NAME)).isEnabled().isOptional().hasValue("Other Carrier Name");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT)).isEnabled().isOptional().hasValue("$1.13");
        });

        LOGGER.info("Step 18");
        intakeInformationTabAssetList.getAsset(OTHER_COVERAGE).setValue("No");
        assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS)).isAbsent();

        LOGGER.info("Step 19");
        Tab.buttonSaveAndExit.click();
        assertThat(tableClaimData).hasMatchingRows(TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName(), "No");
        assertRestResponse(claimNumber, null, null, null, 0.0, "false");

        LOGGER.info("Step 21");
        NavigationPage.toSubTab(EDIT_CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTabAssetList.getAsset(OTHER_COVERAGE)).isEnabled().isOptional().hasValue("No");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS)).isAbsent();
        });

        LOGGER.info("Step 22");
        intakeInformationTabAssetList.getAsset(OTHER_COVERAGE).setValue("Yes");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_NAME).setValue("Other Insured Name");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_DOB).setValue(todayDate);
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_NAME).setValue("Other Carrier Name");
        intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT).setValue("$100.00");
        intakeInformationTabAssetList.getAsset(OTHER_COVERAGE).setValue("No");
        intakeInformationTabAssetList.getAsset(OTHER_COVERAGE).setValue("Yes");
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS)).isPresent();
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_NAME)).isEnabled().isOptional().hasValue("");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_INSURED_DOB)).isEnabled().isOptional().hasValue("");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_NAME)).isEnabled().isOptional().hasValue("");
            softly.assertThat(intakeInformationTabAssetList.getAsset(COORDINATION_OF_BENEFITS).getAsset(OTHER_CARRIER_AMOUNT)).isEnabled().isOptional().hasValue("$0.00");
        });

        LOGGER.info("Step 23");
        buttonSubmitClaim.click();
        assertRestResponse(claimNumber, "", "", null, 0.0, "true");
        assertSoftly(softly -> {
            softly.assertThat(tableClaimData).hasMatchingRows(TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName(), "Yes");
            softly.assertThat(tableResultsOfAdjudication.getColumn(TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.COB_APPLIED)).isPresent();
        });

        LOGGER.info("Step 24");
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        AbstractContainer<?, ?> lineOverrideTabAssetList = new LineOverrideTab().getAssetList();
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED)).isEnabled().isOptional().hasValue("$0.00");

        LOGGER.info("Step 25");
        lineOverrideTabAssetList.getAsset(REASON).setValue("Reason");
        Tab.buttonSaveAndExit.click();
        assertRestResponseCobAmount(claimNumber, "0.00");

        LOGGER.info("Step 26");
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED)).isEnabled().isOptional().hasValue("$0.00");

        LOGGER.info("Step 27");
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED).setValue("-100");
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED)).hasWarningWithText("COB Applied cannot be negative");

        LOGGER.info("Step 28");
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED).setValue("200,000.00");
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED)).hasWarningWithText("COB Applied must not exceed $100,000.00");

        LOGGER.info("Step 29");
        Tab.buttonSaveAndExit.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), "COB Applied must not exceed $100,000.00")).exists();

        LOGGER.info("Step 30");
        Tab.buttonCancel.click();
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED).setValue("1.1251");
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COB_APPLIED)).hasValue("$1.13");

        LOGGER.info("Step 31");
        lineOverrideTabAssetList.getAsset(REASON).setValue("Reason");
        Tab.buttonSaveAndExit.click();
        assertRestResponseCobAmount(claimNumber, "1.13");

        LOGGER.info("Step 32");
        assertSoftly(softly -> {
            softly.assertThat(tableClaimData).hasMatchingRows(TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName(), "Yes");
            softly.assertThat(tableResultsOfAdjudication.getColumn(TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.COB_APPLIED)).isPresent();
        });

        LOGGER.info("Step 33");
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(LINE_VIEW).click();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(By.xpath("//tr[@id='policyDataGatherForm:formGrid_DentalEvaluationFeature-11' and descendant::label[text()='COB Applied']]"))).hasValue("COB Applied");
            softly.assertThat(new StaticElement(By.xpath("//tr[@id='policyDataGatherForm:formGrid_ClaimsDentalValuesOverride-7' and descendant::label[text()='COB Applied']]"))).hasValue("COB Applied $1.13");
        });

        LOGGER.info("Step 37");
        TestData testDataREST = tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath("damages[0]", "loss", "extension", "procedureDate"), todayDateTime);
        assertSoftly(softly -> {
            ProcessEdiClaimResponseModel response1 = createOrUpdateClaimTodayDateREST(testDataREST, policyNumber, customerNumber);
            softly.assertThat(response1.getErrorMessages()).isEmpty();
            softly.assertThat(response1.getClaimNumber()).isNotEmpty();

            ProcessEdiClaimResponseModel response2 = createOrUpdateClaimTodayDateREST(testDataREST.adjust(TestData.makeKeyPath(
                    "claim", "extension", "claimData", "coordinationOfBenefits", "otherCarrierAmount"), "-50"), policyNumber, customerNumber);
            softly.assertThat(response2.getErrorMessages().get(0)).isEqualTo("Other Carrier Amount cannot be negative");
            softly.assertThat(response2.getClaimNumber()).isNotEmpty();

            ProcessEdiClaimResponseModel response3 = createOrUpdateClaimTodayDateREST(testDataREST.adjust(TestData.makeKeyPath(
                    "claim", "extension", "claimData", "coordinationOfBenefits", "otherCarrierAmount"), "200000"), policyNumber, customerNumber);
            softly.assertThat(response3.getErrorMessages().get(0)).isEqualTo("Other Carrier Amount must not exceed $100,000.00");
            softly.assertThat(response3.getClaimNumber()).isNotEmpty();
        });
    }

    private void assertRestResponse(String claimNumber, String otherInsuredName, String otherCarrierName, String otherInsuredDOB, double otherCarrierAmount, String otherCoverage) {
        ClaimBodyModel claimModel = claimCoreRestService.getClaimImage(claimNumber).getModel();
        ClaimDataModel claimData = claimModel.getClaim().getExtension().getClaimData();
        ClaimCoordinationOfBenefitsModel claimCoordinationOfBenefitsModel = claimData.getCoordinationOfBenefits();
        assertSoftly(softly -> {
            softly.assertThat(claimCoordinationOfBenefitsModel.getOtherInsuredName()).isEqualTo(otherInsuredName);
            softly.assertThat(claimCoordinationOfBenefitsModel.getOtherCarrierName()).isEqualTo(otherCarrierName);
            softly.assertThat(claimCoordinationOfBenefitsModel.getOtherInsuredDOB()).isEqualTo(otherInsuredDOB);
            softly.assertThat(claimCoordinationOfBenefitsModel.getOtherCarrierAmount()).isEqualTo(otherCarrierAmount);
            softly.assertThat(claimData.getOtherCoverageInd()).isEqualTo(otherCoverage);
        });
    }

    private void assertRestResponseCobAmount(String claimNumber, String COBApplied) {
        String cobAmount = claimCoreRestService.getClaimImage(claimNumber).getModel().getDamages().get(0).getFeatures().get(0).getExtension().getValuesOverride().getCobAmount();
        assertThat(cobAmount).isEqualTo(COBApplied);
    }

}
