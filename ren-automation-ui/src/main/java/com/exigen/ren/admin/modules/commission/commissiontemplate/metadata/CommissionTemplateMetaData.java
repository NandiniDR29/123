/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissiontemplate.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class CommissionTemplateMetaData extends MetaData {

    public static final AssetDescriptor<TextBox> TEMPLATE_NAME = declare("Template Name", TextBox.class);

    public static final class AddCommissionTemplate extends MetaData {
        public static final AssetDescriptor<TextBox> TEMPLATE_NAME = declare("Template Name", TextBox.class, Waiters.DEFAULT, By.xpath("//input[@id='addTemplateDialogForm_groupBenefitsHeap:templateName']"));
        public static final AssetDescriptor<TextBox> LOWER_BOUNDARY = declare("Lower Boundary", TextBox.class, Waiters.DEFAULT, By.id("addTemplateDialogForm_groupBenefitsGraded:startYear"));
        public static final AssetDescriptor<TextBox> UPPER_BOUNDARY = declare("Upper Boundary", TextBox.class, Waiters.DEFAULT, By.id("addTemplateDialogForm_groupBenefitsGraded:endYear"));
        public static final AssetDescriptor<TextBox> END_YEAR = declare("End Year", TextBox.class, Waiters.DEFAULT, false, By.xpath("//input[@id='addTemplateDialogForm_groupBenefitsHeap:endYear']"));
        public static final AssetDescriptor<TextBox> COMMISSION = declare("Commission %", TextBox.class, By.xpath("//input[contains(@id, 'rate') and not(@disabled)]"));
    }
}
