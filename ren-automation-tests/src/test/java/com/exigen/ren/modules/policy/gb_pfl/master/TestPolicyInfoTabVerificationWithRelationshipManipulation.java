package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyInfoTabVerificationWithRelationshipManipulation extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32449"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyInfoTabVerificationWithRelationshipManipulation() {
        LOGGER.info("General Preconditions");
        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship1Updated = "NameLegalNonInd1Updated";

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        paidFamilyLeaveMasterPolicy.initiate(getDefaultPFLMasterPolicyData());

        LOGGER.info("Scenario#2 Step#1, 3, 4 verification");
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1), PolicyInformationTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);
        assertThat(PolicySummaryPage.tableGeneralInformation.getColumn(QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME.getName()).getCell(1)).hasValue(relationship1);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Scenario#2 Step#5 verification");
        MainPage.QuickSearch.search(customerNumber);
        customerNonIndividual.update().start();
        NavigationPage.toSubTab(NavigationEnum.CustomerTab.RELATIONSHIP.get());
        RelationshipTab.editRelationship(1);
        relationshipTab.getAssetList().getAsset(RelationshipTabMetaData.NAME_LEGAL).setValue(relationship1Updated);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Scenario#2 Steps#6, 7, 9 verification");
        consolidateAndDataGatherVerification(policyNumber, relationship1Updated);

        LOGGER.info("Scenario#2 Step#10 verification");
        Tab.buttonSaveAndExit.click();
        MainPage.QuickSearch.search(customerNumber);
        customerNonIndividual.update().start();
        NavigationPage.toSubTab(NavigationEnum.CustomerTab.RELATIONSHIP.get());
        RelationshipTab.removeRelationship(1);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Scenario#2 Step#11 verification");
        consolidateAndDataGatherVerification(policyNumber, relationship1Updated);
    }

    private void consolidateAndDataGatherVerification(String policyNumber, String memberCompanyName) {
        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.tableGeneralInformation.getColumn(QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME.getName()).getCell(1)).hasValue(memberCompanyName);

        paidFamilyLeaveMasterPolicy.dataGather().start();
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).hasValue(memberCompanyName);
    }
}
