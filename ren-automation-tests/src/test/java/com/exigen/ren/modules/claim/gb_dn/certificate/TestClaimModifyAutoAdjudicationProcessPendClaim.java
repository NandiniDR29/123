package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.ACTIONS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.OVERRIDE_LINE_RULES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.OverrideLineRulesSection.ALLOW_SERVICE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.REASON;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonAddService;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.getClaimDBStatus;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimModifyAutoAdjudicationProcessPendClaim extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13977", component = CLAIMS_GROUPBENEFITS)
    public void testClaimModifyAutoAdjudicationProcess() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("---=={Step 1}==---");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);

        LOGGER.info("---=={Step 3}==---");
        dentalClaim.claimSubmit().perform();

        LOGGER.info("---=={Step 4}==---");
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        Tab lineOverrideTab =  new LineOverrideTab();
        lineOverrideTab.getAssetList().getAsset(OVERRIDE_LINE_RULES).getAsset(ALLOW_SERVICE).setValue(true);
        lineOverrideTab.getAssetList().getAsset(REASON).setValue("test reason");
        Tab.buttonSaveAndExit.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("---=={Step 5}==---");
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.PAID);

        LOGGER.info("---=={Step 6}==---");
        dentalClaim.stopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_StopPayment"), 1);
        dentalClaim.confirmStopPayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_ConfirmStopPayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);
        assertThat(getClaimDBStatus(claimNumber)).isEqualTo(ClaimConstants.ClaimDBStatus.INITIAL);

        LOGGER.info("---=={Step 7}==---");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        dentalClaim.pendClaim().perform("Basic Review");
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_BASIC_REVIEW);

        LOGGER.info("---=={Step 8}==---");
        NavigationPage.toSubTab(EDIT_CLAIM);
        buttonAddService.click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setRawValue(REVIEW_REQUIRED_1);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("3");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        buttonSubmitClaim.click();

        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(PENDED);
        assertThat(getClaimDBStatus(claimNumber)).isEqualTo(ClaimConstants.ClaimDBStatus.NOTIFICATION);
    }
}
