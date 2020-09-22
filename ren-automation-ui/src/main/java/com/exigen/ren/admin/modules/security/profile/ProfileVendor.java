/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.profile.tabs.SearchProfileTab;

public class ProfileVendor extends Profile {
    private static final ProfileType PROFILE_TYPE = ProfileType.VENDOR;

    @Override
    public TestData defaultTestData() {
        return TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/security/profile/vendor");
    }


    @Override
    public void initiate() {
        navigate();
        SearchProfileTab.channel.setValue(PROFILE_TYPE.get());
        SearchProfileTab.buttonAddNewProfile.click();
    }

    @Override
    public void create(TestData td) {
        initiate(PROFILE_TYPE);
        getDefaultWorkspace().fill(td);
        AuthorityLevelsTab.buttonSave.click();
        LOGGER.info("Created Profile " + td.resolveLinks());
    }

    public void createNonEis(TestData td) {
        initiate(PROFILE_TYPE);
        getDefaultWorkspace().getTab(GeneralProfileTab.class).getAssetList().fill(td);
        GeneralProfileTab.buttonSave.click();
        LOGGER.info("Created Profile " + td.resolveLinks());
    }

}
