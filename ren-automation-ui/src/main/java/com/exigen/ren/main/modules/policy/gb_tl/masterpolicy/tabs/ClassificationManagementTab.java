/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.ClassificationManagementTabMetaData;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.COVERAGE_NAME;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.PLAN;

public class ClassificationManagementTab extends DefaultTab {

    public static final TableExtended<TableConstants.PlansAndCoverages> coveragesTable = new TableExtended<>("Plans and Coverages", By.xpath("//div[@id='policyDataGatherForm:groupCoverageDefinitionList']//table"),
            By.xpath(".//*[@id='policyDataGatherForm:groupCoverageDefinitionList_paginator_bottom']"));
    public static final StaticElement labelRate = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_GroupClassDefaultClassRatingInfoDecimalView_rate']"));

    public static TableExtended<TableConstants.CoverageRelationships> tableCoverageRelationships =
            new TableExtended<>("Classification Group Coverage Relationships",
                    By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListGroupClassGroupCoverageRelationship']//table"));
    public static TableExtended<TableConstants.ClassificationSubGroupsAndRatingColumns> tableClassificationSubGroupsAndRatingInfo =
            new TableExtended<>("Classification Sub-Groups And Rating Info",
                    By.xpath("//*[@id='policyDataGatherForm:dataGatherView_ListGroupClassGroupCoverRelationship_SubGroupsRating']/div[1]/table"),
                    By.xpath("//*[@id='policyDataGatherForm:dataGatherView_ListGroupClassGroupCoverRelationship_SubGroupsRating_paginator_bottom']"));

    public ClassificationManagementTab() {
        super(ClassificationManagementTabMetaData.class);

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
                td.getTestDataList(name).forEach(coverage -> {
                    if (coverage.containsKey(COVERAGE_NAME.getName()) && coverage.containsKey(PLAN.getName())) {
                        coveragesTable.findRow(ImmutableMap.of(COVERAGE_NAME.getName(), coverage.getValue(COVERAGE_NAME.getName()), PLAN.getName(), coverage.getValue(PLAN.getName()))).getCell(COVERAGE_NAME.getName()).click();
                    }
                    else if(coverage.containsKey(COVERAGE_NAME.getName())) {
                        coveragesTable.findRow(ImmutableMap.of(COVERAGE_NAME.getName(), coverage.getValue(COVERAGE_NAME.getName()))).getCell(COVERAGE_NAME.getName()).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(coverage);
                    setValue(list);
                });
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return CLASSIFICATION_MANAGEMENT.get();
    }

    @Override
    public Tab submitTab() {
        buttonNext.click(doubleWaiter);
        return this;
    }

    public enum ClassificationSubGroupsAndRatingColumns implements Named {
        CLASSIFICATION_SUB_GROUP_NAME("Classification Sub-Group Name"),
        CRITERIA("Criteria"),
        NUMBER_OF_PARTICIPANTS("Number of Participants"),
        TOTAL_VOLUME("Total Volume"),
        RATE("Rate");

        private String name;

        ClassificationSubGroupsAndRatingColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static void selectPlan(String planName) {
        coveragesTable.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), planName).getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
    }
}
