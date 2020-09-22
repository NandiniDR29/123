package com.exigen.ren.main.modules.caseprofile.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.metadata.GenerateOnDemandDocumentTabMetaData;
import org.openqa.selenium.By;

public class GenerateOnDemandDocumentTab extends DefaultTab {
    public static Table tableDocumentTemplates = new Table(By.xpath("//table[@id='manualDocGenTemplateForm:documentTemplates']"));

    public static Button buttonGenerate = new Button(By.id("manualDocGenForm:btnGenerate_footer"));

    public GenerateOnDemandDocumentTab() {
        super(GenerateOnDemandDocumentTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonGenerate.click();
        Page.dialogConfirmation.confirm();
        return this;
    }
}
