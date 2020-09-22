package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.DENIED;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimAllSingleBenefitCalculationsTable.EMPTY;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.LOGGED_INTAKE;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.PAID;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.DECLINED;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.DOS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.getClaimDBStatus;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimModifyAutoAdjudicationProcess extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13975", component = CLAIMS_GROUPBENEFITS)
    public void testClaimModifyAutoAdjudicationProcess() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("---=={Step 1,2}==---");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), DENIED)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), DOS.getLabel()), EMPTY));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.PREDETERMINED);
        assertThat(getClaimDBStatus(claimNumber)).isEqualTo(ClaimConstants.ClaimDBStatus.CLOSED);

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasNoRows();

        LOGGER.info("---=={Step 5}==---");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithoutPayment")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), DENIED));
        String claimNumber2 = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        assertThat(getClaimDBStatus(claimNumber2)).isEqualTo(ClaimConstants.ClaimDBStatus.OPEN);

        LOGGER.info("---=={Step 6-10}==---");
        issueClaimSteps(claimNumber2);

        LOGGER.info("---=={Step 12}==---");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_TwoServices")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[0]", CDT_CODE.getLabel()), DENIED)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[1]", CDT_CODE.getLabel()), DENIED));
        String claimNumber3 = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        assertThat(getClaimDBStatus(claimNumber3)).isEqualTo(ClaimConstants.ClaimDBStatus.OPEN);

        LOGGER.info("---=={Step 13}==---");
        issueClaimSteps(claimNumber3);

        LOGGER.info("---=={Step 14}==---");
        MainPage.QuickSearch.search(claimNumber);
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.PREDETERMINED);
        assertThat(getClaimDBStatus(claimNumber)).isEqualTo(ClaimConstants.ClaimDBStatus.CLOSED);

        LOGGER.info("---=={Step 16}==---");
        MainPage.QuickSearch.search(claimNumber2);
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        assertThat(getClaimDBStatus(claimNumber2)).isEqualTo(ClaimConstants.ClaimDBStatus.OPEN);

        LOGGER.info("---=={Step 18}==---");
        MainPage.QuickSearch.search(claimNumber3);
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        assertThat(getClaimDBStatus(claimNumber3)).isEqualTo(ClaimConstants.ClaimDBStatus.OPEN);

    }
    private void issueClaimSteps(String claimNumber) {

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED);

        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(PAID);
        assertThat(getClaimDBStatus(claimNumber)).isEqualTo(ClaimConstants.ClaimDBStatus.CLOSED);

        claim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(DECLINED);
        assertThat(getClaimDBStatus(claimNumber)).isEqualTo(ClaimConstants.ClaimDBStatus.INITIAL);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);
    }
}
