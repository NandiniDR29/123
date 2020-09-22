package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.metadata.ApprovalPeriodsCancelActionTabMetaData;
import org.openqa.selenium.By;

public class ApprovalPeriodsCancelActionTab extends ActionTab {

    public static TableExtended<ListOfUnProcessedApprovalPeriods> tableListOfUnProcessedApprovalPeriods = new TableExtended<>("List of Un-processed Approval Periods", By.xpath("//div[@id='policyDataGatherForm:componentView_PrecClaimsUnprocessedApprovalPeriodView_body']//table"));

    public ApprovalPeriodsCancelActionTab() {
        super(ApprovalPeriodsCancelActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }

    public enum ListOfUnProcessedApprovalPeriods implements Named {
        APPROVAL_START_DATE("Approval Start Date"),
        APPROVAL_END_DATE("Approval End Date"),
        DATE_OF_STATUS_CHANGE("Date of Status Change"),
        APPROVER("Approver"),
        STATUS("Status");

        private String name;

        ListOfUnProcessedApprovalPeriods(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}