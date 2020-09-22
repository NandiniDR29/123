package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDITIONAL_INFORMATION;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AdditionalInformationMetaData.SPECIAL_CLAIM_REVIEW;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddSpecialClaimReviewAttributeforINDCustomer extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18856", component = CRM_CUSTOMER)
    public void testAddSpecialClaimReviewWithoutEditAndView() {
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.SPECIAL_CLAIM_REVIEW_EDIT.get(), "EXCLUDE " + Privilege.SPECIAL_CLAIM_REVIEW_VIEW.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);

        assertSoftly(softly -> {
            LOGGER.info("REN-18856 Test case 1");
            customerIndividual.initiate();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Special Claim Review"))).isAbsent();
            customerIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerIndividualTestData(), GeneralTab.class, RelationshipTab.class, true);
            Tab.buttonSaveAndExit.click();
            LOGGER.info("REN-18856 Test case 2");
            String customerIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();
            MainPage.QuickSearch.search(customerIndNum);
            customerIndividual.update().start();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Special Claim Review"))).isAbsent();
        });
    }

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18856", component = CRM_CUSTOMER)
    public void testAddSpecialClaimReviewWithView() {
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.SPECIAL_CLAIM_REVIEW_EDIT.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);

        customerIndividual.initiate();
        assertSoftly(softly -> {
            LOGGER.info("REN-18856 Test case 3");
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(SPECIAL_CLAIM_REVIEW)).isPresent().isDisabled();
            customerIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerIndividualTestData(), GeneralTab.class, RelationshipTab.class, true);
            Tab.buttonSaveAndExit.click();
            LOGGER.info("REN-18856 Test case 4");
            String customerIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();
            MainPage.QuickSearch.search(customerIndNum);
            customerIndividual.update().start();
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(SPECIAL_CLAIM_REVIEW)).isPresent().isDisabled();
        });
    }

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18856", component = CRM_CUSTOMER)
    public void testAddSpecialClaimReviewWithEdit() {
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.SPECIAL_CLAIM_REVIEW_VIEW.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);

        customerIndividual.initiate();
        assertSoftly(softly -> {
            LOGGER.info("REN-18856 Test case 6");
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(SPECIAL_CLAIM_REVIEW)).isPresent().isEnabled();
            customerIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerIndividualTestData(), GeneralTab.class, RelationshipTab.class, true);
            Tab.buttonSaveAndExit.click();
            LOGGER.info("REN-18856 Test case 9");
            String customerIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();
            MainPage.QuickSearch.search(customerIndNum);
            customerIndividual.update().start();
            softly.assertThat(generalTab.getAssetList().getAsset(ADDITIONAL_INFORMATION).getAsset(SPECIAL_CLAIM_REVIEW)).isPresent().isEnabled();
        });
    }
}
