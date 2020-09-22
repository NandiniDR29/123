package com.exigen.ren.main.modules.policy.common.tabs.master;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.TableConstants.CoverageDefinition.COVERAGE_NAME;

public class PlanDefinitionEndorseNPBInfoActionTab extends ActionTab {

    public static final TableExtended<TableConstants.Empty> planTable = new TableExtended<>("Plan", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPreconfigGroupCoverageDefinition']/div/table"));
    public static Button buttonIssue = new Button(By.id("policyDataGatherForm:processPolicyNPBEActionButton_GroupMasterPolicyNPBEAction_footer"), doubleWaiter);


    public PlanDefinitionEndorseNPBInfoActionTab() {
        super(PlanDefinitionEndorseNPBInfoActionTabMetaData.class);

    }

    @Override
    public Tab submitTab() {
        buttonIssue.click(doubleWaiter);
        Page.dialogConfirmation.confirm();
        return this;
    }

    public void changeCoverageTo(String coverage) {
        LOGGER.info(String.format("Set coverage: '%s'", coverage));
        planTable.getRow(ImmutableMap.of(COVERAGE_NAME.getName(), coverage)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
    }
}
