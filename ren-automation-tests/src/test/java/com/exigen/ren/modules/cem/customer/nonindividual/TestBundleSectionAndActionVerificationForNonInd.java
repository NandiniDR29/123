package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.buttonAddBundle;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.tableBundles;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBundleSectionAndActionVerificationForNonInd extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23821", component = CRM_CUSTOMER)
    public void testBundleSectionAndActionVerificationForNonInd() {
        LOGGER.info("User creation without Bundle Create Role ");
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.BUNDLE_CREATE.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);
        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tableBundles).isAbsent();
            softly.assertThat(buttonAddBundle).isAbsent();
        });
    }
}