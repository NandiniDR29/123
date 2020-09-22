/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION;

public class PolicyInformationTab extends DefaultTab {
    public static Button buttonAddAssignment = new Button(By.xpath("//input[@value='Add Agency']"));
    public static TableExtended<AssignedAgenciesTable> tableAssignedAgencies = new TableExtended<>("Assigned Agencies", By.xpath("//div[contains(@id, 'policyDataGatherForm:dataGatherView_ListGroupMasterPolicy')]/div/table"));
    public static final TableExtended<TableConstants.AgenciesProducersTable> agenciesProducersTable = new TableExtended<TableConstants.AgenciesProducersTable>("Plan", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListGroupMasterPolicyMultiProducerInfo']/div/table"));
    public static final StaticElement helpTextGroupIsMemberCompany = new StaticElement(By.id("policyDataGatherForm:sedit_Policy_groupIsMemberCompany_helpText"));

    public PolicyInformationTab() {
        super(PolicyInformationTabMetaData.class);
    }

    public static class AssignedAgencies extends RepeatAssetList {

        public AssignedAgencies(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
        }

        public AssignedAgencies(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
        }

        @Override
        protected boolean sectionExists(int index) {
            return tableAssignedAgencies.getRow(index+1).isPresent();
        }

        @Override
        protected void addSection(int index, int size) {
            buttonAddAssignment.click();
        }
    }

    public enum AssignedAgenciesTable implements Named {
        AGENCY_PRODUCER_NAME("Agency/Producer Name"),
        PRIMARY("Primary?"),
        SPLIT_PERCENT("Split %"),
        AGENT_SUB_PRODUCER_NAME("Agent/Sub-Producer Name");
        private String name;

        AssignedAgenciesTable (String name) {this.name = name;}

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName(), doubleWaiter);
        return this;
    }

    @Override
    public String getTabName() {
        return POLICY_INFORMATION.get();
    }
}
