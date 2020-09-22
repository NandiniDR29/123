/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.ClaimHandlingSpecialHandlingTabMetaData;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLTab.SPECIAL_HANDLING;

public class ClaimHandlingSpecialHandlingTab extends DefaultTab {
    public ClaimHandlingSpecialHandlingTab() {
        super(ClaimHandlingSpecialHandlingTabMetaData.class);
    }

    @Override
    public Tab navigate() {
        NavigationPage.toLeftMenuTab(NavigationEnum.ClaimFNOLTab.CLAIM_HANDLING);
        NavigationPage.toLeftMenuTab(SPECIAL_HANDLING);
        return this;
    }

    @Override
    public String getTabName() {
        return SPECIAL_HANDLING.get();
    }
}
