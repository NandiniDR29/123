package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.rest.RestHelper;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.exigen.ren.rest.customer.model.ExtensionFieldsModel;
import com.exigen.ren.utils.AdminActionsHelper;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.Tab.cancelClickAndCloseDialog;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.AdminActionsHelper.updateRole;
import static com.exigen.ren.utils.components.Components.ACCOUNT_AND_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddWtwoMailingPreferenceFieldsAndCustomerUiLogic extends CustomerBaseTest implements CustomerContext {

    private static final String GROUP = "Group";
    private static final String INDIVIDUAL = "Individual";

    private static final String GROUP_REST = "GROUP";
    private static final String INDIVIDUAL_REST = "INDIVIDUAL";

    private CustomerRestService restCustomer = RESTServiceType.CUSTOMERS.get();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37317", component = ACCOUNT_AND_CUSTOMER)
    public void testAttributesVerificationNonInd() {
        ImmutableList<String> values = ImmutableList.of("", GROUP, INDIVIDUAL);
        LOGGER.info("REN-37317 Preconditions");
        mainApp().open();
        customerNonIndividual.initiate();
        LOGGER.info("REN-37317 STEP#1 Verify following attributes in Business Information section");
        assertSoftly(softly -> {
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_CARDS_TO)).isPresent().isRequired(false).hasOptions(values).hasValue("");
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_W_2_TO)).isPresent().isEnabled().isRequired(false).hasOptions(values).hasValue("");
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.ELECTRONIC_SSA_FILING)).isPresent().isEnabled().isRequired(false).hasValue("");
        });

        LOGGER.info("REN-37317 STEP#2 Fill all required information for Customer");
        customerNonIndividual.getDefaultWorkspace().fillFrom(getDefaultCustomerNonIndividualTestData()
                .mask(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MAIL_CARDS_TO.getLabel()))
                .mask(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MAIL_W_2_TO.getLabel()))
                .mask(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.ELECTRONIC_SSA_FILING.getLabel())), GeneralTab.class);

        LOGGER.info("REN-37317 STEP#4-5 Set and verify following values for attributes");
        updateCustomerRequiredValues(INDIVIDUAL, INDIVIDUAL, VALUE_NO);

        LOGGER.info("REN-37317 STEP#6-7 Set and verify following values for attributes");
        updateCustomerRequiredValues(GROUP, GROUP, VALUE_YES);

        LOGGER.info("REN-37317 STEP#8-9 Set following values for attributes");
        updateCustomerRequiredValues("", "", VALUE_NO);
    }

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37320", component = ACCOUNT_AND_CUSTOMER)
    public void testAttributesVerificationNonIndPrivileges() {
        LOGGER.info("REN-37320 STEP#1 Set up User without privileges: Customer:mailCardsTo:View, Customer:mailCardsTo:Edit and check attributes");
        adminApp().open();
        String roleName = AdminActionsHelper.searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.CUSTOMER_MAIL_CARDS_TO_VIEW.get(), "EXCLUDE " + Privilege.CUSTOMER_MAIL_CARDS_TO_EDIT.get())), roleCorporate);
        User user = AdminActionsHelper.createUserWithSpecificRole(roleName, profileCorporate);
        customerNonIndividual.initiate();
        assertSoftly(softly -> {
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_CARDS_TO)).isAbsent();
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_W_2_TO)).isAbsent();
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.ELECTRONIC_SSA_FILING)).isAbsent();
            cancelClickAndCloseDialog();
        });
        LOGGER.info("REN-37320 STEP#2 Update User privileges: user has only Customer:mailCardsTo:View and check attributes");
        adminApp().switchPanel();
        AdminActionsHelper.updateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .mask(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel()))
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel())
                        , ImmutableList.of("ALL", "EXCLUDE " + Privilege.CUSTOMER_MAIL_CARDS_TO_EDIT.get())), roleName, roleCorporate);
        mainApp().reopen(user.getLogin(), user.getPassword());
        customerNonIndividual.initiate();
        assertSoftly(softly -> {
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_CARDS_TO)).isPresent().isDisabled();
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_W_2_TO)).isPresent().isDisabled();
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.ELECTRONIC_SSA_FILING)).isPresent().isDisabled();
            cancelClickAndCloseDialog();
        });

        LOGGER.info("REN-37320 STEP#3 Update User privileges: user has Customer:mailCardsTo:View and Customer:mailCardsTo:Edit then check attributes");
        adminApp().switchPanel();
        updateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .mask(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel()))
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL")), roleName, roleCorporate);
        mainApp().reopen(user.getLogin(), user.getPassword());
        customerNonIndividual.initiate();
        assertSoftly(softly -> {
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_CARDS_TO)).isPresent().isEnabled();
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.MAIL_W_2_TO)).isPresent().isEnabled();
            softly.assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.ELECTRONIC_SSA_FILING)).isPresent().isEnabled();
            cancelClickAndCloseDialog();
        });
    }

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37436", component = ACCOUNT_AND_CUSTOMER)
    public void testAttributesVerificationApi() {

        LOGGER.info("REN-37436: STEP#1 Create Customer 1 with all 3 attributes set to null.");
        CustomerModel customerNumber1 = createCustomerAndVerifyRequiredValues(null, null, null);

        LOGGER.info(String.format("REN-37436: STEP#2 Create Customer 2 with following values for attributes %s, %s, %s", INDIVIDUAL_REST, INDIVIDUAL_REST, VALUE_YES));
        CustomerModel customerNumber2 = createCustomerAndVerifyRequiredValues(INDIVIDUAL_REST, INDIVIDUAL_REST, VALUE_YES);

        LOGGER.info(String.format("REN-37436: STEP#3 Create Customer 2 with following values for attributes %s, %s, %s", GROUP_REST, GROUP_REST, VALUE_NO));
        CustomerModel customerNumber3 = createCustomerAndVerifyRequiredValues(GROUP_REST, GROUP_REST, VALUE_NO);
        List<String> customerNumbers = new ArrayList<>(Arrays.asList(customerNumber1.getCustomerNumber(), customerNumber2.getCustomerNumber(), customerNumber3.getCustomerNumber()));

        LOGGER.info("REN-37436: STEP#5 Run endpoint with customerNumbers");
        ResponseContainer<List<CustomerModel>> customerDetailsBundle = restCustomer.getCustomerDetails(customerNumbers);

        LOGGER.info("REN-37436: STEP#5 Verify attributes have values as defined for a Customer in Step 1");
        assertSoftly(softly -> {
            softly.assertThat(customerDetailsBundle.getModel().get(0).getExtensionFields().getMailCardsTo()).isEqualTo(null);
            softly.assertThat(customerDetailsBundle.getModel().get(0).getExtensionFields().getMailW2To()).isEqualTo(null);
            softly.assertThat(customerDetailsBundle.getModel().get(0).getExtensionFields().getElectronicSSAFiling()).isEqualTo(null);});

        LOGGER.info("REN-37436: STEP#5 Verify attributes have values as defined for a Customer in Step 2");
        assertSoftly(softly -> {
            softly.assertThat(customerDetailsBundle.getModel().get(1).getExtensionFields().getMailCardsTo()).isEqualTo(INDIVIDUAL_REST);
            softly.assertThat(customerDetailsBundle.getModel().get(1).getExtensionFields().getMailW2To()).isEqualTo(INDIVIDUAL_REST);
            softly.assertThat(customerDetailsBundle.getModel().get(1).getExtensionFields().getElectronicSSAFiling()).isEqualTo(true);});

        LOGGER.info("REN-37436: STEP#5 Verify attributes have values as defined for a Customer in Step 3");
        assertSoftly(softly -> {
            softly.assertThat(customerDetailsBundle.getModel().get(2).getExtensionFields().getMailCardsTo()).isEqualTo(GROUP_REST);
            softly.assertThat(customerDetailsBundle.getModel().get(2).getExtensionFields().getMailW2To()).isEqualTo(GROUP_REST);
            softly.assertThat(customerDetailsBundle.getModel().get(2).getExtensionFields().getElectronicSSAFiling()).isEqualTo(false);});


        LOGGER.info("REN-37436: STEP#6 Update Customer1");
        updateCustomer(customerNumber1, INDIVIDUAL_REST, INDIVIDUAL_REST, VALUE_YES);

        LOGGER.info("REN-37436: STEP#6 Update Customer2");
        updateCustomer(customerNumber2, GROUP_REST, GROUP_REST, VALUE_YES);

        LOGGER.info("REN-37436: STEP#6 Update Customer3");
        updateCustomer(customerNumber3, null, null, null);

        LOGGER.info("REN-37436: STEP#7 Run endpoint with customerNumbers");
        ResponseContainer<List<CustomerModel>> customerBundleAfterUpdate = restCustomer.getCustomerDetails(customerNumbers);

        LOGGER.info("REN-37436: STEP#7 Verify attributes have values as defined for a Customer in Step 6");
        assertSoftly(softly -> {
            softly.assertThat(customerBundleAfterUpdate.getModel().get(0).getExtensionFields().getMailCardsTo()).isEqualTo(INDIVIDUAL_REST);
            softly.assertThat(customerBundleAfterUpdate.getModel().get(0).getExtensionFields().getMailW2To()).isEqualTo(INDIVIDUAL_REST);
            softly.assertThat(customerBundleAfterUpdate.getModel().get(0).getExtensionFields().getElectronicSSAFiling()).isEqualTo(true);});

        LOGGER.info("REN-37436: STEP#7 Verify attributes have values as defined for a Customer in Step 6");
        assertSoftly(softly -> {
            softly.assertThat(customerBundleAfterUpdate.getModel().get(1).getExtensionFields().getMailCardsTo()).isEqualTo(GROUP_REST);
            softly.assertThat(customerBundleAfterUpdate.getModel().get(1).getExtensionFields().getMailW2To()).isEqualTo(GROUP_REST);
            softly.assertThat(customerBundleAfterUpdate.getModel().get(1).getExtensionFields().getElectronicSSAFiling()).isEqualTo(true);});

        LOGGER.info("REN-37436: STEP#7 Verify attributes have values as defined for a Customer in Step 6");
        assertSoftly(softly -> {
            softly.assertThat(customerBundleAfterUpdate.getModel().get(2).getExtensionFields().getMailCardsTo()).isEqualTo(null);
            softly.assertThat(customerBundleAfterUpdate.getModel().get(2).getExtensionFields().getMailW2To()).isEqualTo(null);
            softly.assertThat(customerBundleAfterUpdate.getModel().get(2).getExtensionFields().getElectronicSSAFiling()).isEqualTo(null);});
    }

    private void updateCustomer(CustomerModel customer, String mailCardsTo, String mailW2To, String electronicSsaFiling) {
        ExtensionFieldsModel extensionFieldsModel = new ExtensionFieldsModel();
        extensionFieldsModel.setMailCardsTo(mailCardsTo);
        extensionFieldsModel.setMailW2To(mailW2To);
        extensionFieldsModel.setElectronicSSAFiling(RestHelper.convertStringDataToRestBoolean(electronicSsaFiling));
        customer.setExtensionFields(extensionFieldsModel);
        CustomerModel customerUpdated = restCustomer.putCustomer(customer, customer.getCustomerNumber());
        verifyRequiredValuesApiSingle(customerUpdated, mailCardsTo, mailW2To, electronicSsaFiling);
    }

    private CustomerModel createCustomerAndVerifyRequiredValues(String mailCardsTo, String mailW2To, String electronicSsaFiling) {
        LOGGER.info("REN-37436: Create Customer via REST");
        CustomerModel customer = restCustomer.postCustomers(getDefaultCustomerNonIndividualTestData()
                .adjust(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MAIL_CARDS_TO.getLabel()), mailCardsTo)
                .adjust(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MAIL_W_2_TO.getLabel()), mailW2To)
                .adjust(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.ELECTRONIC_SSA_FILING.getLabel()), electronicSsaFiling));

        LOGGER.info("REN-37436: Verify values after create Customer");
        verifyRequiredValuesApiSingle(customer, mailCardsTo, mailW2To, electronicSsaFiling);

        LOGGER.info("REN-37436: Get Customer via REST");
        ResponseContainer<CustomerModel> getResponseContainer = restCustomer.getCustomersItem(customer.getCustomerNumber());

        LOGGER.info("REN-37436: Verify values after get request Customer");
        verifyRequiredValuesApiSingle(getResponseContainer.getModel(), mailCardsTo, mailW2To, electronicSsaFiling);
        return customer;
    }

    private void verifyRequiredValuesApiSingle(CustomerModel customer, String mailCardsTo, String mailW2To, String electronicSsaFiling) {
        assertSoftly(softly -> {
            softly.assertThat(customer.getExtensionFields().getMailCardsTo()).isEqualTo(mailCardsTo);
            softly.assertThat(customer.getExtensionFields().getMailW2To()).isEqualTo(mailW2To);
            softly.assertThat(customer.getExtensionFields().getElectronicSSAFiling()).isEqualTo(RestHelper.convertStringDataToRestBoolean(electronicSsaFiling));
        });
    }

    private void updateCustomerRequiredValues(String mailCardsTo, String mailW2To, String electronicSsaFiling) {
        customerNonIndividual.update().perform(tdCustomerNonIndividual.getTestData("TestCustomerUpdate", "TestData")
                .adjust(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MAIL_CARDS_TO.getLabel()), mailCardsTo)
                .adjust(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MAIL_W_2_TO.getLabel()), mailW2To)
                .adjust(makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.ELECTRONIC_SSA_FILING.getLabel()), electronicSsaFiling));
    }

}
