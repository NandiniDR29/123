package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.SubmittedServicesColumns;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.LINE_ID;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.PLAN_TO_PAY;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.changeService;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.tableSubmittedServices;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimProcessOrthoPlanToPay extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23537", component = CLAIMS_GROUPBENEFITS)
    public void testClaimProcessOrthoPlanToPay() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        String policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(3).format(MM_DD_YYYY);
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), policyEffectiveDate));
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), policyEffectiveDate));


        LOGGER.info("TEST: Step #1");
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(intakeInformationTab.getMetaKey(), tdSpecific().getTestData("TestData_FourServices", intakeInformationTab.getMetaKey())));
        dentalClaim.claimSubmit().perform();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);
            checkTableResultsOfAdjudication(softly, 1, 3);
            checkTableResultsOfAdjudication(softly, 2);
            checkTableResultsOfAdjudication(softly, 3);
            checkTableResultsOfAdjudication(softly, 4, 3);
        });

        LOGGER.info("TEST: Step #2");
        String dosValueForFirstFeature = ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(SubmittedServicesColumns.DOS.getName()).getValue();
        String dosValueForSecondFeature = ClaimSummaryPage.tableSubmittedServices.getRow(2).getCell(SubmittedServicesColumns.DOS.getName()).getValue();
        String dosValueForThirdFeature = ClaimSummaryPage.tableSubmittedServices.getRow(3).getCell(SubmittedServicesColumns.DOS.getName()).getValue();
        String dosValueForFourthFeature = ClaimSummaryPage.tableSubmittedServices.getRow(4).getCell(SubmittedServicesColumns.DOS.getName()).getValue();

        assertSoftly(softly -> {
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFirstFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFourthFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFirstFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFourthFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFirstFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFourthFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForSecondFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForThirdFeature);
        });


        LOGGER.info("TEST: Step #3");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("5");
        buttonSaveAndExit.click();
        assertSoftly(softly -> {
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFirstFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFourthFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFirstFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFourthFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFirstFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFourthFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M4").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFirstFeature, MM_DD_YYYY).plusMonths(3).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M4").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFourthFeature, MM_DD_YYYY).plusMonths(3).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1-M5").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFirstFeature, MM_DD_YYYY).plusMonths(4).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4-M5").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForFourthFeature, MM_DD_YYYY).plusMonths(4).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForSecondFeature);
//            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForThirdFeature);
        });

        LOGGER.info("TEST: Step #4");
        NavigationPage.toSubTab(EDIT_CLAIM);
        changeService("D8010", new Currency(0), dosValueForFirstFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D8210");

        changeService(REVIEW_REQUIRED_1, new Currency(0), dosValueForSecondFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D8020");

        changeService("D8047", new Currency(0), dosValueForThirdFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D8010");

        changeService("D8010", new Currency(0), dosValueForFourthFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D3351");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("3");
        buttonSaveAndExit.click();

        assertSoftly(softly -> {
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForSecondFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForThirdFeature);

            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M4").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(3).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M4").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(3).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M5").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(4).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M5").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(4).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFirstFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFourthFeature);
        });

        LOGGER.info("TEST: Step #5");
        NavigationPage.toSubTab(EDIT_CLAIM);
        changeService("D8210", new Currency(0), dosValueForFirstFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(dosValueForFourthFeature);

        changeService("D8020", new Currency(0), dosValueForSecondFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(dosValueForThirdFeature);

        changeService("D8010", new Currency(0), dosValueForThirdFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(dosValueForSecondFeature);

        changeService("D3351", new Currency(0), dosValueForFourthFeature);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(dosValueForFirstFeature);
        buttonSaveAndExit.click();
        assertSoftly(softly -> {
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForThirdFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForSecondFeature);

            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M2").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(1).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M3").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(2).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M4").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(3).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M4").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(3).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S2-M5").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForThirdFeature, MM_DD_YYYY).plusMonths(4).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S3-M5").getCell(PLAN_TO_PAY.getName()))
                    .hasValue(LocalDate.parse(dosValueForSecondFeature, MM_DD_YYYY).plusMonths(4).withDayOfMonth(25).format(MM_DD_YYYY));
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S1").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFourthFeature);
            softly.assertThat(tableResultsOfAdjudication.getRow(LINE_ID.getName(), "S4").getCell(PLAN_TO_PAY.getName())).hasValue(dosValueForFirstFeature);
        });

        LOGGER.info("TEST: Step #6");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(TYPE_OF_TRANSACTION).setValue("Predetermination");
        tableSubmittedServices.getRows().forEach(row -> {
            row.getCell(9).controls.links.get(ActionConstants.CHANGE).click();
            intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(ValueConstants.EMPTY);
        });
        buttonSaveAndExit.click();
        assertSoftly(softly ->
                tableResultsOfAdjudication.getRows().forEach(row ->
                        softly.assertThat(row.getCell(PLAN_TO_PAY.getName())).hasValue(ValueConstants.EMPTY)));
    }

    private void checkTableResultsOfAdjudication(ETCSCoreSoftAssertions softly, int serviceNumber) {
        softly.assertThat(tableResultsOfAdjudication)
                .with(LINE_ID, String.format("S%s", serviceNumber))
                .hasMatchingRows(1);
    }

    private void checkTableResultsOfAdjudication(ETCSCoreSoftAssertions softly, int serviceNumber, int orthoMonths) {
        IntStream.range(1, orthoMonths + 1).forEach(index -> {
            softly.assertThat(tableResultsOfAdjudication)
                    .with(LINE_ID,
                            String.format("S%s-M%s", serviceNumber, index))
                    .hasMatchingRows(1);
        });
    }
}
