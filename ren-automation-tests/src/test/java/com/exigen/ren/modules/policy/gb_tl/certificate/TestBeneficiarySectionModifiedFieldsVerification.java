package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.COVERAGES;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ROLE_PERCENT_PRIMARY_ERROR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ROLE_PERCENT_SECONDARY_ERROR;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.BENEFICIARIES;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.BeneficiariesMetaData;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class TestBeneficiarySectionModifiedFieldsVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    private static final String NEW_PERSON = "New Person";
    private static final String NEW_NON_PERSON = "New Non-Person";
    private static final String PRIMARY_BENEFICIARY = "Primary Beneficiary";
    private static final String SECONDARY_BENEFICIARY = "Secondary Beneficiary";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28741", component = POLICY_GROUPBENEFITS)
    public void testBeneficiarySectionModifiedFieldsVerification() {
        LOGGER.info("REN-28741 preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicyViaUI(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataCoverages").resolveLinks()));

        termLifeInsuranceCertificatePolicy.initiate(getDefaultCertificatePolicyDataGatherData());
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultCertificatePolicyDataGatherData()
                .adjust(tdSpecific().getTestData("TestDataCertificatePolicy").resolveLinks()), CoveragesTab.class, true);

        LOGGER.info("Step#1 verification");
        NavigationPage.toLeftMenuTab(COVERAGES.get());
        openAddedPlan(BTL);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ADD_BENEFICIARY).click();
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.BENEFICIARY_SELECTION)).hasOptions(EMPTY, NEW_PERSON, NEW_NON_PERSON);

        LOGGER.info("Step#4 verification");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.BENEFICIARY_SELECTION).setValue(NEW_PERSON);
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_NAME)).hasValue(PRIMARY_BENEFICIARY);

        changExistingBeneficiaryAndAddNewWithVerification(PRIMARY_BENEFICIARY, ROLE_PERCENT_PRIMARY_ERROR);
        rolePercentVerification(ROLE_PERCENT_PRIMARY_ERROR);

        LOGGER.info("Step#15 verification");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).setValue("100");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ADD_BENEFICIARY).click();
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.BENEFICIARY_SELECTION).setValue(NEW_PERSON);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_NAME).setValue(SECONDARY_BENEFICIARY);

        changExistingBeneficiaryAndAddNewWithVerification(SECONDARY_BENEFICIARY, ROLE_PERCENT_SECONDARY_ERROR);

        LOGGER.info("Step#16 verification");
        rolePercentVerification(ROLE_PERCENT_SECONDARY_ERROR);
    }

    private void changExistingBeneficiaryAndAddNewWithVerification(String roleName, String errorText) {
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).setValue("50");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT)).hasWarningWithText(errorText);

        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ADD_BENEFICIARY).click();
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.BENEFICIARY_SELECTION).setValue(NEW_NON_PERSON);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_NAME).setValue(roleName);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).setValue("30");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT)).hasWarningWithText(errorText);
    }

    private void rolePercentVerification(String errorText) {
        LOGGER.info("Step#6 / 19 verification");
        openExistingBeneficiary("30");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).setValue("60");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT)).hasWarningWithText(errorText);

        LOGGER.info("Step#9 / 19 verification");
        removeExistingBeneficiary("60");
        openExistingBeneficiary("50");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).setValue("40");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT)).hasWarningWithText(errorText);

        LOGGER.info("Step#11 / 21 verification");
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).setValue("101");
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(BeneficiariesMetaData.ROLE_PERCENT).getWarning()
                .orElse(EMPTY)).contains(errorText);
    }
}