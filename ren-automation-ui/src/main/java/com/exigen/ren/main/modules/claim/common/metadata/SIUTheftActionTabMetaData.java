/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class SIUTheftActionTabMetaData extends MetaData {
    public static final AssetDescriptor<RadioGroup> ADDRESS_IS_A_POST_OFFICE_BOX_OR_MAIL_DROP = declare("Address is a post office box or mail drop", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> DOES_NOT_HAVE_A_TELEPHONE = declare("Does not have a telephone", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> IS_UNEMPLOYED = declare("Is unemployed", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> IS_VERY_KNOWLEDGEABLE_OF_CLAIM_PROCESS_AND_INSURANCE_TERMINOLOGY =
            declare("Is very knowledgeable of Claim process and insurance terminology", RadioGroup.class);
}
