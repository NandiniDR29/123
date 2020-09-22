/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.COVERAGE_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.ADD_COVERAGE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN_COMBOBOX;

public class PlanDefinitionTab extends DefaultTab {

    public static final TableExtended<TableConstants.Plans> tableCoverageDefinition = new TableExtended<>("Plans", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPreconfigGroupCoverageDefinition']/div/table"));

    public PlanDefinitionTab() {
        super(PlanDefinitionTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void addSection(int index, int size) {
            }

            @Override
            protected void selectSection(int index) {
            }

            @Override
            public void fill(TestData td) {
                if (td.containsKey(name)) {
                    fillSection(td);
                }
            }

            private void fillSection(TestData td) {
                for (TestData plan : td.getTestDataList(name)) {
                    if (plan.containsKey("PlanKey") && plan.containsKey("CoverageKey") && tableCoverageDefinition.isPresent()) {
                        tableCoverageDefinition.getRow(ImmutableMap.of(TableConstants.Plans.PLAN.getName(), plan.getValue("PlanKey"), COVERAGE_NAME.getName(), plan.getValue("CoverageKey")))
                                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                    }
                    else if (plan.containsKey("PlanKey") && tableCoverageDefinition.isPresent()) {
                        tableCoverageDefinition.getRow(TableConstants.Plans.PLAN.getName(), plan.getValue("PlanKey")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                    }
                    else if (plan.containsKey("CoverageKey") && tableCoverageDefinition.isPresent()) {
                        tableCoverageDefinition.findRow(COVERAGE_NAME.getName(), plan.getValue("CoverageKey")).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(plan);
                    setValue(list);
                }
            }

        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName(), Waiters.AJAX.then(Waiters.SLEEP(1000)).then(Waiters.AJAX));
        return this;
    }

    @Override
    public String getTabName() {
        return PLAN_DEFINITION.get();
    }

    @Override
    public Tab submitTab() {
        buttonNext.click(doubleWaiter);
        return this;
    }

    public void addCoverage(String planName, String coverageName) {
        LOGGER.info(String.format("Add coverage: '%s' for plan '%s'", coverageName, planName));
        getAssetList().getAsset(ADD_COVERAGE).click();
        getAssetList().getAsset(PLAN_COMBOBOX).setValue(planName);
        getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(coverageName);
    }

    public static void openAddedCoverage(String coverage) {
        tableCoverageDefinition.getRow(ImmutableMap.of(COVERAGE_NAME.getName(), coverage)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
    }
}
