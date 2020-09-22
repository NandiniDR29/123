package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyTab;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsStatutorySTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.*;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.INVOICING_CALENDAR_VALUE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.InvoicingCalendarTab.CALENDAR_NAME;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData.YTDTaxableWageMetaData.BILLING_LOCATION;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckLocationIndicatorForFICAReimbursement extends ClaimGroupBenefitsSTBaseTest implements CaseProfileContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-40761", "REN-40759"}, component = CLAIMS_GROUPBENEFITS)
    public void testCheckLocationIndicatorForFICAReimbursement() {
        String billingLocationValue = "Location Name 1,1,%s,%s";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        caseProfile.update().perform(tdSpecific().getTestData("UpdateCaseProfile"));

        TestData tdIssue = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar");
        tdIssue.getTestDataList(BillingAccountTab.class.getSimpleName()).get(0)
                .adjust(makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), CALENDAR_NAME.getLabel()), "INC1")
                .adjust(makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "INC1");
        TestData tdPolicy = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(tdIssue);

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(tdPolicy
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey(), OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "INC1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "contains=Default"));
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(masterPolicyNumber);

        statutoryDisabilityInsuranceMasterPolicy.setupBillingGroups().start();
        statutoryDisabilityInsuranceMasterPolicy.setupBillingGroups().getWorkspace().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-40761/REN-40759: Step 1, 5/Step 1-2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsStatutorySTDInjuryPartyInformationTab.class, true);
        BenefitsStatutorySTDInjuryPartyInformationTab.addYTDTaxableWage.click();
        benefitsStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitsStatutorySTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION).setValueContains("Location Name");

        LOGGER.info("TEST REN-40759: Step 4-5");
        BenefitsStatutorySTDInjuryPartyInformationTab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getSTTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsStatutorySTDIncidentTab.class, AdditionalPartiesAdditionalPartyTab.class, true);
        assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.PARTY_NAME).getAllValues())
                .contains(String.format(billingLocationValue, customerName, billingAccountNumber));
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-40761: Step 6-10");
        claim.updateBenefit().start(1);
        BenefitStatutorySTDInjuryPartyInformationTab.addYTDTaxableWage.click();
        assertThat(claim.updateBenefit().getWorkspace().getTab(BenefitStatutorySTDInjuryPartyInformationTab.class).getAssetList().getAsset(YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION))
                .isPresent().isRequired().valueContains(String.format(billingLocationValue, customerName, billingAccountNumber));

    }
}
