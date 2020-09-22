package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.claim.common.metadata.ClaimContinueActionTabMetaData;
import org.openqa.selenium.By;

public class ClaimContinueActionTab extends ActionTab {
    public static Button buttonContinueClaim = new Button(By.xpath("//input[@value='Continue Claim' and not(contains(@style, 'none'))]"));

    public ClaimContinueActionTab() {
        super(ClaimContinueActionTabMetaData.class);
    }
}