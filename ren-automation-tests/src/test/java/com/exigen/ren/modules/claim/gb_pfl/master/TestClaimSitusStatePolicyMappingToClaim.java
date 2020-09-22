package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupPFLCoverages.PFL_FLB_FAMILY_LEAVE_BENEFIT;
import static com.exigen.ren.main.enums.CustomerConstants.EMAIL;
import static com.exigen.ren.main.enums.CustomerConstants.PHONE;
import static com.exigen.ren.main.enums.CustomerConstants.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanPFL.FLB_FAMILY_LEAVE_BENEFIT;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationSponsorTabMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDRESS_DETAILS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EMAIl_DETAILS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimSitusStatePolicyMappingToClaim extends ClaimGroupBenefitsPFLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23635", component = CLAIMS_GROUPBENEFITS)
    public void testClaimNJPolicyMappingToClaim() {

        String GENERAL_TAB = GeneralTab.class.getSimpleName();

        mainApp().open();

        customerNonIndividual.create(customerNonIndividual.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GENERAL_TAB, EMAIl_DETAILS.getLabel()),
                        customerNonIndividual.getDefaultTestData(DATA_GATHER, "TestData_withPhoneEmail")
                                .getTestData(GENERAL_TAB, EMAIl_DETAILS.getLabel())).resolveLinks());

        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        String customerPhone = CustomerSummaryPage.tableCustomerContacts.getRows(ImmutableMap.of(CONTACT_METHOD, PHONE)).get(0).getCell(CONTACT_DETAILS).getValue();
        String customerEmail = CustomerSummaryPage.tableCustomerContacts.getRows(ImmutableMap.of(CONTACT_METHOD, EMAIL)).get(0).getCell(CONTACT_DETAILS).getValue();

        String addressWoCountry = CustomerSummaryPage.labelCustomerAddress.getValue().replace(", ", " ");
        String country = customerNonIndividual.getDefaultTestData(DATA_GATHER, GENERAL_TAB, ADDRESS_DETAILS.getLabel()).getValue(COUNTRY);
        String customerAddress = addressWoCountry + " " + country;

        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());

        paidFamilyLeaveMasterPolicy.createPolicy(getDefaultPFLMasterPolicyData().adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        TestData tdDefaultClaim = disabilityClaim.getPFLTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY);

        disabilityClaim.initiate(tdDefaultClaim);

        LOGGER.info("TEST: Step 2");
        disabilityClaim.getDefaultWorkspace().fillUpTo(tdDefaultClaim, PolicyInformationParticipantParticipantCoverageTab.class);
        policyInformationParticipantParticipantCoverageTab.addCoverage(FLB_FAMILY_LEAVE_BENEFIT, PFL_FLB_FAMILY_LEAVE_BENEFIT);

        LOGGER.info("TEST: Step 3");
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Qualification Period"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Include Recurrence Benefit?"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(ERISA_INDICATOR))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Successive Period"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Elimination Period - Sickness (Calendar Days)"))).isAbsent();
        });

        LOGGER.info("TEST: Steps 5, 9, 17");
        assertSoftly(softly -> {
           softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(ACCUMULATION_PERIOD)).isPresent().isDisabled().hasValue("52");
           softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(MAX_BENEFIT_PERIOD_DAYS)).isPresent().isDisabled().hasValue("56");
           softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(RETROACTIVE_BENEFIT)).isPresent().isDisabled().hasValue("3 Week");
           softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(FICA_MATCH)).isPresent().isDisabled().hasValue("None");
        });

        LOGGER.info("TEST: Step 19");
        Tab.buttonNext.click();
        assertSoftly(softly -> {
            softly.assertThat(policyInformationSponsorTab.getAssetList().getAsset(COMPANY_NAME)).hasValue(customerName);
            softly.assertThat(policyInformationSponsorTab.getAssetList().getAsset(MASTER_POLICY_NUMBER)).hasValue(policyNumber);
            softly.assertThat(policyInformationSponsorTab.getAssetList().getAsset(SPONSOR_PHONE)).hasValue(customerPhone);
            softly.assertThat(policyInformationSponsorTab.getAssetList().getAsset(SPONSOR_EMAIL)).hasValue(customerEmail);
            softly.assertThat(policyInformationSponsorTab.getAssetList().getAsset(SPONSOR_ADDRESS)).hasValue(customerAddress);
        });
    }
}