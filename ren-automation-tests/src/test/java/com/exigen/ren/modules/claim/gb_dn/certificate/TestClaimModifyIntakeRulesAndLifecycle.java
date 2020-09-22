package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.common.pages.NavigationPage.isSubTabSelected;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimModifyIntakeRulesAndLifecycle extends ClaimGroupBenefitsDNBaseTest {

    private static final String ERROR_MESSAGE = "'Ortho Months' is required";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22981", component = CLAIMS_GROUPBENEFITS)
    public void testClaimModifyIntakeRulesAndLifecycleSteps1_12() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST: Step #1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithPayment"));
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(),
                        CDT_CODE.getLabel()), "D8010")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(),
                        CHARGE.getLabel()), "100"));
        buttonSubmitClaim.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("TEST: Step #2");
        intakeInformationTab.fillTab(tdSpecific().getTestData("Adjust_UpdateIntakeInformationTab"));
        buttonSubmitClaim.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("TEST: Step #3");
        tableSubmittedServices.getRow(CDT_CODE.getLabel(), "D8010").getCell(9).controls.links.get(ActionConstants.CHANGE).click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setRawValue("D8005");
        buttonSubmitClaim.click();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("TEST: Step #4");
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        NavigationPage.toSubTab(EDIT_CLAIM);
        tableSubmittedServices.getRow(CDT_CODE.getLabel(), "D8005").getCell(9).controls.links.get(ActionConstants.CHANGE).click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setRawValue("D8010");
        buttonSubmitClaim.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("TEST: Step #5");
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("5");
        buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED_ORTHO);

        LOGGER.info("TEST: Step #6");
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue(StringUtils.EMPTY);
        buttonSaveAndExit.click();

        assertThat(isSubTabSelected(ADJUDICATION)).withFailMessage("Tab Adjudication is not selected!!!").isTrue();

        LOGGER.info("TEST: Step #7");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("5");
        buttonAddService.click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue(REVIEW_REQUIRED_1);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("3");
        buttonSubmitClaim.click();

        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);

        LOGGER.info("TEST: Step #8");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue(StringUtils.EMPTY);
        buttonSaveAndExit.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("TEST: Step #9");
        tableSubmittedServices.getRow(CDT_CODE.getLabel(), REVIEW_REQUIRED_1).getCell(9).controls.links.get(ActionConstants.CHANGE).click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setRawValue("D0160");
        buttonSaveAndExit.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("TEST: Step #10");
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("4");
        buttonSaveAndExit.click();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED_ORTHO);

        LOGGER.info("TEST: Step #12");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED_ORTHO);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22981", component = CLAIMS_GROUPBENEFITS)
    public void testClaimModifyIntakeRulesAndLifecycleSteps13_16() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST: Step #13");
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), "D8670"));
        dentalClaim.claimSubmit().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("TEST: Step #14");
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        NavigationPage.toSubTab(EDIT_CLAIM);

        LOGGER.info("TEST: Step #15");
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("1");
        buttonSubmitClaim.click();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("TEST: Step #16");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 2);

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.PAID);
    }
}
