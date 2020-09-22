/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.ipb.eisa.controls.ComboList;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.customer.tabs.BusinessEntityTab;

public class BusinessEntityTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
    public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name - DBA", TextBox.class);
    public static final AssetDescriptor<TextBox> EIN = declare("EIN", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_CONTINUOUS_YEARS_IN_THE_FIELDS = declare("Number of continuous years in the field", TextBox.class);
    public static final AssetDescriptor<ComboList> ENTITY_TYPE = declare("Entity Type", ComboList.class);
    public static final AssetDescriptor<CheckBox> TAX_ATTEMPT = declare("Tax Exempt", CheckBox.class);
    public static final AssetDescriptor<ComboBox> DIVISION = declare("Division", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INDUSTRY = declare("Industry", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
    public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
    public static final AssetDescriptor<ComboBox> SECTOR = declare("Sector", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SUB_SECTOR = declare("Sub Sector", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INDUSTRY_GROUP = declare("Industry Group", ComboBox.class);
    public static final AssetDescriptor<ComboBox> NAICS_DESCRIPTION = declare("NAICS Description", ComboBox.class);
    public static final AssetDescriptor<TextBox> NAICS_CODE = declare("NAICS Code", TextBox.class);

    public static final AssetDescriptor<BusinessEntityTab.AddressDetailsBusinessEntity> ADDRESS_DETAILS =
            declare("Address Details", BusinessEntityTab.AddressDetailsBusinessEntity.class, AddressDetailsBusinessEntityMetaData.class);
    public static final AssetDescriptor<BusinessEntityTab.PhoneDetailsBusinessEntity> PHONE_DETAILS =
            declare("Phone Details", BusinessEntityTab.PhoneDetailsBusinessEntity.class, PhoneDetailsBusinessEntityMetaData.class);
    public static final AssetDescriptor<BusinessEntityTab.EmailDetailsBusinessEntity> EMAIl_DETAILS =
            declare("Email Details", BusinessEntityTab.EmailDetailsBusinessEntity.class, EmailDetailsBusinessEntityMetaData.class);
    public static final AssetDescriptor<BusinessEntityTab.ChatDetailsBusinessEntity> CHAT_DETAILS =
            declare("Chat Details", BusinessEntityTab.ChatDetailsBusinessEntity.class, ChatDetailsBusinessEntityMetaData.class);
    public static final AssetDescriptor<BusinessEntityTab.WebUrlDetailsBusinessEntity> WEB_URL_DETAILS =
            declare("Web URL Details", BusinessEntityTab.WebUrlDetailsBusinessEntity.class, WebUrlDetailsBusinessEntityMetaData.class);
    public static final AssetDescriptor<BusinessEntityTab.SocialNetDetailsBusinessEntity> SOCIAL_NET_DETAILS =
            declare("Social Net Details", BusinessEntityTab.SocialNetDetailsBusinessEntity.class, SocialNetDetailsBusinessEntityMetaData.class);

    public static final class AddressDetailsBusinessEntityMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip/Post Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class PhoneDetailsBusinessEntityMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> CONSENT_STATUS = declare("Consent Status", ComboBox.class);
    }

    public static final class EmailDetailsBusinessEntityMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> EMAIL_TYPE = declare("Email Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<ComboBox> CONSENT_STATUS = declare("Consent Status", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class ChatDetailsBusinessEntityMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> CHAT_ID = declare("Chat ID", TextBox.class);
        public static final AssetDescriptor<ComboBox> CHAT_TYPE = declare("Chat Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class WebUrlDetailsBusinessEntityMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> WEB_URL = declare("Web URL", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class SocialNetDetailsBusinessEntityMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> SOCIAL_NET_ID = declare("Social Net ID", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }
}
