package com.exigen.ren.modules.cem.customer;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.admin.modules.general.dblookups.DBLookupsContext;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.pages.RolePage;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDITIONAL_INFORMATION;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AdditionalInformationMetaData.E_SIGNED_DOCS;
import static com.exigen.ren.utils.components.Components.ACCOUNT_AND_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestAccountAndCustomerAttributesVerification extends CustomerBaseTest implements RoleContext, ProfileContext, CustomerContext, DBLookupsContext {

    private static ImmutableList<String> listValues = ImmutableList.of("Terms and Conditions Agreement v.1", "Electronic Consent Agreement v.1");
    private static ImmutableList<String> listValuesWithoutTermsAndConditions = ImmutableList.of("Electronic Consent Agreement v.1");

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-14196"}, component = ACCOUNT_AND_CUSTOMER)
    public void testAccountAndCustomerAttributesVerificationWithViewingPrivilege() {

        adminApp().open();
        TestData tdCorporateRole = roleCorporate.defaultTestData();
        TestData tdSecurityProfile = profileCorporate.defaultTestData();
        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL", "EXCLUDE " + Privilege.CUSTOMER_E_SIGNED_DOCS_EDIT.get())));

        String userLoginWithViewing = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPasswordWithViewing = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));
        adminApp().close();

        LOGGER.info("---=={Step 1}==---");
        mainApp().open(userLoginWithViewing, userPasswordWithViewing);

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 2-3}==---");
            initiateCreateIndividualAndFillToTab(getDefaultCustomerIndividualTestData(), GeneralTab.class, false);
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).isPresent().isDisabled();
            Tab.buttonCancel.click();
            Page.dialogConfirmation.confirm();

            LOGGER.info("---=={Step 4}==---");
            initiateCreateNonIndividualAndFillToTab(getDefaultCustomerNonIndividualTestData(), GeneralTab.class, false);
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).isPresent().isDisabled();
            Tab.buttonCancel.click();
            Page.dialogConfirmation.confirm();

            LOGGER.info("---=={Step 5}==---");
            adminApp().open();
            roleCorporate.navigate();
            roleCorporate.search(roleCorporate.defaultTestData().getTestData("SearchData", DEFAULT_TEST_DATA_KEY )
                    .adjust(TestData.makeKeyPath("RoleSearch", RolePage.RoleSearch.ROLE_NAME.getLabel()), roleName));

            roleCorporate.update(tdCorporateRole.getTestData(DATA_GATHER, "TestData_NonSecureUser").adjust(TestData.makeKeyPath(
                    GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL",
                    "EXCLUDE " + Privilege.CUSTOMER_E_SIGNED_DOCS_VIEW.get(),
                    "EXCLUDE " + Privilege.CUSTOMER_E_SIGNED_DOCS_EDIT.get())));

            LOGGER.info("---=={Step 6}==---");
            mainApp().reopen(userLoginWithViewing, userPasswordWithViewing);
            initiateCreateIndividualAndFillToTab(getDefaultCustomerIndividualTestData(), GeneralTab.class, false);
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).isAbsent();
            Tab.buttonCancel.click();
            Page.dialogConfirmation.confirm();

            initiateCreateNonIndividualAndFillToTab(getDefaultCustomerNonIndividualTestData(), GeneralTab.class, false);
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).isAbsent();
            Tab.buttonCancel.click();
            Page.dialogConfirmation.confirm();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-14239"}, component = ACCOUNT_AND_CUSTOMER)
    public void testAccountAndCustomerAttributesVerificationWithEditingPrivilege() {

        adminApp().open();
        TestData tdCorporateRole = roleCorporate.defaultTestData();
        TestData tdSecurityProfile = profileCorporate.defaultTestData();
        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL", "EXCLUDE " + Privilege.CUSTOMER_E_SIGNED_DOCS_VIEW.get())));

        String userLogin = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));
        adminApp().close();

        LOGGER.info("---=={Step 1}==---");
        mainApp().open(userLogin, userPassword);

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 2-6}==---");
            initiateCreateIndividualAndFillToTab(getDefaultCustomerIndividualTestData(), GeneralTab.class, true);
            verifyAndSubmitCustomer(softly);
            String customerIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();

            initiateCreateNonIndividualAndFillToTab(getDefaultCustomerNonIndividualTestData(), GeneralTab.class, true);
            verifyAndSubmitCustomer(softly);
            String customerNonIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();

            LOGGER.info("---=={Step 13}==---");
            adminApp().open();
            roleCorporate.navigate();
            roleCorporate.search(roleCorporate.defaultTestData().getTestData("SearchData", DEFAULT_TEST_DATA_KEY )
                    .adjust(TestData.makeKeyPath("RoleSearch", RolePage.RoleSearch.ROLE_NAME.getLabel()), roleName));
            roleCorporate.update(tdCorporateRole.getTestData(DATA_GATHER, "TestData_NonSecureUser").adjust(TestData.makeKeyPath(
                    GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL",
                    "EXCLUDE " + Privilege.CUSTOMER_E_SIGNED_DOCS_EDIT.get())));
            adminApp().close();

            mainApp().open(userLogin, userPassword);
            MainPage.QuickSearch.search(customerIndNum);
            customerIndividual.update().start();
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).isPresent().isDisabled().hasValue(listValuesWithoutTermsAndConditions);
            Tab.buttonSaveAndExit.click();

            MainPage.QuickSearch.search(customerNonIndNum);
            customerNonIndividual.update().start();
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).isPresent().isDisabled().hasValue(listValuesWithoutTermsAndConditions);
            Tab.buttonSaveAndExit.click();
        });
    }

    private void verifyAndSubmitCustomer(ETCSCoreSoftAssertions softly){
        softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS)).hasOptions(listValues);

        generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS).setValue(listValuesWithoutTermsAndConditions);
        softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(E_SIGNED_DOCS))
                .isPresent()
                .hasValue(listValuesWithoutTermsAndConditions);
        generalTab.submitTab();
        NavigationPage.toSubTab(NavigationEnum.CustomerTab.RELATIONSHIP.get());
        relationshipTab.submitTab();
    }
}