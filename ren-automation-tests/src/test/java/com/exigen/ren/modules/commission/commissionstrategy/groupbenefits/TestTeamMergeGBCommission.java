/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.commission.commissionstrategy.groupbenefits;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.commission.commissiontemplate.CommissionTemplateContext;
import com.exigen.ren.admin.modules.commission.commissiontemplate.metadata.CommissionTemplateMetaData;
import com.exigen.ren.admin.modules.commission.commissiontemplate.pages.CommissionTemplatePage;
import com.exigen.ren.admin.modules.commission.commissiontemplate.tabs.CommissionTemplateTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeGBCommission extends BaseTest implements GBCommissionStrategyContext, CommissionTemplateContext {

    private TestData tdCommissionTemplate = commissionTemplate.defaultTestData();

    @Test(groups = {COMMISSIONS, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23718", component = COMMISIONS_GROUPBENEFITS)
    public void testTeamMergeCommission() {
        adminApp().open();

        LOGGER.info("TEST: Create Commission Template");
        String commissionTemplateName = tdCommissionTemplate.getValue("DataGather", "TestData", CommissionTemplateTab.class.getSimpleName(),
                CommissionTemplateMetaData.AddCommissionTemplate.class.getSimpleName(), CommissionTemplateMetaData.TEMPLATE_NAME.getLabel());
        commissionTemplate.create(tdCommissionTemplate.getTestData("DataGather", "TestData"));
        commissionTemplate.search(tdCommissionTemplate.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(com.exigen.ren.admin.modules.commission.common.tabs.CommissionSearchTab.class.getSimpleName(), CommissionSearchTabMetaData.COMMISSION_TEMPLATE_NAME.getLabel()),
                commissionTemplateName));
        assertThat(CommissionTemplatePage.tableCommissionTemplate).hasRows(1);

        LOGGER.info("TEST: Edit Commission Template");
        String newCommissionTemplateName = tdCommissionTemplate.getValue("Update", "TestData", CommissionTemplateTab.class.getSimpleName(),
                CommissionTemplateMetaData.AddCommissionTemplate.class.getSimpleName(), CommissionTemplateMetaData.TEMPLATE_NAME.getLabel());
        commissionTemplate.edit().perform(tdCommissionTemplate.getTestData("Update", "TestData"), 1);
        commissionTemplate.search(tdCommissionTemplate.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(com.exigen.ren.admin.modules.commission.common.tabs.CommissionSearchTab.class.getSimpleName(), CommissionSearchTabMetaData.COMMISSION_TEMPLATE_NAME.getLabel()),
                newCommissionTemplateName));
        assertThat(CommissionTemplatePage.tableCommissionTemplate).hasRows(1);

        LOGGER.info("TEST: Delete Commission Template");
        commissionTemplate.delete().perform(1);
        commissionTemplate.search(tdCommissionTemplate.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(com.exigen.ren.admin.modules.commission.common.tabs.CommissionSearchTab.class.getSimpleName(), CommissionSearchTabMetaData.COMMISSION_TEMPLATE_NAME.getLabel()),
                newCommissionTemplateName));
        assertThat(CommissionTemplatePage.tableCommissionTemplate).isAbsent();

        LOGGER.info("TEST: Create Commission Strategy");
        gbCommissionStrategy.create(tdSpecific().getTestData("TestData"));
        //TODO(vmarkouski): Workaround until error message appears [If such strategy already exists (which is active at the same period as new one) error message should appear -EISDEV-162239]
        if (GBCommissionStrategyTab.buttonSave.isPresent()) {
            GBCommissionStrategyTab.navButtonCancel.click();
        }
        gbCommissionStrategy.search(tdSpecific().getTestData("TestData_Search"));
        assertThat(CommissionPage.tableCommissionStrategies).hasRows(1);

        LOGGER.info("TEST: Edit Commission Strategy");
        gbCommissionStrategy.edit().perform(tdSpecific().getTestData("TestData_Update"), "GB_AC - Group Accident");
        CommissionPage.search(tdSpecific().getTestData("TestData_Search"));
        gbCommissionStrategy.edit().start("GB_AC - Group Accident");
        assertThat((AssetList) new GBCommissionStrategyTab().getAssetList()).hasPartialValue(tdSpecific().getTestData("TestData_Update", GBCommissionStrategyTab.class.getSimpleName()));
        gbCommissionStrategy.edit().submit();

        LOGGER.info("TEST: Expire Commission Strategy");
        CommissionPage.search(tdSpecific().getTestData("TestData_Search"));
        gbCommissionStrategy.expire().perform();
        CommissionPage.search(tdSpecific().getTestData("TestData_Search"));
        assertThat(CommissionPage.tableCommissionStrategies).isAbsent();
    }
}
