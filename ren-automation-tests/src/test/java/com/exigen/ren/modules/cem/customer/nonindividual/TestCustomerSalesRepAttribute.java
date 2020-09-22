package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.UsersConsts;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerSalesRepAttribute extends CustomerBaseTest {

    private static final String ERROR_SALES_REP_IS_REQUIRED = "Sales Representative is required for customer";
    private static StaticElement ERROR_SALES_REP = new StaticElement(By.id("crmForm:owner_selected_error:crmForm:ownerSelected"));
    private static StaticElement SALES_REP_VALUE = new StaticElement(By.id("crmForm:displayValue"));

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13478", component = CRM_CUSTOMER)
    public void testCustomerSalesRepAttribute() {
        mainApp().open();
        customerNonIndividual.initiate();
        LOGGER.info("TC 3");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Lead Source"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Source Description"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rating"))).isAbsent();
        });

        LOGGER.info("TC 1 Step 4");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Sales Rep"))).isPresent();
        generalTab.fillTab(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .mask(TestData.makeKeyPath(generalTab.getClass().getSimpleName(), GeneralTabMetaData.SALES_REP_WORK_QUEUE.getLabel())));
        GeneralTab.buttonTopSave.click();
        assertThat(ERROR_SALES_REP).isPresent().hasValue(ERROR_SALES_REP_IS_REQUIRED);
        GeneralTab.buttonSaveAndExit.click();
        assertThat(ERROR_SALES_REP).isPresent().hasValue(ERROR_SALES_REP_IS_REQUIRED);
        generalTab.submitTab();
        divisionsTab.submitTab();
        relationshipTab.submitTab();
        assertThat(ERROR_SALES_REP).isPresent().hasValue(ERROR_SALES_REP_IS_REQUIRED);

        LOGGER.info("TC 1 Step 5");
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).getAsset(GeneralTabMetaData.SearchInternalSalesRepMetaData.OPEN_POPUP))
                .isPresent().isEnabled().hasValue("Assign Sales Rep");

        LOGGER.info("TC 1 Step 6-7");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).fill(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(SALES_REP_VALUE).isPresent().hasValue(UsersConsts.USER_ISBA);
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).getAsset(GeneralTabMetaData.SearchInternalSalesRepMetaData.OPEN_POPUP))
                .isPresent().isEnabled().hasValue("Change");
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_REMOVE))
                .isPresent().isEnabled().hasValue("Remove");

        LOGGER.info("TC 1 Step 8");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_REMOVE).click();
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).getAsset(GeneralTabMetaData.SearchInternalSalesRepMetaData.OPEN_POPUP))
                .isPresent().isEnabled().hasValue("Assign Sales Rep");
        assertThat(SALES_REP_VALUE).isAbsent();

        LOGGER.info("TC 1 Step 9-10");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).fill(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(SALES_REP_VALUE).isPresent().hasValue(UsersConsts.USER_ISBA);
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).getAsset(GeneralTabMetaData.SearchInternalSalesRepMetaData.OPEN_POPUP))
                .isPresent().isEnabled().hasValue("Change");
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_REMOVE))
                .isPresent().isEnabled().hasValue("Remove");

        LOGGER.info("TC 1 Step 12");
        GeneralTab.buttonSaveAndExit.click();
        assertThat(CustomerSummaryPage.labelCustomerNumber).isPresent();
    }
}
