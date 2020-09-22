package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInfoEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyTransactionHistoryTable.*;
import static com.exigen.ren.main.enums.ProductConstants.TransactionHistoryType.NPB_ENDORSEMENT;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInfoEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestNPBEndorseWhenGroupMemberCompanyIsYes extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-35554", component = POLICY_GROUPBENEFITS)
    public void testNPBEndorseWhenGroupMemberCompanyIsYes() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestDataCustomer").resolveLinks()).resolveLinks());
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataPolicy").resolveLinks()).resolveLinks());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Steps#1, 2 execution");
        statutoryDisabilityInsuranceMasterPolicy.endorseNPBInfo().start();
        statutoryDisabilityInsuranceMasterPolicy.endorseNPBInfo().getWorkspace()
                .fillUpTo(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT_NPB_INFO, TestDataKey.DEFAULT_TEST_DATA_KEY), PolicyInfoEndorseNPBInfoActionTab.class, false);
        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Step#3 verification");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Internal Team"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Assign Agencies/Producers"))).isAbsent();

            LOGGER.info("Step#4 verification");
            AbstractContainer<?, ?> assetListPolicyInfo = statutoryDisabilityInsuranceMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PolicyInfoEndorseNPBInfoActionTab.class).getAssetList();
            ImmutableList.of(QUOTE_CREATION_DATE, QUOTE_EXPIRATION_DATE, UNDEWRITING_COMPANY, GROUP_IS_MEMBER_COMPANY,
                    POLICY_EFFECTIVE_DATE, DELIVERY_MODEL, SITUS_STATE, ZIP_CODE, SMALL_GROUP, FIRST_TIME_BUYER,
                    UNDER_FIFTY_LIVES, ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isDisabled());
            softly.assertThat(assetListPolicyInfo.getAsset(PRIOR_CLAIMS_ALLOWED)).isPresent().isDisabled();

            LOGGER.info("Step#5 verification");
            ImmutableList.of(MEMBER_COMPANY_NAME, NAME_TO_DISPLAY_ON_MP_DOCUMENTS, COUNTY_CODE)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isEnabled().isRequired());
            softly.assertThat(assetListPolicyInfo.getAsset(PRIOR_CARRIER_NAME_TEXT_BOX)).isPresent().isEnabled();
            softly.assertThat(assetListPolicyInfo.getAsset(PRIOR_CARRIER_POLICY_NUMBER)).isPresent().isEnabled();

            LOGGER.info("Step#6 verification");
            ImmutableList.of(CURRENT_POLICY_YEAR_START_DATE, NEXT_POLICY_YEAR_START_DATE, RATE_GUARANTEE_MONTHS,
                    NEXT_RENEWAL_EFFECTIVE_DATE, NEXT_RENEWAL_QUOTE_START_DATE, UNDER_FIFTY_LIVES_WORKFLOW, ANNUAL_ANNIVERSARY_DAY)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isPresent().isDisabled());

            LOGGER.info("Steps#7, 8 verification");
            ImmutableList.of(RENEWAL_FREQUENCY, POLICY_TERM, PRIOR_CLAIMS_RETROACTIVE_EFFECTIVE_DATE)
                    .forEach(asset -> softly.assertThat(assetListPolicyInfo.getAsset(asset)).isAbsent());
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Application Signed Date"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Amount Received with Application"))).isAbsent();

            LOGGER.info("Step#9 verification");
            ImmutableList.of(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get(), NavigationEnum.GroupBenefitsTab.ENROLLMENT.get(),
                    NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get(), NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get())
                    .forEach(tabName -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(tabName))).isAbsent());

            LOGGER.info("Step#11 verification");
            assetListPolicyInfo.getAsset(MEMBER_COMPANY_NAME).setValue("index=1");
            PolicyInfoEndorseNPBInfoActionTab.buttonIssue.click();
            softly.assertThat(Page.dialogConfirmation.labelHeader).hasValue("Please confirm NPBE action");
            softly.assertThat(Page.dialogConfirmation.labelMessage).hasValue("Are you sure you want to issue Non-Premium Bearing Endorsement?");

            LOGGER.info("Step#12 verification");
            Page.dialogConfirmation.confirm();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });

        //Actions added for correct Endorse NPB Info transaction verification
        CustomSoftAssertions.assertSoftly(softly -> {
            PolicySummaryPage.buttonTransactionHistory.click();
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(TYPE)).hasValue(NPB_ENDORSEMENT);
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(TRAN_PREMIUM)).hasValue(String.valueOf(new Currency()));
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(TRANSACTION_DATE)).hasValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(EFFECTIVE_DATE))
                    .hasValue(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT_NPB_INFO, "StartEndorsementNPBInfoActionTab").getValue("Endorsement Date"));
        });
    }
}
