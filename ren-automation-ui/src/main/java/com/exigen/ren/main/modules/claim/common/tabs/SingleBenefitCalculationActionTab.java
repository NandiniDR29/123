/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.claim.common.metadata.SingleBenefitCalculationActionTabMetaData;
import org.openqa.selenium.By;

public class SingleBenefitCalculationActionTab extends ActionTab {
    public static TableExtended<EliminationQualificationPeriod> eliminationQualificationPeriodTable = new TableExtended<>(
            "Elimination/Qualification Period", By.xpath("//div[@id='policyDataGatherForm:componentInstancesTable_PrecClaimsEliminationQualificationPeriodView']//table"));

    public SingleBenefitCalculationActionTab() {
        super(SingleBenefitCalculationActionTabMetaData.class);
    }

    public enum EliminationQualificationPeriod implements Named {
        ELIMINATION_PERIOD_START_DATE("Elimination Period Start Date"),
        ELIMINATION_PERIOD("Elimination Period"),
        ELIMINATION_PERIOD_END_DATE("Elimination Period End Date"),
        QUALIFICATION_PERIOD_START_DATE("Qualification Period Start Date"),
        QUALIFICATION_PERIOD("Qualification Period"),
        QUALIFICATION_PERIOD_END_DATE("Qualification Period End Date");
        private String name;

        EliminationQualificationPeriod (String name) {this.name = name;}

        @Override
        public String getName() {
            return name;
        }
    }

}
