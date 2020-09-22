package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProductAndPlanManagementTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.dental.claimImage.ClaimImageModel;
import com.exigen.ren.rest.claim.model.dental.claimImage.DamageModel;
import com.exigen.ren.rest.claim.model.dental.claiminfo.ClaimInfoModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MMDDYYYY;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.PENDED_TEAM_LEAD;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.ACTIONS;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.COVERED_CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.OVERRIDE_LINE_VALUES;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonAddService;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimFeeMaxInformationRetrievalCoveredCDTCode extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46325", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFeeMaxInformationRetrievalCoveredCdtCode() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        LocalDateTime localDateMinusThreeMonth = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(3);
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()),
                        localDateMinusThreeMonth.format(MMDDYYYY))
                .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(), ProductAndPlanManagementTabMetaData.PRODUCT.getLabel()),
                        groupDentalMasterPolicy.getType().getName()));
        groupDentalMasterPolicy.createPolicy(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_PlanTripleAdvantage")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), localDateMinusThreeMonth.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", CO_INSURANCE.getLabel()), new SimpleDataProvider().adjust(PlanDefinitionTabMetaData.CoInsuranceMetaData.UC_PERCENTILE_LEVEL.getLabel(), "PPO Schedule"))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ORTHODONTIA.getLabel()), new SimpleDataProvider().adjust(OrthodontiaMetaData.ORTHO_COVERAGE.getLabel(), ValueConstants.VALUE_YES))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, "TestData_TripleAdvantage"))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), localDateMinusThreeMonth.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), SITUS_STATE.getLabel()), "NV")
                .adjust(TestData.makeKeyPath(GroupDentalCertificatePolicyContext.coveragesTab.getMetaKey()), tdSpecific().getTestData("TestData_SpouseHusband", "PlansTab").resolveLinks()));

        LOGGER.info("Step 1-2");
        dentalClaim.create(tdSpecific().getTestData("TestData_Claim_WithThreeServices"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(PENDED_TEAM_LEAD);
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Step 3, 5");
        checkClaimInfo(claimNumber);
        checkClaimImage(claimNumber);

        LOGGER.info("Step 6");
        toSubTab(EDIT_CLAIM);
        IntakeInformationTab.changeService("D3351");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("1000");

        IntakeInformationTab.changeService("D2010");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D5760");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("860");

        IntakeInformationTab.changeService("D5731");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D0171");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("875");
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("Step 7");
        checkClaimInfo(claimNumber);
        checkClaimImage(claimNumber);

        LOGGER.info("Step 8");
        toSubTab(EDIT_CLAIM);
        IntakeInformationTab.removeService("D5760", new Currency(860));
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("1000");

        buttonAddService.click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D1110");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("999.99");
        buttonSaveAndExit.click();

        LOGGER.info("Step 9");
        checkClaimInfo(claimNumber);
        checkClaimImage(claimNumber);

        LOGGER.info("Step 10");
        tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), "D0171").getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        AbstractContainer<?, ?> lineOverrideTabAssetList = new LineOverrideTab().getAssetList();
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(LineOverrideTabMetaData.OverrideLineValuesSection.COVERED_CDT_CODE).setValue("D2751");
        lineOverrideTabAssetList.getAsset(LineOverrideTabMetaData.REASON).setValue("test reason");
        buttonSaveAndExit.click();

        LOGGER.info("Step 11");
        checkClaimInfo(claimNumber);
        checkClaimImage(claimNumber);

        LOGGER.info("Step 12");
        tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), "D1110").getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(LineOverrideTabMetaData.OverrideLineValuesSection.COVERED_CDT_CODE).setValue("D1206");
        lineOverrideTabAssetList.getAsset(LineOverrideTabMetaData.REASON).setValue("test reason 2");
        buttonSaveAndExit.click();

        LOGGER.info("Step 13");
        checkClaimInfo(claimNumber);
        checkClaimImage(claimNumber);

        LOGGER.info("Step 14");
        toSubTab(EDIT_CLAIM);
        IntakeInformationTab.removeProvider();
        intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SEARCH_PROVIDER).fill(tdSpecific().getTestData("TestData_Provider2"));
        buttonSaveAndExit.click();

        LOGGER.info("Step 15");
        ResponseContainer<ClaimInfoModel> claimInfoModel = dentalClaimRest.getClaimInfo(claimNumber);
        assertThat(claimInfoModel.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> claimInfoModel.getModel().getSubmittedProcedures().forEach(proc -> {
            softly.assertThat(proc.getCalculationResult().getFeeMax()).isNull();
            softly.assertThat(proc.getCalculationResult().getDentistAdjustment()).isNull();
            softly.assertThat(proc.getDentist().getState()).isEqualTo("NV");
        }));

        checkClaimImage(claimNumber);

        LOGGER.info("Step 16");
        toSubTab(EDIT_CLAIM);
        IntakeInformationTab.changeService("D0171");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D8010");
        IntakeInformationTab.buttonSaveAndExit.click();

        ResponseContainer<ClaimInfoModel> claimInfoModel_2 = dentalClaimRest.getClaimInfo(claimNumber);
        assertThat(claimInfoModel_2.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> claimInfoModel_2.getModel().getSubmittedProcedures().forEach(proc -> {
            softly.assertThat(proc.getCalculationResult().getFeeMax()).isNull();
            softly.assertThat(proc.getCalculationResult().getDentistAdjustment()).isNull();
            softly.assertThat(proc.getDentist().getState()).isEqualTo("NV");
        }));

        checkClaimImage(claimNumber);
    }

    private void checkClaimImage(String claimNumber) {
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(claimNumber);

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> responseFromLog_4 = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        String fee1 = responseFromLog_4.get("calcResults[0].feeMax");
        String fee2 = responseFromLog_4.get("calcResults[1].feeMax");
        String fee3 = responseFromLog_4.get("calcResults[2].feeMax");
        String dentistAdjustment1 = responseFromLog_4.get("calcResults[0].dentistAdjustment");
        String dentistAdjustment2 = responseFromLog_4.get("calcResults[1].dentistAdjustment");
        String dentistAdjustment3 = responseFromLog_4.get("calcResults[2].dentistAdjustment");

        ResponseContainer<ClaimImageModel> claimImageModel = dentalClaimRest.getClaimImage(claimNumber);
        assertThat(claimImageModel.getResponse().getStatus()).isEqualTo(200);
        assertThat(claimImageModel.getModel().getDamages()).hasSize(3);

        DamageModel damageWithFee1 = claimImageModel.getModel().getDamages().stream().filter(damage ->
                damage.getFeatures().get(0).getExtension().getMaximumApprovedFee().equals(new Currency(fee1).toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Extension with fee=%s isn't found", fee1)));
        DamageModel damageWithFee2 = claimImageModel.getModel().getDamages().stream().filter(damage ->
                damage.getFeatures().get(0).getExtension().getMaximumApprovedFee().equals(new Currency(fee2).toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Extension with fee=%s isn't found", fee2)));
        DamageModel damageWithFee3 = claimImageModel.getModel().getDamages().stream().filter(damage ->
                damage.getFeatures().get(0).getExtension().getMaximumApprovedFee().equals(new Currency(fee3).toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Extension with fee=%s isn't found", fee3)));
        assertSoftly(softly -> {
            softly.assertThat(damageWithFee1.getFeatures().get(0).getExtension().getDentistAdjustment())
                    .isEqualTo(new Currency(dentistAdjustment1).toPlainString());
            softly.assertThat(damageWithFee2.getFeatures().get(0).getExtension().getDentistAdjustment())
                    .isEqualTo(new Currency(dentistAdjustment2).toPlainString());
            softly.assertThat(damageWithFee3.getFeatures().get(0).getExtension().getDentistAdjustment())
                    .isEqualTo(new Currency(dentistAdjustment3).toPlainString());
        });
    }

    private void checkClaimInfo(String claimNumber) {
        ResponseContainer<ClaimInfoModel> claimInfoModel = dentalClaimRest.getClaimInfo(claimNumber);
        assertThat(claimInfoModel.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> claimInfoModel.getModel().getSubmittedProcedures().forEach(proc -> {
            softly.assertThat(proc.getCalculationResult().getFeeMax()).isNull();
            softly.assertThat(proc.getCalculationResult().getDentistAdjustment()).isNull();
            softly.assertThat(proc.getDentist().getState()).isEqualTo("FL");
        }));
    }
}
