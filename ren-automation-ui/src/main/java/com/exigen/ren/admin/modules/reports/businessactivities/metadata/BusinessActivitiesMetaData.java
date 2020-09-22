/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.businessactivities.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class BusinessActivitiesMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> REPORT_FOR = declare("Report For", RadioGroup.class);
    public static final AssetDescriptor<CheckBox> PERFORMER = declare("Performer", CheckBox.class, By.id("advancedCriteriaForm:userSubdetailsHierarchyTable:0:usdhSelected"));
    public static final AssetDescriptor<ComboBox> LOB = declare("LOB", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ROLES = declare("Role(s)", ComboBox.class);
    public static final AssetDescriptor<TextBox> PERIOD_FROM = declare("Period From", TextBox.class);
    public static final AssetDescriptor<TextBox> PERIOD_TO = declare("Period To", TextBox.class);
    public static final AssetDescriptor<DialogAssetList> SAVE_REPORT = declare("Save Report", DialogAssetList.class, SaveReportPopUp.class);

    public static final class SaveReportPopUp extends MetaData {
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT,
                false, By.id("activitySummaryForm:saveReportBtn"));
        public static final AssetDescriptor<RadioGroup> SAVE_REPORT_TO = declare("Save Report To", RadioGroup.class, By.xpath("//table[@id='saveReportDialogForm:saveReportTo']"));
        public static final AssetDescriptor<ComboBox> TEMPLATE = declare("Template", ComboBox.class, By.xpath("//select[@id='saveReportDialogForm:templateType']"));
        public static final AssetDescriptor<ComboBox> FOLDER = declare("Folder", ComboBox.class, By.id("saveReportDialogForm:fList"));
        public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, By.id("saveReportDialogForm:docComment"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT,
                false, By.xpath("//input[@id='saveReportDialogForm:saveReportCriteriaOkFileBtn' or @id='saveReportDialogForm:saveReportCriteriaOkEFolderBtn']"));
    }
}


