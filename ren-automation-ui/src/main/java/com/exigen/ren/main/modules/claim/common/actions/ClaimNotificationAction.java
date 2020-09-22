/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;

public class ClaimNotificationAction implements Action {

    @Override
    public String getName() {
        return "Claim Notification";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(ClaimInquiryAction.class).getWorkspace();
    }

}
