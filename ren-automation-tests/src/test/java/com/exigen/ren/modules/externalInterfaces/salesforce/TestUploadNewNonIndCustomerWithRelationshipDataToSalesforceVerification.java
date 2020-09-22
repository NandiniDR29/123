package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceContactModel;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.NAME_LEGAL;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.AddressDetailsRelationshipMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.EmailDetailsRelationshipMetaData.EMAIL_ADDRESS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUploadNewNonIndCustomerWithRelationshipDataToSalesforceVerification extends SalesforceBaseTest implements CustomerContext {

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15145", component = INTEGRATION)
    public void testUploadNewNonIndCustomerWithRelationshipDataToSalesforceVerification() {
        LOGGER.info("General Preconditions");
        String nameLegal = RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);
        String lastName = RandomStringUtils.randomAlphabetic(10);

        mainApp().open();
        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), NAME_LEGAL.getLabel()), nameLegal)
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey(), FIRST_NAME.getLabel()), firstName)
                        .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey(), LAST_NAME.getLabel()), lastName).resolveLinks()));

        LOGGER.info("Steps#1, 2 execution");
        getSalesforceAccountId();
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        String relationshipCustomerId = RelationshipTab.getRelationShipCustomerReference(1);

        LOGGER.info("Steps#3, 4 verification");
        ResponseContainer<SalesforceContactModel> response = salesforceService.getContactByCustomerNumber(relationshipCustomerId);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);

        assertSoftly(softly -> {
            softly.assertThat(response.getModel().getName()).isEqualTo(String.format("%s %s", firstName, lastName));
            softly.assertThat(response.getModel().getSalutation()).isEqualTo("Dr.");
            softly.assertThat(response.getModel().getFirstName()).isEqualTo(firstName);
            softly.assertThat(response.getModel().getLastName()).isEqualTo(lastName);
            softly.assertThat(response.getModel().getEmail()).isEqualTo(tdSpecific().getValue("RelationshipWithIndividualType", EMAIl_DETAILS.getLabel(), EMAIL_ADDRESS.getLabel()));

            LOGGER.info("Step#5 verification");
            softly.assertThat(response.getModel().getBirthdate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().minusYears(25).format(DateTimeUtilsHelper.YYYY_MM_DD));

            LOGGER.info("Step#6 verification");
            softly.assertThat(response.getModel().getMailingCity()).isEqualTo(tdSpecific().getValue("Address1", CITY.getLabel()));
            softly.assertThat(response.getModel().getMailingCountry()).isEqualTo(tdSpecific().getValue("Address1", COUNTRY.getLabel()));
            softly.assertThat(response.getModel().getMailingState()).isEqualTo(tdSpecific().getValue("Address1", STATE_PROVINCE.getLabel()));
            softly.assertThat(response.getModel().getMailingStreet()).isEqualTo(tdSpecific().getValue("Address1", ADDRESS_LINE_1.getLabel()));
            softly.assertThat(response.getModel().getMailingPostalCode()).isEqualTo(tdSpecific().getValue("Address1", ZIP_POST_CODE.getLabel()));
        });
    }
}
