package com.exigen.ren.modules.platform.admin;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.SecurityProfileSubTabMenu.AUTHORITY_LEVELS;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestUnderwritingAuthorityLevelForUsers extends BaseTest implements ProfileContext {

    private static final String GET_AUTH_LEVEL = "select auth_level from S_AssignedAuthorityLevel where type ='UNDRW' AND userId = (SELECT id FROM S_PRINCIPAL WHERE name ='ipbsys')";
    private static final List<String> AUTHORITY_LEVEL_VALUES = ImmutableList.of("Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6");

    @Test(groups = {PLATFORM, PLATFORM_ADMIN, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-17011", "REN-17014"}, component = Platform_Admin)
    public void testUnderwritingAuthorityLevelForUsers() {
        LOGGER.info("Test scenario for REN-17011");
        assertThat(DBService.get().getRow(GET_AUTH_LEVEL).get("auth_level")).isEqualTo("6");

        LOGGER.info("Test scenario for REN-17014");
        adminApp().open();

        profileCorporate.initiate();
        NavigationPage.toSubTab(AUTHORITY_LEVELS);
        authorityLevelsTab.getAssetList().getAsset(AuthorityLevelsMetaData.TYPE).setValue("Underwriting");

        assertThat(authorityLevelsTab.getAssetList().getAsset(AuthorityLevelsMetaData.LEVEL)).hasOptions(AUTHORITY_LEVEL_VALUES);
    }
}
