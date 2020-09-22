package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.ClaimCancelActionTabMetaData;
import org.openqa.selenium.By;

public class ClaimCancelActionTab extends ActionTab {

    public static final Button buttonCancelClaim = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsDentalCancelClaimAction_footer"));

    public ClaimCancelActionTab() {
        super(ClaimCancelActionTabMetaData.class);
    }
}
