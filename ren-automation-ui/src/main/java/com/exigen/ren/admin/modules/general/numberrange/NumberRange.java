/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.numberrange;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.general.IGeneral;
import com.exigen.ren.admin.modules.general.numberrange.action.NumberRangeCreateAction;
import com.exigen.ren.admin.modules.general.numberrange.pages.NumberRangePage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;

public class NumberRange implements IGeneral {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/general/numberrange");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(NumberRangeCreateAction.class).getWorkspace();
    }

    @Override
    public void initiate() {
        navigate();
        NumberRangePage.buttonAddRange.click();
    }

    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.GENERAL);
        NavigationPage.toLeftMenuTab(NavigationEnum.GeneralTabLeftMenu.NUMBER_OF_RANGES);
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.NUMBER_RANGE);
        NumberRangePage.buttonSubmitRange.click();
        LOGGER.info("Created Number Range " + entity);
    }

    public void search(TestData td) {
        NumberRangePage.search(td);
    }


    public void eliminate(int rowNumber) {
        NumberRangePage.tableSearchResults.getRow(rowNumber).getCell(NumberRangePage.tableSearchResults.getColumnsCount()).controls.links.get(ActionConstants.ELIMINATE).click();
        Page.dialogConfirmation.confirm();

    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
