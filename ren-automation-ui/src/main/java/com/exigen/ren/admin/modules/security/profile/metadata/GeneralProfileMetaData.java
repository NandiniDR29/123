/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile.metadata;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData;
import org.openqa.selenium.By;

public class GeneralProfileMetaData extends MetaData {
    public static final AssetDescriptor<StaticElement> CHANNEL = declare("Channel", StaticElement.class);
    public static final AssetDescriptor<DialogSingleSelector> AGENCY_LOCATIONS = declare("Agency/Locations", DialogSingleSelector.class,
            AddAgencyMetaData.class);
    public static final AssetDescriptor<CheckBox> DEFAULT = declare("Default", CheckBox.class,
            By.xpath("//table[@id='userProfileForm:userAgencyLocations']/tbody/tr[last()]//input[contains(@id, 'default')]"));
    public static final AssetDescriptor<AdvancedSelector> ROLES = declare("Roles", AdvancedSelector.class,
            By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='userProfileForm:select_allowed_roles']]"));
    public static final AssetDescriptor<AdvancedSelector> GROUPS = declare("Groups", AdvancedSelector.class,
            By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='userProfileForm:select_groups']]"));

    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
    public static final AssetDescriptor<TextBox> EXT = declare("Ext.", TextBox.class);
    public static final AssetDescriptor<TextBox> FAX = declare("Fax #", TextBox.class);
    public static final AssetDescriptor<TextBox> E_MAIL_ADDRESS = declare("E-Mail Address", TextBox.class);
    public static final AssetDescriptor<TextBox> JOB_TITLE = declare("Job Title", TextBox.class);
    public static final AssetDescriptor<TextBox> SIGNATURE_URI = declare("Signature URI", TextBox.class);
    public static final AssetDescriptor<RadioGroup> SELLS_INSURANCE_PRODUCTS = declare("Sells Insurance Products", RadioGroup.class);
    public static final AssetDescriptor<CheckBox> COMMISSIONABLE = declare("Commissionable", CheckBox.class);
    public static final AssetDescriptor<TextBox> PROFILE_SUBPRODUCER_ID = declare("Profile/Subproducer ID", TextBox.class, By.id("userProfileForm:profileId"));
    public static final AssetDescriptor<RadioGroup> EIS_USER = declare("EIS User", RadioGroup.class, By.id("userProfileForm:ipbUser"));
    public static final AssetDescriptor<DialogSingleSelector> AGENCY = declare("Agency", DialogSingleSelector.class,
            AddAgencyNotEisUserMetaData.class);
    public static final AssetDescriptor<RadioGroup> RESTRICT_TO_SELECTED_AGENCY_LOCATIONS = declare("Restrict to Selected Agency/Locations", RadioGroup.class);
    public static final AssetDescriptor<TextBox> USER_NAME = declare("User Name", TextBox.class, By.id("userProfileForm:profile_name"));
    public static final AssetDescriptor<TextBox> USER_LOGIN = declare("User Login", TextBox.class, By.id("userProfileForm:profile_login"));
    public static final AssetDescriptor<TextBox> PASSWORD = declare("Password", TextBox.class);
    public static final AssetDescriptor<TextBox> CONFIRM_PASSWORD = declare("Confirm Password", TextBox.class);
    public static final AssetDescriptor<ComboBox> SECURITY_DOMAIN = declare("Security Domain", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> USER_WORK_STATUS = declare("User Work Status", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> USER_LANGUAGE = declare("User Language", ComboBox.class);
    public static final AssetDescriptor<AdvancedSelector> PAR = declare("Product Access Roles", AdvancedSelector.class,
            By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='userProfileForm:select_par']]"));
    public static final AssetDescriptor<StaticElement> MANAGERS = declare("Managers", StaticElement.class);
    public static final AssetDescriptor<AdvancedSelector> MANAGERS_SELECT = declare("Managers Select", AdvancedSelector.class,
            By.xpath("//tr[contains(@class,'oddRow') and .//input[@id='userProfileForm:select_managers']]"));
    public static final AssetDescriptor<StaticElement> USER_SUBORDINATES = declare("User Subordinates", StaticElement.class);
    public static final AssetDescriptor<AdvancedSelector> USER_SUBORDINATES_SELECT = declare("User Subordinates Select", AdvancedSelector.class,
            By.xpath("//tr[contains(@class,'evenRow') and .//input[@id='userProfileForm:select_subordinates']]"));
    public static final AssetDescriptor<ComboBox> BILLING_AUTHORITY_LEVEL = declare("Billing Authority Level", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CLAIM_AUTHORITY_LEVEL = declare("Claim Authority Level", ComboBox.class);
    public static final AssetDescriptor<AssetList> ADDRESS_SECTION= declare("Address", AssetList.class, AddressSection.class, By.id("userProfileForm:addressSection"));

    public static final class AddressSection extends MetaData {
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
    }

    public static final class AddAgencyMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("userProfileForm:selectBroker"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("brokerSearchFromsearchBrokerCd:addBtn"));
        public static final AssetDescriptor<Button> BUTTON_SEARCH = declare("Search", Button.class, Waiters.DEFAULT, false,
                By.id("brokerSearchFromsearchBrokerCd:searchBtn"));
        public static final AssetDescriptor<Button> BUTTON_CANCEL = declare("Cancel", Button.class, Waiters.DEFAULT, false,
                By.id("brokerSearchFromsearchBrokerCd:cancelBtn"));
    }

    public static final class AddAgencyNotEisUserMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.xpath("//*[@id='userProfileForm:selectBroker' or @id='userProfileForm:selectBrokerBtn']"));
    }
}
