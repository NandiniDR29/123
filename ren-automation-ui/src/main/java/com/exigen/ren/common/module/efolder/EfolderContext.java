package com.exigen.ren.common.module.efolder;

import com.exigen.ren.common.AutomationContext;

public interface EfolderContext {

    Efolder efolder = AutomationContext.getService(Efolder.class);

}
