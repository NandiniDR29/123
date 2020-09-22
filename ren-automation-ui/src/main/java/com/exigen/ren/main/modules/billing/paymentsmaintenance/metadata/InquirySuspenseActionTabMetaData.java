/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class InquirySuspenseActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> SUSPENSE_REFERENCE = declare("Suspense Reference #", TextBox.class);
    public static final AssetDescriptor<TextBox> BUSINESS_NAME = declare("Business Name", TextBox.class);
    public static final AssetDescriptor<TextBox> SUSPENSE_AMOUNT = declare("Suspense Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> CHECK_DATE = declare("Check Date", TextBox.class);
    public static final AssetDescriptor<TextBox> CHECK_NUMBER = declare("Check Number", TextBox.class);

    public static final AssetDescriptor<AccountInformationList> ACCOUNT_INFORMATION_LIST = declare("Account Information List", AccountInformationList.class, AccountInformationListData.class,
            false);



    public static final class AccountInformationListData extends MetaData {
        public static final AssetDescriptor<StaticElement> BILLING_ACCOUNT_NUM = declare("Billing Account #", StaticElement.class, Waiters.DEFAULT, true,
                By.xpath(".//span[contains(@id, 'accountNumber')]"));
    }

    public static class AccountInformationList extends RepeatAssetList {

        public AccountInformationList(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//tbody[@id='suspenseForm:accountAssociationResults_data'][%s]"), metaDataClass);
        }

        public AccountInformationList(BaseElement<?, ?> parent, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath(".//tbody[@id='suspenseForm:accountAssociationResults_data'][%s]"), metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) { }
        protected void setSectionValue(int sectionIndex, TestData value) {
            super.setSectionValue(++sectionIndex, value);
        }
    }
}
