/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.caseprofile.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CaseProfileConstants;
import com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;

public class ClassificationManagementTab extends DefaultTab {

    public static TableExtended<ClassificationGroupsTable> tableClassificationGroups = new TableExtended<>("Classification Groups", By.id("caseForm:body_caseClassificationTable"));
    public static Table tableSubGroups = new Table(By.id("caseForm:body_caseClassificationTable"));
    public static TableExtended<ClassificationSubGroups> tableClassificationSubGroups = new TableExtended<>("Classification SubGroups",
            By.xpath("//div[@id='classificationSubgroupTable']//table"),
            By.xpath("//div[@id='classificationSubgroupTable']//span[@id='caseForm:body_classificationSubgroupTable:dataScrollerclassificationSubgroupTable']"));

    public static Button buttonAddClassification = new Button(By.id("caseForm:addClassificationBtn"));
    public static Button buttonAddClassSubGroup = new Button(By.xpath("//a[@id='caseForm:addClassSubgroupBtn']"));
    public static Button buttonNext = new Button(By.id("caseForm:nextBtn_footer"));

    public ClassificationManagementTab() {
        super(ClassificationManagementTabMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void selectSection(int index) {
                tableClassificationGroups.getRow(index).getCell(ClassificationGroupsTable.ACTIONS.getName()).controls.links.get(ActionConstants.CHANGE).click();
            }

            @Override
            protected void addSection(int arg0, int arg1) {
                buttonAddClassification.click();
            }

            @Override
            public void fill(TestData td) {
                if (td.containsKey(name)) {
                    fillSection(td);
                }
            }

            private void fillSection(TestData td) {
                for (TestData tdTab : td.getTestDataList(name)) {
                    if (!tdTab.getKeys().isEmpty()) {
                        List<TestData> list = new ArrayList<>();
                        list.add(tdTab);
                        setValue(list);
                    }
                }
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        buttonNext.click(doubleWaiter);
        return this;
    }

    public static class SubGroupAssetList extends MultiAssetList {
        public SubGroupAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        public SubGroupAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        @Override
        protected void selectSection(int index) {
            tableSubGroups.getRow(index).getCell(CaseProfileConstants.CaseProfileSubGroupsTable.ACTIONS).controls.links.get(ActionConstants.CHANGE).click();
        }

        @Override
        protected void addSection(int arg0, int arg1) {
            //TODO(yshramchanka): try-catch block was added to investigate not reproducible failed regression tests
            try {
                buttonAddClassSubGroup.click();
            } catch (NoSuchElementException e) {
                log.info(BrowserController.get().driver().getPageSource());
                throw e;
            }
        }
    }

    public enum ClassificationSubGroups implements Named {
        SUB_GROUP_NAME("Sub-Group Name"),
        TYPE("Type"),
        PRODUCT("Product"),
        COVERAGE("Coverage"),
        UNI_TOBACCO("Uni-Tobacco"),
        UNISEX("Unisex"),
        ACTIONS("Actions");
        private String name;

        ClassificationSubGroups(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ClassificationGroupsTable implements Named {
        CLASS_NAME("Class Name"),
        CLASS_NUMBER("Class Number"),
        CLASS_TYPE("Class Type"),
        RELATIONSHIP_TYPE("Relationship Type"),
        ORIGINAL_EFFECTIVE_DATE("Original Effective Date"),
        ACTIONS("Actions");

        private String name;

        ClassificationGroupsTable(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
