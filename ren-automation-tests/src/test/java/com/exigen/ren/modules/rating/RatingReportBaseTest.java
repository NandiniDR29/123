package com.exigen.ren.modules.rating;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.modules.RestBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.exigen.ren.common.Tab.doubleWaiter;
import static com.exigen.ren.main.enums.RatingReportConstants.*;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.*;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.REDISTRIBUTED_RATES;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.RENEWAL_RATES;

public class RatingReportBaseTest extends RestBaseTest {

    protected static final String MANUAL = "Manual";
    protected static final String PCT_FROM_CURRENT = "% From Current";
    protected static final String PCT_FROM_RENEWAL = "% From Renewal";
    protected static final String UW_ADJUSTMENT_FIRST = "UW Adjustment 1";
    protected static final String UW_ADJUSTMENT_SECOND = "UW Adjustment 2";
    protected static final String PLAN_ALACARTE = "ALACARTE";
    protected static final String PLAN_ASO_ALACARTE = "ASOALACARTE";
    //test values
    protected static final String UW_ADJUSTMENT_FIRST_TEST_VALUE = "200.00";
    protected static final String UW_ADJUSTMENT_SECOND_TEST_VALUE = "300.00";
    protected static final String UW_REASON_FIRST_TEST_VALUE = DUAL_OPTION;
    protected static final String UW_REASON_SECOND_TEST_VALUE = PARTICIPATION_ADJ;
    protected static final String TEST_COMMENT_FIRST = "Test Comment First";
    protected static final String TEST_COMMENT_SECOND = "Test Comment Second";
    protected static final String CURRENT_RATES_TEST_VALUE = "210.00";
    protected static final String RENEWAL_RATES_TEST_VALUE = "350.00";
    protected static final String CURRENT_ENROLLMENT_TEST_VALUE = "10";
    protected static final Map<String, String> OVERRIDE_REASON_MAPPING = ImmutableMap.of(
            "DUALOP", DUAL_OPTION,
            "PARTICIPATIONADJ", PARTICIPATION_ADJ,
            "FLEXPLAN", FLEX_PLAN,
            "INDIVIDUALADJ", INDIVIDUAL_ADJ
    );
    //locators
    protected static final String PARENT_LOCATOR_MULTIOPTION = "//h3[text()='%s']//following-sibling::div[1]";
    private static final String TIER_SECTION_CELL_MULTIOPTION_LOCATOR = "//h3[text()='%s']//following-sibling::div[2]//table//td[text()='%s']//following-sibling::td[%s]%s";
    private static final String TIER_SECTION_CELL_LOCATOR = "//h3[text() ='Calculate Rate']//following-sibling::div[@class='plan-table']//table//td[text()='%s']//following-sibling::td[%s]%s";

    /**
     * Find and return Cell value from Tier table in case 'Multi Option' isn't selected
     * e.g. only 1 plan is added to quote
     *
     * @param tier  Name of Tier. Similar to Table row name  e.g. 'Employee only'
     * @param rates Name of Rates. Similar to Table column name  e.g. 'CURRENT RATES'
     * @return String cell value
     */
    protected static String getCellValueTierTable(String tier, String rates) {
        int index = getTierTableColumnMapping().get(rates);
        String value;
        StaticElement cell = new StaticElement(ByT.xpath(TIER_SECTION_CELL_LOCATOR).format(tier, index, ""));
        if (!ImmutableList.of(COMPOSITE_RATE, ANNUAL_PREMIUM, PCT_LOWERCASE_FROM_CURRENT, PCT_LOWERCASE_FROM_RENEWAL).contains(tier) && 2 < index && index < 6) {
            value = new TextBox(cell, By.xpath("./input")).getValue();
        }
        else {
            value = cell.getValue();
        }
        return value;
    }

    /**
     * Find and return Cell value from Tier table in case 'Multi Option' selected
     * e.g. 2-3 plans are added to quote
     *
     * @param tier                Name of Tier. Similar to Table row name  e.g. 'Employee only'
     * @param rates               Name of Rates. Similar to Table column name  e.g. 'CURRENT RATES'
     * @param multiOptionPlanName Name of Plan for which values will be getting
     *                            e.g. 'A La Carte'
     * @return String cell value
     */
    protected static String getCellValueTierTable(String tier, String rates, String multiOptionPlanName) {
        int index = getTierTableColumnMapping().get(rates);
        String value;
        StaticElement cell = new StaticElement(ByT.xpath(TIER_SECTION_CELL_MULTIOPTION_LOCATOR).format(multiOptionPlanName, tier, index, ""));
        if (!ImmutableList.of(COMPOSITE_RATE, ANNUAL_PREMIUM, PCT_LOWERCASE_FROM_CURRENT, PCT_LOWERCASE_FROM_RENEWAL).contains(tier) && 2 < index && index < 6) {
            value = new TextBox(cell, By.xpath("./input")).getValue();
        }
        else {
            value = cell.getValue();
        }
        return value;
    }

    /**
     * Set value in Tier table cell in case 'Multi Option' isn't selected
     * e.g. only 1 plan is added to quote
     *
     * @param tier  Name of Tier. Similar to Table row name  e.g. 'Employee only'
     * @param rates Name of Rates. Similar to Table column name  e.g. 'CURRENT RATES'
     * @param value String value that will be setting e.g. '100'
     */
    protected static void setCellValueTierTable(String tier, String rates, String value) {
        int index = getTierTableColumnMapping().get(rates);
        if (!ImmutableList.of(COMPOSITE_RATE, ANNUAL_PREMIUM, PCT_LOWERCASE_FROM_CURRENT, PCT_LOWERCASE_FROM_RENEWAL).contains(tier) && 2 < index && index < 6) {
            new TextBox(ByT.xpath(TIER_SECTION_CELL_LOCATOR).format(tier, index, "/input")).setValue(value, doubleWaiter);
        }
        else {
            throw new IstfException(String.format("'setValue' method is not applicable for non-fillable cell: Tier=%s, Rates=%s, Value=%s", tier, rates, value));
        }
    }

    /**
     * Set value in Tier table cell in case 'Multi Option' selected
     * e.g. 2-3 plans are added to quote
     *
     * @param tier                Name of Tier. Similar to Table row name  e.g. 'Employee only'
     * @param rates               Name of Rates. Similar to Table column name  e.g. 'CURRENT RATES'
     * @param value               String value that will be setting e.g. '100'
     * @param multiOptionPlanName Name of Plan for which values will be setting
     *                            e.g. 'A La Carte'
     */
    protected static void setCellValueTierTable(String tier, String rates, String value, String multiOptionPlanName) {
        int index = getTierTableColumnMapping().get(rates);
        if (!ImmutableList.of(COMPOSITE_RATE, ANNUAL_PREMIUM, PCT_LOWERCASE_FROM_CURRENT, PCT_LOWERCASE_FROM_RENEWAL).contains(tier) && 2 < index && index < 6) {
            new TextBox(ByT.xpath(TIER_SECTION_CELL_MULTIOPTION_LOCATOR).format(multiOptionPlanName, tier, index, "/input")).setValue(value, doubleWaiter);
        }
        else {
            throw new IstfException(String.format("'setValue' method is not applicable for non-fillable cell: Tier=%s, Rates=%s, Value=%s", tier, rates, value));
        }
    }

    private static Map<String, Integer> getTierTableColumnMapping() {
        Map<String, Integer> map = new HashMap<>();
        map.put(ADJUSTED_MANUAL_RATES.getName(), 1);
        map.put(ADJUSTED_FORMULA_RATES.getName(), 2);
        map.put(CURRENT_ENROLLMENT.getName(), 3);
        map.put(CURRENT_RATES.getName(), 4);
        map.put(RENEWAL_RATES.getName(), 5);
        map.put(REDISTRIBUTED_RATES.getName(), 6);
        return map;
    }

    protected String getNumberWithoutDecimal(String value) {
        return value.split("\\.")[0];
    }

    protected String formatValueToCurrency(String value) {
        return new DecimalFormat("#,###,##0.00").format(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP));
    }

    protected String formatValueToPct(String value) {
        return new DecimalFormat("#,###,##0.00%").format(new BigDecimal(value));
    }
}
