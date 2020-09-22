/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.customer.metadata.ViewHistoryActionTabMetaData;
import org.openqa.selenium.By;

import java.util.stream.Stream;

public class ViewHistoryActionTab extends ActionTab {
    public static Link linkHistoryPanel = new Link(By.xpath("//div[@id='customerHistoryTogglePanel_0:header']"));
    public static Link linkEmployeeInformation = new Link(By.xpath("//div[contains(@id, 'customerHistoryTogglePanel')]//div[.='Employee Information']/.."));
    public static Link linkMembershipInformation = new Link(By.xpath("//div[contains(@id, 'customerHistoryTogglePanel')]//div[.='Membership Information']/.."));
    public static Link linkStudentInformation = new Link(By.xpath("//div[contains(@id, 'customerHistoryTogglePanel')]//div[.='Student Information']/.."));
    public static Link linkAdditionalName = new Link(By.xpath("//div[contains(@id, 'customerHistoryTogglePanel')]//div[contains(text(), 'Additional Name')]/.."));

    public static StaticElement parent = new StaticElement(By.xpath("//div[contains(@id,':content') and contains(@style,'block')]"));

    public static TextBox textBoxVersionDate = new TextBox(By.id("allHistoryForm:viewCustomerHistory_customerHistoryDateInputDate"));

    public static Button buttonViewAllVersions = new Button(By.xpath("//div[@id='allHistoryForm:inputWrapper-viewCustomerHistory_customerHistoryDate']/a"));

    public static Table tableGeneralInfo = new Table(By.id("indGeneralInfoCompareTable"));
    public static Table tableEmployeeInformation = new Table(By.id("employmentParticipationDetailsCompareTable"));
    public static Table tableStudentInformation = new Table(By.id("studentParticipationDetailsCompareTable"));
    public static Table tableMembershipInformation = new Table(By.id("membershipParticipationDetailsCompareTable"));
    public static Table tableAdditionalName = new Table(By.id("nonIndvAdditionalNameCompareTable"));
    public static Table tableIndvAdditionalName = new Table(By.id("indvAdditionalNameCompareTable"));
    public static Table tableBusinessEntity = new Table(By.xpath("//th[normalize-space(text())='Business Entity']/ancestor::table[1]"));
    public static Table tableDivision = new Table(By.xpath("//th[normalize-space(text())='Division']/ancestor::table[1]"));
    public static Table tableEmailDetails = new Table(By.xpath("//th[contains(., 'Email Details')]/ancestor::table[1]"));
    public static Table tableAddressDetails = new Table(By.xpath("//th[contains(., 'Address Details')]/ancestor::table[1]"));
    public static Table tablePhoneDetails = new Table(By.xpath("//th[contains(text(), 'Entity Phone Details') or contains(text(),'Division Phone Details')]/ancestor::table[1]"));
    public static Table tableChatDetails = new Table(By.xpath("//th[contains(., 'Chat Details')]/ancestor::table[1]"));
    public static Table tableSocialNetDetails = new Table(By.xpath(".//th[contains(., 'Social Net')]/ancestor::table[1]"));
    public static Table tableWebURLDetails = new Table(By.xpath("//th[contains(., 'Web URL')]/ancestor::table[1]"));
    public static Table tableIndvProductOwned = new Table(By.id("productOwnedCompareTable"));
    public static Table tableAssignmentInfoCompare = new Table(By.id("assignmentInfosCompareTable"));
    public static Table tableSalesInfoCompare = new Table(By.id("salesInfoCompareTable"));
    public static Table tableBrandCompare = new Table(By.id("brandsCompareTable"));
    public static Table additionalInfoCompareTable = new Table(By.id("additionalInfoCompareTable"));
    public static Table nonIndvAdditionalInfoCompareTable = new Table(By.id("nonIndvAdditionalInfoCompareTable"));

    public static ComboBox comboBoxAdditionalNamesVersions = new ComboBox(By.id("versionCompareForm1:firstVersions"));

    private static final String LINK_NAME_SECTION = "//div[contains(@id, 'customerHistoryTogglePanel')]//div[contains(.,'%s %s')]/..";
    private static final String LABEL_MESSAGE_NO_VERSION = "//span[contains(.,'No corresponding prior version exists for this component')]";
    private static ByT tableLocatorTemplate = ByT.xpath("//div[text() = '%s']/ancestor::div[contains(@id, 'customerHistoryTogglePanel')]//form/table");
    private static ByT linkByNameLocatorTemplate = ByT.xpath("//div[@class = 'rf-cp-lbl-colps' and text() =  '%s']");
    public static ByT linkByNumberLocatorTemplate = ByT.xpath("//div[@id = 'customerHistoryTogglePanel_%d']");

    public ViewHistoryActionTab() {
        super(ViewHistoryActionTabMetaData.class);
        assetList = new AssetList(By.xpath("//*"), metaDataClass);
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        return this;
    }

    public static void expandAllHistoryTable() {
        Stream.iterate(0, i -> i + 1).limit(20).filter(i -> new Link(linkByNumberLocatorTemplate.format(i))
                .isPresent()).forEach(i -> new Link(linkByNumberLocatorTemplate.format(i)).click());
    }

    public static Table getTableAndExpand(String tableName) {
        Table table = new Table(tableLocatorTemplate.format(tableName));
        expandTable(table, tableName);
        return table;
    }

    private static void expandTable(Table table, String tableName) {
        if (!table.isVisible()) {
            new Link(linkByNameLocatorTemplate.format(tableName)).click();
        }
    }

    public void expandCollapseSection(String nameSection, String businessEntityName) {
        Link linkExpandBusinessEntity = new Link(By.xpath(String.format(LINK_NAME_SECTION + "//div", nameSection, businessEntityName)));
        linkExpandBusinessEntity.click();
    }

    public boolean isExistMessageNoVersion(String nameSection, String verifiedName) {
        Link labelMessageNoVersion = new Link(By.xpath(String.format(LINK_NAME_SECTION + "%s", nameSection, verifiedName, LABEL_MESSAGE_NO_VERSION)));
        return labelMessageNoVersion.isPresent();
    }
}
