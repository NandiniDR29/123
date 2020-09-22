/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.agency.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class ContactInfoMetaData extends MetaData {
    public static final AssetDescriptor<StaticElement> COUNTRY = declare("Country", StaticElement.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> IS_MAILING_ADDRESS_THE_SAME_AS_PHYSICAL_ADDRESS =
            declare("Is Mailing Address the same as Physical Address?", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> COPY_INSURED_CORRESPONDENCE_REQUIRED = declare("Copy insured correspondence required?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
    public static final AssetDescriptor<TextBox> EXTENSION = declare("Extension #", TextBox.class);
    public static final AssetDescriptor<TextBox> FAX = declare("Fax #", TextBox.class);
    public static final AssetDescriptor<DialogAssetList> ADD_CONTACT = declare("Add Contact", DialogAssetList.class, AddContactMetaData.class);

    public static final class AddContactMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> SALUTATION = declare("Salutation", ComboBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CONTACT_TYPE = declare("Contact Type", ComboBox.class);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("brokerAgencyInfoForm:addPersonalContactBtn"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("addPersonalContactForm:savePersonalContact"));
    }

    public static final AssetDescriptor<TextBox> MAILING_ADDRESS_LINE_1 = declare("Mailing Address Line 1", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_addressLine1"));
    public static final AssetDescriptor<TextBox> MAILING_ZIP_POSTAL_CODE = declare("Mailing Zip / Postal Code", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_postalCode"));
    public static final AssetDescriptor<TextBox> MAILING_CITY = declare("Mailing City", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_city"));
    public static final AssetDescriptor<ComboBox> MAILING_STATE_PROVINCE = declare("Mailing State / Province", ComboBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_stateProvCd"));

}
