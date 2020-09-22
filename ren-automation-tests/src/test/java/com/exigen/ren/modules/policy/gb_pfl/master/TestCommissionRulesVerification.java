package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.commission.commissiongroup.CommissionGroup;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.common.tabs.CommissionSearchTab;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.agency;
import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.agencyInfoTab;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AGENCIES;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AddAgencies.AGENCY_NAME;
import static com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData.COMMISSION_GROUP_NAME;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.generalProfileTab;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.CHANNEL;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.tableErrorsList;
import static com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab.SELECT_QUOTE_BY_ROW_NUMBER_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.ADD_AGENCY;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.ASSIGNED_AGENCIES;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.AssignedAgenciesMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.AssignedAgenciesMetaData.INDEPENDENT_COMMISSIONABLE_PRODUCER_ICP;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCommissionRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    private static final String OVERRIDE_ERROR_MESSAGE = "Proposal requires Underwriter approval as Total Commission exceeds 17%.";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-25531"}, component = POLICY_GROUPBENEFITS)
    public void testCommissionRulesVerification_PFL() {
        LOGGER.info("General admin preconditions execution");
        adminApp().open();
        String agencyName1 = createAgencyWithData("Agency");
        String agencyName2 = createAgencyWithData("Call Center");
        String agencyName3 = createAgencyWithData("Independent Producer");
        String agencyName4 = createAgencyWithData("TPA");
        String agencyName5 = createAgencyWithData("MGA");
        String agencyName6 = createAgencyWithData("GA");

        updateExistingCommGroupWithNewAgency(agencyName1, "Producers - Standard Broker Schedule");
        updateExistingCommGroupWithNewAgency(agencyName2, "GAs - 001A");
        updateExistingCommGroupWithNewAgency(agencyName3, "GAs - 004A");
        updateExistingCommGroupWithNewAgency(agencyName4, "Producers - Standard Broker Schedule");
        updateExistingCommGroupWithNewAgency(agencyName5, "Producers - Standard Broker Schedule");
        updateExistingCommGroupWithNewAgency(agencyName6, "Producers - Standard Broker Schedule");
        userCreateAndReLogin(agencyName1, "Level 0");

        LOGGER.info("General main preconditions execution");
        TestData tdAgency1 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName1);
        TestData tdAgency2 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName2);
        TestData tdAgency3 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName3);
        TestData tdAgency4 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName4);
        TestData tdAgency5 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName5);
        TestData tdAgency6 = customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                .adjust(TestData.makeKeyPath(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName6);

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(tdAgency1, tdAgency2, tdAgency3, tdAgency4, tdAgency5, tdAgency6)));

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(paidFamilyLeaveMasterPolicy.getType())
                .mask(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel())));

        LOGGER.info("Steps#1, 2 execution");
        initiatePFLQuoteAndFillToTab(getDefaultPFLMasterPolicyData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel() + "[0]",
                                AGENCY_PRODUCER.getLabel()), agencyName1).resolveLinks()), PremiumSummaryTab.class, true);

        LOGGER.info("Step#5 execution");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(ADD_AGENCY).click();
        policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(AGENCY_PRODUCER).setValue(agencyName2);
        policyInformationTab.getAssetList().getAsset(ASSIGNED_AGENCIES).getAsset(INDEPENDENT_COMMISSIONABLE_PRODUCER_ICP).setValue(VALUE_YES);

        LOGGER.info("Step#6 verification");
        premiumSummaryTab.navigateToTab();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Proposal will require Underwriter approval as Total Commission exceeds 17%."))).isPresent();

        LOGGER.info("Step#7 verification");
        premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#8 verification");
        paidFamilyLeaveMasterPolicy.propose().start();
        paidFamilyLeaveMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), ProposalActionTab.class, false);
        proposalActionTab.getAssetList().getAsset(ProposalTabMetaData.OVERRIDE_RULES).click();
        assertSoftly(softly -> {
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue(OVERRIDE_ERROR_MESSAGE);
            softly.assertThat(ProposalActionTab.tableErrorsList.getRow(1).getCell(1).controls.checkBoxes.getFirst()).isDisabled();
        });

        LOGGER.info("Step#9 verification");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        userCreateAndReLogin(agencyName1, "Level 1");

        LOGGER.info("Step#10 verification");
        MainPage.QuickSearch.search(policyNumber);

        paidFamilyLeaveMasterPolicy.propose().start();
        paidFamilyLeaveMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), ProposalActionTab.class, false);
        proposalActionTab.getAssetList().getAsset(ProposalTabMetaData.OVERRIDE_RULES).click();
        assertSoftly(softly -> {
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue(OVERRIDE_ERROR_MESSAGE);
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName())).isEnabled();
        });

        LOGGER.info("Step#11 verification");
        paidFamilyLeaveMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class)
                .overrideRules(ImmutableList.of(OVERRIDE_ERROR_MESSAGE), "Term");
        proposalActionTab.submitTab();
        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("Step#14 verification");
        paidFamilyLeaveMasterPolicy.copyQuote().perform(paidFamilyLeaveMasterPolicy.getDefaultTestData("CopyFromQuote", TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("Step#15 verification");
        paidFamilyLeaveMasterPolicy.dataGather().start();
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class)
                .fillTab(tdSpecific().getTestData("TestDataUpdate")
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel() + "[0]",
                                AGENCY_PRODUCER.getLabel()), agencyName3)
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel() + "[1]",
                                AGENCY_PRODUCER.getLabel()), agencyName4)
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel() + "[2]",
                                AGENCY_PRODUCER.getLabel()), agencyName5)
                        .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASSIGNED_AGENCIES.getLabel() + "[3]",
                                AGENCY_PRODUCER.getLabel()), agencyName6).resolveLinks());

        LOGGER.info("Step#16 verification");
        PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.navigateToTab();
        IntStream.range(0, ClassificationManagementTab.coveragesTable.getRowsCount())
                .forEach(row -> ClassificationManagementTab.coveragesTable.getRow(row + 1).getCell(1).click());

        PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.submitTab();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Proposal will require Underwriter Management approval as Total Commission exceeds 22%."))).isPresent();

        LOGGER.info("Step#19 verification");
        overrideCommission(agencyName4);
        overrideCommission(agencyName5);
        overrideCommission(agencyName6);
        premiumSummaryTab.rate();
        PremiumSummaryTab.buttonSaveAndExit.click();
        String policyNumberNew = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#23 execution");
        userCreateAndReLogin(agencyName1, "Level 5");

        LOGGER.info("Step#24 verification");
        MainPage.QuickSearch.search(policyNumberNew);
        paidFamilyLeaveMasterPolicy.propose().start();
        paidFamilyLeaveMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), SELECT_QUOTE_BY_ROW_NUMBER_KEY), ImmutableList.of("2")), ProposalActionTab.class, false);
        proposalActionTab.getAssetList().getAsset(ProposalTabMetaData.OVERRIDE_RULES).click();
        assertSoftly(softly -> {
            softly.assertThat(tableErrorsList.getRow(1).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue("Proposal requires Underwriter Management approval as Total Commission exceeds...");
            softly.assertThat(ProposalActionTab.tableErrorsList.getRow(1).getCell(1).controls.checkBoxes.getFirst()).isEnabled();
            softly.assertThat(tableErrorsList.getRow(2).getCell(TableConstants.OverrideErrorsTable.MESSAGE.getName())).hasValue("Proposal will require Underwriter Management approval due to change in commis...");
            softly.assertThat(ProposalActionTab.tableErrorsList.getRow(2).getCell(1).controls.checkBoxes.getFirst()).isEnabled();
        });

        LOGGER.info("Step#25 verification");
        paidFamilyLeaveMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), ImmutableList.of(
                        "Proposal requires Underwriter Management approval as Total Commission exceeds...",
                        "Proposal will require Underwriter Management approval due to change in commis...")));
        proposalActionTab.submitTab();
        MainPage.QuickSearch.search(policyNumberNew);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);
    }

    private String createAgencyWithData(String agencyType) {
        TestData tdAgencyDefault = agency.defaultTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        return agency.createAgency(tdAgencyDefault
                .adjust(TestData.makeKeyPath(agencyInfoTab.getMetaKey(), AgencyInfoMetaData.AGENCY_TYPE.getLabel()), agencyType));
    }

    private void updateExistingCommGroupWithNewAgency(String agencyName, String groupName) {
        CommissionGroup commissionGroup = new CommissionGroup();
        commissionGroup.search(commissionGroup.getDefaultTestData().getTestData("SearchData", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionSearchTab.class.getSimpleName(), COMMISSION_GROUP_NAME.getLabel()), groupName));
        commissionGroup.edit().perform(commissionGroup.getDefaultTestData().getTestData("EditData", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), AGENCY_NAME.getLabel()), agencyName), 1);
    }

    private void userCreateAndReLogin(String agencyName, String authorityLevel) {
        TestData tdNewProfile = profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), CHANNEL.getLabel()), EMPTY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), agencyName);

        createUserAndRelogin(profileCorporate, tdNewProfile
                .adjust(TestData.makeKeyPath(AuthorityLevelsTab.class.getSimpleName(),
                        AuthorityLevelsMetaData.LEVEL.getLabel()), authorityLevel));
    }

    private void overrideCommission(String agencyName) {
        Button overrideCommissionButton = PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, "PFL");
        overrideCommissionButton.click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue("PFL-Flat");
        DialogOverrideCommissionPremiumSummary.overrideCommissionForAllCoverages("31");
        PremiumSummaryTab.dialogOverrideCommision.confirm();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Proposal will require Underwriter Management approval due to change in commission"))).isPresent();
    }
}
