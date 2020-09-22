package com.exigen.ipb.eisa.controls.ratingreport;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.meters.WaitMeters;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class RedistributionCalculatorComboBox extends AbstractEditableStringElement implements SelectableElement<String>, HighlightableElement {

    private StaticElement optionsHolder = new StaticElement(By.xpath("//body//div[@class=' css-26l3qy-menu']"));
    private StaticElement closeOptionsHolder = new StaticElement(By.xpath("//body//div[@class=' css-1pahdxg-control']"));

    public RedistributionCalculatorComboBox(By locator) {
        super(locator, Waiters.DEFAULT);
    }

    public RedistributionCalculatorComboBox(By locator, Waiter waiter) {
        super(locator, waiter);
    }

    public RedistributionCalculatorComboBox(BaseElement<?, ?> parent, By locator) {
        super(parent, locator, Waiters.DEFAULT);
    }

    public RedistributionCalculatorComboBox(BaseElement<?, ?> parent, By locator, Waiter waiter) {
        super(parent, locator, waiter);
    }

    protected List<WebElement> getOptions() {
        if (!this.getOptionsHolder().isPresent()) {
            this.click();
            this.getOptionsHolder().waitFor(BaseElement::isPresent);
        }
        return this.getOptionsHolder().getWebElement().findElements(By.tagName("div"));
    }

    /**
     * Select value in the combobox.
     *
     * @param value either literal string value to select or one of
     *              <ul>
     *              <li>index={n} where {n} is the index of the element to select</li>
     *              <li>contains={s} where {s} is a part of the visible value to select</li>
     *              <li>starts={s} where {s} is the starting part of the visible value to select</li>
     *              <li>label={l} - same as setRawValue({l})</li>
     *              <li>value={v} where {v} is value attribute of the element to select (as opposed to visible text)</li>
     *              <li>regex={r} where {r} is regular expression which the visible value to be selected should match</li>
     *              </ul>
     */
    @Override
    protected void setRawValue(String value) {
        String[] parsedValue = value.split("=");
        if (!this.getOptionsHolder().isPresent()) {
            this.click();
            this.getOptionsHolder().waitFor(BaseElement::isPresent);
        }
        switch (parsedValue.length) {
            case 1:
                selectByVisibleText(value);
                break;
            case 2:
                switch (parsedValue[0].toLowerCase()) {
                    case "index":
                        selectByIndex(parsedValue[1]);
                        break;
                    case "contains":
                        setValueContains(parsedValue[1]);
                        break;
                    case "starts":
                        setValueStarts(parsedValue[1]);
                        break;
                    case "label":
                        selectByVisibleText(parsedValue[1]);
                        break;
                    case "value":
                        break;
                    case "regex":
                        setValueByRegex(parsedValue[1]);
                        break;
                    default:
                        selectByVisibleText(value);
                }
                break;
            default:
                throw new IllegalArgumentException(format("Cannot parse value %1$s for selection in combobox %2$s", value, this));
        }
        Waiters.SLEEP(300L).go();
        WaitMeters.capture("PAGE_LOAD");
        this.waitForPageUpdate();
    }

    /**
     * Select value by its index
     *
     * @param index value index
     */
    public void setValueByIndex(int index) {
        this.setValue("index=" + index);
    }

    /**
     * Select value that contains provided substring
     *
     * @param partOfValue part of value to select
     */
    public void setValueContains(String partOfValue) {
        this.setValueByRegex(".*" + Pattern.quote(partOfValue) + ".*");
    }

    /**
     * Select value that starts with provided substring
     *
     * @param partOfValue part of value to select
     */
    public void setValueStarts(String partOfValue) {
        this.setValueByRegex("^" + Pattern.quote(partOfValue) + ".*");
    }

    /**
     * Select value that matches provided regular expression
     *
     * @param regex regex to match desired value
     */
    public void setValueByRegex(String regex) {
        if (!getValue().matches(regex)) {
            for (String value : getAllValues()) {
                if (value.replaceAll("\\n", "").matches(regex)) {
                    setValue(value.replaceAll("\\n", ""));
                    return;
                }
            }
            throw new IstfException(format("Combobox %1$s does not contain value that matches %2$s", this, regex));
        }
    }

    protected void selectByVisibleText(String value) {
        this.getOptionsHolder().getWebElement().findElement(By.xpath(String.format(".//div[.='%s']", value))).click();
    }

    protected void selectByIndex(String index) {
        this.getOptionsHolder().getWebElement().findElement(By.xpath(String.format(".//div[%s+1]", index))).click();
    }

    @Override
    protected String getRawValue() {
        return this.getWebElement().getText().trim();
    }

    /**
     * Get all values (i.e. visible texts) from the combobox
     *
     * @return list of values
     */
    @Override
    public List<String> getAllValues() {
        List<String> list = this.getOptions().stream().map(option -> option.getText().trim()).collect(Collectors.toList());
        getCloseOptionsHolder().getWebElement().click();
        Waiters.SLEEP(300L).go();
        WaitMeters.capture("PAGE_LOAD");
        this.waitForPageUpdate();
        return list;
    }

    public List<String> getAllValuesExceptFirstCombineValue() {
        List<String> values = getAllValues();
        values.remove(0);
        return values;
    }

    @Override
    public boolean isEnabled() {
        return !this.getWebElement().findElement(By.xpath("./parent::div")).getAttribute("class").contains("ant-select-disabled");
    }

    public boolean isOptionPresent(String option) {
        return this.getAllValues().contains(option);
    }

    protected StaticElement getOptionsHolder() {
        return this.optionsHolder;
    }

    protected StaticElement getCloseOptionsHolder() {
        return this.closeOptionsHolder;
    }
}

