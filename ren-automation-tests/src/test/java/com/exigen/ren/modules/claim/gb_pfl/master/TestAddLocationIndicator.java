package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyTab;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLParticipantInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimAllSingleBenefitCalculationsTable.EMPTY;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.PAID_FAMILY_LEAVE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.*;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.INVOICING_CALENDAR_VALUE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.InvoicingCalendarTab.CALENDAR_NAME;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.PARTY_NAME;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.BILLING_LOCATION;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddLocationIndicator extends ClaimGroupBenefitsPFLBaseTest implements CaseProfileContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41224", component = CLAIMS_GROUPBENEFITS)
    public void testAddLocationIndicator() {
        String billingLocationValue = "Billing Location 1,10000,%s,%s";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        caseProfile.update().perform(tdSpecific().getTestData("UpdateCaseProfile"));

        TestData tdIssue = paidFamilyLeaveMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar");
        tdIssue.getTestDataList(BillingAccountTab.class.getSimpleName()).get(0)
                .adjust(makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), CALENDAR_NAME.getLabel()), "INC1")
                .adjust(makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "INC1");
        TestData tdPolicy = paidFamilyLeaveMasterPolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(paidFamilyLeaveMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(tdIssue);

        paidFamilyLeaveMasterPolicy.createPolicy(tdPolicy
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey(), OPTIONS.getLabel()), DataProviderFactory.dataOf(FICA_MATCH.getLabel(), "Reimbursement"))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "contains=Default"));
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(masterPolicyNumber);

        paidFamilyLeaveMasterPolicy.setupBillingGroups().start();
        paidFamilyLeaveMasterPolicy.setupBillingGroups().getWorkspace().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-41224: Step 1");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getPFLTestData().getTestData(DATA_GATHER, "TestData_With_PFL_Benefit"), BenefitsPFLParticipantInformationTab.class, false);
        assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(BILLING_LOCATION)).hasValue(String.format(billingLocationValue, customerName, billingAccountNumber));

        LOGGER.info("TEST REN-41224: Step 2");
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getPFLTestData().getTestData(DATA_GATHER, "TestData_With_PFL_Benefit")
                        .adjust(tdSpecific().getTestData("TestData_AdditionalParties"))
                        .adjust(TestData.makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), PARTY_NAME.getLabel()), String.format(billingLocationValue, customerName, billingAccountNumber)),
                BenefitsPFLParticipantInformationTab.class, AdditionalPartiesAdditionalPartyTab.class, true);

        assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.COMPANY_NAME)).hasValue(String.format(billingLocationValue, customerName, billingAccountNumber));

        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-41224: Step 4");
        disabilityClaim.addBenefit().start(PAID_FAMILY_LEAVE);
        assertThat(disabilityClaim.addBenefit().getWorkspace().getTab(BenefitsPFLParticipantInformationTab.class).getAssetList().getAsset(BILLING_LOCATION))
                .hasValue(String.format(billingLocationValue, customerName, billingAccountNumber));

        disabilityClaim.addBenefit().getWorkspace().fillUpTo(disabilityClaim.getPFLTestData().getTestData("NewBenefit", "TestData"), BenefitCoverageEvaluationTab.class, true);
        disabilityClaim.addBenefit().submit();
        assertThat(ClaimAdjudicationBenefitPage.tableAllClaimBenefits).hasRows(2);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41224", component = CLAIMS_GROUPBENEFITS)
    public void testAddLocationIndicatorWithoutBillingLocation() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);

        LOGGER.info("TEST REN-41224: Step 7");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getPFLTestData().getTestData(DATA_GATHER, "TestData_With_PFL_Benefit"), BenefitsPFLParticipantInformationTab.class, false);
        assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(BILLING_LOCATION)).hasValue(EMPTY);
    }
}
