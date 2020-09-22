package com.exigen.ren.admin.modules.agencyvendor.agency.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.agencyvendor.AgencyVendorDefaultTab;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.LicenseTabMetaData;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;

public class LicenseInfoTab extends AgencyVendorDefaultTab {
    public static Button buttonAddLicense = new Button(By.id("brokerLicenseForm:addLicense"));
    public static Dialog dialogAddLicense = new Dialog("//div[@id='addLicenseDialogForm:addLicenseDialog']");
    public static ComboBox listOfAuthority = new ComboBox(By.xpath("//*[@id='brokerManagementForm:loa' or @id='addLicenseDialogForm:loa']"));

    public LicenseInfoTab() {
        super(LicenseTabMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
            }

            @Override
            protected void selectSection(int index) {
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }
}