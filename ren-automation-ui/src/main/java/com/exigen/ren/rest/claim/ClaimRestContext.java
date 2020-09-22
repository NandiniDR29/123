package com.exigen.ren.rest.claim;

import com.exigen.ren.common.AutomationContext;

public interface ClaimRestContext {

    ClaimCoreRestService claimCoreRestService = AutomationContext.getService(ClaimCoreRestService.class);
    ClaimRENCoreRestService claimRENCoreRestService = AutomationContext.getService(ClaimRENCoreRestService.class);
    ClaimDentalRestService claimDentalRestService = AutomationContext.getService(ClaimDentalRestService.class);

}
