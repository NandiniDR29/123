package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.claim.common.metadata.TaxActionTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimCalculateSingleBenefitTab.TAX;

public class TaxActionTab extends ActionTab {
    public static Button buttonAdd = new Button(By.id("policyDataGatherForm:addPrecClaimsTax"));
    public static Button buttonRemove = new Button(By.id("policyDataGatherForm:eliminatePrecClaimsTax"));
    public static Table tableListOfTax = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPrecClaimsTax']/div/table"));

    public TaxActionTab() {
        super(TaxActionTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                if (index > 0 && tableListOfTax.isPresent() && tableListOfTax.getRow(index).isPresent()) {
                    tableListOfTax.getRow(index).getCell(tableListOfTax.getColumnsCount()).controls.links.get(ActionConstants.CHANGE).click();
                } else {
                    buttonAdd.click();
                }
            }

            @Override
            protected void selectSection(int index) {
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return TAX.get();
    }
}