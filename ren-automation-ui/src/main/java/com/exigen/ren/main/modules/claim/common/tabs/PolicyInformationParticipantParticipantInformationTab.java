/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.PARTICIPANT_INFORMATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.POLICY_INFORMATION;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_NO;


public class PolicyInformationParticipantParticipantInformationTab extends DefaultTab {

    public static TableExtended<ListOfParticipantPriorEarnings> listOfParticipantPriorEarnings = new TableExtended<>("List of Participant Prior Earnings",
            By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPrecClaimsPriorEarnings']/div/table"));
    public static TableExtended<ListOfParticipantIndexedPreDisabilityEarnings> listOfParticipantIndexedPreDisabilityEarnings = new TableExtended<>("List of Participant Indexed Pre-Disability Earnings",
            By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListRenClaimsIndexedPreDisabilityEarnings']/div/table"));
    public static ByT btnRemoveParticipant = ByT.id("policyDataGatherForm:dataGatherView_ListRenClaimsIndexedPreDisabilityEarnings:%s:dataGatherView_List_Eliminate");

    public PolicyInformationParticipantParticipantInformationTab() {
        super(PolicyInformationParticipantParticipantInformationTabMetaData.class);
    }

    public static class ParticipantPriorEarnings extends RepeatAssetList {
        public static final Button addParticipantPriorEarning = new Button(By.id("policyDataGatherForm:addPrecClaimsPriorEarnings"));

        public ParticipantPriorEarnings(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
        }

        public ParticipantPriorEarnings(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
        }

        @Override
        // Clicking 'Add' if we should fill more that one section.
        protected boolean sectionExists(int index) {
            return index == 0;
        }

        @Override
        protected void addSection(int index, int size) {
            addParticipantPriorEarning.click();
        }
    }

    public static class ParticipantIndexedPreDisabilityEarnings extends MultiAssetList {
        public static final Button addParticipantIndexedPreDisabilityEarnings = new Button(By.id("policyDataGatherForm:addRenClaimsIndexedPreDisabilityEarnings"));

        public ParticipantIndexedPreDisabilityEarnings(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        public ParticipantIndexedPreDisabilityEarnings(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            addParticipantIndexedPreDisabilityEarnings.click();
        }

        @Override
        protected void selectSection(int i) {
        }
    }

    public enum ListOfParticipantPriorEarnings implements Named {
        WEEK_NO("Week no."),
        WEEK_ENDING_DATE("Week ending date (MM/DD/YYYY)"),
        NUMBER_OF_DAYS_WORKED("Number of days worked"),
        GROSS_AMOUNT_PAID("Gross amount paid");

        private String name;

        ListOfParticipantPriorEarnings(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ListOfParticipantIndexedPreDisabilityEarnings implements Named {
        YEAR_NO("Year No."),
        YEAR_START_DATE("Year Start Date (MM/DD/YYYY)"),
        YEAR_END_DATE("Year End Date (MM/DD/YYYY)"),
        INDEXED_PRE_DISABILITY_EARNINGS("Indexed Pre-Disability Earnings");

        private String name;

        ListOfParticipantIndexedPreDisabilityEarnings(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.expand(POLICY_INFORMATION.get(), this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return PARTICIPANT_INFORMATION.get();
    }

    @Override
    public Tab submitTab() {
        // temporal workaround to avoid test fails because of random uncontrolled click
        if (Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.buttonNo.click();
        }
        buttonNext.click();
        return this;
    }

    public static void removeParticipantByName(String name) {
        listOfParticipantIndexedPreDisabilityEarnings.getRow(YEAR_NO.getName(), name).getCell(6).controls.links.get("Remove").click();
        Page.dialogConfirmation.buttonYes.click();
    }
}
