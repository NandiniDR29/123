package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestNewBusinessCustomerNumberConfiguration extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20440", component = CRM_CUSTOMER)
    public void testNewBusinessCustomerNumberConfiguration() {
        mainApp().open();
        customerIndividual.createViaUI(customerIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
        LOGGER.info("REN-20440 Step 3");
        assertThat(Integer.parseInt(CustomerSummaryPage.labelCustomerNumber.getValue())).as("Customer ID should be between range: 1 to 999999").isBetween(1, 999999);
    }
}
