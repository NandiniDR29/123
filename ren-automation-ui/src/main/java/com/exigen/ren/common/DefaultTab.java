/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common;

import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract DefaultTab class.
 */
public class DefaultTab extends Tab {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultTab.class);

    public DefaultTab(Class<? extends MetaData> mdClass) {
        super(mdClass);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
}
