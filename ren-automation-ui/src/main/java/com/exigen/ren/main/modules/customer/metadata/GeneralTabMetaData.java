/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.ipb.eisa.controls.ComboList;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import org.openqa.selenium.By;

public class GeneralTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> CONTACT_DETAILS_TYPE = declare("Contact Details Type", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> PRIMARY_CONTACT_PREFERENCE = declare("Primary Contact Preference", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DESIGNATION = declare("Designation", ComboBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<CheckBox> DECEASED = declare("Deceased?", CheckBox.class);
    public static final AssetDescriptor<CheckBox> DEATH_NOTIFICATION_RECEIVED = declare("Death Notification Received", CheckBox.class);
    public static final AssetDescriptor<TextBox> DEATH_DATE = declare("Death Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> LEAD_STATUS = declare("Lead Status", ComboBox.class, Waiters.AJAX);
    public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name - DBA", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
    public static final AssetDescriptor<TextBox> SALESFORCE_ID = declare("Salesforce ID", TextBox.class);
    public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> SSN_TAX_IDENTIFICATION = declare("SSN/Tax Identification", TextBox.class, Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
    public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
    public static final AssetDescriptor<Link> SALES_INFO_EXPAND = declare("Sales Info", Link.class, By.xpath("//div[@id='crmForm:salesInfoTogglePanel:header']//div[@class='rf-cp-lbl-colps']"));
    public static final AssetDescriptor<TextBox> SOURCE_DESCRIPTION = declare("Source Description", TextBox.class);
    public static final AssetDescriptor<Button> ASSIGN_OWNER = declare("Assign Owner", Button.class, By.id("crmForm:owner"));
    public static final AssetDescriptor<DialogAssetList> SEARCH_OWNER = declare("Search Owner", DialogAssetList.class, SearchOwnerMetaData.class);
    public static final AssetDescriptor<StaticElement> LEAD_OWNER = declare("Lead Owner", StaticElement.class, By.xpath("//div[@id='crmForm:forTooltip']/span"));
    public static final AssetDescriptor<ComboBox> NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
    public static final AssetDescriptor<CheckBox> ASSOCIATE_BUSINESS_ENTITIES = declare("Associate Business Entities", CheckBox.class);
    public static final AssetDescriptor<CheckBox> ASSOCIATE_DIVISIONS = declare("Associate Divisions", CheckBox.class);

    public static final AssetDescriptor<AssetList> ADDITIONAL_INFORMATION = declare("Additional Information", AssetList.class, AdditionalInformationMetaData.class, By.id("crmForm:custAdditionalInfoTogglePanel"));

    public static final AssetDescriptor<GeneralTab.AgencyAssignment>
            AGENCY_ASSIGNMENT = declare("Agency Assignment", GeneralTab.AgencyAssignment.class, AddAgencyMetaData.class);

    //Two different controls for different types of values (search for agent or select work queue)
    public static final AssetDescriptor<Button> SALES_INFO_HEADER = declare("Sales Info Header", Button.class, By.id("crmForm:salesInfoTogglePanel:header"));
    public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead Source", ComboBox.class);
    public static final AssetDescriptor<ComboBox> RATING = declare("Rating", ComboBox.class);
    public static final AssetDescriptor<DialogSingleSelector> SALES_REP_INTERNAL =
            declare("Sales Rep Internal", DialogSingleSelector.class, SearchInternalSalesRepMetaData.class);
    public static final AssetDescriptor<DialogSingleSelector> SALES_ACCOUNT_MANAGER =
            declare("Sales Account Manager", DialogSingleSelector.class, SalesAccountManagerMetaData.class);
    public static final AssetDescriptor<Button> SALES_REP_REMOVE = declare("Sales Rep Remove", Button.class, By.id("crmForm:removeOwner"));

    public static final AssetDescriptor<DialogAssetList>
            SALES_REP_WORK_QUEUE = declare("Sales Rep Work Queue", DialogAssetList.class, SalesRepWorkQueueMetaData.class);

    public static final AssetDescriptor<TextBox> EIN = declare("EIN", TextBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_EMPLOYEES = declare("Number of Employees", TextBox.class, By.id("crmForm:generalInfoLeft_numberOfEmployees"));
    public static final AssetDescriptor<TextBox> DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
    public static final AssetDescriptor<Button> ADD_SIC_CLASSIFICATION = declare("Add SIC Classification", Button.class, By.id("crmForm:addSIC"));
    public static final AssetDescriptor<ComboBox> MAIL_CARDS_TO = declare("Mail Cards To", ComboBox.class, By.id("crmForm:generalInfoLeft_mailCardsTo"));
    public static final AssetDescriptor<Button> REORDER_CARDS = declare("Reorder Cards", Button.class, By.id("crmForm:reorderCardsBtn"));
    public static final AssetDescriptor<ComboBox> MAIL_W_2_TO = declare("Mail W-2 To", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> ELECTRONIC_SSA_FILING = declare("Electronic SSA Filing", RadioGroup.class);

    public static final AssetDescriptor<Button> ISSUE_ENROLLMENT = declare("Issue Enrollment", Button.class, By.id("crmForm:issueEnrollmentBtn"));
    public static final AssetDescriptor<DialogAssetList> ENROLLMENT_FILE_DIALOG = declare("Enrollment File", DialogAssetList.class, EnrollmentFileMetaData.class, By.id("issueEnrollmentPopup_container"));

    public static class EnrollmentFileMetaData extends MetaData {
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false, By.id("crmForm:issueEnrollmentBtn"));
        public static final AssetDescriptor<RadioGroup> ENROLLMENT_FILE_TYPE = declare("Enrollment File Type", RadioGroup.class, By.id("issueEnrollmentForm:enrollmentFileType"));
        public static final AssetDescriptor<Button> RETRIEVE = declare("Retrieve", Button.class, By.id("issueEnrollmentForm:retrieveBtn"));
        public static final AssetDescriptor<ComboBox> ENROLLMENT_FILE = declare("Enrollment File", ComboBox.class);
        public static final AssetDescriptor<Button> SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.AJAX, false, By.id("issueEnrollmentForm:enrollBtn"));
        public static final AssetDescriptor<Button> CLOSE_POPUP = declare("Close Popup", Button.class, Waiters.AJAX, false, By.id("issueEnrollmentForm:cancelBtn"));
        public static final AssetDescriptor<Button> REMOVE_BTN_POPUP = declare("Remove", Button.class, Waiters.AJAX, false, By.id("issueEnrollmentForm:removeFileBtn"));
    }

    public static final AssetDescriptor<Button> YES_ENROLLMENT_CONFIRMATION_POPUP = declare("Yes Enrollment Confirmation Popup", Button.class, Waiters.AJAX, false, By.id("yes_enrollmentConfirmationPopup"));
    public static final AssetDescriptor<ComboBox> DIVISION = declare("Division", ComboBox.class);
    public static final AssetDescriptor<ComboBox> INDUSTRY = declare("Industry", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
    public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
    public static final AssetDescriptor<Button> REMOVE_SIC_CLASSIFICATION = declare("Remove SIC Classification", Button.class, By.id("crmForm:removeSIC"));

    public static final AssetDescriptor<GeneralTab.AddressDetails> ADDRESS_DETAILS =
            declare("Address Details", GeneralTab.AddressDetails.class, AddressDetailsMetaData.class);
    public static final AssetDescriptor<GeneralTab.PhoneDetails> PHONE_DETAILS =
            declare("Phone Details", GeneralTab.PhoneDetails.class, PhoneDetailsMetaData.class);
    public static final AssetDescriptor<GeneralTab.EmailDetails> EMAIl_DETAILS =
            declare("Email Details", GeneralTab.EmailDetails.class, EmailDetailsMetaData.class);
    public static final AssetDescriptor<GeneralTab.ChatDetails> CHAT_DETAILS =
            declare("Chat Details", GeneralTab.ChatDetails.class, ChatDetailsMetaData.class);
    public static final AssetDescriptor<GeneralTab.WebUrlDetails> WEB_URL_DETAILS =
            declare("Web URL Details", GeneralTab.WebUrlDetails.class, WebUrlDetailsMetaData.class);
    public static final AssetDescriptor<GeneralTab.SocialNetDetails> SOCIAL_NET_DETAILS =
            declare("Social Net Details", GeneralTab.SocialNetDetails.class, SocialNetDetailsMetaData.class);

    public static final class AddressDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip/Post Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class, Waiters.AJAX.then(Waiters.SLEEP(1000)));
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> DO_NOT_SOLICIT = declare("Do not Solicit?", RadioGroup.class);
        public static final AssetDescriptor<ComboList> COMMUNICATION_PREFERENCE = declare("Communication Preferences", ComboList.class);
    }

    public static final class PhoneDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class EmailDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> EMAIL_TYPE = declare("Email Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<ComboBox> CONSENT_STATUS = declare("Consent Status", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
        public static final AssetDescriptor<ComboList> COMMUNICATION_PREFERENCES = declare("Communication Preferences", ComboList.class);
    }

    public static final class ChatDetailsMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> CHAT_ID = declare("Chat ID", TextBox.class);
        public static final AssetDescriptor<ComboBox> CHAT_TYPE = declare("Chat Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class WebUrlDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> WEB_URL = declare("Web URL", TextBox.class);
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final class SocialNetDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> SOCIAL_NET_ID = declare("Social Net ID", TextBox.class, Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
        public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
        public static final AssetDescriptor<RadioGroup> MAKE_PREFERRED = declare("Make Preferred?", RadioGroup.class);
    }

    public static final AssetDescriptor<CheckBox> GROUP_SPONSOR = declare("Group Sponsor", CheckBox.class);

    public static final AssetDescriptor<GeneralTab.GroupInformation> GROUP_INFORMATION =
            declare("Group Information", GeneralTab.GroupInformation.class, AddGroup.class);
    public static final AssetDescriptor<GeneralTab.ProductOwned> ADD_NEW_PRODUCT_DETAILS =
            declare("Add New Product Details", GeneralTab.ProductOwned.class, AddNewProductDetailsMetaData.class);
    public static final AssetDescriptor<GeneralTab.AdditionalNames> ADDITIONAL_NAMES =
            declare("Additional Names", GeneralTab.AdditionalNames.class, AdditionalNameDetailsMetaData.class);
    public static final AssetDescriptor<DialogAssetList> ASSOCIATE_ACCOUNT = declare("Associate Account",
            DialogAssetList.class, AssociateAccountMetaData.class);

    public static final class AssociateAccountMetaData extends MetaData {
        public static final AssetDescriptor<RadioGroup> ACCOUNT_TYPE = declare("Account Type", RadioGroup.class,
                By.id("associateMajorAccount:majorAccountTypeRadio"));
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("crmForm:associateAccountLink"));
        public static final AssetDescriptor<DialogAssetList> ASSOCIATE_NEW_ACCOUNT = declare("New Account",
                DialogAssetList.class, AssociateNewAccountMetaData.class);
        public static final AssetDescriptor<DialogSingleSelector> ASSOCIATE_EXISTING_ACCOUNT = declare("Existing Account",
                DialogSingleSelector.class, SearchAccountMetaData.class);
    }

    public static final class AssociateNewAccountMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> ACCOUNT_DESIGNATION_TYPE = declare("Account Designation Type", ComboBox.class);
        public static final AssetDescriptor<Button> DEFAULT_POPUP_SUBMITTER_NAME = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("associateMajorAccount:addButton"));
    }

    public static final class SearchAccountMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ACCOUNT_ID = declare("Account ID", TextBox.class);
        public static final AssetDescriptor<Button> SEARCH = declare("Account Search", Button.class, By.id("associateMajorAccount:majorAccountSearchBtn"));
    }

    public static final AssetDescriptor<ComboBox> MEMBERSHIP_STATUS = declare("Membership Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> MEMBERSHIP_LEVEL = declare("Membership Level", ComboBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership #", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_DATE = declare("Membership Date", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBER_ID = declare("Member ID", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_ID = declare("Membership ID", TextBox.class);
    public static final AssetDescriptor<TextBox> MEMBERSHIP_START_DATE = declare("Membership Start Date", TextBox.class);
    public static final AssetDescriptor<TextBox> STUDENT_ID = declare("Student ID", TextBox.class);
    public static final AssetDescriptor<TextBox> STUDENT_START_DATE = declare("Student Start Date", TextBox.class);
    public static final AssetDescriptor<RadioGroup> STUDENT_TYPE = declare("Student Type", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> STUDENT_STATUS = declare("Student Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> DIVISION_ID = declare("Division ID", TextBox.class);
    public static final AssetDescriptor<TextBox> FIELD_OF_STUDY = declare("Field of Study", TextBox.class);
    public static final AssetDescriptor<RadioGroup> STUDENT_ATHLETE = declare("Student Athlete", RadioGroup.class);
    public static final AssetDescriptor<TextBox> MAJOR_LARGE_ACCOUNT_ID = declare("Major/Large Account ID", TextBox.class);
    public static final AssetDescriptor<ComboBox> BRAND_NAME = declare("Brand Name", ComboBox.class);

    public static final AssetDescriptor<CheckBox> ASSOCIATE_EMPLOYMENTS = declare("Associate Employments", CheckBox.class);
    public static final AssetDescriptor<CheckBox> ASSOCIATE_PROVIDERS = declare("Associate Providers", CheckBox.class);

    public static final class AddGroup extends MetaData {
        public static final AssetDescriptor<DialogSingleSelector> GROUP_SEARCH = declare("Group Search", DialogSingleSelector.class, AddGroup.class);
        public static final AssetDescriptor<TextBox> GROUP_ID = declare("Group ID", TextBox.class);
        public static final AssetDescriptor<TextBox> GROUP_NAME = declare("Group Name", TextBox.class);
    }

    public static final class AddNewProductDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CARRIER_NAME = declare("Carrier Name", ComboBox.class);
    }

    public static final class AdditionalNameDetailsMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> SALUTATION = declare("Salutation", ComboBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
        public static final AssetDescriptor<ComboBox> DESIGNATION = declare("Designation", ComboBox.class);
        public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
        public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name-DBA", TextBox.class);
    }

    public static final class AddAgencyMetaData extends MetaData {
        public static final AssetDescriptor<DialogSingleSelector> AGENCY_PRODUCER =
                declare("Agency/Producer", DialogSingleSelector.class, AddAgencyMetaData.class);
        public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
    }

    public static final class SalesRepWorkQueueMetaData extends MetaData {
        public static final AssetDescriptor<Button> OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.AJAX, false, By.xpath("//a[@id='crmForm:owner' or @id='crmForm:changeOwner']"));
        public static final AssetDescriptor<RadioGroup> SEARCH_FOR = declare("Search For", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> WORK_QUEUE = declare("Work Queue", ComboBox.class);
        public static final AssetDescriptor<Button> SUBMIT_POPUP = declare("Submit Popup", Button.class, By.id("ownerSearchForm:queueOk"));
    }

    public static final class SearchInternalSalesRepMetaData extends MetaData {
        public static final AssetDescriptor<Button> OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.AJAX, false, By.xpath("//a[@id='crmForm:owner' or @id='crmForm:changeOwner']"));
        public static final AssetDescriptor<RadioGroup> SEARCH_FOR = declare("Search For", RadioGroup.class);
        public static final AssetDescriptor<TextBox> ROLE = declare("Role", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
    }

    public static final class SalesAccountManagerMetaData extends MetaData {
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("crmForm:addAccountManager"));
        public static final AssetDescriptor<TextBox> ROLE = declare("Role", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
    }

    public static final class AdditionalInformationMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> PREFERRED_LANGUAGE_SPOKEN = declare("Preferred Language (Spoken)", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PREFREED_LANGUAGE_WRITTEN = declare("Preferred Language (Written)", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CITIZENSHIP = declare("Citizenship", ComboBox.class);
        public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class);
        public static final AssetDescriptor<CheckBox> PAPERLESS = declare("Paperless", CheckBox.class);
        public static final AssetDescriptor<CheckBox> REGISTERED_ONLINE = declare("Registered Online", CheckBox.class);
        public static final AssetDescriptor<ComboBox> TOBACCO = declare("Tobacco", ComboBox.class);
        public static final AssetDescriptor<ComboList> SEGMENTS = declare("Segment(s)", ComboList.class);
        public static final AssetDescriptor<ComboList> CUSTOMER_INTEREST = declare("Customer Interests", ComboList.class);
        public static final AssetDescriptor<ComboList> IMPAIRMENTS_DISABILITIES = declare("Impairments/Disabilities", ComboList.class);
        public static final AssetDescriptor<ComboList> E_SIGNED_DOCS = declare("eSigned Doc(s)", ComboList.class);
        public static final AssetDescriptor<CheckBox> SPECIAL_CLAIM_REVIEW = declare("Special Claim Review", CheckBox.class);
    }
}