package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceAccountModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.PhoneDetailsMetaData.PHONE_NUMBER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUploadNewNonIndCustomerWithOutRelationshipData extends SalesforceBaseTest implements CustomerContext {

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15163", component = INTEGRATION)
    public void testUploadNewNonIndCustomerWithOutRelationshipData() {
        LOGGER.info("General Preconditions");
        String nameLegal = RandomStringUtils.randomAlphabetic(10);

        mainApp().open();
        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), NAME_LEGAL.getLabel()), nameLegal).resolveLinks());

        LOGGER.info("Steps#1, 2, 3 execution");
        String salesForceID = getSalesforceAccountId();
        customerNonIndividual.update().perform(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks());

        ResponseContainer<SalesforceAccountModel> response = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && !predicate.getModel().getName().equals(nameLegal),
                () -> salesforceService.getAccount(salesForceID),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(response.getResponse().getStatus()).isEqualTo(200);

        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getName()).isEqualTo(tdSpecific().getValue(DEFAULT_TEST_DATA_KEY, generalTab.getMetaKey(), NAME_LEGAL.getLabel()));
            softly.assertThat(response.getModel().getSic()).isEqualTo(tdSpecific().getValue(DEFAULT_TEST_DATA_KEY, generalTab.getMetaKey(), SIC_CODE.getLabel()));
            softly.assertThat(response.getModel().getSicDesc()).isEqualTo(tdSpecific().getValue(DEFAULT_TEST_DATA_KEY, generalTab.getMetaKey(), SIC_DESCRIPTION.getLabel()));
            softly.assertThat(response.getModel().getTaxId()).isEqualTo(tdSpecific().getValue(DEFAULT_TEST_DATA_KEY, generalTab.getMetaKey(), EIN.getLabel()));
            softly.assertThat(response.getModel().getNumberOfEmployees()).isEqualTo(tdSpecific().getValue(DEFAULT_TEST_DATA_KEY, generalTab.getMetaKey(), NUMBER_OF_EMPLOYEES.getLabel()));
            softly.assertThat(response.getModel().getPhone()).isEqualTo(tdSpecific().getValue(DEFAULT_TEST_DATA_KEY, generalTab.getMetaKey(), PHONE_DETAILS.getLabel(), PHONE_NUMBER.getLabel()));
        });
    }
}
