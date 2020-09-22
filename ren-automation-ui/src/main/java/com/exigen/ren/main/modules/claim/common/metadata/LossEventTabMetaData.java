/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class LossEventTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> IS_TIME_KNOWN = declare("Is time known", RadioGroup.class, By.id("policyDataGatherForm:sedit_ClaimsLossEvent_knownTimeInd"));
    public static final AssetDescriptor<TextBox> DATE_OF_LOSS = declare("Date of Loss", TextBox.class, By.id("policyDataGatherForm:sedit_ClaimsLossEvent_lossDtDateInputDate"));
    public static final AssetDescriptor<TextBox> DATE_REPORTED = declare("Date Reported", TextBox.class);
    public static final AssetDescriptor<ComboBox> REPORTING_METHOD = declare("Reporting Method", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> PREVIOUSLY_REPORTED = declare("Previously Reported", RadioGroup.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION_OF_LOSS_EVENT = declare("Description of Loss Event", TextBox.class);
    public static final AssetDescriptor<DialogSingleSelector> PARTY_NAME = declare("Party Name", DialogSingleSelector.class, SearchParty.class, false,
            By.xpath("//div[contains(@class, 'ui-draggable') and contains(@class, 'ui-overlay-visible')]"));
    public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
    public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
    public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
    public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone", TextBox.class);
    public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class);
    public static final AssetDescriptor<ComboBox> CONTACT_PREFERENCE = declare("Contact Preference", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
    public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
    public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
    public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);

    public static final class SearchParty extends MetaData {
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_firstName"));
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_lastName"));
        public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_commercialName"));
        public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_dateOfBirthInputDate"));
        public static final AssetDescriptor<TextBox> PHONE = declare("Phone", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_phoneNumber"));
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_postalCode"));
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_city"));
        public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State / Province", TextBox.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:partySearchInfo_stateProvCd"));
        public static final AssetDescriptor<Button> OTHER_COMPANY = declare("Other Company", Button.class, Waiters.DEFAULT, false,
                By.id("partySearchForm_ClaimsInitParty:otherCompanyLink"));

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("policyDataGatherForm:searchPartyLink_ClaimsInitParty"));
    }
}
