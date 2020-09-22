/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs;


import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.COVERAGES;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;

public class PlansTab extends DefaultTab {

    public static final TableExtended<TableConstants.CertificateParticipants> tableParticipantsList = new TableExtended<>("Participants", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListGroupCertificateParticipant']/div/table"));

    public static final Button buttonAddParticipant = new Button(By.id("policyDataGatherForm:addGroupCertificateParticipant"));

    public PlansTab() {
        super(PlansTabMetaData.class);
    }

    @Override
    public String getTabName() {
        return COVERAGES.get();
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

    public static void updateExistingParticipantByRoleName(String roleName) {
        LOGGER.info(String.format("Update participant : '%s'", roleName));
        PlansTab.tableParticipantsList.getRow(TableConstants.CertificateParticipants.ROLE_NAME.getName(), roleName).getCell(6).controls.links.get(CHANGE).click();
    }
}
