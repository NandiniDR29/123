package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData;
import org.openqa.selenium.By;

public class RecoveryDetailsActionTab extends ActionTab {

    public static Button buttonPostRecovery = new Button(By.id("policyDataGatherForm:postRecoveryBtn_ClaimsDentalRecoveryPostAction"));
    public static Button buttonUpdateRecovery = new Button(By.id("policyDataGatherForm:postRecoveryBtn_ClaimsDentalRecoveryUpdateAction"));

    public RecoveryDetailsActionTab() {
        super(RecoveryDetailsActionTabMetaData.class);
    }

}