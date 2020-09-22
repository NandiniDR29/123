package com.exigen.ren.utils;

import com.exigen.ipb.eisa.base.application.ApplicationFactory;
import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.security.profile.Profile;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.role.Role;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.pages.RolePage;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.main.enums.AdminConstants;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;

public class AdminActionsHelper {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AdminActionsHelper.class);

    public static User createUserAndRelogin(Profile profileType) {
        TestData td = createUserAndReloginThenReturnTD(profileType);

        String userLogin = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        return new User(userLogin, userPassword);
    }

    public static TestData createUserAndRelogin(Profile profileType, TestData td) {
        ApplicationFactory.getInstance().getAdminApplication().reopen();

        String userLogin = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileType.create(td);

        ApplicationFactory.getInstance().getMainApplication().reopen(userLogin, userPassword);
        LOGGER.info("Logging to application with username " + userLogin);
        return td;
    }

    public static TestData createUserAndReloginThenReturnTD(Profile profileType) {
        return createUserAndRelogin(profileType, profileType.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY));
    }

    public static User createUserWithSpecificRole(String roleName, Profile profileType) {
        TestData td = createUserAndRelogin(profileType, profileType.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(
                        GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        String userLogin = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        return new User(userLogin, userPassword);
    }

    public static User createCorporateUserWithOutSpecificRole(String roleToExclude) {
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + roleToExclude)), roleCorporate);

        TestData td = createUserAndRelogin(profileCorporate, profileCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(
                        GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        String userLogin = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        String userPassword = td.getValue(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        return new User(userLogin, userPassword);
    }

    public static String searchOrCreateRole(TestData td, Role roleType) {
        ApplicationFactory.getInstance().getAdminApplication().reopen();

        String roleName = td.getValue(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        TestData tdSecurityRole = roleType.defaultTestData();
        roleType.navigate();

        roleType.search(tdSecurityRole.getTestData("SearchData", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(RolePage.RoleSearch.class.getSimpleName(), RolePage.RoleSearch.ROLE_NAME.getLabel()),
                        roleName));

        if (!RolePage.tableRolesSearchResult.getRow(AdminConstants.AdminRolesSearchResultTable.ROLE_NAME,
                roleName).isPresent()) {
            roleType.create(td);
        }
        return roleName;
    }

    public static void updateRole(TestData td, String roleName, Role roleType) {

        TestData tdSecurityRole = roleType.defaultTestData();
        roleType.navigate();
        roleType.search(tdSecurityRole.getTestData("SearchData", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath("RoleSearch", RolePage.RoleSearch.ROLE_NAME.getLabel()), roleName));
        roleCorporate.update(td);
    }

    public static User createUserWithPrivilege(ImmutableList<String> privilegeList) {

        String roleForUpdate = AdminActionsHelper.searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), privilegeList), roleCorporate);

        return AdminActionsHelper.createUserWithSpecificRole(roleForUpdate, profileCorporate);
    }
}
