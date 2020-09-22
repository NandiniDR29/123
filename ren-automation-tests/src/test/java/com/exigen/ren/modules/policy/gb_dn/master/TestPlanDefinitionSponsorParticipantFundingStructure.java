package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionSponsorParticipantFundingStructure extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static String BLANK_VALUE = "";
    private static ImmutableList<String> CONTRIBUTION_TYPE_LIST = ImmutableList.of(BLANK_VALUE, "Non-contributory", "Voluntary", "Sponsor/Participant Split");
    private static AssetList sponsorParticipantFundingAssetStructureAssetList = planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15803", "REN-15809", "REN-15813", "REN-15924", "REN-15926", "REN-15928", "REN-15929", "REN-15920", "REN-15923"}, component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionSponsorParticipantFundingStructure() {
        mainApp().open();

        createCustomerCaseProfileAndFillQuoteUptoTab(PolicyInformationTab.class);
        assertSoftly(softly -> {
            LOGGER.info("---=={REN-15803 Step 1}==---");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SHORT_TERM)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 2}==---");
            groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "20"), PlanDefinitionTab.class);
            planDefinitionTab.selectDefaultPlan();
            LOGGER.info("---=={REN-15803 Step 3,4,5}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_TYPE)).isPresent().hasValue(BLANK_VALUE).hasOptions(CONTRIBUTION_TYPE_LIST);
            LOGGER.info("---=={REN-15803 Step 7 not displayed for Contribution Type = blank check}==---");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 6 Set Contribution Type = Voluntary,Step 9,REN-15926 Step 2.1}==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_TYPE).setValue(CONTRIBUTION_TYPE_LIST.get(2));
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT)).isPresent().isRequired().isDisabled().hasValue("100");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(REQUIRED_PARTICIPATION_PCT)).isPresent().isOptional();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(ASSUMED_PARTICIPATION_PCT)).isPresent().isRequired();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_PAYMENT_MODE)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 6 displayed check}==---");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).isPresent();
            LOGGER.info("---=={REN-15803 Step 7 Check if member payment mode has default value 12}==---");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).hasValue(ImmutableList.of("12"));
            LOGGER.info("---=={REN-15813 Step 2}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_PERCENTAGE_BASED)).isAbsent();
            LOGGER.info("---=={REN-15809 Step 5}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT)).isPresent().isDisabled().hasValue("100");
            LOGGER.info("---=={REN-15926 Step 2.2,2.3}==---");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            int totalEligibleLives = Integer.parseInt(policyInformationTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).getValue());
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            sponsorParticipantFundingAssetStructureAssetList.getAsset(REQUIRED_PARTICIPATION_PCT).setValue("100");
            int requiredParticiPationPct = Integer.parseInt(sponsorParticipantFundingAssetStructureAssetList.getAsset(REQUIRED_PARTICIPATION_PCT).getValue()) / 100;
            String mimiumNoOfParticipantsValue = String.valueOf(Math.multiplyExact(requiredParticiPationPct, totalEligibleLives));
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(MINIMUM_NO_OF_PARTICIPANTS)).isPresent().isDisabled().isOptional().hasValue(mimiumNoOfParticipantsValue);
            LOGGER.info("---=={REN-15926 Step 4 Set 'Total Number of Eligible Lives' * 'Required Participation %' <2}==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(REQUIRED_PARTICIPATION_PCT).setValue("1");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(MINIMUM_NO_OF_PARTICIPANTS)).hasValue("2");
            LOGGER.info("---=={REN-15926 Step 5}==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(REQUIRED_PARTICIPATION_PCT).setValue(BLANK_VALUE);
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(MINIMUM_NO_OF_PARTICIPANTS)).hasValue(BLANK_VALUE);
            LOGGER.info("---=={REN-15813 Step 2.1}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_PERCENTAGE_BASED)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 3.2}==---");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SELF_ADMINISTERED)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(DIRECT_BILL)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 5,7,Step 8 Set Contribution Type = Sponsor/Participant Split,Step 9 }==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_TYPE).setValue(CONTRIBUTION_TYPE_LIST.get(3));
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).isAbsent();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_PERCENTAGE_BASED)).isPresent().isRequired().isEnabled();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_PAYMENT_MODE)).isPresent().isRequired().hasValue("12");
            LOGGER.info("---=={REN-15803 Step 10,Step 11,REN-15920 Step 5}==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_PERCENTAGE_BASED).setValue(VALUE_YES);
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT_EMPLOYEE_COVERAGE)).isPresent().isEnabled().hasValue(BLANK_VALUE).isRequired();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT_DEPENDENT_COVERAGE)).isPresent().isEnabled().hasValue(BLANK_VALUE).isRequired();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_MONTHLY_COVERAGE)).isAbsent();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_MONTHLY_COVERAGE)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 12,Step 13 REN-15923 Step 7}==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_PERCENTAGE_BASED).setValue(VALUE_NO);
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_EMPLOYEE_MONTHLY_COVERAGE)).isPresent().isEnabled().hasValue(BLANK_VALUE).isRequired();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_MONTHLY_COVERAGE)).isPresent().isEnabled().hasValue(BLANK_VALUE).isRequired();
            LOGGER.info("---=={REN-15920 Step 3}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT_EMPLOYEE_COVERAGE)).isAbsent();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT_DEPENDENT_COVERAGE)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).setValue(ImmutableList.of("Employee Only"));
            Page.dialogConfirmation.buttonOk.click();
            LOGGER.info("---=={REN-15803 Step 11 Validate Rule 002. Hide if 'Plan Definition' tab → 'Plan Selection' section → 'Coverage Tiers' is ONLY \"Employee Only\"}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(PARTICIPANT_CONTRIBUTION_PCT_DEPENDENT_COVERAGE)).isAbsent();
            LOGGER.info("---=={REN-15803 Step 13 Validate Rule 001 Hide if 'Plan Definition' tab → 'Plan Selection' section → 'Coverage Tiers' is ONLY \"Employee Only\"}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_CONTRIBUTION_AMOUNT_DEPENDENT_MONTHLY_COVERAGE)).isAbsent();
            Tab.buttonTopCancel.click();
            Page.dialogConfirmation.buttonYes.click();
            LOGGER.info("---=={REN-15928 Step 1}==---");
            createDefaultNonIndividualCustomer();
            caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName()), caseProfile.defaultTestData().getTestData("CaseProfile", "CaseProfileDetailsTab_NoPaymentMode")), groupDentalMasterPolicy.getType());
            groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
            groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PlanDefinitionTab.class);
            planDefinitionTab.selectDefaultPlan();
            LOGGER.info("---=={REN-15928-REN-15929 Step 2}==---");
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_PAYMENT_MODE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).isAbsent();
            LOGGER.info("---=={REN-15928 Step 3,REN-15929 Step 4}==---");
            sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_TYPE).setValue(CONTRIBUTION_TYPE_LIST.get(2));
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_PAYMENT_MODE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).isPresent();
            softly.assertThat(new StaticElement(By.id("policyDataGatherForm:selectedMemberPaymentModes"))).hasValue(BLANK_VALUE);
            LOGGER.info("---=={REN-15928 Step 4}==---");
            ImmutableList.of("Non-contributory", "Sponsor/Participant Split").forEach(controlvalue -> {
                sponsorParticipantFundingAssetStructureAssetList.getAsset(CONTRIBUTION_TYPE).setValue(controlvalue);
                softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(SPONSOR_PAYMENT_MODE)).isPresent().hasValue(BLANK_VALUE);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(MEMBER_PAYMENT_MODE)).isAbsent();
            });

            LOGGER.info("---=={REN-15924 Step 5,Switch User 2 with Authority Level = 0}==---");
            mainApp().reopen(USER_10_LOGIN, "qa");
            createCustomerCaseProfileAndFillQuoteUptoTab(PlanDefinitionTab.class);
            planDefinitionTab.selectDefaultPlan();
            softly.assertThat(sponsorParticipantFundingAssetStructureAssetList.getAsset(REQUIRED_PARTICIPATION_PCT)).isDisabled();
        });
    }

    private void createCustomerCaseProfileAndFillQuoteUptoTab(Class<? extends Tab> tabClass) {
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), tabClass, false);
    }
}
