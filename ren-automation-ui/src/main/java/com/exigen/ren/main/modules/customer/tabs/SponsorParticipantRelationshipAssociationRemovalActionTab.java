/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.SponsorParticipantRelationshipAssociationRemovalActionTabMetaData;
import org.openqa.selenium.By;

public class SponsorParticipantRelationshipAssociationRemovalActionTab extends ActionTab {

    public static Button buttonRelationshipAssociationRemove = new Button(By.xpath("//*[contains(@id, 'crmForm:relationshipAssociationRemovalConfirmationPopup_btnOK_keepHistory')]"));

    public SponsorParticipantRelationshipAssociationRemovalActionTab() {
        super(SponsorParticipantRelationshipAssociationRemovalActionTabMetaData.class);
        assetList = new AssetList(By.xpath("//*[contains(@id, 'crmForm:relationshipAssociationRemovalConfirmationPopup_container')]"), metaDataClass);
        assetList.applyConfiguration("ParticipantKeepHistory");
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        if (assetList.getAsset(SponsorParticipantRelationshipAssociationRemovalActionTabMetaData.KEEP_RELATIONSHIP_HISTORY).getValue()) {
            buttonRelationshipAssociationRemove.click();
        } else {
            Page.dialogConfirmation.confirm();
        }
        return this;
    }
}
