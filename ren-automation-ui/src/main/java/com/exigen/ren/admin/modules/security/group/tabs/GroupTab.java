/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.group.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.admin.modules.security.group.metadata.GroupMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.TableConstants;
import org.openqa.selenium.By;

public class GroupTab extends DefaultTab {
    public static TableExtended<TableConstants.Empty> tableGroups = new TableExtended<TableConstants.Empty>("Groups", By.id("groupSearchForm:groupSearchResult"),
            By.xpath("//form[@id='groupSearchForm']//span[@id='groupSearchForm:groupSearchResultDataScroller'  and not(contains(@style,'display:none'))]"));
    public static StaticElement groupRoles = new StaticElement(By.id("roleForm:allowRoles"));
    public static StaticElement groupUsers = new StaticElement(By.id("roleForm:users"));
    public static Button buttonReturn = new Button(By.xpath("//input[(@value = 'Return') and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static Button buttonSearch = new Button(By.id("groupSearchForm:searchButton"));

    public GroupTab() {
        super(GroupMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
