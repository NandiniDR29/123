/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class CertificatePolicyTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> MASTER_POLICY_NUMBER = declare("Master Policy Number", TextBox.class);
    public static final AssetDescriptor<TextBox> GROUP_CUSTOMER_NAME = declare("Group Customer Name", TextBox.class);
    public static final AssetDescriptor<TextBox> POLICY_EFFECTIVE_DATE = declare("Policy Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> CREATION_DATE = declare("Creation Date", TextBox.class);
    public static final AssetDescriptor<TextBox> QUOTE_EXPIRATION_DATE = declare("Quote Expiration Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
    public static final AssetDescriptor<TextBox> ANNIVERSARY_DATE = declare("Anniversary Date", TextBox.class);
    public static final AssetDescriptor<TextBox> RENEWAL_DATE = declare("Renewal Date", TextBox.class);
    public static final AssetDescriptor<TextBox> CANCELLATION_DATE = declare("Cancellation Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> ISSUE_COUNTRY = declare("Issue Country", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ISSUE_STATE = declare("Issue State", ComboBox.class);
    public static final AssetDescriptor<ComboBox> SITUS_STATE = declare("Situs State", ComboBox.class);
    public static final AssetDescriptor<ComboBox> ENROLLMENT_TYPE = declare("Enrollment Type", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BILLING_LOCATION = declare("Billing Location", ComboBox.class);
}
