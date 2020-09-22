package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyTab;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsSTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.INVOICING_CALENDAR_VALUE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitSTDInjuryPartyInformationTabMetaData.YTDTaxableWageMetaData.BILLING_LOCATION;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitSTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SELF_ADMINISTERED;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckLocationIndicatorForFICAReimbursement extends ClaimGroupBenefitsSTDBaseTest implements CaseProfileContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-40761", "REN-40759"}, component = CLAIMS_GROUPBENEFITS)
    public void testCheckLocationIndicatorForFICAReimbursement() {
        String billingLocationValue = "Location Name 1,1,%s,%s";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        caseProfile.update().perform(tdSpecific().getTestData("UpdateCaseProfile"));

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SELF_ADMINISTERED.getLabel()), VALUE_YES)
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true"));
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(masterPolicyNumber);

        shortTermDisabilityMasterPolicy.setupBillingGroups().start();
        shortTermDisabilityMasterPolicy.setupBillingGroups().getWorkspace().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-40761/REN-40759: Step 1, 5/Step 1-2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsSTDInjuryPartyInformationTab.class, true);
        BenefitsSTDInjuryPartyInformationTab.addYTDTaxableWage.click();
        benefitsSTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitsSTDInjuryPartyInformationTabMetaData.YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION).setValueContains("Location Name");

        LOGGER.info("TEST REN-40759: Step 4-5");
        BenefitsSTDInjuryPartyInformationTab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsSTDIncidentTab.class, AdditionalPartiesAdditionalPartyTab.class, true);
        assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.PARTY_NAME).getAllValues())
                .contains(String.format(billingLocationValue, customerName, billingAccountNumber));
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-40761: Step 6-10");
        claim.updateBenefit().start(1);
        BenefitSTDInjuryPartyInformationTab.addYTDTaxableWage.click();
        assertThat(claim.updateBenefit().getWorkspace().getTab(BenefitSTDInjuryPartyInformationTab.class).getAssetList().getAsset(YTD_TAXABLE_WAGE).getAsset(BILLING_LOCATION))
                .isPresent().isRequired().valueContains(String.format(billingLocationValue, customerName, billingAccountNumber));

    }
}
