/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.components;

import com.exigen.istf.webdriver.controls.StaticElement;
import org.openqa.selenium.By;

public class DialogMoratoriumWarning extends Dialog {

    public StaticElement labelMoratoriumWarning;
    public StaticElement labelMoratoriumOverrideReason;

    public DialogMoratoriumWarning(String dialogLocator) {

        super(dialogLocator);

        labelMoratoriumWarning = new StaticElement((By.id("quoteForm:moratoriumExistsMessage")));
        labelMoratoriumOverrideReason=new StaticElement(By.xpath("//*[@id='policyDataGatherForm:overrideGridId']/tbody/tr/td[1]"));
    }
}
