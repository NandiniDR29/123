/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.security.ISecurity;
import com.exigen.ren.admin.modules.security.profile.actions.ProfileCreateAction;
import com.exigen.ren.admin.modules.security.profile.actions.ProfileSearchAction;
import com.exigen.ren.admin.modules.security.profile.actions.ProfileUpdateAction;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.profile.tabs.SearchProfileTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.AdminConstants;
import com.google.common.collect.ImmutableMap;

import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.*;

public abstract class Profile implements ISecurity {

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(ProfileCreateAction.class).getWorkspace();
    }

    public void create(TestData td, String agencyName) {
        create(td.adjust(TestData.makeKeyPath(GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.AGENCY_LOCATIONS.getLabel(), "Agency Name"),
                agencyName));
    }

    public ImmutableMap<String, String> createProfile(TestData td) {
        String generalProfileTab = getDefaultWorkspace().getTab(GeneralProfileTab.class).getMetaKey();
        String firstName = td.getTestData(generalProfileTab).getValue(FIRST_NAME.getLabel());
        String lastName = td.getTestData(generalProfileTab).getValue(LAST_NAME.getLabel());
        String userName = td.getTestData(generalProfileTab).getValue(USER_NAME.getLabel());
        create(td.adjust(TestData.makeKeyPath(generalProfileTab, FIRST_NAME.getLabel()), firstName)
                .adjust(TestData.makeKeyPath(generalProfileTab, LAST_NAME.getLabel()), lastName)
                .adjust(TestData.makeKeyPath(generalProfileTab, USER_NAME.getLabel()), userName));
        return ImmutableMap.of(
                "First Name", td.getTestData(generalProfileTab).getValue(FIRST_NAME.getLabel()),
                "Last Name", td.getTestData(generalProfileTab).getValue(LAST_NAME.getLabel()),
                "User Name", td.getTestData(generalProfileTab).getValue(USER_NAME.getLabel()));
    }

    public void createNonEisUser(TestData td) {
        initiate(ProfileType.valueOf(td.getValue("Channel")));
        getDefaultWorkspace().fill(td);
        GeneralProfileTab.buttonSave.click();
        LOGGER.info("Created Non-Eis User Profile " + td);
    }

    @Override
    public void initiate() {
        throw new UnsupportedOperationException("Role not supported initiate() method");
    }

    public void initiate(ProfileType channel) {
        navigate();
        SearchProfileTab.channel.setValue(channel.get());
        SearchProfileTab.buttonAddNewProfile.click();
    }

    @Override
    public ProfileUpdateAction update() {
        return AutomationContext.getAction(ProfileUpdateAction.class);
    }

    public void search(TestData td) {
        search().perform(td);
    }

    public Action search() {
        return AutomationContext.getAction(ProfileSearchAction.class);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.SECURITY);
        NavigationPage.toLeftMenuTab(NavigationEnum.SecurityLeftMenu.PROFILE);
    }


    public void update(String loginName, TestData td) {
        navigate();
        change(loginName);
        getDefaultWorkspace().fill(td);
        Tab.buttonUpdate.click();
    }

    @Override
    public TestData defaultTestData() {
        throw new UnsupportedOperationException("");
    }


    public void search(String userLogin) {
        SearchProfileTab.textBoxUserLogin.setValue(userLogin);
        SearchProfileTab.buttonSearch.click();
    }

    public void change(TestData td) {
        search(td);
        SearchProfileTab.tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
    }


    public void change(String userLogin) {
        search(userLogin);
        SearchProfileTab.tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
    }

    public void inquiry(TestData td) {
        search(td);
        SearchProfileTab.tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.FIRST_NAME).controls.links.getFirst().click();
    }
}
