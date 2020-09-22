package com.exigen.ren.modules.docgen.gb_di_ltd;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage.CaseProfilesTable;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.SC4;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.SC5;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CASE;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.caseprofile.tabs.GenerateOnDemandDocumentTab.buttonGenerate;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.labelCustomerNumber;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationBenefitSummaryDocument extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-23980", component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationBenefitSummaryDocument() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createQuote(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.propose().perform();
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());

        LOGGER.info("REN-23980 Step 1");
        NavigationPage.toMainTab(CASE);
        CaseProfileSummaryPage.tableSelectCaseProfile.getRow(1).getCell(CaseProfilesTable.CASE_PROFILE_NAME.getName()).controls.links.getFirst().click();
        caseProfile.generateDocument().start(1);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Delivery Channels options"))).isAbsent();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Open Document Preview"))).isAbsent();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Please fill out fields that apply"))).isAbsent();

        LOGGER.info("REN-23980 Step 2");
        buttonGenerate.click();
        Page.dialogConfirmation.confirm();
        NavigationPage.toMainTab(CUSTOMER);
        assertThat(Efolder.getFileName("Policy and Cert/Benefit Summaries", "BENEFIT-SUMMARY")).withFailMessage("'Benefit Summaries' document does not exist in EFolder").isNotNull();

        String customerNumber = labelCustomerNumber.getValue();

        LOGGER.info("REN-23980 Step 3");
        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER);

        //SC4
        Integer currentYear = TimeSetterUtil.getInstance().getCurrentTime().getYear();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        xmlValidator.checkDocument(SC4, String.format("G-UW-SUMMARY-%d-%s-%s", currentYear, customerName, customerNumber));

        //SC5
        xmlValidator.checkDocument(SC5, String.format("PA [%s]", TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("MM/yy"))));
    }
}
