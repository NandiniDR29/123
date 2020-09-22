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
import com.exigen.ren.main.modules.claim.common.tabs.ApprovalPeriodsActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import org.openqa.selenium.By;

public class PremiumWaiverApprovalPeriodsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ApprovalPeriodsActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Premium Waiver Approval Periods";
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
        ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations.getRow(rowNumber).getCell(ClaimConstants.ClaimAllSingleBenefitCalculationsTable.SINGLE_BENEFIT_NUMBER).controls.links.getFirst().click();
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

    @Override
    public Action submit() {
        if(Tab.buttonSaveAndExit.isPresent()) {
            Tab.buttonSaveAndExit.click();
        }
        if (Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.confirm();
        }
        Tab.buttonCancel.click();
        Tab.buttonCancel.click();
        return this;
    }
}