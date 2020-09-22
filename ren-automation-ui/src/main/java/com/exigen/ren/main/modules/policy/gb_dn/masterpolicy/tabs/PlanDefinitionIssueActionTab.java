package com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionIssueActionTabMetaData;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class PlanDefinitionIssueActionTab extends DefaultTab {

    public static final TableExtended<TableConstants.Empty> tablePlanSelection = new TableExtended<>("Plan", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPreconfigGroupCoverageDefinition']/div/table"));
    public static final TableExtended<TableConstants.Empty> tableProcedureCodeCoInsuranceOverride = new TableExtended<>("Procedure Code Co-Insurance Override", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListRenGroupProcedureOverriddenComponent']/div/table"));

    public PlanDefinitionIssueActionTab() {
        super(PlanDefinitionIssueActionTabMetaData.class);

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
                td.getTestDataList(name).forEach(plan -> {
                    if (plan.containsKey("PlanKey")) {
                        tablePlanSelection.getRow(PolicyConstants.PolicyPlanTable.PLAN, plan.getValue("PlanKey")).getCell(6).controls.links.get(ActionConstants.CHANGE).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(plan);
                    setValue(list);
                });
            }

        };
        assetList.setName(this.getClass().getSimpleName());
    }

    public static class ProcedureCodeCoInsuranceOverrideMultiAssetList extends MultiAssetList {

        public ProcedureCodeCoInsuranceOverrideMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        public ProcedureCodeCoInsuranceOverrideMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            if (index > 0 && getAssetCollection().containsKey("Add Code")) {
                getAsset("Add Code", Button.class).click();
            }
        }

        @Override
        protected void selectSection(int i) {
        }

        @Override
        protected void setSectionValue(int index, TestData value) {
            selectSection(index);
            super.setSectionValue(index, value);
        }

    }

    @Override
    public Tab submitTab() {
        buttonNext.click(doubleWaiter);
        return this;
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

}
