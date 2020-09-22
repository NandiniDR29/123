/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.ipb.eisa.controls.ComboList;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;

public class RelationshipTabMetaData extends MetaData{
    public static final AssetDescriptor<RadioGroup> TYPE = declare("Type", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> SALUTATION = declare("Salutation", ComboBox.class);
    public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class, Waiters.DEFAULT);
    public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> SSN_TAX_IDENTIFICATION = declare("SSN/Tax Identification", TextBox.class);
    public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_CUSTOMER = declare("Relationship to Customer", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REVERSED_RELATIONSHIP_TO_CUSTOMER = declare("Reversed Relationship to Customer", ComboBox.class);
    public static final AssetDescriptor<TextBox> RELATIONSHIP_DESCRIPTION = declare("Relationship Description", TextBox.class);
    public static final AssetDescriptor<Button> SEARCH_PARTY_RELATIONSHIP = declare("Search Party Relationship", Button.class);
    public static final AssetDescriptor<ComboBox> NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
    public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name - DBA", TextBox.class);
    public static final AssetDescriptor<RadioGroup> ASSIGN_SERVICE_ROLE = declare("Assign Service Role?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> AUTHORIZATION_OPTION = declare("Authorization Option", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CHALLENGE_QUESTION = declare("Challenge Question", ComboBox.class);
    public static final AssetDescriptor<TextBox> PASSWORD = declare("Password", TextBox.class);
    public static final AssetDescriptor<TextBox> PASSWORD_REMINDER = declare("Password Reminder", TextBox.class);
    public static final AssetDescriptor<TextBox> ANSWER = declare("Answer", TextBox.class);
    public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
    public static final AssetDescriptor<ComboList> SERVICE_ROLE = declare("Service Role", ComboList.class);
    public static final AssetDescriptor<TextBox> DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class, Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
    public static final AssetDescriptor<CheckBox> GROUP_SPONSOR = declare("Group Sponsor", CheckBox.class);
    public static final AssetDescriptor<RadioGroup> PRIMARY_CONTACT_PREFERENCE = declare("Primary Contact Preference", RadioGroup.class);
    public static final AssetDescriptor<ComboList> COMMUNICATION_PREFERENCES = declare("Communication Preferences", ComboList.class);


    public static final AssetDescriptor<RelationshipTab.AddressDetailsRelationship> ADDRESS_DETAILS =
            declare("Address Details", RelationshipTab.AddressDetailsRelationship.class, AddressDetailsRelationshipMetaData.class);
    public static final AssetDescriptor<RelationshipTab.PhoneDetailsRelationship> PHONE_DETAILS =
            declare("Phone Details", RelationshipTab.PhoneDetailsRelationship.class, PhoneDetailsRelationshipMetaData.class);
    public static final AssetDescriptor<RelationshipTab.EmailDetailsRelationship> EMAIl_DETAILS =
            declare("Email Details", RelationshipTab.EmailDetailsRelationship.class, EmailDetailsRelationshipMetaData.class);
    public static final AssetDescriptor<RelationshipTab.ChatDetailsRelationship> CHAT_DETAILS =
            declare("Chat Details", RelationshipTab.ChatDetailsRelationship.class, ChatDetailsRelationshipMetaData.class);
    public static final AssetDescriptor<RelationshipTab.WebUrlDetailsRelationship> WEB_URL_DETAILS =
            declare("Web URL Details", RelationshipTab.WebUrlDetailsRelationship.class, WebUrlDetailsRelationshipMetaData.class);
    public static final AssetDescriptor<RelationshipTab.SocialNetDetailsRelationship> SOCIAL_NET_DETAILS =
            declare("Social Net Details", RelationshipTab.SocialNetDetailsRelationship.class, SocialNetDetailsRelationshipMetaData.class);

    public static final class AddressDetailsRelationshipMetaData extends MetaData {
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

    public static final class PhoneDetailsRelationshipMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> CONSENT_STATUS = declare("Consent Status", ComboBox.class);
    }

    public static final class EmailDetailsRelationshipMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> EMAIL_TYPE = declare("Email Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<ComboBox> CONSENT_STATUS = declare("Consent Status", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class ChatDetailsRelationshipMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> CHAT_ID = declare("Chat ID", TextBox.class);
        public static final AssetDescriptor<ComboBox> CHAT_TYPE = declare("Chat Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFFERED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class WebUrlDetailsRelationshipMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> WEB_URL = declare("Web URL", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class SocialNetDetailsRelationshipMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> SOCIAL_NET_ID = declare("Social Net ID", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }
}
