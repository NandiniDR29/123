package com.exigen.ren.rest.platform.providers;

import com.exigen.ren.common.AutomationContext;

public interface ProviderRestServiceContext {
    ProviderRestService providerRestService = AutomationContext.getService(ProviderRestService.class);
}
