package com.exigen.ren.modules.caseprofile;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDITIONAL_INFORMATION;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AdditionalInformationMetaData.E_SIGNED_DOCS;
import static com.exigen.ren.utils.components.Components.ACCOUNT_AND_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestAccountAndCustomerESignedDocHistoryVerification extends CustomerBaseTest {
    private static final String ESIGNED_DOC = "eSigned Doc(s)";
    private static final String ELECTRONIC_CONSENT_AGREEMENT_V_1 = "Electronic Consent Agreement v.1";

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-16447"}, component = ACCOUNT_AND_CUSTOMER)
    public void testAccountAndCustomerESignedDocHistoryVerification() {

        mainApp().open();
        createDefaultIndividualCustomer();
        String customerIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultNonIndividualCustomer();
        String customerNonIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        assertSoftly(softly -> {
            MainPage.QuickSearch.search(customerIndNum);
            LOGGER.info("---=={Step 1-2}==---");
            customerIndividual.viewHistory().start();
            ViewHistoryActionTab.expandAllHistoryTable();
            softly.assertThat(ViewHistoryActionTab.additionalInfoCompareTable.getRow(1, ESIGNED_DOC)).isPresent();
            softly.assertThat(ViewHistoryActionTab.additionalInfoCompareTable.getRow(1, ESIGNED_DOC).getCell(2)).hasValue("");
            Tab.buttonCancel.click();

            LOGGER.info("---=={Step 3-4}==---");
            updateCustomer();
            softly.assertThat(ViewHistoryActionTab.additionalInfoCompareTable.getRow(1, ESIGNED_DOC).getCell(2)).hasValue(ELECTRONIC_CONSENT_AGREEMENT_V_1);

            LOGGER.info("---=={Step 5-7}==---");
            MainPage.QuickSearch.search(customerNonIndNum);
            updateCustomer();
            softly.assertThat(ViewHistoryActionTab.nonIndvAdditionalInfoCompareTable.getRow(1, ESIGNED_DOC).getCell(2)).hasValue(ELECTRONIC_CONSENT_AGREEMENT_V_1);
        });
    }

    private void updateCustomer(){
        customerIndividual.update().start();
        generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS).setValue(ELECTRONIC_CONSENT_AGREEMENT_V_1);
        NavigationPage.toSubTab(NavigationEnum.CustomerTab.RELATIONSHIP.get());
        relationshipTab.submitTab();
        customerIndividual.viewHistory().start();
        ViewHistoryActionTab.expandAllHistoryTable();
    }
}