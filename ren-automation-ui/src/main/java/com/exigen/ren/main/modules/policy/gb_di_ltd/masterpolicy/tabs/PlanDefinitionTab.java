/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicy;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.PLAN;

public class PlanDefinitionTab extends DefaultTab {

    public static final TableExtended<TableConstants.CoverageDefinition> tableCoverageDefinition = new TableExtended<>("Coverage Definition", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPreconfigGroupCoverageDefinition']/div/table"));

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
                    if (plan.containsKey("PlanKey") && tableCoverageDefinition.isPresent()) {
                        tableCoverageDefinition.getRow(PolicyConstants.PolicyPlanTable.PLAN, plan.getValue("PlanKey")).getCell(7).controls.links.get(CHANGE).click();
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
    public String getTabName() {
        return PLAN_DEFINITION.get();
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    @Override
    public Tab submitTab() {
        buttonNext.click(doubleWaiter);
        return this;
    }

    public void selectDefaultPlan() {
        getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).fill(AutomationContext.getService(LongTermDisabilityMasterPolicy.class).getDefaultTestData(TestDataKey.DATA_GATHER, "SelectPlan"));
    }

    public static void openAddedPlan(String plan) {
        LOGGER.info(String.format("Set Plan: '%s'", plan));
        tableCoverageDefinition.getRow(ImmutableMap.of(PLAN.getName(), String.format("%s-%s", plan, plan))).getCell(7).controls.links.get(CHANGE).click();
    }

    public AssetList getCoverageIncludedInPackageAsset() {
        return getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE);
    }
    public AssetList getEligibilityAsset() {
        return getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY);
    }
    public AssetList getSponsorParticipantFundingStructureAsset() {
        return getAssetList().getAsset(PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE);
    }
    public AssetList getBenefitScheduleAsset() {
        return getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE);
    }
    public AssetList getOptionsAsset() {
        return getAssetList().getAsset(PlanDefinitionTabMetaData.OPTIONS);
    }
    public AssetList getEmployerBenefitsAsset() {
        return getAssetList().getAsset(PlanDefinitionTabMetaData.EMPLOYER_BENEFITS);
    }
}
