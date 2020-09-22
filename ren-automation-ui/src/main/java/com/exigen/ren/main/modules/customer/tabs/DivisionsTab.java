/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.customer.metadata.DivisionsTabMetaData;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.IntStream;

public class DivisionsTab extends DefaultTab {
    public static Button buttonAddDivision = new Button(By.id("crmForm:addDivision"));

    public static CheckBox checkBoxAssociateDivisions = new CheckBox(By.id("crmForm:generalInfoLeft_associateDivisions"));

    private static final By ADD_NEW_CONTACT_DETAILS = By.xpath(".//a[contains(.,'Add New Contacts Details')]");
    private static final By SELECT_CONTACT_METHOD = By.xpath(".//select[contains(@id,'contactMethodSelect')]");
    private static final ByT ADD_ALL = ByT.id("crmForm:saveButton_%s");
    private static final ByT EDIT_BUSINESS_ENTITY = ByT.id("crmForm:editBusinessEntity_%s");
    private static final ByT EDIT_CONTACT_DETAILS = ByT.xpath("//input[@id='crmForm:customerContactsTable%s:%s:editContact']");
    private static final ByT TABLE_CONTACT_DETAILS = ByT.id("crmForm:customerContactsTable%s");

    public DivisionsTab() {
        super(DivisionsTabMetaData.class);

        assetList = new RepeatAssetList(ByT.id("crmForm:divisionCollapsiblePanel%s"), metaDataClass) {

            @Override
            protected void setRawValue(List<TestData> value) {
                if (!value.get(0).getKeys().isEmpty()) {
                    int size = value.size();
                    for (int i = 0; i < size; i++) {
                        if (!sectionExists(i)) {
                            addSection(i, size);
                        }
                        setSectionValue(i, value.get(i));
                        Button buttonAddAllContacts = new Button(ADD_ALL.format(i));
                        if (buttonAddAllContacts.isPresent()) {
                            buttonAddAllContacts.click();
                        }
                    }
                }
            }

            @Override
            protected void addSection(int i, int size) {
                buttonAddDivision.click();
            }

            @Override
            protected void selectSection(int i) {
                new Button(EDIT_BUSINESS_ENTITY.format(i)).click();
            }
        };
        this.assetList.setName(DivisionsTab.class.getSimpleName());
    }

    public static void expandAllContactDetails(int businessNum) {
        IntStream.range(0, new Table(TABLE_CONTACT_DETAILS.format(businessNum - 1)).getRowsCount()).forEach(
                i -> new Link(EDIT_CONTACT_DETAILS.format(businessNum - 1, i)).click());
    }

    @Override
    public Tab submitTab() {
        if (buttonDone.isPresent()){
            buttonDone.click();
        } else {
            buttonNext.click();
        }
        return this;
    }

    public static void editContactDetails(int businessNum, int contactNum) {
        new Button(EDIT_CONTACT_DETAILS.format(businessNum - 1, contactNum - 1)).click();
    }

    public static class AddressDetailsDivision extends ContactDetails {

        public AddressDetailsDivision(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:addressInfo') and contains(@id,'_%s') and .='Address Details']/.."), metaDataClass);
        }

        public AddressDetailsDivision(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:addressInfo') and contains(@id,'_%s') and .='Address Details']/.."), metaDataClass);
        }

        public AddressDetailsDivision(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:addressInfo') and contains(@id,'_%s') and .='Address Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Address";
        }
    }

    public static class PhoneDetailsDivision extends ContactDetails {

        public PhoneDetailsDivision(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:phoneInfo') and contains(@id,'_%s') and .='Phone Details' ]/.."), metaDataClass);
        }

        public PhoneDetailsDivision(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:phoneInfo') and contains(@id,'_%s') and .='Phone Details' ]/.."), metaDataClass);
        }

        public PhoneDetailsDivision(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:phoneInfo') and contains(@id,'_%s') and .='Phone Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Phone";
        }
    }

    public static class EmailDetailsDivision extends ContactDetails {

        public EmailDetailsDivision(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:emailInfo') and contains(@id,'_%s') and .='Email Details']/.."), metaDataClass);
        }

        public EmailDetailsDivision(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:emailInfo') and contains(@id,'_%s') and .='Email Details']/.."), metaDataClass);
        }

        public EmailDetailsDivision(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:emailsInfo') and contains(@id,'_%s') and .='Email Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Email";
        }
    }

    public static class ChatDetailsDivision extends ContactDetails {

        public ChatDetailsDivision(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:chatInfo') and contains(@id,'_%s') and .='Chat Details']/.."), metaDataClass);
        }

        public ChatDetailsDivision(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:chatInfo') and contains(@id,'_%s') and .='Chat Details']/.."), metaDataClass);
        }

        public ChatDetailsDivision(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:chatInfo') and contains(@id,'_%s') and .='Chat Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Chat";
        }
    }

    public static class WebUrlDetailsDivision extends ContactDetails {

        public WebUrlDetailsDivision(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:webAddressInfo') and contains(@id,'_%s') and .='Web URL Details']/.."), metaDataClass);
        }

        public WebUrlDetailsDivision(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:webAddressInfo') and contains(@id,'_%s') and .='Web URL Details']/.."), metaDataClass);
        }

        public WebUrlDetailsDivision(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:webAddressInfo') and contains(@id,'_%s') and .='Web URL Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Web URL";
        }
    }

    public static class SocialNetDetailsDivision extends ContactDetails {

        public SocialNetDetailsDivision(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:socialNetInfo') and contains(@id,'_%s') and .='Social Net Details']/.."), metaDataClass);
        }

        public SocialNetDetailsDivision(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:socialNetInfo') and contains(@id,'_%s') and .='Social Net Details']/.."), metaDataClass);
        }

        public SocialNetDetailsDivision(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:socialNetInfo') and contains(@id,'_%s') and .='Social Net Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Social Net";
        }
    }

    private abstract static class ContactDetails extends RepeatAssetList {

        public ContactDetails(ByT locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(locatorTemplate, metaDataClass);
        }

        public ContactDetails(BaseElement<?, ?> parent, ByT locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, locatorTemplate, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            new ComboBox(new StaticElement(container.get().getLocator()), SELECT_CONTACT_METHOD).setValue(getContactType());
            new Button(new StaticElement(container.get().getLocator()), ADD_NEW_CONTACT_DETAILS).click();
        }

        protected abstract String getContactType();
    }
}
