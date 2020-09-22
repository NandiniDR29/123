/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.groupsinformation;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.admin.modules.cem.groupsinformation.metadata.GroupsInformationMetaData;
import com.exigen.ren.admin.modules.cem.groupsinformation.pages.GroupInformationPage;
import com.exigen.ren.admin.modules.cem.groupsinformation.tabs.GroupsInformationTab;
import com.exigen.ren.main.enums.CEMConstants;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGroupsInformationAddUpdate extends CemBaseTest {

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24823", component = CRM_CUSTOMER)
    public void testGroupsInformationAddUpdate() {
        TestData tdGroupsInformation = groupsInformation.getDefaultTestData();

        adminApp().open();

        LOGGER.info("TEST: Create Update New Major/Large Account");
        String groupId = tdGroupsInformation.getValue("GroupsInformation", "TestData",
                GroupsInformationTab.class.getSimpleName(),
                GroupsInformationMetaData.GROUP_ID.getLabel());

        groupsInformation.create(tdGroupsInformation.getTestData("GroupsInformation", "TestData"));

        groupsInformation.search(tdGroupsInformation.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.GROUP_ID.getLabel()), groupId));

        tdGroupsInformation.getValue("GroupsInformation", "TestData", GroupsInformationTab.class.getSimpleName(),
                GroupsInformationMetaData.GROUP_TYPE.getLabel());

        LOGGER.info("TEST: Update Major/Large Account");
        groupsInformation.editGroupsInformation().perform(tdGroupsInformation.getTestData("GroupsInformationUpdate", "TestData").adjust(TestData.makeKeyPath(
                GroupsInformationTab.class.getSimpleName(), GroupsInformationMetaData.GROUP_ID.getLabel()), groupId), 1);

        groupsInformation.search(tdGroupsInformation.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.GROUP_ID.getLabel()), groupId));

        assertThat(GroupInformationPage.tableGroupsInformation.getRow(1).getCell(CEMConstants.CEMGroupsInformationTable.GROUP_TYPE)).hasValue(tdGroupsInformation.getTestData(
                "GroupsInformationUpdate", "TestData", GroupsInformationTab.class.getSimpleName())
                .getValue(GroupsInformationMetaData.GROUP_TYPE.getLabel()));

        assertThat(GroupInformationPage.tableGroupsInformation.getRow(1)).isPresent();
    }
}
