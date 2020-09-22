package com.exigen.ren.admin.modules.agencyvendor.agency.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class LicenseTabMetaData extends MetaData {

    public static final AssetDescriptor<DialogAssetList> ADD_LICENSE = declare("Add License", DialogAssetList.class, AddLicenseMetaData.class);

    public static final class AddLicenseMetaData extends MetaData {
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("brokerLicenseForm:addLicense"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("addLicenseDialogForm:saveLicenseBtn"));
        public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License Number", TextBox.class, Waiters.DEFAULT, false, By.id("addLicenseDialogForm:licenseNumber"));
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.DEFAULT, false,
                By.id("addLicenseDialogForm:effectiveDateInputDate"));
        public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class, Waiters.DEFAULT, false,
                By.id("addLicenseDialogForm:expirationDateInputDate"));
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.DEFAULT, false, By.id("addLicenseDialogForm:country"));
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class, Waiters.DEFAULT, false,
                By.xpath("//select[@id='addLicenseDialogForm:stateProvince']"));
        public static final AssetDescriptor<ComboBox> LINE_OF_AUTHORITY = declare("Line Of Authority",
                ComboBox.class, Waiters.DEFAULT, false, By.xpath("//select[@id='addLicenseDialogForm:loa']"));
    }
}
