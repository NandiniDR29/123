package com.exigen.ren.modules.commission.commissionstrategy.gb_di_ltd;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionStrategyContext;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionRuleMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionRuleTab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest.commissionStrategyProducts;
import static com.exigen.ren.utils.components.Components.COMMISIONS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.COMMISSIONS;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestValidateCommissionStrategyProductsAndRules extends BaseTest implements GBCommissionStrategyContext {
    @Test(groups = {COMMISSIONS, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-16765", component = COMMISIONS_GROUPBENEFITS)
    public void testValidateCommissionStrategyProductsAndRules() {
        adminApp().open();
        gbCommissionStrategy.navigate();
        gbCommissionStrategy.initiate();
        assertSoftly(softly -> commissionStrategyProducts.forEach(productCodeName -> { /* For each product */
            ImmutableList.of(
                    "TestData_With_Flat_CommissionRule",
                    "TestData_With_Tiered_CommissionRule",
                    "TestData_With_Cumulative_Tiered_CommissionRule",
                    "TestData_With_Subscriber_Count_CommissionRule").forEach(ruleTestData -> { /* For each rule */

                LOGGER.info("Check if rule {} is absent before add it", ruleTestData);
                softly.assertThat(GBCommissionRuleTab.tableCommissionRules).isAbsent();

                TestData tdRule = gbCommissionStrategy.getDefaultTestData().getTestData("DataGather", ruleTestData);
                LOGGER.info("Initiate commission strategy for product {} and add rule {}", productCodeName, ruleTestData);
                gbCommissionStrategy.getDefaultWorkspace().fill(tdRule
                        .adjust(TestData.makeKeyPath(gbCommissionStrategyTab.getMetaKey(), GBCommissionStrategyMetaData.PRODUCT_CODE_NAME.getLabel()), productCodeName)
                        .adjust(TestData.makeKeyPath(gbCommissionStrategyTab.getMetaKey(), GBCommissionStrategyMetaData.AVAILABLE_FOR_OVERRIDE.getLabel()), "false")
                        .adjust(TestData.makeKeyPath(gbCommissionStrategyTab.getMetaKey(), GBCommissionStrategyMetaData.EFFECTIVE_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1).format(DateTimeUtils.MM_DD_YYYY)));

                LOGGER.info("Check if rule {} is added", ruleTestData);
                ImmutableMap<String, String> searchedMap = ImmutableMap.of(
                        GBCommissionRuleTab.CommissionRules.COMMISSION_TYPE.getName(), tdRule.getTestDataList(GBCommissionRuleTab.class.getSimpleName()).get(0).getValue(GBCommissionRuleMetaData.COMMISSION_TYPE.getLabel()),
                        GBCommissionRuleTab.CommissionRules.SALES_CHANNEL.getName(), tdRule.getTestDataList(GBCommissionRuleTab.class.getSimpleName()).get(0).getTestData(GBCommissionRuleMetaData.ADD_COMMISSION_RULE.getLabel()).getValue(GBCommissionRuleMetaData.AddCommissionRule.SALES_CHANNEL.getLabel()));
                softly.assertThat(GBCommissionRuleTab.tableCommissionRules).hasMatchingRows(1, searchedMap);

                LOGGER.info("Remove rule {}", ruleTestData);
                GBCommissionRuleTab.tableCommissionRules.getRow(searchedMap).getCell(GBCommissionRuleTab.CommissionRules.ACTIONS.getName()).controls.links.get(ActionConstants.DELETE).click();
                CampaignPage.dialogConfirmation.confirm();

                LOGGER.info("Check if rule {} is removed", ruleTestData);
                softly.assertThat(GBCommissionRuleTab.tableCommissionRules).isAbsent();
            });
        }));
    }
}
