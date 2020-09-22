package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CommonLocators;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.PatientHistoryRecordTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PatientHistoryRecordActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.Tab.cancelClickAndCloseDialog;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PATIENT_HISTORY;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.PENDED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.ACTIONS;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.EHB;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.TYPE_OF_TRANSACTION;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.PatientHistoryRecordActionTab.buttonAddPatientHistoryRecord;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestIntegrationDentalInformationRequestLetter extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35386", component = CLAIMS_GROUPBENEFITS)
    public void testDentalInformationRequestLetterHiddenAttributes() {
        final String essentialHealthBenefit = "Essential Health Benefit";
        LOGGER.info("REN-35386 PRECONDITION");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();
        String certPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("REN-35386 STEP#1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), DOS.getLabel()), DateTimeUtils.getCurrentDateTime().format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3"));
        buttonSubmitClaim.click();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        assertSoftly(softly -> {
            LOGGER.info("REN-35386 STEP#2");
            softly.assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(PENDED);

            LOGGER.info("REN-35386 STEP#3");
            softly.assertThat(ClaimSummaryPage.tableResultsOfAdjudication.getHeader().getValue()).doesNotContain(EHB.getName());

            LOGGER.info("REN-35386 STEP#4");
            ClaimSummaryPage.tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
            softly.assertThat(new StaticElement(CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR.format(essentialHealthBenefit))).isAbsent();

            LOGGER.info("REN-35386 STEP#6");
            cancelClickAndCloseDialog();
            NavigationPage.toSubTab(PATIENT_HISTORY);
            softly.assertThat(ClaimSummaryPage.tablePatientHistory.getHeader().getValue()).doesNotContain(EHB.getName());

            LOGGER.info("REN-35386 STEP#7");
            dentalClaim.addPatientHistoryRecord().start();
            softly.assertThat(new StaticElement(CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR.format(essentialHealthBenefit))).isAbsent();

            LOGGER.info("REN-35386 STEP#8");
            dentalClaim.addPatientHistoryRecord().getWorkspace().fill(dentalClaim.getDefaultTestData("ClaimPatientHistory", DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(PatientHistoryRecordActionTab.class.getSimpleName(), PatientHistoryRecordTabMetaData.POLICY.getLabel()), certPolicyNumber)
                    .adjust(TestData.makeKeyPath(PatientHistoryRecordActionTab.class.getSimpleName(), PatientHistoryRecordTabMetaData.CLAIM.getLabel()), claimNumber));
            buttonAddPatientHistoryRecord.click();
            ClaimSummaryPage.tablePatientHistory.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.UPDATE).click();
            softly.assertThat(new StaticElement(CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR.format(essentialHealthBenefit))).isAbsent();
        });
    }
}

