package com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs;


import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData;
import org.openqa.selenium.By;

public class BenefitsPFLParticipantInformationTab extends DefaultTab {
    public static StaticElement lableAddressLine1= new StaticElement(By.id("policyDataGatherForm:sedit_PaidFamilyLeavePartyAddressContact_addressContact_address_addressLine1"));

    public BenefitsPFLParticipantInformationTab() {
        super(BenefitsPFLParticipantInformationTabMetaData.class);
    }
}
