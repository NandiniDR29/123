/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissiontrategy.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class CommissionPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.xpath("//form[contains(@id, 'SearchForm')]"), CommissionSearchTabMetaData.class);

    public static Button buttonAddNewCommissionGroup = new Button(By.id("groupSearchForm:addGroupBtn_footer"));
    public static Button buttonAddNewCommissionStrategy = new Button(By.id("strategySearchForm:addStrategyBtn_footer"));
    public static Button buttonAddCommissionBonus = new Button(By.id("bonusSearchForm:addBonusBtn_footer"));
    public static Button buttonAddCommissionReferral = new Button(By.id("referralSearchForm:addReferralBtn_footer"));
    public static Button buttonAddBulkAdjustment = new Button(By.id("adjustmentSearchForm:addAdjustmentBtn_footer"));
    public static Button buttonAddBulkAdjustmentRule = new Button(By.id("adjustmentTopForm:addRuleBtn"));
    public static Button buttonSaveCommissionStrategy = new Button(By.id("strategyTopForm:saveBtn_footer"));
    public static Button buttonSaveBulkAdjustment = new Button(By.xpath("//.[@id='adjustmentTopForm:saveBtn' or @id='adjustmentTopForm:saveBtn_footer']"));
    public static Button buttonExpireCommissionGroup = new Button(By.id("groupSearchForm:expireGroupBtn"));
    public static Button buttonExpireCommissionStrategy = new Button(By.id("strategySearchForm:expireStrategyBtn"));
    public static Button buttonExpireCommissionBonus = new Button(By.id("bonusSearchForm:expireBonusBtn"));
    public static Button buttonExpireCommissionReferral = new Button(By.id("referralSearchForm:expireReferralBtn"));
    public static TextBox textBoxExpirationDate = new TextBox(By.id("expireForm:expireTable_expirationDateInputDate"));

    public static Table tableCommissionGroup = new Table(By.id("groupSearchForm:body_groupSearchTable"));
    public static TableExtended<CommissionStrategies> tableCommissionStrategies = new TableExtended<>("CommissionStrategies", By.id("strategySearchForm:body_strategySearchTable"));
    public static Table tableCommissionBonus = new Table(By.id("bonusSearchForm:body_bonusSearchTable"));
    public static Table tableCommissionReferral = new Table(By.id("referralSearchForm:body_referralSearchTable"));
    public static Table tableCommissionRules = new Table(By.id("strategyTopForm:gradedRulesTable"));
    public static Table tableCommissionMarketingFeeRules = new Table(By.xpath("//div[@id='strategyTopForm:marketingRulesTable']//table"));
    public static Table tableBulkAdjustment = new Table(By.id("adjustmentSearchForm:body_AdjustmentSearchTable"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public enum CommissionStrategies implements Named {
        COMMISSION_STRATEGY_FOR_PRODUCT("Commission Strategy for Product"),
        EFFECTIVE_DATE("Effective Date"),
        EXPIRATION_DATE("Expiration Date"),
        STATUS("Status"),
        ACTION("Action");
        private String name;

        CommissionStrategies(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
