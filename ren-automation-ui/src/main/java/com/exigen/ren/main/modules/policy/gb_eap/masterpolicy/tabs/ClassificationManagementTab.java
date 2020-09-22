/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.ClassificationManagementTabMetaData;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT;

public class ClassificationManagementTab extends DefaultTab {

    public static TableExtended<TableConstants.CoverageRelationships> tableCoverageRelationships =
            new TableExtended<>("Coverage Relationships", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListGroupClassGroupCoverageRelationship']//table"));
    public static TableExtended<TableConstants.PlansAndCoverages> tablePlansAndCoverages =
            new TableExtended<>("Plans And Coverages", By.xpath("//*[@id='policyDataGatherForm:groupCoverageDefinitionList']/div[1]/table"));
    public static TableExtended<ClassificationSubGroupsAndRatingColumns> tableClassificationSubGroupsAndRatingInfo =
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
                int count = 1;
                for (TestData plan : td.getTestDataList(name)) {
                    if (plan.containsKey("PlanKey")) {
                        tablePlansAndCoverages.getRow(PolicyConstants.PolicyPlanTable.PLAN, plan.getValue("PlanKey"))
                                .getCell(PolicyConstants.PolicyCoveragesTable.PLAN).click();
                    } else {
                        tablePlansAndCoverages.getRow(count).getCell(PolicyConstants.PolicyCoveragesTable.PLAN).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(plan);
                    setValue(list);
                    count++;
                }
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

}
