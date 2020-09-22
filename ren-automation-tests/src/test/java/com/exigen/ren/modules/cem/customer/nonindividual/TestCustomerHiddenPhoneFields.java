package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CustomerConstants.*;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerHiddenPhoneFields extends CustomerBaseTest {

    private final String phoneDetailsElementGeneralLocatorPattern = "//div[@id='crmForm:phonePanelGroup']//*[normalize-space(text())='%s']";
    private final String phoneDetailsRelationshipElementLocatorPattern = "//div[@id='crmForm:phonePanelGroup0']//*[normalize-space(text())='%s']";
    private final ImmutableList<String> phoneList = ImmutableList.of("Consent Status", "Consent Date", "Reason", "Temporary", "Effective From", "Effective To", "Duration");
    private final ImmutableList<String> methodsList = ImmutableList.of("Address", "Email", "Phone");

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-11310", "REN-11346"}, component = CRM_CUSTOMER)
    public void testCustomerHiddenPhoneFields() {
        mainApp().open();
        customerNonIndividual.initiate();

        assertSoftly(softly -> {
            // Asserts for REN-11346/#1
            softly.assertThat(GeneralTab.comboBoxSelectContactMethod)
                    .doesNotContainOption(CHAT)
                    .doesNotContainOption(SOCIAL_NET)
                    .doesNotContainOption(WEB_URL)
                    .containsAllOptions(methodsList);

            GeneralTab.comboBoxSelectContactMethod.setValue(CustomerConstants.PHONE);
            GeneralTab.buttonAddContact.click();
            assertThatFieldsAreHidden(softly, phoneList, phoneDetailsElementGeneralLocatorPattern);
        });

        NavigationPage.toSubTab(NavigationEnum.CustomerTab.RELATIONSHIP.get());
        RelationshipTab.buttonAddRelationship.click();

        assertSoftly(softly -> {
            // Asserts for REN-11346/#2
            softly.assertThat(RelationshipTab.comboBoxSelectContactMethod)
                .doesNotContainOption(CHAT)
                .doesNotContainOption(SOCIAL_NET)
                .doesNotContainOption(WEB_URL)
                .containsAllOptions(methodsList);

            customerNonIndividual.getDefaultWorkspace().getTab(RelationshipTab.class).getAssetList().fill(tdSpecific().getTestData("TestData_RelationshipWithPhoneDetails"));
            assertThatFieldsAreHidden(softly, phoneList, phoneDetailsRelationshipElementLocatorPattern);
        });
    }

    private void assertThatFieldsAreHidden(ETCSCoreSoftAssertions softly, ImmutableList<String> fieldsList, String fieldsLocatorPattern) {
        fieldsList.forEach(fieldName -> softly.assertThat(new StaticElement(ByT.xpath(fieldsLocatorPattern).format(fieldName)))
                .as(String.format("Field '%s' isn't hidden", fieldName))
                .isAbsent());
    }
}