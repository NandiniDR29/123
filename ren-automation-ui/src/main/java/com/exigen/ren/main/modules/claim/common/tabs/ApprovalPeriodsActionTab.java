package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.claim.common.metadata.ApprovalPeriodsActionTabMetaData;
import org.openqa.selenium.By;

public class ApprovalPeriodsActionTab extends ActionTab {

    public static final Button buttonAdd = new Button(By.id("policyDataGatherForm:addPrecClaimsApprovalPeriod"));
    public static final Button buttonRemove = new Button(By.id("policyDataGatherForm:eliminatePrecClaimsApprovalPeriod"));
    public static TableExtended<ListOfApprovalPeriodsTable> tableListOfApprovalPeriods = new TableExtended<>("List of Approval Periods", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPrecClaimsApprovalPeriod']/div/table"));

    public ApprovalPeriodsActionTab() {
        super(ApprovalPeriodsActionTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                if (index > 0 && tableListOfApprovalPeriods.isPresent() && tableListOfApprovalPeriods.getRow(index).isPresent()) {
                    tableListOfApprovalPeriods.getRow(index).getCell(tableListOfApprovalPeriods.getColumnsCount()).controls.links.get(ActionConstants.CHANGE).click();
                }
                else {
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
    public Tab submitTab() {
        buttonSaveAndExit.click();
        return this;
    }

    public enum ListOfApprovalPeriodsTable implements Named {
        APPROVAL_START_DATE("Approval Start Date"),
        APPROVAL_END_DATE("Approval End Date"),
        DATE_OF_STATUS_CHANGE("Date of Status Change"),
        APPROVER("Approver"),
        STATUS("Status");

        private String name;

        ListOfApprovalPeriodsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}