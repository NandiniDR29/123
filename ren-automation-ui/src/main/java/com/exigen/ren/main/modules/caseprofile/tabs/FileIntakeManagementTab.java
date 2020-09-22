package com.exigen.ren.main.modules.caseprofile.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import org.openqa.selenium.By;

public class FileIntakeManagementTab extends DefaultTab {

    public static Button buttonAddNewIntakeProfile = new Button(By.id("caseForm:intakeProfileAddBtn"));
    public static TableExtended<TableConstants.FileIntakeManagement> fileIntakeManagementTable = new TableExtended<>("File Intake Management", By.id("caseForm:body_intakeProfiles"));

    public FileIntakeManagementTab() {
        super(FileIntakeManagementTabMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                buttonAddNewIntakeProfile.click();
            }

            @Override
            protected void selectSection(int index) {

            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        buttonSaveAndFinalize.click();
        return this;
    }
}
