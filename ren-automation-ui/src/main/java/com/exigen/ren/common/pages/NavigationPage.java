/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.pages;

import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Stream;

public class NavigationPage extends Page {

    //Main tab template e.g. CEM, Billing, CMS. Include main tabs from Admin and Main apps
    private static final ByT MAIN_TAB_TEMPLATE = ByT.xpath("//form[@id='tabForm' or @id='adminTabsForm' or @id='tabForm:topTabsBarList' or @id='customerMainTab' or @id='caseForm']//a[span[.='%1s']]");

    //Left tab template e.g. Bulletin, Notes. Include tabs from Admin and Main apps
    private static final ByT LEFT_MENU_TEMPLATE = ByT.xpath("//*[@id='left_menu_form:left_menu' or @id='left_menu_form:left_menu_commission' or @id='policyDataGatherForm:tabTree']//span[.='%1s']");

    //Sub tabs template e.g. FNOL(for Claims), Companies. Include sub tabs from Admin and Main apps.
    private static final ByT SAB_TAB_TEMPLATE = ByT.xpath("//*[contains(@id, ':billingTabs') or @id='policyDataGatherForm:CFClaimOverviewDataGatherTabsList' "
            + "or @id='policyDataGatherForm:tabList' or @id='generalTab' or @id='customerMainTab:customerFirstTabs' or @id='pmBrokerForm:brokerInformationTabs' or @id='producContextInfoPanel' "
            + "or @id='producContextInfoForm:CFClaimOverviewTabsList' or @id='efolderMenuForm' or @id='printMenuForm' or contains(@id, 'CFClaimOverviewTabsList') or contains(@id, 'TabsList') "
            + "or @id='reportsSubTabsForm:reportTabsBarList' or @id='policyDataGatherForm:sideTree' or @id='crmForm' or @id='caseConsolidatedTabsForm:caseConsolidatedTabsList' "
            + "or @id='proposalForm:groupProposalDataGatherTabsList' or @id='tabsList' or @id='brokerSearchSelectForm' or contains(@id, 'TabBarList')]//a[span[.='%1s']]");

    //Templates for verification selected tabs
    private static final ByT MAIN_TAB_SELECTED_TEMPLATE = ByT.xpath("//form[@id='tabForm' or @id='adminTabsForm' or @id='tabForm:topTabsBarList' or @id='customerMainTab' or @id='caseForm']//a[span[.='%1s']]/..");
    private static final ByT SUB_TAB_SELECTED_TEMPLATE = ByT.xpath("//form[@id='adminCalendarTabsForm' or @id='crmForm' or @id='policyDataGatherForm' or @id='vendorContactInfoForm' or @id='customerMainTab' or @id='generalTab' or @id='efolderMenuForm' or @id='printMenuForm' or @id='left_menu_form' or @id='producContextInfoForm']//a[span[.='%s']]/..");

    public static void toMainTab(String tab) {
        new Link(MAIN_TAB_TEMPLATE.format(tab)).click();
    }

    public static void toMainTab(NavigationEnum.Navigational tab) {
        new Link(MAIN_TAB_TEMPLATE.format(tab.get())).click();
    }

    public static boolean isMainTabPresent(String tab) {
        return new Link(MAIN_TAB_TEMPLATE.format(tab)).isPresent();
    }

    public static boolean isMainTabPresent(NavigationEnum.Navigational tab) {
        return new Link(MAIN_TAB_TEMPLATE.format(tab.get())).isPresent();
    }

    public static boolean isMainTabSelected(NavigationEnum.Navigational tab) {
        return new StaticElement(MAIN_TAB_SELECTED_TEMPLATE.format(tab.get())).getAttribute("class").contains("selected");
    }

    public static void toLeftMenuTab(String tab) {
        new Link(LEFT_MENU_TEMPLATE.format(tab)).click();
    }

    public static void toLeftMenuTab(NavigationEnum.Navigational tab) {
        new Link(LEFT_MENU_TEMPLATE.format(tab.get())).click();
    }

    public static boolean isLeftMenuPresent(String tab) {
        return new Link(LEFT_MENU_TEMPLATE.format(tab)).isPresent();
    }

    public static void toSubTab(String tab) {
        new Link(SAB_TAB_TEMPLATE.format(tab)).click();
    }

    public static void toSubTab(String tab, String linkTemplate) {
        new Link(ByT.xpath(linkTemplate).format(tab)).click();
    }

    public static void toSubTab(NavigationEnum.Navigational tab) {
        new Link(SAB_TAB_TEMPLATE.format(tab.get())).click();
    }

    public static boolean isSubTabSelected(NavigationEnum.Navigational tab) {
        return new StaticElement(SUB_TAB_SELECTED_TEMPLATE.format(tab.get())).getAttribute("class").contains("sel");
    }

    public static boolean isSubTabSelected(String tab) {
        return new StaticElement(SUB_TAB_SELECTED_TEMPLATE.format(tab)).getAttribute("class").contains("sel");
    }

    public static boolean isSubTabPresent(String tab) {
        return new Link(SAB_TAB_TEMPLATE.format(tab)).isPresent();
    }

    public static boolean isSubTabPresent(NavigationEnum.Navigational tab) {
        return new Link(SAB_TAB_TEMPLATE.format(tab.get())).isPresent();
    }

    //Directly navigation to Main tab, Left tab, Sub tab
    public static void directlyTo(NavigationEnum.Navigational mainTab, NavigationEnum.Navigational leftTab, NavigationEnum.Navigational subTab) {
        toMainTab(mainTab);
        toLeftMenuTab(leftTab);
        toSubTab(subTab);
    }

    //Directly navigation to Main tab, Left tab
    public static void directlyTo(NavigationEnum.Navigational mainTab, NavigationEnum.Navigational leftTab) {
        toMainTab(mainTab);
        toLeftMenuTab(leftTab);
    }

    public static ComboBox comboBoxListAction = new ComboBox(By.xpath("//select[@id='productContextInfoForm:moveToBox' or @id='producContextInfoForm:moveToBox' "
            + "or @id='producContextInfoForm:moveToBox:0:moveToDropdown' or @id='productContextInfoForm:moveToBox:0:moveToDropdown' "
            + "or @id='endorsementForm:endorsementList:0:moveToDropdown' or @id='renewalForm:renewals_list_table:0:moveToDropdown' "
            + "or @id='billingInfoForm:moveToDropdown' or @id='custInfoForm:actionsForCustomerHeaderId' or @id='oppInfoForm:actionsForOpportunity' "
            + "or @id='acctInfoForm:actionsForCustomerHeaderId' or @id='actionsMenu' or @id='producContextInfoForm:select' or @id='caseProfileContextInfoForm:moveToBox'"
            + "or @id='schemeActionForm:actionSelect' or @id='quoteForm:moveToDropdown']"));

    public static void setActionAndGo(String action) {
        comboBoxListAction.setValue(action);
        if (Tab.buttonGo.isPresent()) {
            Tab.buttonGo.click();
        }
    }

    //Navigation to tree tab
    public static void toTreeTab(String treeTabs) {
        String tabXpath = "//div[@title='%s']";
        StringBuilder tabFullXpath = new StringBuilder();
        Stream.of(treeTabs.split("\\|")).forEach(tab -> {
            tabFullXpath.append(String.format(tabXpath, tab));
            new Link(By.xpath(tabFullXpath.toString())).click();
            tabFullXpath.append("/..");
        });
    }

    //Separate class for policy navigation
    public static class PolicyNavigation {

        private static final ByT MAIN_MENU_TAB_TMPL = ByT.xpath("//*[contains(@id, 'policyDataGatherForm:tabList')]//*[.='%1s']");
        private static final ByT LEFT_MENU_TAB_TMPL = ByT.xpath("//*[@id='policyDataGatherForm:tabTree']//*[.='%1s']");

        private static final ByT LEFT_MENU_TAB_SELECTED_TMPL = ByT.xpath("//*[@id='policyDataGatherForm:tabTree']//*[.='%1s']/div/span[2]");

        public static void expand(String parentTab, String tab) {
            List<WebElement> pluses = BrowserController.get().driver().findElements(By.xpath("//span[@class='rf-trn-hnd-colps rf-trn-hnd']"));
            pluses.forEach(WebElement::click);
            new Link(By.xpath(String.format("//div[@id='policyDataGatherForm:sideTree']//div[@title='%1s']/..//span[text()='%2s']", parentTab, tab))).click();
        }

        public static void mainMenu(String tabName) {
            new Link(MAIN_MENU_TAB_TMPL.format(tabName)).click();
        }

        public static boolean isMainMenuTabSelected(String tabName) {
            return new StaticElement(MAIN_MENU_TAB_TMPL.format(tabName)).getAttribute("class").contains("selected");
        }

        public static void leftMenu(String tabName) {
            new Link(LEFT_MENU_TAB_TMPL.format(tabName)).click();
        }

        public static void leftMenu(String tabName, Waiter waiter) {
            new Link(LEFT_MENU_TAB_TMPL.format(tabName)).click(waiter);
        }


        public static boolean isLeftMenuTabSelected(String tabName) {
            return new Link(LEFT_MENU_TAB_SELECTED_TMPL.format(tabName)).getAttribute("class").contains("sel");
        }
    }

    public static class WebStudioNavigation {
        private static final String NAVIGATION_TMPL = "//a[.='%s']";

        public static void navigateTo(NavigationEnum.Navigational page) {
            if (new Link(By.xpath(String.format(NAVIGATION_TMPL, page.get()))).isEnabled()) {
                new Link(By.xpath(String.format(NAVIGATION_TMPL, page.get()))).click();
            }
        }
    }

    //Separate class for case navigation
    public static class CaseNavigation {

        private static final ByT MAIN_MENU_TAB_TMPL = ByT.xpath("//*[contains(@id, 'caseConsolidatedTabsForm:caseConsolidatedTabsList')]//*[.='%1s']");

        public static void mainMenu(String tabName) {
            new Link(MAIN_MENU_TAB_TMPL.format(tabName)).click();
        }

        public static boolean isMainMenuTabSelected(String tabName) {
            return new StaticElement(MAIN_MENU_TAB_TMPL.format(tabName)).getAttribute("class").contains("selected");
        }

    }
}
