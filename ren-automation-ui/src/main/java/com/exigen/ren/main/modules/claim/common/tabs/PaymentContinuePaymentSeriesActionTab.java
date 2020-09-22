package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentContinuePaymentSeriesActionTabMetaData;

public class PaymentContinuePaymentSeriesActionTab extends ActionTab {

    public PaymentContinuePaymentSeriesActionTab() {
        super(PaymentContinuePaymentSeriesActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }

}
