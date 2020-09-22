/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class DamageActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DAMAGE_TYPE = declare("Damage Type", TextBox.class);
    public static final AssetDescriptor<ComboBox> PARTY_TYPE = declare("Party Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> DAMAGE = declare("Damage", TextBox.class);
    public static final AssetDescriptor<TextBox> DAMAGE_DESCRIPTION = declare("Damage Description", TextBox.class);
    public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> ROLE = declare("Role", ComboBox.class);
    public static final AssetDescriptor<TextBox> ADDRESS = declare("Address", TextBox.class);
    public static final AssetDescriptor<TextBox> PHONE = declare("Phone", TextBox.class);
    public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class);
    public static final AssetDescriptor<TextBox> COMPANY_NAME = declare("Company Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> SERVICE_TYPE = declare("Service Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_PHONE = declare("Default Contact Phone #", TextBox.class);
    public static final AssetDescriptor<TextBox> DEFAULT_CONTACT_EMAIL = declare("Default Contact Email", TextBox.class);
    public static final AssetDescriptor<TextBox> RESERVE = declare("Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> UNALLOCATED_AMOUNT = declare("Unallocated Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> ALLOCATED_AMOUNT = declare("Allocated Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_AMOUNT = declare("Total Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> DATE_OF_TRANSACTION = declare("Date of Transaction", TextBox.class);
    public static final AssetDescriptor<TextBox> RESERVE_TYPE = declare("Reserve Type", TextBox.class);
    public static final AssetDescriptor<TextBox> NEW_RESERVE = declare("New Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> OLD_RESERVE = declare("Old Reserve", TextBox.class);
    public static final AssetDescriptor<TextBox> UPDATE_AMOUNT = declare("Update Amount", TextBox.class);
    public static final AssetDescriptor<TextBox> TRANSACTION = declare("Transaction", TextBox.class);
    public static final AssetDescriptor<TextBox> TRANSACTION_COMMENT = declare("Transaction Comment", TextBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_NUMBER = declare("Feature Number", TextBox.class);
    public static final AssetDescriptor<TextBox> FEATURE_ID = declare("Feature ID", TextBox.class);
    public static final AssetDescriptor<TextBox> CLAIMANT = declare("Claimant", TextBox.class);
    public static final AssetDescriptor<TextBox> ASSOCIATED_INSURABLE_RISK = declare("Associated Insurable Risk", TextBox.class);
    public static final AssetDescriptor<TextBox> COVERAGE = declare("Coverage", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_INCURRED = declare("Total incurred", TextBox.class);
    public static final AssetDescriptor<ComboBox> FEATURE_STATUS = declare("Feature Status", ComboBox.class);
}
