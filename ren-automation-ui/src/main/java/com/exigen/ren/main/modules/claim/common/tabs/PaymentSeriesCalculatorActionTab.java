package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimPaymentsTab.CALCULATOR;

public class PaymentSeriesCalculatorActionTab extends ActionTab {

    public static Button buttonCreatePaymentSeries = new Button(By.xpath("//input[@value='Create Payment Series']"));
    public static TableExtended<PaymentCalculatorPaymentAddition> tableListOfPaymentAddition = new TableExtended<>("List of Payment Addition",
            By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListClaimsPaymentCalculatorAdditionTemplate']//table"));
    public static final TableExtended<ListOfPaymentReduction> tableListOfPaymentReduction = new TableExtended<>("List of Payment Reduction",
            By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListClaimsPaymentCalculatorReductionTemplate']//table"));

    public PaymentSeriesCalculatorActionTab() {
        super(PaymentSeriesCalculatorActionTabMetaData.class);
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return CALCULATOR.get();
    }

    public enum ListOfPaymentReduction implements Named {
        REDUCTION_TYPE("Reduction Type"),
        BEGINNING_DATE("Beginning Date"),
        THROUGH_DATE("Through Date"),
        AMOUNT("Amount"),
        EXCLUDED("Excluded");

        private String name;

        ListOfPaymentReduction(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PaymentCalculatorPaymentAddition implements Named {
        ADDITION_TYPE("Addition Type"),
        AMOUNT("Amount");

        private String name;

        PaymentCalculatorPaymentAddition(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class PaymentAdditionMultiAssetList extends MultiAssetList {
        public static final Button addPaymentAddition = new Button(By.id("policyDataGatherForm:addClaimsPaymentCalculatorAdditionTemplate"));

        public PaymentAdditionMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        public PaymentAdditionMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            addPaymentAddition.click();
        }

        @Override
        protected void selectSection(int i) {
        }
    }
}
