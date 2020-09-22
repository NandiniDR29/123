package com.exigen.ren.modules.claim.gb_tl.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimPolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import com.exigen.ren.utils.CommonMethods;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationPolicyTabMetaData.UNDERWRITING_COMPANY;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.UNDEWRITING_COMPANY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimSavePolicyInformationDataAndDisplayOnFNOL extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23498", component = CLAIMS_GROUPBENEFITS)
    public void testCWMPClaimSavePolicyInformationDataAndDisplayOnFNOL() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("Precondition 2.");
        String randomUnderwritingCompany = CommonMethods.getRandomElementFromList(ImmutableList.of(
                "Renaissance Life & Health Insurance Company of America", "Renaissance Life & Health Insurance Company of New York"));
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), UNDEWRITING_COMPANY.getLabel()), randomUnderwritingCompany));

        LOGGER.info("Step 1");
        createDefaultTermLifeInsuranceClaimForMasterPolicy();

        LOGGER.info("Step 2");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FNOL.get());
        policyInformationPolicyTab.navigateToTab();
        assertThat(policyInformationPolicyTab.getAssetList().getAsset(UNDERWRITING_COMPANY)).hasValue(randomUnderwritingCompany);
        Tab.buttonSaveAndExit.click();

        assertSoftly(softly -> {
            LOGGER.info("Step 3");
            ClaimSummaryPage.labelUnderwritingCompany.mouseOver();
            softly.assertThat(ClaimSummaryPage.headerUnderwritingCompany).hasValue(randomUnderwritingCompany);

            LOGGER.info("Step 4");
            NavigationPage.toSubTab(NavigationEnum.ClaimTab.POLICY_SUMMARY.get());
            softly.assertThat(ClaimPolicySummaryPage.policySummaryUnderwritingCompany).hasValue(randomUnderwritingCompany);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23498", component = CLAIMS_GROUPBENEFITS)
    public void testCWCPClaimSavePolicyInformationDataAndDisplayOnFNOL() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("Precondition 2.");
        String randomUnderwritingCompany = CommonMethods.getRandomElementFromList(ImmutableList.of(
                "Renaissance Life & Health Insurance Company of America", "Renaissance Life & Health Insurance Company of New York"));
        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), UNDEWRITING_COMPANY.getLabel()), randomUnderwritingCompany));

        LOGGER.info("Precondition 3.");
        createDefaultTermLifeInsuranceCertificatePolicy();

        LOGGER.info("Step 5");
        createDefaultTermLifeInsuranceClaimForCertificatePolicy();
        assertSoftly(softly -> {
            LOGGER.info("Step 3");
            ClaimSummaryPage.labelUnderwritingCompany.mouseOver();
            softly.assertThat(new StaticElement(By.id("producContextInfoForm:j_id_24_3c_s_c_5_1_6:content"))).hasValue(randomUnderwritingCompany);

            LOGGER.info("Step 4");
            NavigationPage.toSubTab(NavigationEnum.ClaimTab.POLICY_SUMMARY.get());
            softly.assertThat(ClaimPolicySummaryPage.policySummaryUnderwritingCompany).hasValue(randomUnderwritingCompany);
        });
    }
}
