/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.carrier.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class CompaniesMetaData extends MetaData {
    public static final AssetDescriptor<DialogAssetList> ADD_COMPANY = declare("AddCompanyDialog", DialogAssetList.class, AddCompanyDialog.class);

    public static class AddCompanyDialog extends MetaData {
        public static final AssetDescriptor<TextBox> COMPANY_CODE = declare("Company Code", TextBox.class);
        public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector> UNDERWRITING_COMPANIES = declare("Underwriting Companies", AdvancedSelector.class);
        public static final AssetDescriptor<Button> DEFAULT_POPUP_OPENER_NAME = declare(AbstractDialog.getDefaultPopupOpenerName(),
                Button.class, Waiters.DEFAULT, false, By.id("carrierInfoForm:addCompanies"));
        public static final AssetDescriptor<Button> DEFAULT_POPUP_SUBMITTER_NAME = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("addCompanyButtonForm:saveLegalEntity2"));
    }
}
