/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.caseprofile.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector2;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab;
import org.openqa.selenium.By;

public class ClassificationManagementTabMetaData extends MetaData {

    public static final AssetDescriptor<DialogAssetList> CLASSIFICATION_GROUP = declare("Classification Group",
            DialogAssetList.class, ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.class);
    public static final AssetDescriptor<AssetList> GROUP_DETAILS = declare("Group Details",
            AssetList.class, ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
    public static final AssetDescriptor<ClassificationManagementTab.SubGroupAssetList> SUB_GROUP = declare("Sub Groups",
            ClassificationManagementTab.SubGroupAssetList.class, ClassificationManagementTabMetaData.ClassificationManagementTabSubGroup.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

    public static final class ClassificationManagementTabGroupPopup extends MetaData {
        public static final AssetDescriptor<TextBox> CLASS_NAME = declare("Class Name", TextBox.class);
        public static final AssetDescriptor<TextBox> CLASS_NUMBER = declare("Class Number", TextBox.class);
        public static final AssetDescriptor<ComboBox> RELATIONSHIP_TYPE = declare("Relationship Type", ComboBox.class);
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("addClassificationForm:saveClassificationBtn"));
    }

    public static final class ClassificationManagementTabGroupData extends MetaData {
        public static final AssetDescriptor<TextBox> CLASS_NAME = declare("Class Name", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector2> MEMBERSHIP_STATUS = declare("Membership Status", AdvancedSelector2.class);
        public static final AssetDescriptor<AdvancedSelector2> STUDENT_STATUS = declare("Student Status", AdvancedSelector2.class);
        public static final AssetDescriptor<ComboBox> STUDENT_TYPE = declare("Student Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STUDENT_ATHLETE = declare("Student Athlete", ComboBox.class);
        public static final AssetDescriptor<AdvancedSelector2> JOB_CODE = declare("Job Code", AdvancedSelector2.class);
        public static final AssetDescriptor<AdvancedSelector2> EMPLOYMENT_STATUS = declare("Employment Status", AdvancedSelector2.class);
        public static final AssetDescriptor<AdvancedSelector2> EMPLOYMENT_TYPE = declare("Employment Type", AdvancedSelector2.class);
        public static final AssetDescriptor<ComboBox> EXPATRIATE = declare("Expatriate", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PAY_CLASS = declare("Pay Class", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PAY_TYPE = declare("Pay Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> UNION_MEMBER = declare("Union Member", ComboBox.class);
        public static final AssetDescriptor<TextBox> CLASS_DESCRIPTION = declare("Class Description", TextBox.class);
        public static final AssetDescriptor<RadioGroup> CREATE_SUB_GROUPS = declare("Create Sub-Groups?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> CREATE_SUB_GROUPS_AUTO = declare("Create Sub-Groups Automatically?", RadioGroup.class);
    }

    public static final class ClassificationManagementTabSubGroup extends MetaData {
        public static final AssetDescriptor<DialogAssetList> CLASSIFICATION_SUB_GROUP = declare("Classification Sub-Group",
                DialogAssetList.class, ClassificationManagementTabMetaData.ClassificationManagementTabSubGroup.ClassificationManagementTabSubGroupPopup.class);
        public static final AssetDescriptor<AssetList> SUB_GROUP_DETAIS = declare("Sub Group Details",
                AssetList.class, ClassificationManagementTabMetaData.ClassificationManagementTabSubGroup.ClassificationManagementTabSubGroupData.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

        public static final class ClassificationManagementTabSubGroupPopup extends MetaData {
            public static final AssetDescriptor<TextBox> SUB_GROUP_NAME = declare("Sub-Group Name", TextBox.class);
            public static final AssetDescriptor<ComboBox> SUB_GROUP_TYPE = declare("Sub-Group Type", ComboBox.class);
            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                    By.id("addClassSubgroupForm:saveClassSubgroupBtn"));
        }

        public static final class ClassificationManagementTabSubGroupData extends MetaData {
            public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
            public static final AssetDescriptor<ComboBox> TOBACCO = declare("Tobacco", ComboBox.class);
            public static final AssetDescriptor<TextBox> MIN_AGE = declare("Min Age", TextBox.class);
            public static final AssetDescriptor<TextBox> MAX_AGE = declare("Max Age", TextBox.class);
            public static final AssetDescriptor<RadioGroup> UNI_TOBACCO = declare("Uni-Tobacco", RadioGroup.class);
            public static final AssetDescriptor<RadioGroup> UNI_SEX = declare("Uni-Sex", RadioGroup.class);
        }
    }
}
