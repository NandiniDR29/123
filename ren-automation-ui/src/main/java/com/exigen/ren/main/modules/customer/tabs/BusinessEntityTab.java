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
import com.exigen.ren.main.modules.customer.metadata.BusinessEntityTabMetaData;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.IntStream;

public class BusinessEntityTab extends DefaultTab {

    public static final Button buttonAddBusinessEntity = new Button(By.id("crmForm:addBusinessEntity"));

    private static final By ADD_NEW_CONTACT_DETAILS = By.xpath(".//a[contains(.,'Add New Contacts Details')]");
    private static final By SELECT_CONTACT_METHOD = By.xpath(".//select[contains(@id,'contactMethodSelect')]");
    private static final ByT ADD_ALL = ByT.id("crmForm:saveButton_%s");
    private static final ByT EDIT_BUSINESS_ENTITY = ByT.id("crmForm:editBusinessEntity_%s");
    private static final ByT EDIT_CONTACT_DETAILS = ByT.xpath("//input[@id='crmForm:customerContactsTable%s:%s:editContact']");
    private static final ByT TABLE_CONTACT_DETAILS = ByT.id("crmForm:customerContactsTable%s");

    public BusinessEntityTab() {
        super(BusinessEntityTabMetaData.class);

        assetList = new RepeatAssetList(ByT.id("crmForm:businessEntityTogglePanel_%s"), metaDataClass) {

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
            protected void addSection(int index, int size) {
                buttonAddBusinessEntity.click();
            }

            @Override
            public void selectSection(int i) {
                new Button(EDIT_BUSINESS_ENTITY.format(i)).click();
            }

            @Override
            protected TestData getSectionValue(int index) {
                selectSection(index);
                return super.getSectionValue(index);
            }
        };
        this.assetList.setName(BusinessEntityTab.class.getSimpleName());
    }

    public static void editBusinessEntity(int businessNum) {
        new Button(EDIT_BUSINESS_ENTITY.format(businessNum - 1)).click();
    }

    public static void editContactDetails(int businessNum, int contactNum) {
        new Link(EDIT_CONTACT_DETAILS.format(businessNum - 1, contactNum - 1)).click();
    }

    public static void expandAllContactDetails(int businessNum) {
        IntStream.range(0, new Table(TABLE_CONTACT_DETAILS.format(businessNum-1)).getRowsCount()).forEach(
                i -> new Link(EDIT_CONTACT_DETAILS.format(businessNum - 1, i)).click());
    }
    //TODO iguliam move from here. same classes as in general tab. move to separate controls
    public static class AddressDetailsBusinessEntity extends ContactDetails {
        public AddressDetailsBusinessEntity(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:addressInfo') and contains(@id,'_%s') and .='Address Details']/.."), metaDataClass);
        }

        public AddressDetailsBusinessEntity(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:addressInfo') and contains(@id,'_%s') and .='Address Details']/.."), metaDataClass);
        }

        public AddressDetailsBusinessEntity(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:addressInfo') and contains(@id,'_%s') and .='Address Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Address";
        }
    }

    public static class PhoneDetailsBusinessEntity extends ContactDetails {
        public PhoneDetailsBusinessEntity(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:phoneInfo') and contains(@id,'_%s') and .='Phone Details' ]/.."), metaDataClass);
        }

        public PhoneDetailsBusinessEntity(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:phoneInfo') and contains(@id,'_%s') and .='Phone Details' ]/.."), metaDataClass);
        }

        public PhoneDetailsBusinessEntity(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:phoneInfo') and contains(@id,'_%s') and .='Phone Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Phone";
        }
    }

    public static class EmailDetailsBusinessEntity extends ContactDetails {
        public EmailDetailsBusinessEntity(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:emailInfo') and contains(@id,'_%s') and .='Email Details']/.."), metaDataClass);
        }

        public EmailDetailsBusinessEntity(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:emailInfo') and contains(@id,'_%s') and .='Email Details']/.."), metaDataClass);
        }

        public EmailDetailsBusinessEntity(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:emailsInfo') and contains(@id,'_%s') and .='Email Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Email";
        }
    }

    public static class ChatDetailsBusinessEntity extends ContactDetails {

        public ChatDetailsBusinessEntity(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:chatInfo') and contains(@id,'_%s') and .='Chat Details']/.."), metaDataClass);
        }

        public ChatDetailsBusinessEntity(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:chatInfo') and contains(@id,'_%s') and .='Chat Details']/.."), metaDataClass);
        }

        public ChatDetailsBusinessEntity(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:chatInfo') and contains(@id,'_%s') and .='Chat Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Chat";
        }
    }

    public static class WebUrlDetailsBusinessEntity extends ContactDetails {
        public WebUrlDetailsBusinessEntity(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:webAddressInfo') and contains(@id,'_%s') and .='Web URL Details']/.."), metaDataClass);
        }

        public WebUrlDetailsBusinessEntity(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:webAddressInfo') and contains(@id,'_%s') and .='Web URL Details']/.."), metaDataClass);
        }

        public WebUrlDetailsBusinessEntity(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:webAddressInfo') and contains(@id,'_%s') and .='Web URL Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Web URL";
        }
    }

    public static class SocialNetDetailsBusinessEntity extends ContactDetails {

        public SocialNetDetailsBusinessEntity(Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:socialNetInfo') and contains(@id,'_%s') and .='Social Net Details']/.."), metaDataClass);
        }

        public SocialNetDetailsBusinessEntity(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath("//div[contains(@id,'crmForm:socialNetInfo') and contains(@id,'_%s') and .='Social Net Details']/.."), metaDataClass);
        }

        public SocialNetDetailsBusinessEntity(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath("//div[contains(@id,'crmForm:socialNetInfo') and contains(@id,'_%s') and .='Social Net Details']/.."), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Social Net";
        }
    }

    private abstract static class ContactDetails extends RepeatAssetList {

        public ContactDetails(ByT locatorTemplate1, Class<? extends MetaData> metaDataClass) {
            super(locatorTemplate1, metaDataClass);
        }

        public ContactDetails(BaseElement<?, ?> parent1, ByT locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent1, locatorTemplate, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            new ComboBox(new StaticElement(container.get().getLocator()), SELECT_CONTACT_METHOD).setValue(getContactType());
            new Button(new StaticElement(container.get().getLocator()), ADD_NEW_CONTACT_DETAILS).click();
        }

        protected abstract String getContactType();
    }
}
