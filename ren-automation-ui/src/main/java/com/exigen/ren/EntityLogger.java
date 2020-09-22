/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.admin.modules.agencyvendor.brand.metadata.BrandMetaData;
import com.exigen.ren.admin.modules.agencyvendor.brand.pages.BrandPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.groupsinformation.tabs.GroupsInformationTab;
import com.exigen.ren.admin.modules.cem.majorlargeaccount.tabs.MajorLargeAccountTab;
import com.exigen.ren.admin.modules.commission.bulkadjustment.tabs.BulkAdjustmentRuleTab;
import com.exigen.ren.admin.modules.commission.bulkadjustment.tabs.BulkAdjustmentTab;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.pages.CommissionPage;
import com.exigen.ren.admin.modules.general.note.tabs.NoteCategoryTab;
import com.exigen.ren.admin.modules.general.numberrange.tabs.NumberRangeTab;
import com.exigen.ren.admin.modules.security.par.metadata.PARGeneralMetaData;
import com.exigen.ren.admin.modules.security.par.tabs.PARGeneralTab;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.admin.modules.workflow.processmanagement.tabs.ManualTaskDefinitionTab;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class EntityLogger {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EntityLogger.class);

    public enum EntityType {
        ACCOUNT,
        AGENCY_VENDOR,
        AGENCY_NAME,
        BILLING_ACCOUNT,
        BRAND,
        BRAND_TYPE,
        BULK_ADJUSTMENT,
        BULLETIN,
        CASEPROFILE,
        CAMPAIGN,
        CLAIM,
        CLAIM_TERM_LIFE,
        COMMISSION_BONUS,
        COMMISSION_GROUP,
        CUSTOMER,
        FEE_GROUP,
        GROUPS_INFORMATION,
        LEAD_IMPORT,
        MAJOR_LARGE_ACCOUNT,
        NOTE,
        NUMBER_RANGE,
        POLICY,
        POLICY_CERTIFICATE,
        PROFILE,
        QUOTE,
        ROLE,
        PAR,
        TASK,
        TAX_FEE_REGISTRY,
        TAX_FEE_STRATEGY,
        VENDOR
    }

    public static String getEntityHeader(EntityType quote) {
        String content = "";
        String entityId = "";
        String entityType = "";
        String[] headers, values;

        try {
            switch (quote) {

                case CUSTOMER:
                    content = new StaticElement(By.id("custInfoForm:grid")).getValue().replaceAll(":\n", ": ").replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Customer ID: ").split(";")[0];

                    content = "Customer #" + entityId + " (" + StringUtils.replaceEach(content.replace("Customer ID: " + entityId, "").trim(),
                            new String[]{"; ;", "  ", ";)"}, new String[]{";", " ", ")"}) + ")";
                    break;

                case ACCOUNT:
                    content = new StaticElement(By.id("acctInfoForm:header_panel")).getValue().replaceAll("\n", ": ").replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Account #: ").split(";")[0];
                    content = "Account #" + entityId + " (" + StringUtils.replaceEach(content.replace("Account #: " + entityId, "").trim(),
                            new String[]{"; ;", "  ", ";)"}, new String[]{";", " ", ")"}) + ")";
                    break;

                case CLAIM:
                    StaticElement claimHeaderNumber = new StaticElement(By.xpath("//div[@id='producContextInfoForm:header_panel']//div[@class='header']/div"));
                    content = claimHeaderNumber.isPresent() ? claimHeaderNumber.getValue() : new StaticElement(By.xpath("//div[@id='claimsDatagatherForm:header_panel']//div[@class='header']/div")).getValue();
                    break;

                case CLAIM_TERM_LIFE:
                    content = new StaticElement(By.xpath("//div[@id='producContextInfoForm:header_panel']//div[@class='header']/div")).getValue() + " ("
                            + new StaticElement(By.xpath("//span[@id='producContextInfoForm:headerColumn_0']/div[2]")).getValue() + ")";
                    content = content.replace("\n", "").replace("Claim:", "").trim();
                    break;

                case POLICY:
                    content = new StaticElement(By.id("productContextInfoForm:grid")).getValue().replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Policy #: ").split(";")[0];
                    entityType = StringUtils.substringAfter(content, "Product Name: ").split(";")[0];

                    content = "#" + entityId + " (" + "Product Name: " + entityType + "; " + StringUtils.replaceEach(
                            StringUtils.replaceEach(content, new String[]{"Policy #: " + entityId, "Product Name: " + entityType}, new String[]{"", ""}).trim() + ")",
                            new String[]{"; ;", ";)"}, new String[]{";", ")"});
                    break;

                case POLICY_CERTIFICATE:
                    content = new StaticElement(By.id("productContextInfoForm:grid")).getValue().replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Certificate Policy #: ").split(";")[0];
                    entityType = StringUtils.substringAfter(content, "Product Name: ").split(";")[0];

                    content = "#" + entityId + " (" + "Product Name: " + entityType + "; " + StringUtils.replaceEach(
                            StringUtils.replaceEach(content, new String[]{"Certificate Policy #: " + entityId, "Product Name: " + entityType}, new String[]{"", ""}).trim() + ")",
                            new String[]{"; ;", ";)"}, new String[]{";", ")"});
                    break;

                case QUOTE:
                    content = new StaticElement(By.id("productContextInfoForm:grid")).getValue().replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Quote #: ").split(";")[0];
                    entityType = StringUtils.substringAfter(content, "Product Name: ").split(";")[0];

                    content = "#" + entityId + " (" + "Product Name: " + entityType + "; " + StringUtils.replaceEach(
                            StringUtils.replaceEach(content, new String[]{"Policy #: " + entityId, "Product Name: " + entityType}, new String[]{"", ""}).trim() + ")",
                            new String[]{"; ;", ";)"}, new String[]{";", ")"});
                    break;

                case AGENCY_VENDOR:
                    content = "(" + new StaticElement(By.xpath("//div[@id='contents']//table//table//tr[.//span[@id='nameLabel']][1]")).getValue().replace("Agency Code:", "; Agency Code:")
                            .replace(" ; ", "; ") + ")";
                    break;

                case AGENCY_NAME:
                    content = new StaticElement(By.xpath("//div[@id='contents']//table//table//span[@id='nameText']")).getValue();
                    break;

                case VENDOR:
                    content = new StaticElement(By.xpath("//div[@id='contents']//table//table//span[@id='nameLabel']")).getValue().replace("Vendor Name:", "Vendor Name:")
                            .replace(" ; ", "; ") + ")";
                    break;

                case CAMPAIGN:
                    content = "(" + trim(new CampaignTab().getAssetList().getValue().toString()) + ")";
                    break;

                case GROUPS_INFORMATION:
                    content = "(" + trim(new GroupsInformationTab().getAssetList().getValue().toString()) + ")";
                    break;

                case TASK:
                    content = "(" + trim(new ManualTaskDefinitionTab().getAssetList().getValue().toString()) + ")";
                    break;

                case NUMBER_RANGE:
                    content = "(" + trim(new NumberRangeTab().getAssetList().getValue().toString()) + ")";
                    break;

                case NOTE:
                    content = "(" + trim(new NoteCategoryTab().getAssetList().getValue().toString()) + ")";
                    break;

                case ROLE:
                    content = "(Channel: " + new GeneralRoleTab().getAssetList().getAsset(GeneralRoleMetaData.CHANNEL).getValue()
                            + "; Role Name: " + new GeneralRoleTab().getAssetList().getAsset(GeneralRoleMetaData.ROLE_NAME).getValue() + ")";
                    break;

                case PAR:
                    content = "(RoleCode = " + new PARGeneralTab().getAssetList().getAsset(PARGeneralMetaData.ROLE_CODE).getValue() + "; " +
                            "RoleName = " + new PARGeneralTab().getAssetList().getAsset(PARGeneralMetaData.ROLE_NAME).getValue() + ")";
                    break;

                case BRAND:
                    content = "(Brand Code = " + BrandPage.assetListAddBrand.getAsset(BrandMetaData.AddBrandDialog.BRAND_CODE).getValue() + "; "
                            + "Brand Name = " + BrandPage.assetListAddBrand.getAsset(BrandMetaData.AddBrandDialog.BRAND_NAME).getValue() + ")";
                    break;

                case BRAND_TYPE:
                    content = "(Brand Type Code = " + BrandPage.assetListAddBrandType.getAsset(BrandMetaData.AddBrandTypeDialog.BRAND_TYPE_CODE).getValue() + "; "
                            + "Brand Type Name = " + BrandPage.assetListAddBrandType.getAsset(BrandMetaData.AddBrandTypeDialog.BRAND_TYPE_NAME).getValue() + ")";
                    break;

                case MAJOR_LARGE_ACCOUNT:
                    content = "(" + trim(new MajorLargeAccountTab().getAssetList().getValue().toString()) + ")";
                    break;

                case BULK_ADJUSTMENT:
                    content = "(" + trim(new BulkAdjustmentTab().getAssetList().getValue().toString()) + ", "
                            + trim(new BulkAdjustmentRuleTab().getAssetList().getValue().toString()) + ")";
                    break;

                case COMMISSION_BONUS:
                    CommissionPage.buttonSearch.click();
                    headers = Arrays.copyOf(CommissionPage.tableCommissionBonus.getHeader().getValue().toArray(),
                            CommissionPage.tableCommissionBonus.getHeader().getValue().toArray().length, String[].class);
                    values = Arrays.copyOf(CommissionPage.tableCommissionBonus.getRow(1).getValue().toArray(),
                            CommissionPage.tableCommissionBonus.getRow(1).getValue().toArray().length, String[].class);
                    content = "(" + joinHeaders(headers, values) + ")";
                    break;

                case COMMISSION_GROUP:
                    content = "(" + trim(new CommissionGroupTab().getAssetList().getValue().toString());
                    break;

                default:
                    break;
            }
        } catch (Exception e) {

        }
        return content;
    }

    private static String joinHeaders(String[] headers, String[] values) {
        String content = "";
        for (int i = 0; i <= headers.length - 2; i++) {
            if (!values[i].equals("")) {
                content += headers[i] + " = " + values[i] + ", ";
            }
        }
        return content.substring(0, content.length() - 2);
    }

    private static String trim(String values) {
        String content = "";
        values = values.replace("{", "").replace("}", "");
        for (int i = 0; i <= values.split(",").length - 1; i++) {
            if (!values.split(",")[i].equals("")) {
                if (values.split(",")[i].split("=").length > 1) {
                    content += values.split(",")[i] + ", ";
                }
            }
        }
        return content.substring(0, content.length() - 2);
    }
}
