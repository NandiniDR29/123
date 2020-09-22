package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.ClaimAdjustActionTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.ClaimPendClaimActionTabMetaData;
import org.openqa.selenium.By;

public class ClaimPendClaimActionTab extends ActionTab {

    public static final Button buttonPendClaim = new Button(By.id("policyDataGatherForm:pendClaimBtn_DentalPendClaimActionComponent_footer"));

    public ClaimPendClaimActionTab() {
        super(ClaimPendClaimActionTabMetaData.class);
    }
}
