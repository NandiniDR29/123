package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.SearchPage;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerSalesAccountManager extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13476", component = CRM_CUSTOMER)
    public void testCustomerSalesAccountManager() {
        mainApp().open();
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.buttonCreateCustomer.click();
        customerNonIndividual.getDefaultWorkspace().fillUpTo(tdCustomerNonIndividual.getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY), GeneralTab.class, true);
        TestData tdSpec = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY);
        generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_ACCOUNT_MANAGER).fill(tdSpec);
        assertSoftly(softly -> {
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_ACCOUNT_MANAGER)
                    .getAsset(GeneralTabMetaData.SalesAccountManagerMetaData.BUTTON_OPEN_POPUP)).isAbsent();
            softly.assertThat(new StaticElement(By.id("crmForm:accDisplayValue")))
                    .isPresent()
                    .hasValue(String.format("%s %s",
                            tdSpec.getTestData("Sales Account Manager").getValue("First Name"),
                            tdSpec.getTestData("Sales Account Manager").getValue("Last Name")));
            softly.assertThat(new Button(By.id("crmForm:removeAccountManager"))).isPresent();
        });
        Tab.buttonSaveAndExit.click();
        assertThat(CustomerSummaryPage.tableCustomers).hasRows(1);
    }
}
