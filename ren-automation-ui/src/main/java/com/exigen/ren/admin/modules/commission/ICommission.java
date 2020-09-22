/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.IAdmin;
import com.exigen.ren.admin.modules.commission.common.actions.CommissionStrategySearchAction;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;

public interface ICommission extends IAdmin {

    void initiate();

    default void search(TestData td) {
        navigate();
        search().perform(td);
    }

    void submit();

    boolean searched(TestData td);

    default Action search() {
        return AutomationContext.getAction(CommissionStrategySearchAction.class);
    }

    Action expire();

    Action copy();

    Action delete();

    Action edit();

}
