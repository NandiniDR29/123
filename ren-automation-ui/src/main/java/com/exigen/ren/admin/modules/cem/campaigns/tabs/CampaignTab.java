/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.campaigns.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.Dialog;
import org.openqa.selenium.By;

public class CampaignTab extends DefaultTab {

    public static Button buttonAddCampaignProducts = new Button(By.id("addProductButton"));
    public static Button buttonAddMarketingChannels = new Button(By.id("addChannelButton"));
    public static Button buttonDisassociateALL = new Button(By.id("disassociateAllLink"));

    public static Link linkCampaignProductsCollapsed = new Link(By.xpath("//div[text()='Campaign Products' and contains(@class, 'colps')]"));
    public static Link linkTargetCharacteristicsCollapsed = new Link(By.xpath("//div[text()='Target Characteristics' and contains(@class, 'colps')]"));
    public static Link linkMarketingChannelsCollapsed = new Link(By.xpath("//div[text()='Marketing Channels' and contains(@class, 'colps')]"));
    public static Link linkCampaignSchedulingCollapsed = new Link(By.xpath("//div[text()='Campaign Scheduling' and contains(@class, 'colps')]"));
    public static Link linkCampaigns = new Link(By.id("breadcrumbs:campaignBreadcrumb"));

    public static StaticElement labelCampaignId = new StaticElement(By.id("campaignCard:campaignID"));
    public static StaticElement labelCampaignName = new StaticElement(By.id("campaignCard:name"));
    public static StaticElement labelPromotionCode = new StaticElement(By.id("campaignCard:campaignPromotionCd"));
    public static StaticElement labelMessages = new StaticElement(By.id("messages"));

    public static Table tableCampaignInfo = new Table(By.xpath("//div[@id='campaignCard:campaignRelationshipsChain']//table"));
    public static Table tableCampaignRelationshipInfo = new Table(By.xpath("//div[@id='campaignRelationshipsChain']//table"));
    public static Table tableProduct = new Table(By.id("campaignCard:productTable"));
    public static Table tableChannel = new Table(By.id("campaignCard:channelTable"));

    public static Dialog dialogDisassociateAll = new Dialog("//div[@id='disassociateAllCampaignsConfirmDialog_container']");
    public static Dialog dialogDisassociate = new Dialog("//div[@id='disassociateCampaignConfirmDialog_container']");

    public CampaignTab() {
        super(CampaignMetaData.class);
    }

    @Override
    public Tab fillTab(TestData td) {

        linkCampaignProductsCollapsed.click();
        linkTargetCharacteristicsCollapsed.click();
        linkMarketingChannelsCollapsed.click();
        linkCampaignSchedulingCollapsed.click();

        assetList.fill(td);

        return this;
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
