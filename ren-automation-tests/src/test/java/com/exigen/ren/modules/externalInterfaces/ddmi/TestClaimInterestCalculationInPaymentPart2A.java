package com.exigen.ren.modules.externalInterfaces.ddmi;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.model.dental.claiminterest.ClaimInterestModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.LOGGED_INTAKE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderSection.INTERNATIONAL_PROVIDER_VALUE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.COUNTRY;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimInterestCalculationInPaymentPart2A extends ClaimGroupBenefitsDNBaseTest implements CustomerContext, DentalClaimContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-41572", component = CLAIMS_GROUPBENEFITS)
    public void testClaimInterestCalculationInPaymentPart2A() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        String policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(2).format(MM_DD_YYYY);
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), policyEffectiveDate));
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), policyEffectiveDate));

        LOGGER.info("Steps 1,2");
        dentalClaim.create(tdSpecific().getTestData("TestData_Claim_IntProvider").resolveLinks());
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);

        LOGGER.info("Step 4");
        NavigationPage.toSubTab(EDIT_CLAIM);
        AbstractContainer<?, ?> intakeInfoTabAssetList = intakeInformationTab.getAssetList();
        intakeInfoTabAssetList.getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(COUNTRY).setValue("El Salvador");
        Tab.buttonTopSave.click();

        String todayDateTime = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        ClaimInterestModel claimInterestModel = dentalClaimRest.getClaimInterest(claimNumber).getModel();
        assertSoftly(softly -> {
            softly.assertThat(claimInterestModel.getCleanClaimDate()).isNull();
            softly.assertThat(claimInterestModel.getClaimReceivedDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel.getPaymentPostDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel.getInterestState()).isNull();
            softly.assertThat(claimInterestModel.isInternationalProvider()).isTrue();
            softly.assertThat(claimInterestModel.getFeatures()).hasSize(0);
        });

        LOGGER.info("Step 5");
        NavigationPage.toSubTab(EDIT_CLAIM);
        Page.dialogConfirmation.confirm();
        intakeInfoTabAssetList.getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue("No");
        intakeInfoTabAssetList.fill(tdSpecific().getTestData("TestData_Claim_AddProvider"));
        Tab.buttonTopSave.click();

        ClaimInterestModel claimInterestModel2 = dentalClaimRest.getClaimInterest(claimNumber).getModel();
        assertSoftly(softly -> {
            softly.assertThat(claimInterestModel2.getCleanClaimDate()).isNull();
            softly.assertThat(claimInterestModel2.getClaimReceivedDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel2.getPaymentPostDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel2.getInterestState()).isEqualTo("NV");
            softly.assertThat(claimInterestModel2.isInternationalProvider()).isFalse();
            softly.assertThat(claimInterestModel2.getFeatures()).hasSize(0);
        });

        LOGGER.info("Step 6");
        NavigationPage.toSubTab(ADJUDICATION.get());
        Page.dialogConfirmation.confirm();
        dentalClaim.generateDocument().perform(new SimpleDataProvider(), 1);
        NavigationPage.toSubTab(EDIT_CLAIM);
        removeService("D2971", new Currency(100));
        intakeInfoTabAssetList.fill(tdSpecific().getTestData("TestData_Claim_Step6"));
        buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);

        String todayMinus2DaysDateTime = TimeSetterUtil.getInstance().getCurrentTime().minusDays(2).toLocalDate().atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        String todayMinus3DaysDateTime = TimeSetterUtil.getInstance().getCurrentTime().minusDays(3).toLocalDate().atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        ClaimInterestModel claimInterestModel3 = dentalClaimRest.getClaimInterest(claimNumber).getModel();
        assertSoftly(softly -> {
            softly.assertThat(claimInterestModel3.getCleanClaimDate()).isEqualTo(todayMinus2DaysDateTime);
            softly.assertThat(claimInterestModel3.getClaimReceivedDate()).isEqualTo(todayMinus3DaysDateTime);
            softly.assertThat(claimInterestModel3.getPaymentPostDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel3.getInterestState()).isEqualTo("NV");
            softly.assertThat(claimInterestModel3.isInternationalProvider()).isFalse();
            softly.assertThat(claimInterestModel3.getFeatures()).hasSize(2);
        });

        LOGGER.info("Step 7");
        NavigationPage.toSubTab(EDIT_CLAIM);
        removeService("D2971", new Currency(100));
        removeProvider();
        intakeInfoTabAssetList.fill(tdSpecific().getTestData("TestData_Claim_Step7"));
        Tab.buttonSaveAndExit.click();

        String todayMinus6DaysDateTime = TimeSetterUtil.getInstance().getCurrentTime().minusDays(6).toLocalDate().atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        ClaimInterestModel claimInterestModel4 = dentalClaimRest.getClaimInterest(claimNumber).getModel();
        assertSoftly(softly -> {
            softly.assertThat(claimInterestModel4.getCleanClaimDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel4.getClaimReceivedDate()).isEqualTo(todayMinus6DaysDateTime);
            softly.assertThat(claimInterestModel4.getPaymentPostDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel4.getInterestState()).isEqualTo("NV");
            softly.assertThat(claimInterestModel4.isInternationalProvider()).isFalse();
            softly.assertThat(claimInterestModel4.getFeatures()).hasSize(1);
        });

        LOGGER.info("Step 8");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInfoTabAssetList.fill(tdSpecific().getTestData("TestData_Claim_Step8"));
        Tab.buttonSaveAndExit.click();

        String todayMinus4DaysDateTime = TimeSetterUtil.getInstance().getCurrentTime().minusDays(4).toLocalDate().atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        ClaimInterestModel claimInterestModel5 = dentalClaimRest.getClaimInterest(claimNumber).getModel();
        assertSoftly(softly -> {
            softly.assertThat(claimInterestModel5.getCleanClaimDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel5.getClaimReceivedDate()).isEqualTo(todayMinus4DaysDateTime);
            softly.assertThat(claimInterestModel5.getPaymentPostDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel5.getInterestState()).isEqualTo("NV");
            softly.assertThat(claimInterestModel5.isInternationalProvider()).isFalse();
            softly.assertThat(claimInterestModel5.getFeatures()).hasSize(0);
        });

        LOGGER.info("Step 9");
        NavigationPage.toSubTab(ADJUDICATION.get());
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));

        ClaimInterestModel claimInterestModel6 = dentalClaimRest.getClaimInterest(claimNumber).getModel();
        assertSoftly(softly -> {
            softly.assertThat(claimInterestModel6.getCleanClaimDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel6.getClaimReceivedDate()).isEqualTo(todayMinus4DaysDateTime);
            softly.assertThat(claimInterestModel6.getPaymentPostDate()).isEqualTo(todayDateTime);
            softly.assertThat(claimInterestModel6.getInterestState()).isEqualTo("NV");
            softly.assertThat(claimInterestModel6.isInternationalProvider()).isFalse();
            softly.assertThat(claimInterestModel6.getFeatures()).hasSize(1);
        });

        LOGGER.info("Step 10");
        mainApp().close();
        LocalDateTime todayPlus1Day = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).toLocalDate().atStartOfDay();
        TimeSetterUtil.getInstance().nextPhase(todayPlus1Day);
        assertThat(dentalClaimRest.getClaimInterest(claimNumber).getModel().getPaymentPostDate()).isEqualTo(todayPlus1Day.format(YYYY_MM_DD_HH_MM_SS_Z));
    }

}
