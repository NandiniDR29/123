package com.exigen.ipb.eisa.controls;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab;
import org.openqa.selenium.By;

public class BeneficiaryMultiAssetList extends MultiAssetList {
    public static final Button buttonAddBeneficiary = new Button(By.id("policyDataGatherForm:addGroupCertificateB"));

    public BeneficiaryMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
        super(locator, metaDataClass);
    }

    public BeneficiaryMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
        super(parent, locator, metaDataClass);
    }

    @Override
    protected boolean sectionExists(int index) {
        return CoveragesTab.tableBeneficiariesList.getRow(index + 1).isPresent();
    }

    @Override
    protected void addSection(int i, int i1) {

    }

    @Override
    protected void selectSection(int i) {
        buttonAddBeneficiary.click();
    }
}
