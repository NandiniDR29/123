package com.exigen.ren.modules.docgen.gb_di_std;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CaseProfileConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.generalProfileTab;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.INTERNAL_TEAM;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.SALES_REPRESENTATIVE;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CoverageSummary.MODAL_PREMIUM;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserAndRelogin;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationProposalDocument extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private String userFirstName;
    private String userLastName;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18853"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationCombinedProposalDocument() {
        createProfile();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createQuoteViaUI(getDefaultSTDMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), INTERNAL_TEAM.getLabel(),
                        SALES_REPRESENTATIVE.getLabel()), String.format("%s %s", userFirstName, userLastName)));
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());

        LOGGER.info("TEST: Get expected testData from UI");
        String caseProfileNumber = labelCaseProfileNumber.getValue();
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue();
        String totalMonthlyCost = tableCoverageSummary.getRow(1).getCell(MODAL_PREMIUM.getName()).getValue();
        String salesRepresentativeName = String.format("%s %s", userFirstName, userLastName);
        String proposalNumber = getProposalNumber();
        String footerDocInfo = String.format("COMBINED_PROPOSAL-%s-%s-%s", TimeSetterUtil.getInstance().getCurrentTime().getYear(), groupName, proposalNumber);

        LOGGER.info("TEST: Prepare testData for verification");
        TestData tdDataSource = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY);
        tdDataSource.adjust(DocGenEnum.AllSections.C3.name(), groupName.toUpperCase());
        tdDataSource.adjust(DocGenEnum.AllSections.C5.name(), salesRepresentativeName);
        tdDataSource.adjust(DocGenEnum.AllSections.C10.name(), footerDocInfo);
        tdDataSource.adjust(DocGenEnum.AllSections.C17.name(), new Currency(totalMonthlyCost).toPlainString());

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("TEST: Validate document");
        xmlValidator.checkDocument(tdDataSource);
    }

    private void createProfile() {
        TestData td = createUserAndRelogin(profileCorporate, profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(generalProfileTab.getMetaKey(), tdSpecific().getTestData("Adjust_GeneralProfileTab").getTestData(generalProfileTab.getMetaKey())));

        userFirstName = td.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileMetaData.FIRST_NAME.getLabel());
        userLastName = td.getValue(GeneralProfileTab.class.getSimpleName(),
                GeneralProfileMetaData.LAST_NAME.getLabel());
    }

    private String getProposalNumber() {
        labelCaseProfileNumber.click();
        NavigationPage.toSubTab(NavigationEnum.CaseProfileTab.PROPOSALS.get());
        return CaseProfileSummaryPage.tableProposal.getRow(1).getCell(CaseProfileConstants.CaseProfileProposalTable.PROPOSAL_NUMBER).getValue();
    }
}
