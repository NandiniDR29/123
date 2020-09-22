/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import org.openqa.selenium.By;

public class RemoveNewRelationshipContactsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RelationshipTab.class)
            .build();

    @Override
    public String getName() {
        return "Remove Relationship Contact";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action start(int rowNumber) {
        new Link(By.xpath("//div[@id='crmForm:customerRelationshipsTogglePanel:content']//*[@id= 'crmForm:editRelationship_" + rowNumber + "']")).click();
        new Link(By.xpath("//*[@id='crmForm:removeRelationshipBtn_" + rowNumber + "']")).click();
        Page.dialogConfirmation.confirm();
        new Button(By.xpath("//input[@id='crmForm:yes_deleteLeadPopup_" + rowNumber + "']")).click();
        return this;
    }

    public Action perform(int rowNumber) {
        start(rowNumber);
        return submit();
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        RelationshipTab.buttonDone.click();
        return this;
    }
}
