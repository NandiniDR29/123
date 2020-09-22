package com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.ren.admin.modules.commission.commissioncorrection.ICommissionCorrection;
import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.actions.CommissionCorrectionApplyCorrectionAction;
import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.metadata.CommissionCorrectionMetaData;
import com.exigen.ren.admin.modules.commission.commissioncorrection.pages.CommissionCorrectionPage;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;

public class GBCommissionCorrection implements ICommissionCorrection {

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.COMMISSION);
        NavigationPage.toLeftMenuTab(NavigationEnum.CommissionLeftMenu.COMMISSION_CORRECTION);
    }

    public void start(String policyNumber) {
        CommissionCorrectionPage.assetListSearchForm.getAsset(CommissionCorrectionMetaData.POLICY).setValue(policyNumber);
        AdminPage.buttonSearch.click();
        CommissionCorrectionPage.tableCommissionCorrection.getRow(CommissionCorrectionPage.CommissionCorrection.POLICY.getName(), policyNumber).getCell(4).controls.links.get("Apply Correction").click();
    }

    @Override
    public void initiate() {

    }

    @Override
    public void submit() {
        Tab.buttonSave.waitFor(BaseElement::isEnabled);
        Tab.buttonSave.click();
    }

    @Override
    public Action expire() {
        return null;
    }

    @Override
    public Action copy() {
        return null;
    }

    @Override
    public Action delete() {
        return null;
    }

    @Override
    public Action edit() {
        return null;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CommissionCorrectionApplyCorrectionAction.class).getWorkspace();
    }

    @Override
    public TestData defaultTestData() {
        return null;
    }

    @Override
    public void create(TestData td) {
        navigate();
        initiate();
        getDefaultWorkspace().fill(td);
        submit();
    }
}
