package com.exigen.ren.main.modules.mywork.actiontabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.mywork.metadata.AddAttachmentForTaskActionTabMetaData;
import org.openqa.selenium.By;

public class AddAttachmentForTaskActionTab extends ActionTab {

    public static Button buttonOk = new Button(By.id("addEfDocumentForm:okBtn"));

    public AddAttachmentForTaskActionTab() {
        super(AddAttachmentForTaskActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }

}
