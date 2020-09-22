/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum.CustomerTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CustomerConstants.CustomerContactsTable;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerContactsTable.ACTION;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerContactsTable.CONTACT_METHOD;
import static com.exigen.ren.main.enums.CustomerConstants.EMAIL;

public class GeneralTab extends DefaultTab {

    public static final StaticElement labelEINfieldsIsRequired = new StaticElement(By.id("crmForm:generalInfoLeft_legalId_error:crmForm:generalInfoLeft_legalId"));
    public static final StaticElement labelSicFieldsIsRequired = new StaticElement(By.id("crmForm:sicCode_sicCode_error:crmForm:sicCode_sicCode"));
    public static final StaticElement labelSicSectionsRequired = new StaticElement(By.id("crmForm:sicOrNaicsSelected_error:crmForm:sicOrNaicsSelected"));

    public static final ComboBox comboBoxSelectContactMethod = new ComboBox(By.id("crmForm:contactMethod:contactMethodSelect"));
    public static final Button buttonAddContact = new Button(By.id("crmForm:contactMethod:addContactEvent"));
    public static final Button buttonSearchParty = new Button(By.xpath("//input[@value='Search Party']"));
    public static final Button buttonTimeline = new Button(By.xpath("//a[.='Timeline']"));
    public static final Button buttonAddAllContacts = new Button(By.id("crmForm:contactActionsAddButton"));
    public static Button buttonAddAssignment = new Button(By.id("crmForm:addAssignmentBtn"));
    public static Button buttonAddAllProducts = new Button(By.id("crmForm:otherProductsActionsAddButton"));

    public static final Table tableAdditionalNames = new Table(By.id("crmForm:additionalNamesTable"));
    public static final Table tableVersionCompare = new Table(By.id("indGeneralInfoCompareTable"));
    public static final Table tableTimeLine = new Table(By.xpath("//table[tbody[@id='historyForm:historyTable_data']]"));
    public static final Table tableProductsOwned = new Table(By.xpath("//*[@id='crmForm:otherProductsTable']//table"));
    public static final Table tableBrandInfo = new Table(By.id("brandInfoTable"));
    public static final TableExtended<CustomerContactsTable> tableContactDetails =
            new TableExtended<>("Contact Details Summary", By.xpath("//table[@id='crmForm:customerContactsTable']"));

    public static final Link linkChangeProductOwned = new Link(By.id("crmForm:otherProductsTable:0:editProductOwned"));
    public static final Link linkRemoveProductOwned = new Link(By.id("crmForm:otherProductsTable:1:removeLink"));
    public static final Link linkStudentRelationship = new Link(By.xpath("//span[contains(text(),'Student Information for')]//a"));
    public static final Link linkMembershipRelationship = new Link(By.xpath("//span[contains(text(),'Membership Information for')]//a"));

    public GeneralTab() {
        super(GeneralTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        if (NavigationPage.isSubTabPresent(CustomerTab.EMPLOYEE_INFO.get())) {
            NavigationPage.toSubTab(CustomerTab.EMPLOYEE_INFO);
        } else {
            if (buttonDone.isPresent()) {
                if (!buttonNext.isPresent() && NavigationPage.isSubTabPresent(CustomerTab.DIVISIONS.get())) {
                    NavigationPage.toSubTab(CustomerTab.DIVISIONS);
                    return this;
                }
                buttonDone.click();
            } else {
                buttonNext.click();
            }
        }
        return this;
    }

    /**
     * 'Agency/Producer' repeatedAssetList implementation. <br/><br/>
     * If needed to add one more agency and save existing ones <br/>
     * testDataList should contain one empty testData for each saved agency.<br/>
     * (Example: Agency Assignment: [{},{},{keys for new agency}] <--- first two agencies will not be changed)<br/><br/>
     * If needed to create customer w/o agency empty testDataList must be set. <br/>
     * (Example: Agency Assignment: [{}] <--- will be removed all existing agencies.
     */
    public static class AgencyAssignment extends RepeatAssetList {
        private ByT selectAgencyLocatorTemplate = ByT.id("crmForm:changeCustomerProducerCdBtn_%s");

        public static void removeAgencyAssignment(int index) {
            new Button(ByT.xpath("//a[@id='crmForm:removeAssignmentBtn_%s']").format(index)).click();
        }

        public AgencyAssignment(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
        }

        public AgencyAssignment(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
        }

        @Override
        protected void setSectionValue(int sectionIndex, TestData value) {
            if (!value.getKeys().isEmpty()) {
                if (sectionExists(sectionIndex)) {
                    getAddAgencyButton(sectionIndex).click();
                }
                super.setSectionValue(sectionIndex, value);
            }
        }

        @Override
        protected void setRawValue(List<TestData> value) {
            //empty testData value define that should be no agency applied
            if (value.size() == 1 && value.get(0).getKeys().isEmpty()) {
                while (sectionExists(0)) {
                    new Button(ByT.xpath("//a[@id='crmForm:removeAssignmentBtn_%s']").format(0)).click();
                }
                return;
            }
            super.setRawValue(value);
        }

        @Override
        protected boolean sectionExists(int index) {
            return getAddAgencyButton(index).isPresent();
        }

        @Override
        protected void addSection(int index, int size) {
            buttonAddAssignment.click();
        }

        private Button getAddAgencyButton(int index) {
            return new Button(selectAgencyLocatorTemplate.format(index));
        }
    }


    /**
     * 'Product Owned' section, adding new product is available through button 'Add New Product Details'
     */
    public static class ProductOwned extends RepeatAssetList {
        public static Button addNewProductDetails = new Button(By.id("crmForm:otherProductMethod:addProductOwnedEvent"));
        public static Button addAll = new Button(By.id("crmForm:otherProductsActionsAddButton"));

        public ProductOwned(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:productOwnedInfo%s"), metaDataClass);
        }

        public ProductOwned(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:productOwnedInfo%s"), metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            addNewProductDetails.click();
        }
    }

    /**
     * 'Additional Names' section, adding new product is available through button 'Add Additional Name'
     */
    public static class AdditionalNames extends RepeatAssetList {
        public static Button addAdditionalName = new Button(By.id("crmForm:additionalNameMethod:addAdditionalNameBtn"));
        public static Button cancelEditing = new Button(By.id("crmForm:additionalNamesActionsCancelButton"));
        public static Button addAll= new Button(By.id("crmForm:additionalNamesActionsAddButton"));

        public AdditionalNames(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:additionalNameInfo%s"), metaDataClass);
        }

        public AdditionalNames(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:additionalNameInfo%s"), metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            expandAllSections();
            if (tableAdditionalNames.getRowsCount() < index + 1) {
                addAdditionalName.click();
            }
        }

        private void expandAllSections() {
            IntStream.range(0, tableAdditionalNames.getRowsCount()).forEach(
                    row -> tableAdditionalNames.getRow(row + 1).getCell("Action").controls.buttons.get("Change").click());
        }
    }

    /**
     * 'Group Information' section, adding new group is available through button 'Add Group'
     */
    public static class GroupInformation extends RepeatAssetList {
        public static Button addAdditionalName = new Button(By.id("crmForm:additionalNameMethod:addAdditionalNameBtn"));
        public static Button cancelEditing = new Button(By.id("crmForm:additionalNamesActionsCancelButton"));

        public GroupInformation(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:groupInfoInfo%s"), metaDataClass);
        }

        public GroupInformation(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:groupInfoInfo%s"), metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            new Button(By.id("crmForm:groupInfoMethod:addGroupInfoEvent")).click();
            new Button(ByT.id("crmForm:showGroupSearchPopup_%s").format(index)).click();
        }
    }

    public static class AddressDetails extends ContactDetails {
        public static Button buttonAddAll = new Button(By.xpath("//input[@id='crmForm:contactActionsAddButton']"));

        public AddressDetails(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:addressInfo_%s"), metaDataClass);
        }

        public AddressDetails(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:addressInfo_%s"), metaDataClass);
        }

        @Override
        public void fill(TestData td) {
            if (td.containsKey(name)) {
                setValue(td.getTestDataList(name));
                buttonAddAll.click();
            }
        }


        @Override
        protected String getContactType() {
            return "Address";
        }
    }

    public static class PhoneDetails extends ContactDetails {

        public PhoneDetails(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:phoneInfo_%s"), metaDataClass);
        }

        public PhoneDetails(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:phoneInfo_%s"), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Phone";
        }


    }

    public static class EmailDetails extends ContactDetails {

        public EmailDetails(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:emailInfo_%s"), metaDataClass);
        }

        public EmailDetails(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:emailInfo_%s"), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Email";
        }
    }

    public static class ChatDetails extends ContactDetails {

        public ChatDetails(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:chatInfo_%s"), metaDataClass);
        }

        public ChatDetails(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:chatInfo_%s"), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Chat";
        }
    }

    public static class WebUrlDetails extends ContactDetails {

        public WebUrlDetails(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:webAddressInfo_%s"), metaDataClass);
        }

        public WebUrlDetails(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:webAddressInfo_%s"), metaDataClass);
        }

        @Override
        protected String getContactType() {
            return "Web URL";
        }
    }

    public static class SocialNetDetails extends ContactDetails {

        public SocialNetDetails(By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(ByT.id("crmForm:socialNetInfo_%s"), metaDataClass);
        }

        public SocialNetDetails(BaseElement<?, ?> parent, By locatorTemplate, Class<? extends MetaData> metaDataClass) {
            super(parent, ByT.id("crmForm:socialNetInfo_%s"), metaDataClass);
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
        public void fill(TestData td) {
            if (td.containsKey(name)) {
                setValue(td.getTestDataList(name));
                RetryService.run(pred -> !new StaticElement(By.id("crmForm:contactActionsCancelButton")).isPresent(), () -> {
                    buttonAddAllContacts.click();
                    return null;
                }, StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
            }
        }

        @Override
        protected void addSection(int index, int size) {}

        @Override
        protected void setSectionValue(int sectionIndex, TestData value) {
            if (!sectionExists(sectionIndex)) {
                if (value.getKeys().contains(ActionConstants.ACTIONS) && value.getValue(ActionConstants.ACTIONS).contains(CHANGE)) {
                    tableContactDetails.getRow(1).getCell(CustomerContactsTable.ACTION.getName()).controls.buttons.get(CHANGE).click();
                }
                else {
                    comboBoxSelectContactMethod.setValue(getContactType());
                    Waiters.SLEEP(2000).go();
                    buttonAddContact.click();
                }
            }

            super.setSectionValue(sectionIndex, value);
        }

        protected abstract String getContactType();
    }

}
