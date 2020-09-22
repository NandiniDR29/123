package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.commission.commissiongroup.tabs.CommissionGroupTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData.CommissionOverrideOptions;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab;
import com.exigen.ren.admin.modules.commission.common.tabs.CommissionSearchTab;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.commission.commissionstrategy.CommissionStrategyBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AGENCIES;
import static com.exigen.ren.admin.modules.commission.commissiongroup.metadata.CommissionGroupMetaData.AddAgencies.AGENCY_NAME;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData.AddCommissionOverrideOptionsMetaData.ADD_COMMISSION_OVERRIDE_OPTIONS;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData.COMMISSION_OVERRIDES;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData.CommissionOverrideOptions.BUTTON_CLOSE_POPUP;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionOverrideOptions.ACTIONS;
import static com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs.GBCommissionStrategyTab.CommissionOverrideOptions.COMMISSION_TYPE;
import static com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData.COMMISSION_GROUP_NAME;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.CommissionRateInfo.OVERRIDE_MAX;
import static com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary.CommissionRateInfo.OVERRIDE_MIN;
import static com.exigen.ren.main.enums.AdminConstants.AdminJobTable.NAME;
import static com.exigen.ren.main.enums.TableConstants.CommissionsTable.OVERRIDDEN;
import static com.exigen.ren.main.enums.TableConstants.CommissionsTable.RATE;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AGENCY_ASSIGNMENT;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyAllowManualDefinitionSubscriberCountBasedCommissionAmount extends CommissionStrategyBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION, COMMISSIONS})
    @TestInfo(testCaseId = "REN-26242", component = POLICY_GROUPBENEFITS)
    public void testPolicyAllowManualDefinitionSubscriberCountBasedCommissionAmount() {
        adminApp().open();

        String agencyName = agency.createAgency(tdAgencyDefault);
        commissionGroup.search(commissionGroup.getDefaultTestData().getTestData("SearchData", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionSearchTab.class.getSimpleName(), COMMISSION_GROUP_NAME.getLabel()), "Producers - Standard Broker Schedule"));
        commissionGroup.edit().perform(commissionGroup.getDefaultTestData().getTestData("EditData", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CommissionGroupTab.class.getSimpleName(), AGENCIES.getLabel(), AGENCY_NAME.getLabel()), agencyName), 1);

        gbCommissionStrategy.search(gbCommissionStrategyDefaultTestData.getTestData("SearchData", TestDataKey.DEFAULT_TEST_DATA_KEY));
        gbCommissionStrategy.edit().start("GB_DN - Group Dental");
        String overrideOptionName = GBCommissionStrategyTab.tableCommissionOverrideOptions.getRow(COMMISSION_TYPE.getName(), "Subscriber Count - Flat").getCell(NAME.getName()).getValue();
        GBCommissionStrategyTab.tableCommissionOverrideOptions.getRow(COMMISSION_TYPE.getName(), "Subscriber Count - Flat").getCell(ACTIONS.getName()).controls.links.get(ActionConstants.VIEW).click();
        assertSoftly(softly -> {
            DialogAssetList overrideOptions = gbCommissionStrategyTab.getAssetList().getAsset(COMMISSION_OVERRIDES).getAsset(ADD_COMMISSION_OVERRIDE_OPTIONS);
            softly.assertThat(overrideOptions.getAsset(CommissionOverrideOptions.OVERRIDE_RANGE_MIN)).hasValue(new Currency().toPlainString());
            softly.assertThat(overrideOptions.getAsset(CommissionOverrideOptions.OVERRIDE_RANGE_MAX)).hasValue(new Currency(10).toPlainString());
            overrideOptions.getAsset(BUTTON_CLOSE_POPUP).click();
        });

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AGENCY_ASSIGNMENT.getLabel()),
                        ImmutableList.of(customerNonIndividual.getDefaultTestData("AddAgency", "Add_Agency_By_AgencyName")
                                .adjust(TestData.makeKeyPath(AGENCY_PRODUCER.getLabel(), GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()), agencyName))));
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.AGENCY_PRODUCER.getLabel()), ImmutableList.of("ALL")), groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());

        LOGGER.info("Step 1");
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENT_SUB_PRODUCER.getLabel()))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS.getLabel()), "Yes")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), AGENCY_PRODUCER_COMBO.getLabel()), agencyName), premiumSummaryTab.getClass());


        LOGGER.info("Step 2");
        PremiumSummaryTab.getCommissionOverrideButtonForAgencyWithCoverage(agencyName, GroupDentalCoverages.DENTAL).click();
        DialogOverrideCommissionPremiumSummary.commissionOverrideOption.setValue(overrideOptionName);
        Row firstRowCommissionRate = DialogOverrideCommissionPremiumSummary.commissionRateInfoTable.getRow(1);
        assertSoftly(softly -> {
            softly.assertThat(firstRowCommissionRate.getCell(OVERRIDE_MIN.getName())).hasValue(new Currency().toString());
            softly.assertThat(firstRowCommissionRate.getCell(OVERRIDE_MAX.getName())).hasValue(new Currency(10).toString());
        });
        firstRowCommissionRate.getCell(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.RATE.getName()).controls.textBoxes.getFirst().setValue("2");
        assertThat(firstRowCommissionRate.getCell(DialogOverrideCommissionPremiumSummary.CommissionRateInfo.RATE.getName()).controls.textBoxes.getFirst()).hasValue(new Currency(2).toString());

        LOGGER.info("Step 3");
        PremiumSummaryTab.dialogOverrideCommission.confirm();
        assertSoftly(softly -> {
            Row firstRowCommissionsTable = PremiumSummaryTab.getCommissionTableForAgencySection(agencyName, GroupDentalCoverages.DENTAL).getRow(1);
            softly.assertThat(firstRowCommissionsTable.getCell(RATE.getName())).hasValue(new Currency(2).toString());
            softly.assertThat(firstRowCommissionsTable.getCell(OVERRIDDEN.getName())).hasValue("Yes");
        });
    }
}
