package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData;
import org.openqa.selenium.By;

public class LineOverrideTab extends DefaultTab {
    public static StaticElement labelDecision = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_DentalEvaluationFeature_dentalFeature_statusReason']"));
    public static StaticElement labelRemarkCode = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:dentalFeatureRemarks']"));
    public static StaticElement labelRemarkCodeDescription = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:dentalFeatureRemarkDescriptions']"));

    public LineOverrideTab() {
        super(LineOverrideTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        Tab.buttonSaveAndExit.click();
        return this;
    }

}
