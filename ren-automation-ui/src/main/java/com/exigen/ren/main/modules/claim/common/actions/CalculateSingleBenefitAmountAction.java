/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.DeductionsActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.OtherIncomeBenefitActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.TaxActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.ClaimConstants.ClaimAllBenefitsTable.BENEFIT_TYPE;

public class CalculateSingleBenefitAmountAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(CoveragesActionTab.class)
            .registerTab(OtherIncomeBenefitActionTab.class)
            .registerTab(DeductionsActionTab.class)
            .registerTab(TaxActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Calculate a Single Benefit Amount";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    public Action start(int rowNumber) {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(rowNumber).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        new Button(By.xpath(String.format("//a[text()='%s']", getName()))).click();
        return this;
    }

    public Action start(String benefitType) {
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        ClaimAdjudicationBenefitPage.tableAllClaimBenefits
                .getRow(BENEFIT_TYPE, benefitType).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        new Button(By.xpath(String.format("//a[text()='%s']", getName()))).click();
        return this;
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        submit();
        return this;
    }

    public Action perform(TestData td, String benefitType) {
        start(benefitType);
        getWorkspace().fill(td);
        submit();
        return this;
    }

    @Override
    public Action submit() {
        Tab.buttonSaveAndExit.click();
        if(Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.confirm();
        }
        while(Tab.buttonCancel.isPresent()) {
            Tab.buttonCancel.click();
        }
        return this;
    }
}
