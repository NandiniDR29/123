package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.ACTIONS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.TOOTH;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.OverrideLineValuesSection.COPAY;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddCopay extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20038", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddCopay() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            softly.assertThat(tableResultsOfAdjudication.getHeader().getValue()).contains("Copay");
            softly.assertThat(tableResultsOfAdjudication.getFooter().getCell(4)).hasValue(new Currency("0").toString());
        });

        LOGGER.info("Step 2");
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        AbstractContainer<?, ?> lineOverrideTabAssetList = new LineOverrideTab().getAssetList();
        TextBox copay = lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(COPAY);
        assertSoftly(softly -> {
            softly.assertThat(lineOverrideTabAssetList.getAsset(REASON)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(copay).isPresent().isEnabled().isOptional().hasValue(StringUtils.EMPTY);
            ImmutableList.of("0", "50000", "100000").forEach(value -> {
                copay.setValue(value);
                softly.assertThat(copay).hasValue(new Currency(value).toString());
                softly.assertThat(copay).hasNoWarning();
            });
            copay.setValue("100001");
            softly.assertThat(copay).hasWarningWithText("The value must be within range: [0.00 ; 100,000.00]");
            softly.assertThat(lineOverrideTabAssetList.getAsset(RESULTS_OF_ADJUDICATION).getAsset(ResultsOfAdjudicationSection.COPAY))
                    .isOptional().hasValue(StringUtils.EMPTY);
        });

        LOGGER.info("Step 3");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        assertThat(ClaimSummaryPage.activitiesAndUserNotes).hasMatchingRows(DESCRIPTION, String.format("Override Line # S1 for Claim # %s", claimNumber));

        LOGGER.info("Step 4");
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_OVERRIDE_VALUES.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        assertThat(copay).isPresent().isDisabled();
    }
}
