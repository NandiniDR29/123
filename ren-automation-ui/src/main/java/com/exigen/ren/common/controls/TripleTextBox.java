package com.exigen.ren.common.controls;

import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.AbstractRepeatStringElement;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.HighlightableElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

import java.util.List;

public class TripleTextBox extends AbstractRepeatStringElement<TextBox> implements HighlightableElement {
    private static ByT locatorTemplate = ByT.xpath(".//descendant::input[last()-1 and position() = %1s]");

    public TripleTextBox(By locator) {
        this(null, locator, Waiters.DEFAULT);
    }

    public TripleTextBox(By locator, Waiter waiter) {
        this(null, locator, waiter);
    }

    public TripleTextBox(BaseElement<?, ?> parent, By locator) {
        this(parent, locator, Waiters.DEFAULT);
    }

    public TripleTextBox(BaseElement<?, ?> parent, By locator, Waiter waiter) {
        super(parent, locator, waiter, TextBox.class, 3);
    }

    @Override
    protected By controlLocator(int index) {
        return locatorTemplate.format(index + 1);
    }

    public void setValueByIndex(int index, String value) {
        controls.get(index).setValue(value);
    }

    public List<TextBox> getControls() {
        return controls;
    }

    public TextBox getControlByIndex(int index) {
        return getControls().get(index);
    }
}