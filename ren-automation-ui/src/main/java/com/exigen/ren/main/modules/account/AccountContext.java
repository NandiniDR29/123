package com.exigen.ren.main.modules.account;

import com.exigen.ren.common.AutomationContext;

public interface AccountContext {

    Account accountIndividual = AutomationContext.getService(AccountIndividual.class);
    Account accountNonIndividual = AutomationContext.getService(AccountNonIndividual.class);
}
