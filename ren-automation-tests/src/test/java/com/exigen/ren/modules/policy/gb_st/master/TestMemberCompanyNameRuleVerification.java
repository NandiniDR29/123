package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestMemberCompanyNameRuleVerification extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-32658", component = POLICY_GROUPBENEFITS)
    public void testMemberCompanyNameRuleVerification() {
        LOGGER.info("General Preconditions");

        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship2 = tdSpecific().getValue("RelationshipTestData2", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship1Updated = "NameLegalNonInd1A";

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.initiate(getDefaultSTMasterPolicyData());

        LOGGER.info("Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isAbsent();
        });

        LOGGER.info("Step#2 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isRequired().isPresent().hasValue(EMPTY);

        LOGGER.info("Step#3 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).containsAllOptions(EMPTY, relationship1, relationship2);

        LOGGER.info("Step#4 verification");
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).fillTab(getDefaultSTMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1));

        LOGGER.info("Step#5 verification");
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.tableGeneralInformation.getColumn(QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME.getName()).getCell(1)).hasValue(relationship1);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#6 verification");
        MainPage.QuickSearch.search(customerNumber);
        customerNonIndividual.update().start();
        relationshipTab.navigateToTab();
        RelationshipTab.editRelationship(1);
        relationshipTab.getAssetList().getAsset(RelationshipTabMetaData.NAME_LEGAL).setValue(relationship1Updated);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step#7 verification");
        MainPage.QuickSearch.search(policyNumber);
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).hasValue(relationship1Updated);

        LOGGER.info("Step#8, 9 verification");
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.tableGeneralInformation.getColumn(QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME.getName()).getCell(1)).hasValue(relationship1Updated);

        LOGGER.info("Step#10 execution");
        MainPage.QuickSearch.search(customerNumber);
        customerNonIndividual.update().start();
        relationshipTab.navigateToTab();
        RelationshipTab.removeRelationship(1);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step#11 verification");
        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.tableGeneralInformation.getColumn(QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME.getName()).getCell(1)).hasValue(relationship1Updated);

        LOGGER.info("Step#12 verification");
        statutoryDisabilityInsuranceMasterPolicy.dataGather().start();
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).hasValue(relationship1Updated);

        LOGGER.info("Step#13, 14 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).setValue(relationship2);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME).getAllValues()).containsOnly(relationship2);
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.tableGeneralInformation.getColumn(QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME.getName()).getCell(1)).hasValue(relationship2);
    }
}
