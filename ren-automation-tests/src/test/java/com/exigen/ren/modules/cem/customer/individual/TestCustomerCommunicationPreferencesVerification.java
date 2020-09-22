package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CustomerConstants.ADDRESS;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerAddressTypes.*;
import static com.exigen.ren.main.enums.CustomerConstants.EMAIL;
import static com.exigen.ren.main.modules.customer.metadata.DivisionsTabMetaData.PHONE_DETAILS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDRESS_DETAILS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_TYPE;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.COMMUNICATION_PREFERENCE;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EMAIl_DETAILS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EmailDetailsMetaData.EMAIL_TYPE;
import static com.exigen.ren.main.modules.customer.tabs.GeneralTab.comboBoxSelectContactMethod;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerCommunicationPreferencesVerification extends CustomerBaseTest {
    private RepeatAssetList assetListGeneralTab = generalTab.getAssetList().getAsset(ADDRESS_DETAILS);

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14698", component = CRM_CUSTOMER)
    public void testCustomerCommunicationPreferencesVerification() {
        List<String> communicationPreferencesValues = ImmutableList.of("Administrative", "Claims");

        LOGGER.info("Step #1. Individual customer creation initiation and Comm Pref field verificatoin");
        mainApp().open();
        initiateCreateIndividualAndFillToTab(getDefaultCustomerIndividualTestData(), GeneralTab.class, false);
        communicationPreferencesVerification(RESIDENCE);
        communicationPreferencesVerification(PREVIOUS);
        communicationPreferencesVerification(OTHER);

        LOGGER.info("Step #2. Communication Preference verificatoin with Mailing type");
        assetListGeneralTab.getAsset(ADDRESS_TYPE).setValue(MAILING);
        assertThat(assetListGeneralTab.getAsset(COMMUNICATION_PREFERENCE)).isPresent();
        assertThat(assetListGeneralTab.getAsset(COMMUNICATION_PREFERENCE).getValue()).isEqualTo(communicationPreferencesValues);

        LOGGER.info("Step #3. Communication Preference verificatoin in the Contact Details Table");
        generalTab.getAssetList().fill(tdSpecific().getTestData("AddressDetailsTestData"));
        assertThat(Arrays.asList(CustomerSummaryPage.tableCustomerContacts.getRow(1).getCell(CustomerConstants.CustomerContactsTable.COMMUNICATION_PREFERENCE.getName()).getHintValue().split(", ")))
                .isEqualTo(communicationPreferencesValues);

        LOGGER.info("Step #4. Communication Preference verificatoin for the new address with Mailing type");
        comboBoxSelectContactMethod.setValue(ADDRESS);
        CustomerSummaryPage.buttonAddNewContactsDetails.click();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(assetListGeneralTab.getAsset(ADDRESS_TYPE).getValue()).isEqualTo(MAILING);
            softly.assertThat(assetListGeneralTab.getAsset(COMMUNICATION_PREFERENCE)).isPresent();
            softly.assertThat(assetListGeneralTab.getAsset(COMMUNICATION_PREFERENCE).getValue()).isEmpty();
        });
        generalTab.getAssetList().fill(tdSpecific().getTestData("AddressDetailsTestData"));

        LOGGER.info("Step #5. Email Details verificatoin for the new Email Address");
        comboBoxSelectContactMethod.setValue(EMAIL);
        CustomerSummaryPage.buttonAddNewContactsDetails.click();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(generalTab.getAssetList().getAsset(EMAIl_DETAILS).getAsset(EMAIL_TYPE)).isEnabled()
                    .hasOptions(ValueConstants.EMPTY, "Personal", "Work").hasValue("Personal");
        });

        generalTab.getAssetList().fill(tdSpecific().getTestData("EmailDetailsTestData"));
        customerIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerIndividualTestData()
                        .mask(TestData.makeKeyPath(generalTab.getClass().getSimpleName(), ADDRESS_DETAILS.getLabel()))
                        .mask(TestData.makeKeyPath(generalTab.getClass().getSimpleName(), PHONE_DETAILS.getLabel())),
                GeneralTab.class, RelationshipTab.class, true);
        Tab.buttonDone.click();
        assertThat(CustomerSummaryPage.labelCustomerNumber).isPresent();
    }

    private void communicationPreferencesVerification(String addressTypeValue) {
        assetListGeneralTab.getAsset(ADDRESS_TYPE).setValue(addressTypeValue);
        assertThat(assetListGeneralTab.getAsset(COMMUNICATION_PREFERENCE)).isAbsent();
    }
}